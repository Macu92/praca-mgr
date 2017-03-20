package pl.com.stock.work.model;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StockDataSet {
	private List<StockRecord> records;
	private String currency;
	private List<DateTimeFormatter> dateFormats;
	
	public StockDataSet(){
		dateFormats = initDateFormats();
		records = new LinkedList<StockRecord>();
	}
	
	public void addRecord(StockRecord record){
		records.add(record);
	}
	
	public List<StockRecord> getRecords(){
		return records;
	}
	
	private List<DateTimeFormatter > initDateFormats(){
		List<DateTimeFormatter > strings =  new LinkedList<DateTimeFormatter >();
		strings.add(DateTimeFormat.forPattern(("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")));
		strings.add(DateTimeFormat.forPattern(("yyyy-MM-dd HH:mm:ss.SSS")));
		strings.add(DateTimeFormat.forPattern(("yyyy-MM-dd HH:mm:ss")));
		return strings;
		
	}

	public List<DateTimeFormatter > getDateFormats() {
		return dateFormats;
	}

	public void setDateFormats(List<DateTimeFormatter > dateFormats) {
		this.dateFormats = dateFormats;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
