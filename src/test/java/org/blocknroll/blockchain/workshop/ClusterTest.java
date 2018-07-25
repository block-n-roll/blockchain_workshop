package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

public class ClusterTest {

  private LocalCluster cluster1;
  private LocalCluster cluster2;
  private Node node1;
  private Node node2;

  private Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<Fact>();
    facts.add(new Fact(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
    return facts;
  }

  @Before
  public void setup() throws Exception {
    // Create nodes
    cluster1 = new LocalCluster("localhost1111");
    node1 = new Node(cluster1);

    cluster2 = new LocalCluster("localhost2222");
    node2 = new Node(cluster2);

  }

  @Test
  public void addPeer() {
    assertEquals(cluster1.getPeers().size(), 0);
    assertEquals(cluster2.getPeers().size(), 0);
    cluster1.addPeer(cluster2);
    assertEquals(cluster1.getPeers().size(), 1);
    assertEquals(cluster2.getPeers().size(), 0);
    cluster2.addPeer(cluster1);
    assertEquals(cluster1.getPeers().size(), 1);
    assertEquals(cluster2.getPeers().size(), 1);
  }
}
