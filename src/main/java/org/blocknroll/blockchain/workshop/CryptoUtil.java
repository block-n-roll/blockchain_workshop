package org.blocknroll.blockchain.workshop;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Useful class to perform some crypto computation.
 */
class CryptoUtil {

  static final int HASH_SIZE = 32;
  static final int SIGNATURE_SIZE = 64;
  private static final Random random = new Random();
  private static final Logger logger = LogManager.getLogger(CryptoUtil.class);

  static {
    String libraryPath;
    if (Platform.isMac()) {
      // MacOS
      libraryPath = "/usr/local/lib/libsodium.dylib";
      logger.info("Library path in Mac: " + libraryPath);
    } else if (Platform.isWindows()) {
      // Windows
      libraryPath = "lib/libsodium.dll";
      logger.info("Library path in Windows: " + libraryPath);
    } else {
      // Linux
      libraryPath = "lib/libsodium.so";
      logger.info("Library path: " + libraryPath);
    }

    logger.info("loading libsodium...");
    SodiumLibrary.setLibraryPath(libraryPath);
    String v = SodiumLibrary.libsodiumVersionString();
    logger.info("libsodium version: " + v);
  }

  /**
   * Signs a chunk of data with the private key.
   *
   * @param data the data to be signed.
   * @param secretKey the secret key.
   * @return the signature for the data.
   * @throws SodiumLibraryException throws if signature failed.
   */
  private static ByteBuffer sign(ByteBuffer data, ByteBuffer secretKey)
      throws SodiumLibraryException {
    logger.trace("Signing data: " + bufferToHexString(data));
    data.rewind();
    secretKey.rewind();
    byte[] dat = new byte[data.limit()];
    data.get(dat);
    byte[] sec = new byte[secretKey.limit()];
    secretKey.get(sec);
    byte[] sig = SodiumLibrary.cryptoSignDetached(dat, sec);
    ByteBuffer signature = ByteBuffer.allocate(sig.length);
    signature.put(sig);
    logger.trace("Created signature: " + bufferToHexString(signature));
    return signature;
  }

  /**
   * Verify a message and signature with a given public key
   *
   * @param signature the signature.
   * @param data the content to be verified.
   * @param pubKey the public key.
   * @return true if message and signature correspong with the given public key.
   * @throws SodiumLibraryException throw if verification failed.
   */
  private static boolean verify(ByteBuffer signature, ByteBuffer data, ByteBuffer pubKey)
      throws SodiumLibraryException {
    logger.trace("Verifying data: " + bufferToHexString(data));
    signature.rewind();
    byte[] sig = new byte[signature.limit()];
    data.rewind();
    byte[] dat = new byte[data.limit()];
    pubKey.rewind();
    byte[] pub = new byte[pubKey.limit()];
    pubKey.get(pub);
    return SodiumLibrary.cryptoSignVerifyDetached(sig, dat, pub);
  }

  /**
   * Signs a fact with the given secret key and returns the signature for that document.
   *
   * @param fact the fact to be signed.
   * @param secKey the secret key to sign the document.
   * @return the signature for the given doc and secret key.
   */
  static ByteBuffer sign(Fact fact, ByteBuffer secKey) throws SodiumLibraryException {
    logger.trace("Signing fact ...");
    return CryptoUtil.sign(fact.getData(), secKey);
  }

  /**
   * Verifies the fact's content on a given key.
   *
   * @param fact the fact to be verified.
   * @param pubKey the key used to proof the fact.
   * @return true if the fact is authentic, false otherwise.
   */
  static boolean verify(Fact fact, ByteBuffer pubKey) throws SodiumLibraryException {
    logger.trace("Verifying fact ...");
    return verify(fact.getSignature(), fact.getData(), pubKey);
  }

  /**
   * Signs a block with the given secret key and returns the signature for that block.
   *
   * @param block the block to be signed.
   * @param secKey the secret key to sign the block.
   * @return the signature for the given block and secret key.
   */
  static ByteBuffer sign(Block block, ByteBuffer secKey) throws SodiumLibraryException {
    logger.trace("Signing block ...");
    return CryptoUtil.sign(block.serialise(), secKey);
  }

  /**
   * Verifies the block's content on a given key.
   *
   * @param block the block to be verified.
   * @param pubKey the key used to proof the block.
   * @return true if the block is authentic, false otherwise.
   */
  static boolean verify(Block block, ByteBuffer pubKey) throws SodiumLibraryException {
    logger.trace("Verifying block ...");
    return verify(block.getSignature(), block.serialise(), pubKey);
  }

  /**
   * This is the function that calculate a hash for a block.
   */
  static ByteBuffer calculateHash(Block doc) throws SodiumLibraryException {
    // Clean the hash and signatures cannot be considered on hash calculations
    byte[] payload = new byte[doc.getSize() - HASH_SIZE];
    doc.serialise().get(payload);
    byte[] h = SodiumLibrary.cryptoGenerichash(payload, HASH_SIZE);
    ByteBuffer hash = ByteBuffer.allocate(h.length);
    hash.put(h);
    hash.rewind();
    return hash;
  }

  /**
   * Returns a random long number.
   *
   * @return a random long number.
   */
  static Long getRandomLong() {
    return random.nextLong();
  }

  /**
   * Loads a pair public and secret keys from the given path.
   */
  static ByteBuffer loadKey(String path) throws IOException {
    byte[] k = Files.readAllBytes(Paths.get(path));
    ByteBuffer key = ByteBuffer.allocate(k.length);
    key.put(k);
    key.rewind();
    return key;
  }

  /**
   * Generates a pair public and secret keys into the given buffers.
   */
  static void generatePublicSecretKeys(String pubPath, String secPath)
      throws SodiumLibraryException, IOException {
    SodiumKeyPair kp = SodiumLibrary.cryptoBoxKeyPair();
    byte[] pub = kp.getPublicKey();
    byte[] sec = kp.getPrivateKey();
    Files.write(Paths.get(pubPath), pub);
    Files.write(Paths.get(secPath), sec);
  }

  /**
   * Converts a byte buffer into an hexadecimal string.
   *
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

  /**
   * Converts from hex string into a byte buffer.
   *
   * @param hex the hex string to be converted.
   * @return the byte buffer containing the hexadecimal information.
   */
  static ByteBuffer hexStringToByteBuffer(String hex) {
    byte[] b = DatatypeConverter.parseHexBinary(hex);
    ByteBuffer bb = ByteBuffer.allocate(b.length);
    bb.put(b);
    bb.rewind();
    return bb;
  }

}
