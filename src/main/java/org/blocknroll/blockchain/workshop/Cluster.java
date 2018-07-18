package org.blocknroll.blockchain.workshop;

import java.util.Collection;
import java.util.List;

/**
 * This is the interface to be implemented by a node.
 */
public interface Cluster {

  void setSeed(Cluster seed);

  void requestProofOfWork(Block block) throws Exception;

  String getId(); // IP + PORT

  void addPeer(Cluster n);

  List<Cluster> getPeers();

  // -----------------------------------------------------------------------------------------------
  // Requested by application level
  // -----------------------------------------------------------------------------------------------

  /**
   * Add facts into the blockchain.
   *
   * @param facts the collection of facts to be mined into a blockchain.
   */
  void addFacts(Collection<Fact> facts) throws Exception;

  /**
   * Requests the whole chain.
   *
   * @return the chain.
   */
  Chain getChain();

  // -----------------------------------------------------------------------------------------------
  // Requested by node
  // -----------------------------------------------------------------------------------------------

  /**
   * Obtain the latest block in the chain.
   *
   * @return the latest block in the chain.
   */
  Block getLastBlock();

  /**
   * Process a block requested from other peer node.
   *
   *
   */
  void processBlocks(List<Block> block) throws Exception;

  /**
   * Request the whole chain.
   */
  void requestChain() throws Exception;
}
