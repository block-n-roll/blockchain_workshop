package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Node {

  // -----------------------------------------------------------------------------------------------
  // Requested by application level
  // -----------------------------------------------------------------------------------------------

  void addFacts(Collection<Fact> facts) throws SodiumLibraryException;

  Collection<Node> getPeers();

  void addPeer(Node node);

  /**
   * Requests the whole chain.
   * @return the chain.
   */
  Chain getChain();

  // -----------------------------------------------------------------------------------------------
  // Requested by node
  // -----------------------------------------------------------------------------------------------

  /**
   * Sends a message to a peer node.
   *
   * @param sender the sender node.
   * @param blocks the blocks to be sent to the peer node.
   */
  void send(Node sender, Chain blocks);

  /**
   * Obtain the latest block in the chain.
   * @return the latest block in the chain.
   */
  Block getLastBlock();

  /**
   * Process a block requested from other peer node.
   * @param sender the sender node.
   * @param block
   */
  void processBlocks(Node sender, List<Block> block) throws SodiumLibraryException;

  /**
   * Request the whole chain.
   * @param sender the sender node.
   */
  void requestChain(Node sender) throws SodiumLibraryException;
}
