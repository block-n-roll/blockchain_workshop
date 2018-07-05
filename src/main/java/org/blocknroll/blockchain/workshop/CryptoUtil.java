package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.nio.ByteBuffer;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Useful class to perform some crypto computation.
 */
class CryptoUtil {

  private static Random random = new Random();
  private static Logger logger = LogManager.getLogger(NodeImp.class);
  static final int HASH_SIZE = 32;
  static final int SIGNATURE_SIZE = 64;

  static {
    SodiumLibrary.setLibraryPath("lib/libsodium.dll");
  }

  private static ByteBuffer sign(ByteBuffer data, ByteBuffer secretKey) throws SodiumLibraryException {
    data.rewind();
    secretKey.rewind();
    byte[] dat = new byte[data.limit()];
    data.get(dat);
    byte[] sec = new byte[secretKey.limit()];
    secretKey.get(sec);
    byte[] sig = SodiumLibrary.cryptoSign(dat, sec);
    ByteBuffer sign = ByteBuffer.allocateDirect(sig.length);
    sign.put(sig);
    return sign;
  }

  private static boolean verify(ByteBuffer data, ByteBuffer pubKey) {
    // TODO
    return true;
  }

  /**
   * Signs a document with the given secret key and returns the signature for that document.
   *
   * @param fact the document to be signed.
   * @param secKey the secret key to sign the document.
   * @return the signature for the given doc and secret key.
   */
  static ByteBuffer sign(Fact fact, ByteBuffer secKey) throws SodiumLibraryException {
    return CryptoUtil.sign(fact.getData(), secKey);
  }

  /**
   * Verifies the fact's content on a given key.
   *
   * @param fact the fact to be verified.
   * @param pubKey the key used to proof the fact.
   * @return true if the fact is authentic, false otherwise.
   */
  static boolean verify(Fact fact, ByteBuffer pubKey) {
    return verify(fact.getData(), pubKey);
  }

  /**
   * Signs a block with the given secret key and returns the signature for that block.
   *
   * @param block the block to be signed.
   * @param secKey the secret key to sign the block.
   * @return the signature for the given block and secret key.
   */
  static ByteBuffer sign(Block block, ByteBuffer secKey) throws SodiumLibraryException {
    return CryptoUtil.sign(block.serialise(), secKey);
  }

  /**
   * Verifies the block's content on a given key.
   *
   * @param block the block to be verified.
   * @param pubKey the key used to proof the block.
   * @return true if the block is authentic, false otherwise.
   */
  static boolean verify(Block block, ByteBuffer pubKey) {
    return verify(block.serialise(), pubKey);
  }

  /**
   * This is the function to
   */
  static ByteBuffer calculateHash(Block doc) throws SodiumLibraryException {
    // Clean the hash and signatures cannot be considered on hash calculations
    byte[] payload = new byte[doc.getSize() - HASH_SIZE - SIGNATURE_SIZE];
    doc.serialise().get(payload);
    byte[] h = SodiumLibrary.cryptoGenerichash(payload, 32);
    ByteBuffer hash = ByteBuffer.allocateDirect(h.length);
    hash.put(h);
    hash.rewind();
    bufferToHexString(hash);
    return hash;
  }

  /**
   * Returns a random long number.
   * @return a random long number.
   */
  static Long getRandomLong() {
    return random.nextLong();
  }

  /**
   * Generates a pair public and secret keys into the given buffers.
   *
   * @param pub the generated public key.
   * @param sec the generated secret key.
   */
  static void generatePublicSecretKeys(ByteBuffer pub, ByteBuffer sec) {
    // TODO:
  }

  /**
   * Converts a byte buffer into an hexadecimal string.
   * @param bb the byte buffer to be converted.
   * @return hexadecimal string representing the given buffer.
   */
  static String bufferToHexString(ByteBuffer bb) {
    bb.rewind();
    byte[] buff = new byte[bb.limit()];
    bb.get(buff);
    String hexStr = DatatypeConverter.printHexBinary(buff);
    bb.rewind();
    return hexStr;
  }
}
