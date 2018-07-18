package org.blocknroll.blockchain.workshop;

import static org.blocknroll.blockchain.workshop.CryptoUtil.HASH_SIZE;
import static org.blocknroll.blockchain.workshop.CryptoUtil.SIGNATURE_SIZE;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import scala.concurrent.BlockContext;

public class BlockTest {

  @Test
  public void testGenesis() {
    Block genesis = new Block();
    Assert.assertTrue(Arrays.equals(genesis.getPreviousHash().array(), new byte[HASH_SIZE]));
    String hash = "73B796D604455465432B624786380AB4F1E6EE6E934482B43D3266C561767B0F";
    assertEquals(CryptoUtil.bufferToHexString(genesis.getHash()), hash);
    Block anotherGenesis = new Block();
    assertEquals(CryptoUtil.bufferToHexString(genesis.getHash()),
        CryptoUtil.bufferToHexString(anotherGenesis.getHash()));
  }

  @Test
  public void testSetId() {
    Block prev = new Block();
    assertEquals(prev.getIdentifier().longValue(), 0L);
    Block block = new Block(new ArrayList<>(), prev);
    assertEquals(block.getIdentifier().longValue(), 1L);
  }

  @Test
  public void testSetNonce() {
    Block b = new Block();
    Long nonce = 123456789L;
    b.setNonce(nonce);
    assertEquals(nonce, b.getNonce());
  }

  @Test
  public void testTimestamp() {
    Block b = new Block();
    Long timestamp = System.currentTimeMillis();
    b.setTimestamp(timestamp);
    assertEquals(timestamp, b.getTimestamp());
  }

  @Test
  public void testSignature() {
    Block b = new Block();
    b.setSignature(ByteBuffer.allocate(SIGNATURE_SIZE));
    assertEquals(ByteBuffer.allocate(SIGNATURE_SIZE), b.getSignature());
  }

  @Test
  public void testHash() {
    Block b = new Block();
    b.setHash(ByteBuffer.allocate(HASH_SIZE));
    assertEquals(ByteBuffer.allocate(HASH_SIZE), b.getHash());
  }

  @Test
  public void testPreviousHash() {
    Block b = new Block();
    assertEquals(ByteBuffer.allocate(HASH_SIZE), b.getPreviousHash());
  }

  @Test
  public void serialise() {
    Block b = new Block();
    Block another = new Block();
    another.deserialise(b.serialise());
    assertEquals(b, another);
  }
}
