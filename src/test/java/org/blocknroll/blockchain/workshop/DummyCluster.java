package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DummyCluster implements Cluster {

  private Cluster seed;
  private String name;
  private List<Cluster> peers;

  public DummyCluster(String name) {
    this.name = name;
    this.peers = new ArrayList<>();
  }

  public void setSeed(Cluster seed) {
    this.seed = seed;
  }

  @Override
  public void requestProofOfWork(Block block) throws Exception {
    for (Cluster peer : peers) {
      peer.processBlocks(Collections.singletonList(block));
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
    seed.addFacts(facts);
  }

  @Override
  public Chain getChain() {
    return seed.getChain();
  }

  @Override
  public Block getLastBlock() {
    return seed.getLastBlock();
  }

  @Override
  public void processBlocks(List<Block> block) throws Exception {
    seed.processBlocks(block);
  }

  @Override
  public void requestChain() throws Exception {
    seed.requestChain();
  }
}
