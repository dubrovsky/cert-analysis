package org.isc.certanalysis.service.util;

import java.time.LocalDateTime;

/**
 * @author p.dzeviarylin
 */
public class DateUtils {

	private final LocalDateTime now;
	private final LocalDateTime sevenDaysAfter;


	public DateUtils() {
		now = LocalDateTime.now();
		sevenDaysAfter = now.plusDays(7);
	}

	public boolean nowIsBefore(LocalDateTime dateTime){
		return now.isBefore(dateTime);
	}

	public boolean nowIsAfter(LocalDateTime dateTime){
		return now.isAfter(dateTime);
	}

	public boolean nowIsSevenDaysAfter(LocalDateTime dateTime){
		return sevenDaysAfter.isAfter(dateTime);
	}
}
