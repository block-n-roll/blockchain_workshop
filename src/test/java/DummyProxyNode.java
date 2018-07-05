import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.util.Collection;
import java.util.List;
import org.blocknroll.blockchain.workshop.Block;
import org.blocknroll.blockchain.workshop.Chain;
import org.blocknroll.blockchain.workshop.Fact;
import org.blocknroll.blockchain.workshop.Node;

public class DummyProxyNode implements Node {

  private Node node;

  DummyProxyNode(Node node) {
    this.node = node;
  }

  public Chain getChain() {
    return node.getChain();
  }

  public void addFacts(Collection<Fact> facts) throws SodiumLibraryException {
    node.addFacts(facts);
  }

  public Collection<Node> getPeers() {
    return node.getPeers();
  }

  public void addPeer(Node node) {
    node.addPeer(node);
  }

  public void send(Node sender, Chain blocks) {
    node.send(sender, blocks);
  }

  public Block getLastBlock() {
    return node.getLastBlock();
  }

  public void processBlocks(Node sender, List<Block> blocks) throws SodiumLibraryException {
    node.processBlocks(sender, blocks);
  }

  public void requestChain(Node sender) throws SodiumLibraryException {
    sender.processBlocks(this, node.getChain().getBlocks());
  }
}
