package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This is the miner class in charge of mining pending facts.
 */
class Miner {

  private ByteBuffer secretKey;
  private Logger logger = LogManager.getLogger(Miner.class);

  /**
   * Constructor.
   */
  Miner() throws Exception {
    // Generate the key pair if directory does not exist
    if (!Files.isDirectory(Paths.get("key"))) {
      Files.createDirectory(Paths.get("key"));
      CryptoUtil.generatePublicSecretKeys("key/pub.key", "key/sec.key");
    }

    // Load secret key to sign mined blocks
    secretKey = CryptoUtil.loadKey("key/sec.key");
  }

  /**
   * This method creates the block on the given facts.
   *
   * @param facts the facts to be mined inside the block.
   * @param difficulty the number of preceding zeros of the generated hash.
   * @return the mined block.
   */
  Block mine(Collection<Fact> facts, Block prev, int difficulty) throws SodiumLibraryException {
    return mine(new Block(facts, prev), difficulty);
  }

  /**
   * This method mine a block with the basic information.
   *
   * @param block the block to be mined.
   * @return the mined block.
   */
  Block mine(Block block, int difficulty) throws SodiumLibraryException {
    long timestamp = System.currentTimeMillis();
    logger.info("Mining facts with difficulty " + difficulty);
    int numComp = 0;

    // Mine the facts into a block and proof that it passes the validates
    ByteBuffer hash;
    Long nonce = CryptoUtil.getRandomLong();
    do {
      block.setNonce(nonce++);
      block.setTimestamp(System.currentTimeMillis());
      block.setSignature(CryptoUtil.sign(block, secretKey));
      hash = CryptoUtil.calculateHash(block);
      numComp++;
    } while (!validates(hash, difficulty));

    // Sets the hash
    block.setHash(hash);
    logger.debug("Computed hash: " + CryptoUtil.bufferToHexString(block.getHash()));
    logger.debug("Computation time (ms): " + (System.currentTimeMillis() - timestamp));
    logger.debug("Number of computations performed: " + numComp);
    return block;
  }

  /**
   * This is the method that checks the hash conditions required by the proof of work.
   *
   * @param hash the hash to be tested against the proof of work condition.
   * @return true if it is a valid hash, false otherwise.
   */
  boolean validates(ByteBuffer hash, int difficulty) {
    hash.rewind();
    boolean verified = true;
    while (verified && (difficulty > 0)) {
      difficulty--;
      verified = (hash.get(difficulty) == 0);
    }
    hash.rewind();

    return verified;
  }
}
