package org.blocknroll.blockchain.workshop;

import java.util.Collection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents the interface towards the cluster, thus declaring input / output
 * interfaces.
 */
public class Node {

  Logger logger = LogManager.getLogger(Node.class);
  private Miner miner;
  private Chain chain;

  // -----------------------------------------------------------------------------------------------------------------
  // Cluster methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Constructor.
   */
  public Node() {
    // TODO: Create the miner
    // TODO: Create genesis block
    // TODO: Create the chain
  }

  public void addToCluster() {

  }

  public void removeFromCluster() {

  }

  // -----------------------------------------------------------------------------------------------------------------
  // Blockchain methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Add facts to be grouped and mined into a block.
   *
   * @param facts the facts to be mined.
   */
  public void addFacts(Collection<Fact> facts) {
    if (facts == null) {
      throw new IllegalArgumentException("Cannot create a fact with null values");
    }
    Block block = miner.mine(facts);
    if (requestProofOfWork(block)) {
      addBlock(block);
    }
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
   * Add a block to the chain.
   *
   * @param block the block to be send to verify and add to the chain.
   */
  public void addBlock(Block block) {
    // Check inputs
    if (block == null) {
      throw new IllegalArgumentException("Cannot add a null block.");
    }

    // Check errors
    if (chain.addBlock(block)) {
      // TODO: Response ERROR
    }

    // TODO: Response OK
  }

  /**
   * Request to the cluster the proof of work for a given mined block
   *
   * @param block the block to be send to the cluster for verification.
   */
  public boolean requestProofOfWork(Block block) {
    // Check inputs
    if (block == null) {
      throw new IllegalArgumentException("Cannot add a null block.");
    }

    // TODO:

    return true;
  }

  /**
   * Synchronize the given chain with the current one.
   *
   * @param chain the chain to be synchronised.
   */
  public boolean verifyChain(Chain chain) {
    // TODO:
    return true;
  }
}
