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
import org.junit.Before;
import org.junit.Test;

public class ConflictTest {

  private LocalCluster cluster1;
  private LocalCluster cluster2;
  private Node node1;
  private Node node2;

  private Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<>();
    facts.add(new Fact(ByteBuffer.allocate(10), ByteBuffer.allocate(10)));
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
  public void testDifferentChainsDifferentSizes() throws Exception {
    // Add two blocks to the node
    node1.mineFacts(createFacts());
    node1.mineFacts(createFacts());
    assertEquals(3, node1.getChain().getSize());

    // add one fact to another node
    node2.mineFacts(createFacts());
    assertEquals(2, node2.getChain().getSize());

    // Join the cluster
    cluster1.addPeer(cluster2);
    cluster2.addPeer(cluster1);

    // Add one more fact to the node
    node1.mineFacts(createFacts());
    assertEquals(node2.getChain().getBlocks().size(), 4);
    assertEquals(node1.getChain().getBlocks().size(), 4);
    assertTrue(node1.verifyChain(node2.getChain().getBlocks()));
    assertTrue(node2.verifyChain(node1.getChain().getBlocks()));
  }

  @Test
  public void testDifferentChainsSameSize() throws Exception {
    // Add two blocks to the node
    node1.mineFacts(createFacts());
    assertEquals(2, node1.getChain().getSize());

    // add one fact to another node
    node2.mineFacts(createFacts());
    assertEquals(2, node2.getChain().getSize());

    // Join the cluster
    cluster1.addPeer(cluster2);
    cluster2.addPeer(cluster1);

    // Add one more fact to the node
    node1.mineFacts(createFacts());
    assertEquals(3, node2.getChain().getBlocks().size());
    assertEquals(3, node1.getChain().getBlocks().size());
    assertTrue(node1.verifyChain(node2.getChain().getBlocks()));
    assertTrue(node2.verifyChain(node1.getChain().getBlocks()));
  }

}
