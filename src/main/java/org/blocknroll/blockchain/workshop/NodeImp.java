package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents the interface towards the peers, thus declaring input / output interfaces.
 */
public class NodeImp {

  private Cluster cluster;
  private Logger logger = LogManager.getLogger(NodeImp.class);
  private Miner miner;
  private Chain chain;

  // -----------------------------------------------------------------------------------------------------------------
  // Cluster methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Constructor.
   *
   * @param cluster the cluster interface.
   */
  public NodeImp(Cluster cluster) throws IOException, SodiumLibraryException {
    // Generate the key pair if directory does not exist
    if (!Files.isDirectory(Paths.get("key"))) {
      Files.createDirectory(Paths.get("key"));
      CryptoUtil.generatePublicSecretKeys("key/pub.key", "key/sec.key");
    }

    // Initialise members
    this.cluster = cluster;
    chain = new Chain(cluster.getId());
    miner = new Miner();

    // Load the chain from disk if it exists or generate genesis block
    if (Files.isDirectory(Paths.get("chain/" + cluster.getId() + "/"))) {
      loadChain();
    } else {
      // Generate the genesis block if it does not exist
      Paths.get("chain/" + cluster.getId() + "/").toFile().mkdirs();
      chain.addBlock(new Block());
    }
  }

  /**
   * Loads the chain from directory chain.
   *
   * @throws IOException throws input output or crypto exceptions.
   */
  private void loadChain() throws IOException, SodiumLibraryException {
    try (DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get("chain/" + cluster.getId()))) {
      List<Block> blocks = StreamSupport.stream(files.spliterator(), false)
          .sorted((o1, o2) -> {
            try {
              return Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2));
            } catch (IOException e) {
              e.printStackTrace();
            }
            return 0;
          })
          .filter(file -> Files.isRegularFile(file))
          .map(file -> {
            Block tmp = new Block();
            try {
              byte[] buffer = Files.readAllBytes(file);
              tmp.deserialise(CryptoUtil.hexStringToByteBuffer(new String(buffer)));
            } catch (IOException e) {
              e.printStackTrace();
            }
            return tmp;
          })
          .collect(Collectors.toList());
      if (verifyChain(blocks)) {
        chain = new Chain(cluster.getId(), blocks);
      }
    }
  }

  /**
   * @param blocks the blocks to be processed.
   */
  public void processBlock(List<Block> blocks)
      throws Exception {
    processBlockResponse(blocks, chain.getLastBlock());
  }

  /**
   * This is invoked when other peers are requesting the chain.
   */
  public void requestChain() throws Exception {
    processBlock(chain.getBlocks());
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Blockchain methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Add facts to be grouped and mined into a block.
   *
   * @param facts the facts to be mined.
   */
  public void mineFacts(Collection<Fact> facts) throws Exception {
    // Check inputs
    if (facts == null) {
      throw new IllegalArgumentException("Cannot create a fact with null values");
    }

    // Mine block, verify it and add it to the chain.
    Block block = miner.mine(facts, chain.getLastBlock(), 2);
    logger.info("Mining facts into block " + block.getIdentifier());
    cluster.requestProofOfWork(block);
  }

  /**
   * Returns the latest block in this chain.
   *
   * @return the latest block in this chain.
   */
  public Block getLastBlock() {
    return chain.getLastBlock();
  }

  /**
   * Returns the chain.
   *
   * @return the Chain for this node.
   */
  public Chain getChain() {
    return chain;
  }

  /**
   * Verifies that the given block has a valid hash and is the next one in the chain
   *
   * @param blocks the blocks to be processed.
   * @return true if block is valid, false otherwise.
   */
  private void processBlockResponse(List<Block> blocks, Block previous)
      throws Exception {
    Block block = blocks.get(blocks.size() - 1);
    if (block.getIdentifier() > previous.getIdentifier()) {
      block.getPreviousHash().rewind();
      previous.getHash().rewind();
      if (block.getPreviousHash().equals(previous.getHash())) {
        logger.debug("This is a good block!");
        chain.addBlock(block);
      } else if (blocks.size() == 1) {
        logger.warn("Request the chain to the peers.");
        cluster.requestChain();
      } else {
        logger.warn("Received blockchain is longer, replace current one.");
        if (verifyChain(blocks)) {
          chain = new Chain(cluster.getId(), blocks);
        }
      }
    } else {
      logger.warn("The received block is in the past ... just ignore it.");
    }
  }

  /**
   * Synchronize the given chain with the current one.
   *
   * @param blocks the chain to be synchronised.
   */
  public boolean verifyChain(List<Block> blocks) throws SodiumLibraryException {
    Iterator<Block> iter = blocks.iterator();
    Block previous = iter.next();
    while (iter.hasNext()) {
      Block block = iter.next();
      if (!verifyBlock(block, previous)) {
        return false;
      }
      previous = block;
    }
    return true;
  }

  /**
   * Synchronize the given chain with the current one.
   *
   * @param newBlock the block to be verified.
   * @param previousBlock the block previous to the new one.
   */
  public boolean verifyBlock(Block newBlock, Block previousBlock) throws SodiumLibraryException {
    newBlock.getPreviousHash().rewind();
    previousBlock.getHash().rewind();
    if ((previousBlock.getIdentifier() + 1) != newBlock.getIdentifier()) {
      logger.warn("Invalid index");
      return false;
    } else if (!newBlock.getPreviousHash().equals(previousBlock.getHash())) {
      logger.warn("New block previous hash is:");
      logger.warn(CryptoUtil.bufferToHexString(newBlock.getPreviousHash()));
      logger.warn("Last block's hash is:");
      logger.warn(CryptoUtil.bufferToHexString(previousBlock.getHash()));
      return false;
    } else if (!CryptoUtil.calculateHash(newBlock).equals(newBlock.getHash())) {
      logger.warn("Proof of work failed.");
      logger.warn("Calculated hash for block is:");
      logger.warn(CryptoUtil.bufferToHexString(CryptoUtil.calculateHash(newBlock)));
      logger.warn("Actual hash for block is:");
      logger.warn(CryptoUtil.bufferToHexString(newBlock.getHash()));
      return false;
    }
    return true;
  }
}
