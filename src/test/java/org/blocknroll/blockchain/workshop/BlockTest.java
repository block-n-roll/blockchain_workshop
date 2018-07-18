package org.blocknroll.blockchain.workshop;

import static org.blocknroll.blockchain.workshop.CryptoUtil.HASH_SIZE;
import static org.blocknroll.blockchain.workshop.CryptoUtil.SIGNATURE_SIZE;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class BlockTest {

  @Test
  public void testGenesis() {
    Block prev = new Block();
    Assert.assertTrue(Arrays.equals(prev.getPreviousHash().array(), new byte[HASH_SIZE]));
    String hash = "73B796D604455465432B624786380AB4F1E6EE6E934482B43D3266C561767B0F";
    Assert.assertEquals(CryptoUtil.bufferToHexString(prev.getHash()), hash);
  }

  @Test
  public void testSetId() {
    Block prev = new Block();
    Assert.assertEquals(prev.getIdentifier().longValue(), 0L);
    Block block = new Block(new ArrayList<>(), prev, 2);
    Assert.assertEquals(block.getIdentifier().longValue(), 1L);
  }

  @Test
  public void testSetNonce() {
    Block b = new Block();
    Long nonce = 123456789L;
    b.setNonce(nonce);
    Assert.assertEquals(nonce, b.getNonce());
  }

  @Test
  public void testTimestamp() {
    Block b = new Block();
    Long timestamp = System.currentTimeMillis();
    b.setTimestamp(timestamp);
    Assert.assertEquals(timestamp, b.getTimestamp());
  }

  @Test
  public void testSignature() {
    Block b = new Block();
    b.setSignature(ByteBuffer.allocate(SIGNATURE_SIZE));
    Assert.assertEquals(ByteBuffer.allocate(SIGNATURE_SIZE), b.getSignature());
  }

  @Test
  public void testHash() {
    Block b = new Block();
    b.setHash(ByteBuffer.allocate(HASH_SIZE));
    Assert.assertEquals(ByteBuffer.allocate(HASH_SIZE), b.getHash());
  }

  @Test
  public void testPreviousHash() {
    Block b = new Block();
    Assert.assertEquals(ByteBuffer.allocate(HASH_SIZE), b.getPreviousHash());
  }

  @Test
  public void testDifficulty() {
    Block b = new Block();
    b.setDifficulty(5);
    Assert.assertEquals(5, b.getDifficulty().intValue());
  }
}
