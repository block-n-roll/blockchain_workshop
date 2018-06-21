package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a blockchain.
 */
class Chain {

    private List<Block> chain = new ArrayList<Block>();

    /**
     * Constructor.
     *
     * @param genesis the genesis block.
     * @throws Exception in case that something when wrong here building the chain then raise an exception.
     */
    Chain(Block genesis) throws IllegalArgumentException {
        if (!addBlock(genesis)) {
            throw new IllegalArgumentException("Wrong genesis block!");
        }
    }

    /**
     * Adds a block to the blockchain.
     *
     * @param block the block to be added to the chain.
     */
    boolean addBlock(Block block) {
        boolean passed = proof(block);
        if (passed) {
            chain.add(block);
        }
        return passed;
    }

    /**
     * Returns the last block of the chain.
     *
     * @return the last block of the chain.
     */
    Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    /**
     * Verifies that the given block has a valid hash and is the next one in the chain
     *
     * @param block the block to be validated.
     * @return true if block is valid, false otherwise.
     */
    boolean proof(Block block) {
        // TODO: Verify block is the next one in the chain.
        // TODO: Proof that block is good.
        return false;
    }
}
