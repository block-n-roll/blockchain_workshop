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

  private DummyCluster cluster1;
  private DummyCluster cluster2;
  private NodeImp node1;
  private NodeImp node2;

  private Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<Fact>();
    facts.add(new Fact(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
    return facts;
  }

  @Before
  public void setup() throws IOException, SodiumLibraryException {
    // Create nodes
    cluster1 = new DummyCluster("localhost1111");
    node1 = new NodeImp(cluster1);

    cluster2 = new DummyCluster("localhost2222");
    node2 = new NodeImp(cluster2);

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
