package org.blocknroll.blockchain.workshop;

import org.junit.Assert;
import org.junit.Test;

public class BlockTest {


  @Test
  public void testSetNonceOnblock() {
    Block b = new Block();
    Long noce = 123456789l;
    b.setNonce(noce);
    Assert.assertEquals(noce, b.getNonce());
  }


}
