package org.blocknroll.blockchain.workshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Test;

public class ChainTest {

  @Test
  public void addBlock() throws Exception {
    Chain chain = new Chain("myChain");
    assertEquals(0, chain.getSize());
    chain.addBlock(new Block());
    assertEquals(1, chain.getSize());
  }

  @Test
  public void getLastBlock() throws Exception {
    Chain chain = new Chain("myChain");
    Block gen = new Block();
    chain.addBlock(gen);
    assertEquals(gen, chain.getLastBlock());
  }

  @Test
  public void getBlocks() throws Exception {
    Chain chain = new Chain("myChain");
    Block gen = new Block();
    chain.addBlock(gen);
    assertEquals(1, chain.getBlocks().size());
  }

  @Test
  public void getSize() throws Exception {
    Chain chain = new Chain("myChain");
    Block gen = new Block();
    chain.addBlock(gen);
    assertEquals(1, chain.getSize());
  }

}
