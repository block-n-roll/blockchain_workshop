package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
import java.util.Collection;

public interface Connector {
  /**
   * Sends a message to a peer node.
   * @param blocks the blocks to be sent to the peer node.
   * @param peer the peer node to receive the blocks.
   */
  void send(Chain blocks, Node peer);

  /**
   * Sends a message to a peer node.
   * @param block the blocks to be sent to the peer node.
   * @param peer the peer node to receive the blocks.
   */
  void send(Block block, Node peer);
}
