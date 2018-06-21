package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Useful class to perform some crypto computation.
 */
class CryptoUtil {

  private static Random random = new Random();

  /**
   * Signs a document with the given secret key and returns the signature for that document.
   *
   * @param fact the document to be signed.
   * @param secKey the secret key to sign the document.
   * @return the signature for the given doc and secret key.
   */
  static ByteBuffer sign(Fact fact, ByteBuffer secKey) {
    // TODO
    return null;
  }

  /**
   * Verifies the fact's content on a given key.
   *
   * @param fact the fact to be verified.
   * @param pubKey the key used to proof the fact.
   * @return true if the fact is authentic, false otherwise.
   */
  static boolean verify(Fact fact, ByteBuffer pubKey) {
    // TODO:
    return false;
  }

  /**
   * Signs a block with the given secret key and returns the signature for that block.
   *
   * @param block the block to be signed.
   * @param secKey the secret key to sign the block.
   * @return the signature for the given block and secret key.
   */
  static ByteBuffer sign(Block block, ByteBuffer secKey) {
    // TODO: Sign the whole block info to proof that no byte is changed in the future.
    return null;
  }

  /**
   * Verifies the block's content on a given key.
   *
   * @param block the block to be verified.
   * @param pubKey the key used to proof the block.
   * @return true if the block is authentic, false otherwise.
   */
  static boolean verify(Block block, ByteBuffer pubKey) {
    // TODO:
    return false;
  }

  /**
   * This is the function to
   */
  static ByteBuffer calculateHash(Block doc) {
    // TODO:
    return null;
  }

  static Long getRandomLong() {
    return random.nextLong();
  }
}
