package pl.com.stock.work.model;

import org.joda.time.DateTime;

import eu.verdelhan.ta4j.Decimal;

public class StockRecord {
	private DateTime date;
	private Decimal priceValue;
	
	public DateTime getDate() {
		return date;
	}
	public void setDate(DateTime date) {
		this.date = date;
	}
	public Decimal getPriceValue() {
		return priceValue;
	}
	public void setPriceValue(Decimal priceValue) {
		this.priceValue = priceValue;
	}
	
	
}
