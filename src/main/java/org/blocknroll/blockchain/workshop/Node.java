package org.blocknroll.blockchain.workshop;

import java.util.Collection;

/**
 * This class represents the interface towards the cluster, thus declaring input / output interfaces.
 */
public class Node {
    private Collection<Fact> pendingFacts;
    private Chain chain;

    // -----------------------------------------------------------------------------------------------------------------
    // Cluster methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public Node() {
        // TODO
    }

    public void addToCluster() {

    }

    public void removeFromCluster() {

    }

    // -----------------------------------------------------------------------------------------------------------------
    // Blockchain methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Add facts to be grouped and mined into a block.
     * @param facts
     */
    public void addFacts(Collection<Fact> facts) {
        // TODO
    }

    /**
     * Returns the chain (http request).
     */
    public Chain getChain() {
        return chain;
    }

    /**
     * Add a block to the chain.
     */
    public void addBlock() {

    }

    public Collection<Fact> getPendingFacts() {
        return pendingFacts;
    }

}
