package pl.com.stock.work.util;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class DateParser {

	public DateTime parseDate(String dateString, List<DateTimeFormatter> dateFormats) throws IllegalArgumentException {
		IllegalArgumentException e = null;
		for (DateTimeFormatter format : dateFormats) {
			try {
				return format.parseDateTime(dateString);
			} catch (IllegalArgumentException exception) {
				e = exception;
			}
		}
		throw new IllegalArgumentException("No known date format");
	}
}
