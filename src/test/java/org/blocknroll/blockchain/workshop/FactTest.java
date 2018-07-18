package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void getData() {
    Fact fact = new Fact(ByteBuffer.allocate(10), ByteBuffer.allocate(10));
    assertNotNull(fact.getData());
    assertEquals(fact.getData(), ByteBuffer.allocate(10));
  }

  @Test
  public void getSignature() {
    Fact fact = new Fact(ByteBuffer.allocate(10), ByteBuffer.allocate(10));
    assertNotNull(fact.getSignature());
    assertEquals(fact.getSignature(), ByteBuffer.allocate(10));
  }

  @Test
  public void serialise() {
    Fact fact = new Fact(ByteBuffer.allocate(10), ByteBuffer.allocate(10));
    Fact other = new Fact(ByteBuffer.allocate(5), ByteBuffer.allocate(5));
    assertTrue(!fact.equals(other));
    other.deserialise(fact.serialise());
    assertEquals(other, fact);
  }
}
