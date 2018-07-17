package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * This is the interface to be implemented by a node.
 */
public interface Node {

  // -----------------------------------------------------------------------------------------------
  // Requested by application level
  // -----------------------------------------------------------------------------------------------

  /**
   * Add facts into the blockchain.
   *
   * @param facts the collection of facts to be mined into a blockchain.
   */
  void addFacts(Collection<Fact> facts) throws SodiumLibraryException, IOException;

  /**
   * Returns the peers linked to this node.
   *
   * @return the peers linked to this node.
   */
  Collection<Node> getPeers();

  /**
   * Adds a peer to this node.
   *
   * @param node the node to be linked to this one.
   */
  void addPeer(Node node);

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
   * @param sender the sender node.
   */
  void processBlocks(Node sender, List<Block> block) throws SodiumLibraryException, IOException;

  /**
   * Request the whole chain.
   *
   * @param sender the sender node.
   */
  void requestChain(Node sender) throws SodiumLibraryException, IOException;
}
