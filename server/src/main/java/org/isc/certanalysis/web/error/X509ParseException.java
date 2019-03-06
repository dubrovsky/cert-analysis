package org.isc.certanalysis.web.error;

/**
 * @author p.dzeviarylin
 */
public class X509ParseException extends RuntimeException {
	public X509ParseException(String msg) {
		super(msg);
	}
}
