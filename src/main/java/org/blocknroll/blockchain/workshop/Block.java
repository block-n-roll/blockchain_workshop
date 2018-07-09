package org.blocknroll.blockchain.workshop;

import static org.blocknroll.blockchain.workshop.CryptoUtil.HASH_SIZE;
import static org.blocknroll.blockchain.workshop.CryptoUtil.SIGNATURE_SIZE;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a possible block in the chain.
 */
public class Block {

  public static final int FACTS_SIZE_FIELD = 4;
  private static final int ID_SIZE_FIELD = 4;
  private static final int NONCE_SIZE_FIELD = 4;
  private static final int TIMESTAMP_SIZE_FIELD = 4;

  private Long identifier;
  private Collection<Fact> facts;
  private ByteBuffer previousHash;
  private Long nonce;
  private ByteBuffer hash;
  private ByteBuffer signature;
  private Long timestamp;

  /**
   * Constructor for genesis block.
   */
  // Todo: Remove when genesis is hardcoded
  Block() {
    // Init Genesis block
    this.identifier = 0L;
    this.facts = new ArrayList<Fact>();
    this.previousHash = ByteBuffer.allocateDirect(HASH_SIZE);
    this.nonce = 0L;
    this.hash = ByteBuffer.allocateDirect(HASH_SIZE);
    this.signature = ByteBuffer.allocateDirect(SIGNATURE_SIZE);
  }

  /**
   * Constructor
   *
   * @param facts the facts to be consolidated in this block.
   * @param prev the previous block.
   */
  Block(Collection<Fact> facts, Block prev) {
    // Init block's members non related to mining process.
    this.identifier = prev.identifier + 1;
    this.facts = facts;
    this.previousHash = ByteBuffer.allocateDirect(HASH_SIZE);
    this.previousHash.put(prev.hash);
    this.hash = ByteBuffer.allocateDirect(HASH_SIZE);
    this.signature = ByteBuffer.allocateDirect(SIGNATURE_SIZE);
    CryptoUtil.bufferToHexString(previousHash);
    CryptoUtil.bufferToHexString(prev.hash);
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
   * @return this block.
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
    return previousHash;
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
  int getFactsSize() {
    int size = 0;
    for (Fact f : facts) {
      size += f.getSize();
    }
    return FACTS_SIZE_FIELD + size;
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
    ByteBuffer bb = ByteBuffer.allocateDirect(getSize());
    bb.putLong(identifier);
    bb.putInt(facts.size());
    for (Fact f : facts) {
      bb.put(f.serialise());
    }
    bb.putLong(nonce);
    bb.putLong(timestamp);
    bb.put(previousHash);
    bb.put(hash);
    bb.put(signature);
    bb.rewind();
    return bb;
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
