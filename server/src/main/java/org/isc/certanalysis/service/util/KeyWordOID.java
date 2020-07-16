package org.isc.certanalysis.service.util;

import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.X500Name;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: vva
 * Date: 21.11.13
 * Time: 9:51
 * To change this template use File | Settings | File Templates.
 */
public class KeyWordOID {
  private static Vector<String> keyword = new Vector();
  private static Vector<ObjectIdentifier> oid = new Vector();

  private static final int[] anyExtendedKeyUsageOidData = new int[]{2, 5, 29, 37, 0};
  private static final int[] serverAuthOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 1};
  private static final int[] clientAuthOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 2};
  private static final int[] codeSigningOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 3};
  private static final int[] emailProtectionOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 4};
  private static final int[] ipsecEndSystemOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 5};
  private static final int[] ipsecTunnelOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 6};
  private static final int[] ipsecUserOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 7};
  private static final int[] timeStampingOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 8};
  private static final int[] OCSPSigningOidData = new int[]{1, 3, 6, 1, 5, 5, 7, 3, 9};

  private static final int[] avestNameOidData = new int[]{2,5,4,41};

  private static final ObjectIdentifier Ad_OCSP_Id = ObjectIdentifier.newInternal(new int[]{1, 3, 6, 1, 5, 5, 7, 48, 1});
  private static final ObjectIdentifier Ad_CAISSUERS_Id = ObjectIdentifier.newInternal(new int[]{1, 3, 6, 1, 5, 5, 7, 48, 2});
  private static final ObjectIdentifier Ad_TIMESTAMPING_Id = ObjectIdentifier.newInternal(new int[]{1, 3, 6, 1, 5, 5, 7, 48, 3});
  private static final ObjectIdentifier Ad_CAREPOSITORY_Id = ObjectIdentifier.newInternal(new int[]{1, 3, 6, 1, 5, 5, 7, 48, 5});

  public static String getKeyword(ObjectIdentifier OID) {
    int i = oid.indexOf(OID);
    return i != -1 ? keyword.get(i) : OID.toString();
  }

  public static ObjectIdentifier getOid(String KeyWord) {
    int i = keyword.indexOf(KeyWord);
    return i != -1 ? oid.get(i) : null;
  }

  static {
    keyword.add("CN");
    oid.add(X500Name.commonName_oid);

    keyword.add("C");
    oid.add(X500Name.countryName_oid);

    keyword.add("L");
    oid.add(X500Name.localityName_oid);

    keyword.add("S");
    oid.add(X500Name.stateName_oid);

    keyword.add("ST");
    oid.add(X500Name.stateName_oid);

    keyword.add("O");
    oid.add(X500Name.orgName_oid);

    keyword.add("OU");
    oid.add(X500Name.orgUnitName_oid);

    keyword.add("T");
    oid.add(X500Name.title_oid);

    keyword.add("IP");
    oid.add(X500Name.ipAddress_oid);

    keyword.add("STREET");
    oid.add(X500Name.streetAddress_oid);

    keyword.add("DC");
    oid.add(X500Name.DOMAIN_COMPONENT_OID);

    keyword.add("DNQUALIFIER");
    oid.add(X500Name.DNQUALIFIER_OID);

    keyword.add("DNQ");
    oid.add(X500Name.DNQUALIFIER_OID);

    keyword.add("SURNAME");
    oid.add(X500Name.SURNAME_OID);

    keyword.add("GIVENNAME");
    oid.add(X500Name.GIVENNAME_OID);

    keyword.add("INITIALS");
    oid.add(X500Name.INITIALS_OID);

    keyword.add("GENERATION");
    oid.add(X500Name.GENERATIONQUALIFIER_OID);

    keyword.add("EMAIL");
    oid.add(PKCS9Attribute.EMAIL_ADDRESS_OID);

    keyword.add("EMAILADDRESS");
    oid.add(PKCS9Attribute.EMAIL_ADDRESS_OID);

    keyword.add("UID");
    oid.add(X500Name.userid_oid);

    keyword.add("SERIALNUMBER");
    oid.add(X500Name.SERIALNUMBER_OID);

    keyword.add("AVESTNAME");
    oid.add(ObjectIdentifier.newInternal(avestNameOidData));

    keyword.add("anyExtendedKeyUsage");
    oid.add(ObjectIdentifier.newInternal(anyExtendedKeyUsageOidData));

    keyword.add("serverAuth");
    oid.add(ObjectIdentifier.newInternal(serverAuthOidData));

    keyword.add("clientAuth");
    oid.add(ObjectIdentifier.newInternal(clientAuthOidData));

    keyword.add("codeSigning");
    oid.add(ObjectIdentifier.newInternal(codeSigningOidData));

    keyword.add("emailProtection");
    oid.add(ObjectIdentifier.newInternal(emailProtectionOidData));

    keyword.add("ipsecEndSystem");
    oid.add(ObjectIdentifier.newInternal(ipsecEndSystemOidData));

    keyword.add("ipsecTunnel");
    oid.add(ObjectIdentifier.newInternal(ipsecTunnelOidData));

    keyword.add("ipsecUser");
    oid.add(ObjectIdentifier.newInternal(ipsecUserOidData));

    keyword.add("timeStamping");
    oid.add(ObjectIdentifier.newInternal(timeStampingOidData));

    keyword.add("OCSPSigning");
    oid.add(ObjectIdentifier.newInternal(OCSPSigningOidData));

    keyword.add("caIssuers");
    oid.add(Ad_CAISSUERS_Id);

    keyword.add("caRepository");
    oid.add(Ad_CAREPOSITORY_Id);

    keyword.add("timeStamping");
    oid.add(Ad_TIMESTAMPING_Id);

    keyword.add("ocsp");
    oid.add(Ad_OCSP_Id);



  }
}
