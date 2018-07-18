package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;

public interface Cluster {

  void setSeed(Node seed);

  void notify(Block block) throws Exception;

  String getId(); // IP + PORT

  void addPeer(Node n);
}
