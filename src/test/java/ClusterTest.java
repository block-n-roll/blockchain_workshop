import static org.junit.Assert.assertEquals;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import org.blocknroll.blockchain.workshop.Fact;
import org.blocknroll.blockchain.workshop.Node;
import org.blocknroll.blockchain.workshop.NodeImp;
import org.junit.Before;
import org.junit.Test;

public class ClusterTest {

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
  public void setup() {
    // Create nodes
    nodeOne = new NodeImp("localhost", 1111);
    proxyOne = new DummyProxyNode(nodeOne);
    nodeTwo = new NodeImp("localhost", 2222);
    proxyTwo = new DummyProxyNode(nodeTwo);
  }

  @Test
  public void addPeer() {
    assertEquals(nodeOne.getPeers().size() ,0);
    assertEquals(nodeOne.getPeers().size() ,0);
    nodeOne.addPeer(proxyTwo);
    assertEquals(nodeOne.getPeers().size() ,1);
    assertEquals(nodeTwo.getPeers().size() ,0);
    nodeTwo.addPeer(proxyOne);
    assertEquals(nodeOne.getPeers().size() ,1);
    assertEquals(nodeTwo.getPeers().size() ,1);
  }
}
