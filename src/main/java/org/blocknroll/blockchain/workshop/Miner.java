package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * This is the miner class in charge of mining pending facts.
 */
class Miner {

  private Chain chain;
  private ByteBuffer publicKey;
  private ByteBuffer secretKey;

  /**
   * Constructor.
   */
  Miner(Chain chain) {
    // Load secret and public key stuff for the miner
    this.publicKey = ByteBuffer.allocate(32);
    this.secretKey = ByteBuffer.allocate(64);
    CryptoUtil.generatePublicSecretKeys(publicKey, secretKey);
    this.chain = chain;
  }

  /**
   * This method creates the block on the given facts.
   *
   * @param facts the facts to be mined inside the block.
   * @return the mined block.
   */
  Block mine(Collection<Fact> facts) {
    // Create the block
    Block block = new Block(facts, chain.getLastBlock());

    // Mine the facts into a block and proof that it passes the validates
    ByteBuffer hash;
    Long nonce = CryptoUtil.getRandomLong();
    do {
      block.setNonce(nonce++);
      block.setTimestamp(System.currentTimeMillis());
      hash = CryptoUtil.calculateHash(block);
    } while (!validates(hash));
    block.setHash(hash);

    // Sign the block
    block.setSignature(CryptoUtil.sign(block, secretKey));
    return block;
  }

  /**
   * This is the method that checks the hash conditions required by the proof of work.
   *
   * @param hash the hash to be tested against the proof of work condition.
   * @return true if it is a valid hash, false otherwise.
   */
  boolean validates(ByteBuffer hash) {
    // TODO: Verify the hash meets the mine conditions
    return true;
  }
}
