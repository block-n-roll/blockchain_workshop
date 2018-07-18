package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DummyCluster implements Cluster {

  private Node seed;
  private String name;
  private List<Node> peers;

  public DummyCluster(String name) {
    this.name = name;
    this.peers = new ArrayList<>();
  }

  public void setSeed(Node seed) {
    this.seed = seed;
  }

  @Override
  public void notify(Block block) throws Exception {
    for (Node peer : peers) {
      peer.processBlocks(seed, Collections.singletonList(block));
    }
  }

  @Override
  public String getId() {
    return name;
  }

  @Override
  public void addPeer(Node n) {
    peers.add(n);
  }

  @Override
  public List<Node> getPeers() {
    return peers;
  }
}
