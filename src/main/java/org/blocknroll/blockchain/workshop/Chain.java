package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
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
   * Constructor from given blocks.
   * @param blocks the blocks forming the chain.
   */
  Chain(List<Block> blocks) {
    chain = blocks;
  }

  /**
   * Adds a block to the blockchain.
   *
   * @param block the block to be added to the chain.
   */
  void addBlock(Block block) {
      chain.add(block);
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
   * Returns the list of blocks that forms the chain.
   *
   * @return the list of blocks that forms the chain.
   */
  public List<Block> getBlocks() {
    return chain;
  }

  /**
   * Returns the number of blocks in the chain.
   * @return the number of blocks in the chain.
   */
  public int getSize() {
    return chain.size();
  }


}
