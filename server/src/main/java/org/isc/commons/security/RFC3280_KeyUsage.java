package org.isc.commons.security;

import java.util.Vector;

public class RFC3280_KeyUsage {
    private static Vector<String> keyUsage = new Vector();

    public static String get(int p) {
        return keyUsage.get(p);
    }

    /*
    KeyUsage ::= BIT STRING {
           digitalSignature        (0),
           nonRepudiation          (1),
           keyEncipherment         (2),
           dataEncipherment        (3),
           keyAgreement            (4),
           keyCertSign             (5),
           cRLSign                 (6),
           encipherOnly            (7),
           decipherOnly            (8) }
     */
    static {
        keyUsage.add("digitalSignature");
        keyUsage.add("nonRepudiation");
        keyUsage.add("keyEncipherment");
        keyUsage.add("dataEncipherment");
        keyUsage.add("keyAgreement");
        keyUsage.add("keyCertSign");
        keyUsage.add("cRLSign");
        keyUsage.add("encipherOnly");
        keyUsage.add("decipherOnly");
    }
}
