package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

public class ClusterTest {

  private Cluster cluster1;
  private Cluster cluster2;
  private NodeImp nodeOne;
  private Node proxyOne;
  private NodeImp nodeTwo;
  private Node proxyTwo;

  private Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<Fact>();
    facts.add(new Fact(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
    return facts;
  }

  @Before
  public void setup() throws IOException, SodiumLibraryException {
    // Create nodes
    cluster1 = new DummyCluster("localhost1111");
    nodeOne = new NodeImp(cluster1);
    cluster1.setSeed(nodeOne);
    proxyOne = new DummyProxyNode(nodeOne);
    cluster2 = new DummyCluster("localhost2222");
    nodeTwo = new NodeImp(cluster2);
    cluster2.setSeed(nodeTwo);
    proxyTwo = new DummyProxyNode(nodeTwo);
  }

  @Test
  public void addPeer() {
    assertEquals(cluster1.getPeers().size(), 0);
    assertEquals(cluster2.getPeers().size(), 0);
    cluster1.addPeer(proxyTwo);
    assertEquals(cluster1.getPeers().size(), 1);
    assertEquals(cluster2.getPeers().size(), 0);
    cluster2.addPeer(proxyOne);
    assertEquals(cluster1.getPeers().size(), 1);
    assertEquals(cluster2.getPeers().size(), 1);
  }
}
