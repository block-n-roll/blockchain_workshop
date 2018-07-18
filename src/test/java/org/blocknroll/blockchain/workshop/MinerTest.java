package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import java.io.IOException;
import org.junit.Test;

public class MinerTest {

  @Test
  public void mineTest() throws Exception {
    Miner miner = new Miner();
    Block b = new Block();
    String hash = CryptoUtil.bufferToHexString(miner.mine(b, 1).getHash());
    assertEquals(CryptoUtil.HASH_SIZE, hash.length() / 2);
    assertEquals(CryptoUtil.bufferToHexString(CryptoUtil.calculateHash(b)), hash);
    assertTrue(miner.validates(b.getHash(), 1));
  }

  @Test
  public void difficultyTest() throws Exception {
    Miner miner = new Miner();
    for (int diff = 0; diff < 3; diff++) {
      String hash = CryptoUtil.bufferToHexString(miner.mine(new Block(), diff).getHash());
      assertTrue(hash.startsWith(new String(new char[diff]).replace("\0", "00")));
    }
  }

  @Test
  public void validate() throws Exception {
    Miner miner = new Miner();
    for (int diff = 0; diff < 3; diff++) {
      assertTrue(miner.validates(miner.mine(new Block(), diff).getHash(), diff));
    }
  }
}
