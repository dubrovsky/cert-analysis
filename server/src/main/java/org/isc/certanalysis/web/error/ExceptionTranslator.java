package org.isc.certanalysis.web.error;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author p.dzeviarylin
 */
@ControllerAdvice
public class ExceptionTranslator {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleExceptions(Exception ex) {
		return ResponseEntity.badRequest().body(ExceptionUtils.getRootCauseMessage(ex));
	}
}
