package org.blocknroll.blockchain.workshop;

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
    this.previousHash = ByteBuffer.allocateDirect(0);
    this.nonce = 0L;
    this.hash = ByteBuffer.allocateDirect(0);
    this.signature = ByteBuffer.allocateDirect(0);
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
    this.previousHash = prev.previousHash;
  }

  /**
   * Returns the block identifier.
   * @return the block identifier.
   */
  Long getIdentifier() {
    return identifier;
  }

  /**
   * Returns the nonce of the block.
   * @return the nonce of the block.
   */
  Long getNonce() {
    return nonce;
  }

  /**
   * Sets the nonce for this block.
   * @param nonce the nonce for this block.
   * @return this block.
   */
  void setNonce(Long nonce) {
    this.nonce = nonce;
  }

  /**
   * Returns the collection of facts into this block.
   * @return
   */
  Collection<Fact> getFacts() {
    return facts;
  }

  /**
   * Returns the previous hash.
   * @return the previous hash.
   */
  ByteBuffer getPreviousHash() {
    return previousHash;
  }

  /**
   * Returns the hash for this block.
   * @return the hash for this block.
   */
  ByteBuffer getHash() {
    return hash;
  }

  /**
   * Sets the hash for this block.
   * @param hash the hash for this block.
   */
  void setHash(ByteBuffer hash) {
    this.hash = hash;
  }

  /**
   * Returns the signature for this block.
   * @return the signature for this block.
   */
  ByteBuffer getSignature() {
    return signature;
  }

  /**
   * Sets the signature for this block.
   * @param signature the signature for this block.
   */
  void setSignature(ByteBuffer signature) {
    this.signature = signature;
  }

  /**
   * Returns the timestamp for this block.
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
   * @return
   */
  int getFactsSize() {
    int size = 0;
    for(Fact f: facts) {
      size += f.getSize();
    }
    return FACTS_SIZE_FIELD + size;
  }

  /**
   * Returns the size in bytes for this block.
   * @return the size in bytes for this block.
   */
  int getSize() {
    previousHash.rewind();
    hash.rewind();
    signature.rewind();
    return ID_SIZE_FIELD + getFactsSize() + previousHash.limit() + NONCE_SIZE_FIELD +
        hash.limit() + signature.limit() + TIMESTAMP_SIZE_FIELD;
  }

  /**
   * Serialises this block into a ByteBuffer object.
   * @return ByteBuffer containing this block.
   */
  ByteBuffer serialise() {
    ByteBuffer bb = ByteBuffer.allocateDirect(getFactsSize());
    bb.putLong(identifier);
    bb.putInt(facts.size());
    for(Fact f: facts) {
      bb.put(f.serialise());
    }
    bb.put(previousHash);
    bb.putLong(nonce);
    bb.put(hash);
    bb.put(signature);
    bb.putLong(timestamp);
    return bb;
  }

  /**
   * Returns true if both blocks are identical false otherwise.
   * @param other the other block to compare with.
   * @return true if both blocks are identical false otherwise.
   */
  public boolean equals(Object other) {
    if(other == null) {
      return false;
    }
    return serialise().equals(((Block)other).serialise());

  }
}
