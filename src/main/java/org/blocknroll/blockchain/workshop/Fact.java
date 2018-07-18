package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

/**
 * This represents a fact to be mined into a block.
 */
public class Fact {

  private static final int DATA_SIZE_FIELD = 4;
  private static final int SIG_SIZE_FIELD = 4;
  private ByteBuffer data;
  private ByteBuffer signature;

  /**
   * Constructor
   *
   * @param dat the data held by this fact.
   * @param sig the signature used to protect this fact.
   */
  public Fact(ByteBuffer dat, ByteBuffer sig) {
    if ((dat == null) || (dat.limit() == 0)) {
      throw new IllegalArgumentException("Data cannot be null or empty");
    }
    if ((sig == null) || (sig.limit() == 0)) {
      throw new IllegalArgumentException("Signature cannot be null");
    }

    // TODO: Store the data and signature
  }

  /**
   * Class constructor
   */
  Fact(ByteBuffer fact) {
    deserialise(fact);
  }

  /**
   * Returns the data associated to this fact.
   *
   * @return the data associated to this fact.
   */
  ByteBuffer getData() {
    return data;
  }

  /**
   * Returns the signature associated to this fact that guaranties that data has not been modified.
   *
   * @return the signature associated to this fact.
   */
  ByteBuffer getSignature() {
    return signature;
  }

  /**
   * Returns the size of this fact.
   *
   * @return the size of this fact.
   */
  int getSize() {
    // Get the size of the data and signature and join the two buffers
    data.rewind();
    signature.rewind();
    return DATA_SIZE_FIELD + SIG_SIZE_FIELD + data.limit() + signature.limit();
  }

  /**
   * Returns the Fact serialised into an ByteBuffer object.
   *
   * @return the Fact serialised into an ByteBuffer object.
   */
  ByteBuffer serialise() {
    // Get the size of the data and signature and join the two buffers
    data.rewind();
    signature.rewind();
    ByteBuffer bb = ByteBuffer.allocate(getSize());

    // Write size of data and signature
    bb.putInt(data.limit());
    bb.putInt(signature.limit());

    // TODO: Write the data and signature respectively

    bb.rewind();
    return bb;
  }

  /**
   * Deserialize the content of the buffer into this object.
   */
  void deserialise(ByteBuffer bb) {
    // Get the size of the data and signature
    int dataSize = bb.getInt();
    int sigSize = bb.getInt();

    // Read the data
    data = ByteBuffer.allocate(dataSize);
    IntStream.range(0, dataSize).forEach(idx -> data.put(bb.get()));
    data.rewind();

    // Read the signature
    signature = ByteBuffer.allocate(sigSize);
    // TODO: Read the signature
    signature.rewind();
  }

  /**
   * Compares with another fact.
   *
   * @param other another fact.
   * @return true if both facts are equal, false otherwise.
   */
  public boolean equals(Object other) {
    return (other != null) && (serialise().equals(((Fact) other).serialise()));
  }
}
