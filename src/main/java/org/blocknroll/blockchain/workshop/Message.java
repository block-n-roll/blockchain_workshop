package org.blocknroll.blockchain.workshop;

import java.io.Serializable;
import java.util.Collection;

public interface Message {

  public static class Join implements Serializable {

    public String host;
    public int port;

    public Join(String host, int port) {
      this.host = host;
      this.port = port;
    }
  }

  public static class Leave implements Serializable {

    public Leave() {
    }
  }

  public static class AddFacts implements Serializable {

    public final Collection<Fact> facts;

    public AddFacts(Collection<Fact> facts) {
      this.facts = facts;
    }
  }

  public static class RequestProofOfWork implements Serializable {

    public final Block block;

    public RequestProofOfWork(Block block) {
      this.block = block;
    }
  }

  public static class ProofOfWorkResponse implements Serializable {

    public final Block block;
    public final boolean result;

    public ProofOfWorkResponse(Block block, boolean result) {
      this.block = block;
      this.result = result;
    }
  }


}
