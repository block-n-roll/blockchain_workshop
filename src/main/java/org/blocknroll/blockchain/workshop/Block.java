package org.blocknroll.blockchain.workshop;

import static org.blocknroll.blockchain.workshop.CryptoUtil.HASH_SIZE;
import static org.blocknroll.blockchain.workshop.CryptoUtil.SIGNATURE_SIZE;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents a possible block in the chain.
 */
public class Block {

  private static final int FACTS_SIZE_FIELD = 4;
  private static final int ID_SIZE_FIELD = 8;
  private static final int NONCE_SIZE_FIELD = 8;
  private static final int TIMESTAMP_SIZE_FIELD = 8;
  private static final Logger logger = LogManager.getLogger(Block.class);
  private Long identifier;
  private Collection<Fact> facts;
  private ByteBuffer previousHash;
  private Long nonce;
  private Long timestamp;
  private ByteBuffer hash;
  private ByteBuffer signature;

  /**
   * Constructor for genesis block.
   */
  Block() {
    // Init Genesis block
    logger.debug("Creating block genesis block");
    final String genesis = "000000000000000000000000000000000000000000000164AA2133AD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000AA2537DAE96F41B7D5CF696C6168EE524D48DF9F48A46A2C6934CFAC85D1D78CE03233F6D8E3D3A02D3F07D59BB55CA4BB185DB073B796D604455465432B624786380AB4F1E6EE6E934482B43D3266C561767B0F";
    this.facts = new ArrayList<>();
    this.previousHash = ByteBuffer.allocate(HASH_SIZE);
    this.hash = ByteBuffer.allocate(HASH_SIZE);
    this.signature = ByteBuffer.allocate(SIGNATURE_SIZE);
    deserialise(CryptoUtil.hexStringToByteBuffer(genesis));
  }

  /**
   * Constructor
   *
   * @param facts the facts to be consolidated in this block.
   * @param prev the previous block.
   */
  Block(Collection<Fact> facts, Block prev) {
    // Init block's members non related to mining process.
    logger.debug("Creating block ...");
    this.identifier = prev.identifier + 1;
    this.facts = facts;
    this.previousHash = ByteBuffer.allocate(HASH_SIZE);
    prev.hash.rewind();
    this.previousHash.put(prev.hash);
    this.hash = ByteBuffer.allocate(HASH_SIZE);
    this.signature = ByteBuffer.allocate(SIGNATURE_SIZE);
    logger.debug("Pointing to previous " + CryptoUtil.bufferToHexString(prev.hash));
  }

  /**
   * Returns the block identifier.
   *
   * @return the block identifier.
   */
  Long getIdentifier() {
    return identifier;
  }

  /**
   * Returns the nonce of the block.
   *
   * @return the nonce of the block.
   */
  Long getNonce() {
    return nonce;
  }

  /**
   * Sets the nonce for this block.
   *
   * @param nonce the nonce for this block.
   */
  void setNonce(Long nonce) {
    this.nonce = nonce;
  }

  /**
   * Returns the collection of facts into this block.
   */
  Collection<Fact> getFacts() {
    return facts;
  }

  /**
   * Returns the previous hash.
   *
   * @return the previous hash.
   */
  ByteBuffer getPreviousHash() {
    previousHash.rewind();
    return previousHash;
  }

  /**
   * Returns the previous hash.
   */
  void resetPreviousHash() {
    previousHash = ByteBuffer.allocateDirect(HASH_SIZE);
    previousHash.rewind();
  }

  /**
   * Returns the hash for this block.
   *
   * @return the hash for this block.
   */
  ByteBuffer getHash() {
    hash.rewind();
    return hash;
  }

  /**
   * Sets the hash for this block.
   *
   * @param hash the hash for this block.
   */
  void setHash(ByteBuffer hash) {
    this.hash = hash;
  }

  /**
   * Returns the signature for this block.
   *
   * @return the signature for this block.
   */
  ByteBuffer getSignature() {
    signature.rewind();
    return signature;
  }

  /**
   * Sets the signature for this block.
   *
   * @param signature the signature for this block.
   */
  void setSignature(ByteBuffer signature) {
    this.signature = signature;
  }

  /**
   * Returns the timestamp for this block.
   *
   * @return the timestamp for this block.
   */
  Long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp for this block.   * @param timestamp
   */
  void setTimestamp(Long timestamp) {
    // Check inputs
    if (timestamp == null) {
      throw new IllegalArgumentException("Timestamp cannot be null.");
    }
    this.timestamp = timestamp;
  }

  /**
   * Returns the size of the facts.
   */
  private int getFactsSize() {
    int size = 0;
    for (Fact f : facts) {
      size += f.getSize();
    }
    return size;
  }

  /**
   * Returns the size in bytes for this block.
   *
   * @return the size in bytes for this block.
   */
  int getSize() {
    return ID_SIZE_FIELD + FACTS_SIZE_FIELD + getFactsSize() + NONCE_SIZE_FIELD +
        TIMESTAMP_SIZE_FIELD + HASH_SIZE + SIGNATURE_SIZE + HASH_SIZE;
  }

  /**
   * Serialises this block into a ByteBuffer object.
   *
   * @return ByteBuffer containing this block.
   */
  ByteBuffer serialise() {
    ByteBuffer bb = ByteBuffer.allocate(getSize());
    bb.putLong(identifier);
    bb.putInt(facts.size());
    for (Fact f : facts) {
      bb.put(f.serialise());
    }
    bb.putLong(nonce);
    bb.putLong(timestamp);
    previousHash.rewind();
    bb.put(previousHash);
    signature.rewind();
    bb.put(signature);
    hash.rewind();
    bb.put(hash);
    bb.rewind();
    return bb;
  }

  /**
   * Serialises this block into a ByteBuffer object.
   *
   * @return ByteBuffer containing this block.
   */
  void deserialise(final ByteBuffer bb) {
    identifier = bb.getLong();
    IntStream.range(0, bb.getInt()).forEach(idx -> facts.add(new Fact(bb)));
    nonce = bb.getLong();
    timestamp = bb.getLong();
    previousHash.rewind();
    IntStream.range(0, HASH_SIZE).forEach(idx -> previousHash.put(bb.get()));
    signature.rewind();
    IntStream.range(0, SIGNATURE_SIZE).forEach(idx -> signature.put(bb.get()));
    hash.rewind();
    IntStream.range(0, HASH_SIZE).forEach(idx -> hash.put(bb.get()));
    bb.rewind();
  }

  /**
   * Returns true if both blocks are identical false otherwise.
   *
   * @param other the other block to compare with.
   * @return true if both blocks are identical false otherwise.
   */
  public boolean equals(Object other) {
    return (other != null) && serialise().equals(((Block) other).serialise());
  }

}
