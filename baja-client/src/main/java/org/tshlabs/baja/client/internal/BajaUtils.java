package org.tshlabs.baja.client.internal;

/**
 *
 */
public class BajaUtils {

    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }

        return obj;
    }
}
