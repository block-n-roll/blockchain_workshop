package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents the interface towards the peers, thus declaring input / output interfaces.
 */
public class NodeImp implements Node {

  private final String ip;
  private final int port;
  private Logger logger = LogManager.getLogger(NodeImp.class);
  private Miner miner;
  private Chain chain;
  private Collection<Node> peers;

  // -----------------------------------------------------------------------------------------------------------------
  // Node methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Constructor.
   *
   * @param ip the IP where this node is running.
   * @param port the port where this node is running.
   */
  public NodeImp(String ip, int port) throws IOException, SodiumLibraryException {
    // Generate the key pair if directory does not exist
    if(!Files.isDirectory(Paths.get("keys"))) {
      Files.createDirectory(Paths.get("keys"));
      CryptoUtil.generatePublicSecretKeys("keys/pub.key", "keys/sec.key");
    }

    // Initialise members
    this.ip = ip;
    this.port = port;
    peers = new ArrayList<>();
    chain = new Chain();
    miner = new Miner(chain);
  }

  /**
   * Add a peer node to the cluster.
   */
  public void addPeer(Node node) {
    peers.add(node);
    node.getLastBlock();
  }

  /**
   * Returns the peers connected to this node.
   *
   * @return the peers connected to this node.
   */
  public Collection<Node> getPeers() {
    return peers;
  }

  /**
   * @param sender the sender node.
   * @param blocks the blocks to be processed.
   */
  public void processBlocks(Node sender, List<Block> blocks) throws SodiumLibraryException {
    processBlockResponse(sender, blocks, chain.getLastBlock());
  }

  /**
   * This is invoked when other peers are requesting the chain.
   *
   * @param sender the sender node.
   */
  public void requestChain(Node sender) throws SodiumLibraryException {
    sender.processBlocks(this, chain.getBlocks());
  }

  /**
   * Broadcast a message to all the nodes in the cluster.
   *
   * @param block the blocks to be sent to the cluster.
   */
  public void notifyNewBlock(Block block) throws SodiumLibraryException {
    for (Node peer : peers) {
      peer.processBlocks(this, Collections.singletonList(block));
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Blockchain methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Add facts to be grouped and mined into a block.
   *
   * @param facts the facts to be mined.
   */
  public void addFacts(Collection<Fact> facts) throws SodiumLibraryException {
    // Check inputs
    if (facts == null) {
      throw new IllegalArgumentException("Cannot create a fact with null values");
    }

    // Mine block, verify it and add it to the chain.
    Block block = miner.mine(facts);
    logger.warn(CryptoUtil.bufferToHexString(block.getHash()));
    if (verifyBlock(block, chain.getLastBlock())) {
      chain.addBlock(block);
      notifyNewBlock(block);
      // TODO: Response OK
    }
    // TODO: Response ERROR
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
  private boolean processBlockResponse(Node sender, List<Block> blocks, Block previous)
      throws SodiumLibraryException {
    Block block = blocks.get(blocks.size() - 1);
    if (block.getIdentifier() > previous.getIdentifier()) {
      block.getPreviousHash().rewind();
      previous.getHash().rewind();
      if (block.getPreviousHash().equals(previous.getHash())) {
        logger.debug("This is a good block!");
        chain.addBlock(block);
        notifyNewBlock(block);
        return true;
      } else if (blocks.size() == 1) {
        logger.warn("Request the chain to the peers.");
        sender.requestChain(this);
      } else {
        logger.warn("Received blockchain is longer, replace current one.");
        if (verifyChain(blocks)) {
          chain = new Chain(blocks);
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
