package org.blocknroll.blockchain.workshop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class represents a blockchain.
 */
public class Chain {

  private String id;
  private List<Block> chain = new ArrayList<>();
  private Logger logger = LogManager.getLogger(Chain.class);

  /**
   * Constructor.
   */
  Chain(String id) {
    this.id = id;
  }

  /**
   * Constructor from given blocks.
   *
   * @param blocks the blocks forming the chain.
   */
  Chain(String name, List<Block> blocks) {
    // TODO add the identifier and blocks here
  }

  /**
   * Adds a block to the blockchain.
   *
   * @param block the block to be added to the chain.
   */
  void addBlock(Block block) throws IOException {
    logger.info("Adding block " + block.getIdentifier() + " to the chain.");
    // Generate the genesis block if it does not exist
    Paths.get("chain/" + id + "/").toFile().mkdirs();
    chain.add(block);
    Files.write(Paths.get("chain/" + id + "/" + block.getIdentifier() + ".block"),
        CryptoUtil.bufferToHexString(block.serialise()).getBytes());
  }

  /**
   * Returns the last block of the chain.
   *
   * @return the last block of the chain.
   */
  Block getLastBlock() {
    // TODO: Return the last block here
    return null;
  }

  /**
   * Returns the list of blocks that forms the chain.
   *
   * @return the list of blocks that forms the chain.
   */
  public List<Block> getBlocks() {
    // TODO: Return the list of blocks that compose this chain
    return null;
  }

  /**
   * Returns the number of blocks in the chain.
   *
   * @return the number of blocks in the chain.
   */
  public int getSize() {
    // TODO: Return the number of blocks of this chain
    return 0;
  }
  
}
