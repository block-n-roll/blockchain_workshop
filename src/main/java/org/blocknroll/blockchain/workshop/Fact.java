package org.blocknroll.blockchain.workshop;

import java.nio.ByteBuffer;

class Fact {
    private ByteBuffer data;
    private ByteBuffer signature;

    /**
     * Constructor
     * @param dat the data held by this fact.
     * @param sig the signature used to protect this fact.
     */
    Fact(ByteBuffer dat, ByteBuffer sig) {
        data = dat;
        signature = sig;
    }

    /**
     * Returns the data associated to this fact.
     * @return the data associated to this fact.
     */
    ByteBuffer getData() {
        return data;
    }

    /**
     * Returns the signature associated to this fact that guaranties that data has not been modified.
     * @return the signature associated to this fact.
     */
    ByteBuffer getSignature() {
        return signature;
    }
}
