package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * This is the miner class in charge of mining pending facts.
 */
public class Miner implements Worker {
    private Chain chain;
    private ByteBuffer publicKey;
    private ByteBuffer secretKey;

    /**
     * Constructor.
     */
    public Miner(ByteBuffer pubKey, ByteBuffer secKey, Chain chain) {
        if ((pubKey == null) || (secKey == null) || (chain == null)) {
            throw new IllegalArgumentException("Cannot create a miner with null values");
        }
        if ((!pubKey.isDirect()) || (!secKey.isDirect())) {
            throw new IllegalArgumentException("Public and secret keys must be allocated in a direct memory buffer");
        }
        this.publicKey = pubKey;
        this.secretKey = secKey;
        this.chain = chain;
    }

    public Block work(Collection<Fact> facts) {
        // Create the block
        Block block = new Block(facts, chain.getLastBlock());

        // Mine the facts into a block and proof that it passes the validates
        ByteBuffer hash;
        Long nonce = CryptoUtil.getRandomLong();
        do {
            block.setNonce(nonce++);
            hash = CryptoUtil.calculateHash(block);
        } while(!validates(hash));
        block.setHash(hash);

        // Sign the block
        block.setSignature(CryptoUtil.sign(block, secretKey));
        return block;
    }

    public boolean validates(ByteBuffer hash) {
        // TODO: Verify the hash meets the work conditions
        return false;
    }
}
