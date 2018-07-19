package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents the interface towards the peers, thus declaring input / output interfaces.
 */
public class Node {

  public static final int DIFFICULTY = 1;
  final private Cluster cluster;
  final private Logger logger = LogManager.getLogger(Node.class);
  final private Miner miner;
  private Chain chain;

  // -----------------------------------------------------------------------------------------------------------------
  // Cluster methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Constructor.
   *
   * @param cluster the cluster interface.
   */
  public Node(Cluster cluster) throws Exception {
    // Initialise members
    this.cluster = cluster;
    chain = new Chain(cluster.getId());
    miner = new Miner();

    // Load the chain from disk if it exists or generate genesis block
    if (Files.isDirectory(Paths.get("chain/" + cluster.getId() + "/"))) {
      // TODO: Load the chain if file already exist (hint: see loadChain method
    } else {
      // TODO: Add the genesis block if no chain is persisted
    }
  }

  /**
   * Loads the chain from directory chain.
   *
   * @throws IOException throws input output or crypto exceptions.
   */
  private void loadChain() throws Exception {
    try (DirectoryStream<Path> files = Files
        .newDirectoryStream(Paths.get("chain/" + cluster.getId()))) {
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
  public boolean processBlock(List<Block> blocks)
      throws Exception {
    return processBlockResponse(blocks, chain.getLastBlock());
  }

  /**
   * This is invoked when other peers are requesting the chain.
   */
  public Chain requestChain() throws Exception {
    // TODO: Return this node's chain
    return null;
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
    logger.info("Mining facts into block ...");
    // TODO: Mine the facts here
    Block block = null;
    logger.info("Facts mined into block " + block.getIdentifier());
    cluster.requestProofOfWork(block);
  }

  /**
   * Returns the latest block in this chain.
   *
   * @return the latest block in this chain.
   */
  public Block getLastBlock() {
    // TODO: Return the last block
    return null;
  }

  /**
   * Returns the chain.
   *
   * @return the Chain for this node.
   */
  public Chain getChain() {
    // TODO: Return the chain here
    return null;
  }

  /**
   * Verifies that the given block has a valid hash and is the next one in the chain
   *
   * @param blocks the blocks to be processed.
   * @return true if block is valid, false otherwise.
   */
  private boolean processBlockResponse(List<Block> blocks, Block previous)
      throws Exception {
    Block block = blocks.get(blocks.size() - 1);
    if (block.getIdentifier() > previous.getIdentifier()) {
      block.getPreviousHash().rewind();
      previous.getHash().rewind();
      if (block.getPreviousHash().equals(previous.getHash())) {
        logger.debug("This is a good block!");
        // TODO: Add the block to the chain!!
        return true;
      } else if (blocks.size() == 1) {
        logger.warn("Request the chain to the peers.");
        // TODO: Request the chain to the cluster
      } else {
        logger.warn("Received blockchain is longer, replace current one.");
        if (verifyChain(blocks)) {
          // TODO: Replace this chain's content with the given blocks
        }
      }
    } else {
      logger.warn("The received block is in the past ... just ignore it.");
    }
    return false;
  }

  /**
   * Synchronize the given chain with the current one.
   *
   * @param blocks the chain to be synchronised.
   */
  public boolean verifyChain(List<Block> blocks) throws SodiumLibraryException {
    // TODO: Verify the list of blocks ... hint see verifyBlock
    return false;
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
    // TODO: Check blocks are not consecutive
    if (true) {
      logger.warn("Invalid index");
      return false;
    }
    // TODO: Check previous hash is not correct
    else if (true) {
      logger.warn("New block previous hash is:");
      logger.warn(CryptoUtil.bufferToHexString(newBlock.getPreviousHash()));
      logger.warn("Last block's hash is:");
      logger.warn(CryptoUtil.bufferToHexString(previousBlock.getHash()));
      return false;
    }
    // TODO: Compute the hash and validate that is wrong (hint: CryptoUtil.calculateHash)
    else if (true) {
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
