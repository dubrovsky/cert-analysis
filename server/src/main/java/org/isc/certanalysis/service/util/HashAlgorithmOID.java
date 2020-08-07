package org.isc.certanalysis.service.util;

import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: vva
 * Date: 21.11.13
 * Time: 9:51
 * To change this template use File | Settings | File Templates.
 */
public class HashAlgorithmOID {
  private static Vector<String> hashAlgorithm = new Vector();
  private static Vector<ObjectIdentifier> oid = new Vector();

  public static String getHashAlgorithm(ObjectIdentifier OID) {
    int i = oid.indexOf(OID);
    return i != -1 ? hashAlgorithm.get(i) : OID.toString();
  }

  public static ObjectIdentifier getOid(String KeyWord) {
    int i = hashAlgorithm.indexOf(KeyWord);
    return i != -1 ? oid.get(i) : null;
  }

  static {
    try {
      hashAlgorithm.add("SHA-512");
      oid.add(AlgorithmId.SHA512_oid);

      hashAlgorithm.add("AvBhf");
      oid.add(new ObjectIdentifier("1.3.6.1.4.1.12656.1.10"));

      hashAlgorithm.add("AvBelT");
      oid.add(new ObjectIdentifier("1.3.6.1.4.1.12656.1.42"));

      hashAlgorithm.add("BelT");
      oid.add(new ObjectIdentifier("1.2.112.0.2.0.34.101.31.81"));

      hashAlgorithm.add("Bhf");
      oid.add(new ObjectIdentifier("1.2.112.0.2.0.1176.1.13"));

      hashAlgorithm.add("GOST3411");
      oid.add(new ObjectIdentifier("1.2.643.2.2.9"));

      hashAlgorithm.add("GOST3411_2012_256");
      oid.add(new ObjectIdentifier("1.2.643.7.1.1.2.2"));

      hashAlgorithm.add("GOST3411_2012_512");
      oid.add(new ObjectIdentifier("1.2.643.7.1.1.2.3"));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
