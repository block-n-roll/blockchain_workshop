package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;
import org.junit.Test;

public class FactTest {

  @Test(expected = IllegalArgumentException.class)
  public void createFactWithNullDataAndNullSignature() {
    new Fact(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createFactWithNullDataEmptySignature() {
    new Fact(null, ByteBuffer.allocate(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createFactWithEmptyDataNullSignature() {
    new Fact(ByteBuffer.allocate(0), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createFactWithEmptyDataAndEmptySignature() {
    new Fact(ByteBuffer.allocate(0), ByteBuffer.allocate(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createFactWithEmptyDataAndValidSignature() {
    
    new Fact(ByteBuffer.allocate(0), ByteBuffer.allocate(10));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createFactWithValidDataAndEmptySignature() {

    new Fact(ByteBuffer.allocate(0), ByteBuffer.allocate(0));
  }

  @Test
  public void createFactWithValidDataAndValidSignature() {

    new Fact(ByteBuffer.allocate(10), ByteBuffer.allocate(10));
  }

}
