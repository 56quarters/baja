package org.tshlabs.baja.client;

import org.tshlabs.baja.client.operations.HashOperations;
import org.tshlabs.baja.client.operations.StringOperations;

/**
 *
 */
public interface BajaClient {

    HashOperations getHashOperations();

    StringOperations getStringOperations();
}
