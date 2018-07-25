package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

  private DummyCluster cluster1;
  private DummyCluster cluster2;
  private Node node1;
  private Node node2;

  private Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<>();
    facts.add(new Fact(ByteBuffer.allocate(10), ByteBuffer.allocate(10)));
    return facts;
  }

  private Collection<Fact> createFactsFromFile(String path) throws IOException {
    Collection<Fact> facts = new ArrayList<>();
    facts.add(new Fact(ByteBuffer.wrap(Files.readAllBytes(Paths.get(path))), ByteBuffer.allocate(10)));
    return facts;
  }

  private Collection<Fact> createFactsFromFileSigned(String path, ByteBuffer secKey)
      throws IOException, SodiumLibraryException {
    Collection<Fact> facts = new ArrayList<>();

    ByteBuffer info = ByteBuffer.wrap(Files.readAllBytes(Paths.get(path)));
    ByteBuffer sig = CryptoUtil.sign(info, secKey);
    facts.add(new Fact(info, sig));
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
    cluster1 = new DummyCluster("localhost1111");
    node1 = new Node(cluster1);
    cluster2 = new DummyCluster("localhost2222");
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

  @Test
  public void hackingTheChainFailed() throws Exception {
    // Mine facts for node 1
    node1.mineFacts(createFacts());
    assertEquals(2, node1.getChain().getSize());

    // Mine facts for node 1
    node1.mineFacts(createFacts());
    assertEquals(3, node1.getChain().getSize());

    // Hack one block in node 1
    byte[] buffer = Files.readAllBytes(Paths.get("chain/localhost1111/2.block"));
    Files.write(Paths.get("chain/localhost1111/1.block"), buffer);

    // Node 1 shutdown and restart
    cluster1 = new LocalCluster("localhost1111");
    node1 = new Node(cluster1);
    cluster1.setSeed(cluster2);
    cluster1.setSeed(node1);
    cluster2.setSeed(cluster1);
    cluster2.setSeed(node2);
    assertEquals(1, node1.getChain().getSize());
  }

  @Test
  public void insertRealInfo() throws Exception {
    // Mine document
    node1.mineFacts(createFactsFromFile("C:\\Users\\Neueda\\Desktop\\bitcoin.pdf"));

    // Node 1 shutdown and restart
    cluster1 = new LocalCluster("localhost1111");
    node1 = new Node(cluster1);
    cluster1.setSeed(cluster2);
    cluster1.setSeed(node1);

    // Read to byte array
    ByteBuffer buffer = node1.getLastBlock().getFacts().iterator().next().getData();
    Files.write(Paths.get("C:\\Users\\Neueda\\Desktop\\bitcoin_2.pdf"), buffer.array());
  }

  @Test
  public void encryptDecryptInfoOk() throws Exception {
    // Mine document
    node1.mineFacts(createFactsFromFileSigned("C:\\Users\\Neueda\\Desktop\\bitcoin.pdf", CryptoUtil.loadKey("key\\sec.key")));

    // Node 1 shutdown and restart
    cluster1 = new LocalCluster("localhost1111");
    node1 = new Node(cluster1);
    cluster1.setSeed(cluster2);
    cluster1.setSeed(node1);

    // Read to byte array
    ByteBuffer info = node1.getLastBlock().getFacts().iterator().next().getData();
    ByteBuffer sig = node1.getLastBlock().getFacts().iterator().next().getSignature();
    System.out.println(">>>>>>>" + CryptoUtil.bufferToHexString(sig));
    if(CryptoUtil.verify(sig, info, CryptoUtil.loadKey("key\\sec.key"))) {
      Files.write(Paths.get("C:\\Users\\Neueda\\Desktop\\bitcoin_2.pdf"), info.array());
    } else {
      fail("La firma no es valida.");
    }
  }

  @Test
  public void encryptDecryptInfoFailed() throws Exception {
    // Mine document
    node1.mineFacts(createFactsFromFileSigned("C:\\Users\\Neueda\\Desktop\\bitcoin.pdf", CryptoUtil.loadKey("key\\sec.key")));

    // Node 1 shutdown and restart
    cluster1 = new LocalCluster("localhost1111");
    node1 = new Node(cluster1);
    cluster1.setSeed(cluster2);
    cluster1.setSeed(node1);

    // Read to byte array
    ByteBuffer info = node1.getLastBlock().getFacts().iterator().next().getData();
    ByteBuffer sig = node1.getLastBlock().getFacts().iterator().next().getSignature();

    // Hack the document
    info.putLong(0L);
    info.rewind();
    System.out.println(">>>>>>>" + CryptoUtil.bufferToHexString(sig));
    if(CryptoUtil.verify(sig, info, CryptoUtil.loadKey("key\\pub.key"))) {
      fail("La firma no deberia ser valida.");
    } else {
      Files.write(Paths.get("C:\\Users\\Neueda\\Desktop\\bitcoin_2.pdf"), info.array());
    }
  }
}
