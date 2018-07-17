package org.blocknroll.blockchain.workshop;

import java.io.Serializable;

public interface NodeMessages {

  public static class Join implements Serializable {

    public String host;
    public int port;

    public Join(String host, int port) {
      System.out.println("[" + host + ":" + port + "]");
      this.host = host;
      this.port = port;
    }
  }

  public static class Leave implements Serializable {

    public Leave() {
    }
  }

  public static class AddFacts implements Serializable{
    public AddFacts (String filename)
    {}
  }
}
