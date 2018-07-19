package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DummyCluster implements Cluster {

  private Cluster seed;
  private String name;
  private List<Cluster> peers;
  private Node node;

  public DummyCluster(String name) {
    this.node = node;
    this.name = name;
    this.peers = new ArrayList<>();
  }

  public void setSeed(Node node) {
    this.node = node;
  }

  public void setSeed(Cluster seed) {
    this.seed = seed;
  }

  @Override
  public void requestProofOfWork(Block block) throws Exception {
    node.processBlock(Collections.singletonList(block));
    for (Cluster peer : peers) {
      ((DummyCluster) peer).node.processBlock(Collections.singletonList(block));
    }
  }

  @Override
  public String getId() {
    return name;
  }

  @Override
  public void addPeer(Cluster n) {
    peers.add(n);
  }

  @Override
  public List<Cluster> getPeers() {
    return peers;
  }

  @Override
  public void addFacts(Collection<Fact> facts) throws Exception {
    node.mineFacts(facts);
  }

  @Override
  public Chain getChain() {
    return node.getChain();
  }

  @Override
  public Block getLastBlock() {
    return node.getLastBlock();
  }

  @Override
  public void processBlocks(List<Block> block) throws Exception {
    node.processBlock(block);
  }

  @Override
  public void requestChain() throws Exception {
    Chain chain;
    chain = ((DummyCluster) peers.get(0)).node.requestChain();
    node.processBlock(chain.getBlocks());
  }
}
