package org.isc.certanalysis.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author p.dzeviarylin
 */
public class Sha256PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();

			for (byte hash1 : hash) {
				String hex = Integer.toHexString(0xff & hash1);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword != null && encodedPassword.length() != 0) {
			return encode(rawPassword).equals(encodedPassword);
		} else {
			return false;
		}
	}
}
