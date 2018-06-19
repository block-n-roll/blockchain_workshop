package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * This class represents a possible block in the chain.
 */
public class Block {
    private String identifier;
    private Collection<Fact> facts;
    private ByteBuffer previousHash;
    private Long nonce;
    private ByteBuffer hash;
    private ByteBuffer signature;
    private Long timestamp;

    /**
     * Constructor/
     * @param facts the facts to be consolidated in this block.
     * @param prev the previous block.
     */
    public Block(Collection<Fact> facts, Block prev) {
        this.identifier = prev.identifier;
        this.facts = facts;
        this.previousHash = prev.previousHash;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Long getNonce() {
        return nonce;
    }

    public Collection<Fact> getFacts() {
        return facts;
    }

    public ByteBuffer getPreviousHash() {
        return previousHash;
    }

    public ByteBuffer getHash() {
        return hash;
    }

    public ByteBuffer getSignature() {
        return signature;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Block setNonce(Long nonce) {
        this.nonce = nonce;
        return this;
    }

    public void setHash(ByteBuffer hash) {
        this.hash = hash;
    }

    public void setSignature(ByteBuffer signature) {
        this.signature = signature;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
