package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DummyProxyNode implements Node {

  private Node node;

  DummyProxyNode(Node node) {
    this.node = node;
  }

  public Chain getChain() {
    return node.getChain();
  }

  public void addFacts(Collection<Fact> facts) throws Exception {
    node.addFacts(facts);
  }

  public Collection<Node> getPeers() {
    return null; //node.getPeers();
  }

  public void addPeer(Node node) {
    //node.addPeer(node);
  }

  public Block getLastBlock() {
    return node.getLastBlock();
  }

  public void processBlocks(Node sender, List<Block> blocks)
      throws Exception {
    node.processBlocks(sender, blocks);
  }

  public void requestChain(Node sender) throws Exception {
    sender.processBlocks(this, node.getChain().getBlocks());
  }
}
