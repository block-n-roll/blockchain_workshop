package org.blocknroll.blockchain.workshop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LocalCluster implements Cluster {

  private static final Logger logger = LogManager.getLogger(LocalCluster.class);
  private Cluster seed;
  private String name;
  private List<Cluster> peers;
  private Node node;
  private static Map<Long, Block> minedBlocks = new HashMap<>();

  public LocalCluster(String name) {
    this.name = name;
    this.peers = new ArrayList<>();
  }

  public void setSeed(Node node) {
    this.node = node;
  }

  public void setSeed(Cluster seed) {
    this.seed = seed;
  }

  @Override
  public void requestProofOfWork(Block block) throws Exception {
    if(minedBlocks.containsKey(block.getFacts().iterator().next().getData().getLong())) {
      logger.warn("This block has already been mined! Skipping...");
      return;
    }
    int vote = 0;
    if (node.verifyBlock(block, node.getLastBlock())) {
      vote++;
    }
    for (Cluster peer : peers) {
      if (((LocalCluster) peer).node.verifyBlock(block, node.getLastBlock())) {
        vote++;
      }
    }
    if (vote > (peers.size() / 2)) {
      logger.info("PoW passed! Inserting the block into the blockchain");
      node.processBlock(Collections.singletonList(block));
      for (Cluster peer : peers) {
        if(((LocalCluster) peer).node.processBlock(Collections.singletonList(block))) {
          minedBlocks.put(block.getFacts().iterator().next().getData().getLong(), block);
        }
      }
    } else {
      logger.warn("");
    }
  }

  @Override
  public String getId() {
    return name;
  }

  @Override
  public void addPeer(Cluster n) {
    peers.add(n);
  }

  @Override
  public List<Cluster> getPeers() {
    return peers;
  }

  @Override
  public void addFacts(Collection<Fact> facts) throws Exception {
    node.mineFacts(facts);
    for (Cluster node : peers) {
//      if(minedBlocks.containsKey(facts.iterator().next().getData().getLong())) {
//        logger.warn("This block has already been mined! Skipping...");
//        return;
//      }
      ((LocalCluster) node).node.mineFacts(facts);
    }
  }

  @Override
  public Chain getChain() {
    return node.getChain();
  }

  @Override
  public Block getLastBlock() {
    return node.getLastBlock();
  }

  @Override
  public void processBlocks(List<Block> block) throws Exception {
    node.processBlock(block);
  }

  @Override
  public void requestChain() throws Exception {
    Chain chain;
    chain = ((LocalCluster) peers.get(0)).node.requestChain();
    node.processBlock(chain.getBlocks());
  }
}
