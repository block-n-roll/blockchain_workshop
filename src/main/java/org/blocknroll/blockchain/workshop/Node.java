package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents the interface towards the peers, thus declaring input / output
 * interfaces.
 */
public class Node {

  private final Connector connector;
  private Logger logger = LogManager.getLogger(Node.class);
  private final String ip;
  private final int port;
  private Miner miner;
  private Chain chain;
  private Collection<Node> peers;

  // -----------------------------------------------------------------------------------------------------------------
  // Connector methods
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Constructor.
   *
   * @param ip the IP where this node is running.
   * @param port the port where this node is running.
   */
  public Node(String ip, int port, Connector connector) {
    this.ip = ip;
    this.port = port;
    this.connector = connector;
    peers = new ArrayList<Node>();
    chain = new Chain();
    miner = new Miner(chain);
  }

  /**
   * Add a peer node to the cluster.
   * @param node
   */
  public void addPeer(Node node) {
    peers.add(node);
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
    addBlock(block);
    broadcast(block);
    // Return failure
  }

  /**
   * Broadcast a message to all the nodes in the cluster.
   * @param block the blocks to be sent to the cluster.
   */
  public void broadcast(Block block) {
    for(Node peer: peers) {
      connector.send(block, peer);
    }
  }

  /**
   * Broadcast a message to all the nodes in the cluster.
   * @param blocks the blocks to be sent to the cluster.
   */
  public void broadcast(Chain blocks) {
    for(Node peer: peers) {
      connector.send(blocks, peer);
    }
  }

  /**
   * Returns the latest block in this chain.
   *
   * @return the latest block in this chain.
   */
  public Block getLatestBlock() {
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
   * Synchronize the given chain with the current one.
   *
   * @param chain the chain to be synchronised.
   */
  public boolean verifyChain(Chain chain) {
    // TODO:
    return true;
  }
}
