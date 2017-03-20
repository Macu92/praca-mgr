package pl.com.stock.work.model;

import org.joda.time.DateTime;

public class StockRecord {
	private DateTime date;
	private Double priceValue;
	
	public DateTime getDate() {
		return date;
	}
	public void setDate(DateTime date) {
		this.date = date;
	}
	public Double getPriceValue() {
		return priceValue;
	}
	public void setPriceValue(Double priceValue) {
		this.priceValue = priceValue;
	}
	
	
}
