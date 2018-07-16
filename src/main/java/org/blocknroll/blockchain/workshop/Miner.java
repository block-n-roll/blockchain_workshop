package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This is the miner class in charge of mining pending facts.
 */
class Miner {

  private Chain chain;
  //  private ByteBuffer publicKey;
  private ByteBuffer secretKey;
  private Logger logger = LogManager.getLogger(Miner.class);

  /**
   * Constructor.
   */
  Miner(Chain chain) throws IOException {
    // Load secret and public key stuff for the miner
//    publicKey = CryptoUtil.loadKey("keys/pub.key");
    secretKey = CryptoUtil.loadKey("keys/sec.key");
    this.chain = chain;
  }

  /**
   * This method creates the block on the given facts.
   *
   * @param facts the facts to be mined inside the block.
   * @return the mined block.
   */
  Block mine(Collection<Fact> facts, int difficulty) throws SodiumLibraryException {
    logger.info("Mining facts with difficulty " + difficulty);

    int numComp = 0;

    // Create the block
    Block block = new Block(facts, chain.getLastBlock());

    // Mine the facts into a block and proof that it passes the validates
    ByteBuffer hash;
    Long nonce = CryptoUtil.getRandomLong();
    do {
      block.setNonce(nonce++);
      block.setTimestamp(System.currentTimeMillis());
      hash = CryptoUtil.calculateHash(block);
      numComp++;
    } while (!validates(hash, difficulty));

    // Sets the hash and signature to the block
    block.setHash(hash);
    block.setSignature(CryptoUtil.sign(block, secretKey));
    logger.debug("Computed hash: " + CryptoUtil.bufferToHexString(block.getHash()));
    logger.debug("Number of computations performed " + numComp);
    return block;
  }

  /**
   * This is the method that checks the hash conditions required by the proof of work.
   *
   * @param hash the hash to be tested against the proof of work condition.
   * @return true if it is a valid hash, false otherwise.
   */
  private boolean validates(ByteBuffer hash, int difficulty) {
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
