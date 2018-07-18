package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.security.IdentityScope;
import java.util.List;

public interface Cluster {

  void setSeed(Node seed);

  void notify(Block block) throws Exception;

  String getId(); // IP + PORT

  void addPeer(Node n);

  List<Node> getPeers();
}
