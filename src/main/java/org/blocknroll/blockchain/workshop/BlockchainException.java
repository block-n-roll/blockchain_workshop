package org.blocknroll.blockchain.workshop;

public class BlockchainException extends Exception {

  public BlockchainException(String message) {
    super(message);
  }

  public BlockchainException(String message, Throwable t) {
    super(message, t);

  }
}
