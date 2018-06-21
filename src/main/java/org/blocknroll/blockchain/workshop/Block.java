package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a possible block in the chain.
 */
class Block {
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
    Block() {
        // Init Genesis block
        this.identifier = 0L;
        this.facts = new ArrayList<Fact>();
        this.previousHash = ByteBuffer.allocate(32);
    }

    /**
     * Constructor
     * @param facts the facts to be consolidated in this block.
     * @param prev the previous block.
     */
    Block(Collection<Fact> facts, Block prev) {
        // Init block's members non related to mining process.
        this.identifier = prev.identifier + 1;
        this.facts = facts;
        this.previousHash = prev.previousHash;
    }

    Long getIdentifier() {
        return identifier;
    }

    Long getNonce() {
        return nonce;
    }

    Collection<Fact> getFacts() {
        return facts;
    }

    ByteBuffer getPreviousHash() {
        return previousHash;
    }

    ByteBuffer getHash() {
        return hash;
    }

    ByteBuffer getSignature() {
        return signature;
    }

    Long getTimestamp() {
        return timestamp;
    }

    Block setNonce(Long nonce) {
        this.nonce = nonce;
        return this;
    }

    void setHash(ByteBuffer hash) {
        this.hash = hash;
    }

    void setSignature(ByteBuffer signature) {
        this.signature = signature;
    }

    void setTimestamp(Long timestamp) {
        // Check inputs
        if(timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null.");
        }
        this.timestamp = timestamp;
    }

}
