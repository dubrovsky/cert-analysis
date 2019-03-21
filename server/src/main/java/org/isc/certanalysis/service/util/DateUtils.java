package org.isc.certanalysis.service.util;

import java.time.LocalDateTime;

/**
 * @author p.dzeviarylin
 */
public class DateUtils {

	private final LocalDateTime now;
	private final LocalDateTime _7DaysAfter;
	private final LocalDateTime _1DayAfter;
	private final LocalDateTime _28DaysAfter;


	public DateUtils() {
		now = LocalDateTime.now();
		_7DaysAfter = now.plusDays(7);
		_1DayAfter = now.plusDays(1);
		_28DaysAfter = now.plusDays(28);
	}

	public boolean nowIsBefore(LocalDateTime dateTime){
		return now.isBefore(dateTime);
	}

	public boolean nowIsAfter(LocalDateTime dateTime){
		return now.isAfter(dateTime);
	}

	public boolean nowIs7DaysAfter(LocalDateTime dateTime){
		return _7DaysAfter.isAfter(dateTime);
	}

	public boolean nowIs1DaysAfter(LocalDateTime dateTime){
		return _1DayAfter.isAfter(dateTime);
	}

	public boolean nowIs28DaysAfter(LocalDateTime dateTime){
		return _28DaysAfter.isAfter(dateTime);
	}
}
