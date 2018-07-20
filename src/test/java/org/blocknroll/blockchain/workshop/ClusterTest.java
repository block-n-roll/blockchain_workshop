package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ClusterTest {

  private LocalCluster cluster1;
  private LocalCluster cluster2;
  private Node node1;
  private Node node2;

  private Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<>();
    ByteBuffer data = ByteBuffer.allocate(10);
    data.putLong(System.currentTimeMillis());
    data.rewind();
    facts.add(new Fact(data, ByteBuffer.allocate(10)));
    return facts;
  }

  @Before
  public void setup() throws Exception {
    // Clean up chains
    if (Files.exists(Paths.get("chain"))) {
      Files.walk(Paths.get("chain"))
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }

    // Create nodes
    cluster1 = new LocalCluster("localhost1111");
    node1 = new Node(cluster1);
    cluster2 = new LocalCluster("localhost2222");
    node2 = new Node(cluster2);

    // Join the cluster
    cluster1.setSeed(cluster2);
    cluster1.setSeed(node1);
    cluster2.setSeed(cluster1);
    cluster2.setSeed(node2);
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

  @Test
  public void tenNodesMining() throws Exception {
    // Create a cluster
    List<LocalCluster> cluster = new ArrayList<>();
    List<Node> nodes = new ArrayList<>();
    for(int idx = 0; idx < 10; idx++) {
      LocalCluster c = new LocalCluster("Node " + idx);
      cluster.add(c);
      Node n = new Node(c);
      nodes.add(n);
    }

    // Connect the nodes
    for(int i = 0; i < cluster.size(); i++) {
      for(int j = 0; j < nodes.size(); j++) {
        if (i != j) {
          cluster.get(i).setSeed(cluster.get(j));
          cluster.get(i).setSeed(nodes.get(i));
          cluster.get(i).addPeer(cluster.get(j));
        }
      }
    }

    // Tell the cluster to mine facts
    cluster.get(1).addFacts(createFacts());
    for(Node node: nodes) {
      assertEquals(2, node.getChain().getSize());
    }

    cluster.get(8).addFacts(createFacts());
    for(Node node: nodes) {
      assertEquals(3, node.getChain().getSize());
    }
  }
}
