package org.isc.certanalysis.web.error;

public class X509ExistsException extends RuntimeException {
    public X509ExistsException(String msg) {
        super(msg);
    }
}
