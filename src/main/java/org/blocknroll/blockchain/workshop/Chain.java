package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents a blockchain.
 */
public class Chain {

  private List<Block> chain = new ArrayList<Block>();
  private Logger logger = LogManager.getLogger(Chain.class);

  /**
   * Constructor.
   */
  Chain() {
    // TODO: replace by hardcoded genesis block
    chain.add(new Block());
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
    Block lastBlock = getLastBlock();
    if(block.getIdentifier() > lastBlock.getIdentifier()) {
      if(block.getIdentifier() == (lastBlock.getIdentifier() + 1)) {
        if (block.getPreviousHash().equals(lastBlock.getHash())) {
          logger.debug("This is a good block!");
          return true;
        } else {
          logger.error("Wrong hash for ");
        }
      } else {
        logger.error("Received block is some steps above ... request the chain to the peers.");
      }
    } else {
      logger.warn("The received block is in the past ... just ignore it.");
    }
    // TODO: Proof that block is good.
    return false;
  }

  /**
   * Returns the list of blocks that forms the chain.
   * @return the list of blocks that forms the chain.
   */
  public List<Block> getBlocks() {
    return chain;
  }
}
