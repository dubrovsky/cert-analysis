package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.CrlRevoked;
import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.repository.CertificateRepository;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.web.error.X509ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLEntry;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Optional;

/**
 * @author p.dzeviarylin
 */

@Service
@Transactional(rollbackFor = Throwable.class)
public class FileParserService {

	private final CertificateRepository certificateRepository;
	private final CrlRepository crlRepository;
	private final CertificateFactory certificateFactory;
	private ObjectIdentifier fioOI;
	private ObjectIdentifier addressOI;
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public FileParserService(CertificateRepository certificateRepository, CrlRepository crlRepository) throws CertificateException {
		this.certificateRepository = certificateRepository;
		this.crlRepository = crlRepository;
		this.certificateFactory = CertificateFactory.getInstance("X.509");
		this.fioOI = ObjectIdentifier.newInternal(new int[]{2, 5, 4, 41});
		this.addressOI = ObjectIdentifier.newInternal(new int[]{2, 5, 4, 9});
	}

	@Transactional(noRollbackFor = X509ParseException.class)
	void parse(File file, boolean doCheck) throws CertificateException, CRLException, IOException, NoSuchAlgorithmException {
		switch (file.getType()) {
			case CER:
			case CRT:
				file.addCertificate(parse((X509CertImpl) certificateFactory.generateCertificate(new ByteArrayInputStream(file.getBytes())), doCheck));
				break;
			case CRL:
				file.addCrl(parse((X509CRLImpl) certificateFactory.generateCRL(new ByteArrayInputStream(file.getBytes()))));
				break;
			case P7B:
				@SuppressWarnings("unchecked") final Collection<X509CertImpl> certificates = (Collection<X509CertImpl>) certificateFactory.generateCertificates(new ByteArrayInputStream(file.getBytes()));
				for (X509CertImpl certificate : certificates) {
					try {
						file.addCertificate(parse(certificate, doCheck));
					} catch (RuntimeException ignore) {}
				}
				@SuppressWarnings("unchecked") final Collection<X509CRLImpl> crls = (Collection<X509CRLImpl>) certificateFactory.generateCRLs(new ByteArrayInputStream(file.getBytes()));
				for (X509CRLImpl crl : crls) {
					try {
						file.addCrl(parse(crl));
					}  catch (RuntimeException ignore) {}
				}
				break;
		}
	}

	private Certificate parse(X509CertImpl x509Certificate, boolean doCheck) throws IOException, NoSuchAlgorithmException {
		Certificate certificate = new Certificate();

		if (x509Certificate.getSubjectKeyId() != null) {
			certificate.setSubjectKeyIdentifier(bytesToHex(x509Certificate.getSubjectKeyId().getIdentifier()));
		}

		certificate.setSerialNumber(x509Certificate.getSerialNumber().toString(16));

		if(doCheck && certificateRepository.countBySubjectKeyIdentifierAndSerialNumber(certificate.getSubjectKeyIdentifier(), certificate.getSerialNumber()) > 0){
			throw new X509ParseException("Такой сертификат уже есть в этой системе");
		}

		final X500Name x500Name = new X500Name(x509Certificate.getSubjectDN().getName());

		DerValue specificAttribute = x500Name.findMostSpecificAttribute(fioOI);
		if (specificAttribute != null) {
			certificate.setFio(x500Name.getSurname() + " " + specificAttribute.getAsString());
		}

		specificAttribute = x500Name.findMostSpecificAttribute(X500Name.title_oid);
		if (specificAttribute != null) {
			certificate.setPosition(specificAttribute.getAsString());
		}

		specificAttribute = x500Name.findMostSpecificAttribute(addressOI);
		if (specificAttribute != null) {
			certificate.setAddress(specificAttribute.getAsString());
		}

		certificate.setCommonName(x500Name.getCommonName());

		if (x509Certificate.getAuthKeyId() != null) {
			certificate.setIssuerKeyIdentifier(bytesToHex(x509Certificate.getAuthKeyId().getIdentifier()));
		}

		if (x509Certificate.getSubjectX500Principal() != null) {
			certificate.setSubjectPrincipal(shaBytesToHex(x509Certificate.getSubjectX500Principal().getEncoded()));
		}

		if (x509Certificate.getIssuerX500Principal() != null) {
			certificate.setIssuerPrincipal(shaBytesToHex(x509Certificate.getIssuerX500Principal().getEncoded()));
		}

		certificate.setNotAfter(x509Certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		certificate.setNotBefore(x509Certificate.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

		return certificate;
	}

	private Crl parse(X509CRLImpl x509CRL) throws NoSuchAlgorithmException, IOException {
		if(x509CRL.getThisUpdate() == null) {
			throw new X509ParseException("СОС не актуален");
		}

		Crl crl = new Crl();
		crl.setIssuerPrincipal(shaBytesToHex(x509CRL.getIssuerX500Principal().getEncoded()));

		Crl currentCrl = crlRepository.findByActiveIsTrueAndIssuerPrincipal(crl.getIssuerPrincipal());
		if(currentCrl != null && currentCrl.getThisUpdate().compareTo(x509CRL.getThisUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) >= 0) {
			throw new X509ParseException("СОС не актуален");
		}

		crl.setActive(true);
		crl.setVersion(1);
		if(currentCrl != null) {
			currentCrl.setActive(false);
			crl.setVersion(currentCrl.getVersion() + 1);
			crlRepository.save(currentCrl);
		}

		if (x509CRL.getAuthKeyId() != null) {
			crl.setAuthKeyIdentifier(shaBytesToHex(x509CRL.getAuthKeyId().getIdentifier()));
		}

		crl.setNextUpdate(x509CRL.getNextUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		crl.setThisUpdate(x509CRL.getThisUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		crl.setCrlNumber(x509CRL.getCRLNumber().toString(16));

		if(x509CRL.getRevokedCertificates() != null){
			for (X509CRLEntry x509CRLEntry : x509CRL.getRevokedCertificates()) {
				CrlRevoked crlRevoked = new CrlRevoked();
				crlRevoked.setRevocationDate(x509CRLEntry.getRevocationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				crlRevoked.setSerialNumber(x509CRLEntry.getSerialNumber().toString(16));
				if(((X509CRLEntryImpl) x509CRLEntry).getReasonCode() != null){
					crlRevoked.setReasonCode(((X509CRLEntryImpl) x509CRLEntry).getReasonCode().shortValue());
				}
				crl.addCrlRevoked(crlRevoked);
			}
		}

		return crl;
	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	private static String shaBytesToHex(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return bytesToHex(md.digest(bytes));
	}

	File.Type getTypeByFileName(String fileName) {
		return File.Type.valueOf(Optional.ofNullable(fileName)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(fileName.lastIndexOf(".") + 1)).orElseThrow(() -> new X509ParseException("File format is not supported")).toUpperCase());
	}
}
