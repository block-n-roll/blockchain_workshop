package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.File;
import java.io.IOException;
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

  private Cluster cluster1;
  private Cluster cluster2;
  private NodeImp nodeOne;
  private Node proxyOne;
  private NodeImp nodeTwo;
  private Node proxyTwo;

  private Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<>();
    facts.add(new Fact(ByteBuffer.allocate(10), ByteBuffer.allocate(10)));
    return facts;
  }

  @Before
  public void setup() throws IOException, SodiumLibraryException {

    // Clean up chains
    if (Files.exists(Paths.get("chain"))) {
      Files.walk(Paths.get("chain"))
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }

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
  public void testDifferentChainsDifferentSizes() throws Exception {
    // Add two blocks to the node
    nodeOne.addFacts(createFacts());
    nodeOne.addFacts(createFacts());
    assertEquals(3, nodeOne.getChain().getSize());

    // add one fact to another node
    nodeTwo.addFacts(createFacts());
    assertEquals(2, nodeTwo.getChain().getSize());

    // Join the nodes into a cluster
    cluster1.addPeer(proxyTwo);
    cluster2.addPeer(proxyOne);

    // Add one more fact to the node
    nodeOne.addFacts(createFacts());
    assertEquals(nodeTwo.getChain().getBlocks().size(), 4);
    assertEquals(nodeOne.getChain().getBlocks().size(), 4);
    assertTrue(nodeOne.verifyChain(nodeTwo.getChain().getBlocks()));
    assertTrue(nodeTwo.verifyChain(nodeOne.getChain().getBlocks()));
  }

  @Test
  public void testDifferentChainsSameSize() throws Exception {
    // Add two blocks to the node
    nodeOne.addFacts(createFacts());
    assertEquals(2, nodeOne.getChain().getSize());

    // add one fact to another node
    nodeTwo.addFacts(createFacts());
    assertEquals(2, nodeTwo.getChain().getSize());

    // Join the nodes into a cluster
    cluster1.addPeer(proxyTwo);
    cluster2.addPeer(proxyOne);

    // Add one more fact to the node
    nodeOne.addFacts(createFacts());
    assertEquals(nodeTwo.getChain().getBlocks().size(), 3);
    assertEquals(nodeOne.getChain().getBlocks().size(), 3);
    assertTrue(nodeOne.verifyChain(nodeTwo.getChain().getBlocks()));
    assertTrue(nodeTwo.verifyChain(nodeOne.getChain().getBlocks()));
  }

}
