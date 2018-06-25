import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import org.blocknroll.blockchain.workshop.Block;
import org.blocknroll.blockchain.workshop.Chain;
import org.blocknroll.blockchain.workshop.Connector;
import org.blocknroll.blockchain.workshop.Fact;
import org.blocknroll.blockchain.workshop.Node;
import org.junit.Before;
import org.junit.Test;

public class ConflictTest {

  Node oneNode;
  Node anotherNode;
  Connector peers;

  public Connector createConnector() {
    return new Connector() {
      public void send(Chain blocks, Node peer) {
        peer.verifyChain(blocks);
      }
      public void send(Block block, Node peer) {
        peer.addBlock(block);
      }
    };
  }

  public Collection<Fact> createFacts() {
    Collection<Fact> facts = new ArrayList<Fact>();
    facts.add(new Fact(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
    return facts;
  }

  @Before
  public void setup() {
    // Create nodes
    oneNode = new Node("localhost", 1111, createConnector());
    anotherNode = new Node("localhost", 2222, createConnector());

    // Build the peers
    peers = createConnector();
  }

  @Test
  public void testDifferentChainsSameSize() {
    // Add nodes to different clusters
    oneNode.addPeer(anotherNode);

    // Add node to the facts
    oneNode.addFacts(createFacts());
    oneNode.addFacts(createFacts());
    int size = oneNode.getChain().getBlocks().size();

    anotherNode.addFacts(createFacts());
    size = anotherNode.getChain().getBlocks().size();
    anotherNode.addPeer(oneNode);
    oneNode.addFacts(createFacts());
  }

  @Test
  public void chainAGreaterThanB() {
  }

}
