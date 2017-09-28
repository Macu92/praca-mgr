package pl.com.stock.work.util;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Period;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import pl.com.stock.work.model.StockDataSet;
import pl.com.stock.work.model.StockRecord;

public class TimeSeriesGenerator {

	public TimeSeries generateTimeSeries(StockDataSet dataSet, Integer interval) {
		List<Tick> tickList = new LinkedList();
		MutableTick mutableTick = null;
		for (StockRecord record : dataSet.getRecords()) {
			if (mutableTick != null
					&& mutableTick.getStartDateTime().getMinuteOfHour() == record.getDate().getMinuteOfHour()
					&& Minutes.minutesBetween(mutableTick.getStartDateTime(), record.getDate()).getMinutes() == 0) {
				mutableTick.setHighPrice(record.getPriceValue().isGreaterThan(mutableTick.getHighPrice())
						? record.getPriceValue() : mutableTick.getHighPrice());
				mutableTick.setLowPrice(record.getPriceValue().isLessThan(mutableTick.getLowPrice())
						? record.getPriceValue() : mutableTick.getLowPrice());
				mutableTick.setClosePrice(record.getPriceValue());
			} else {
				if (mutableTick != null) {
					tickList.add(createTick(mutableTick));
				}
				DateTime time = new DateTime(record.getDate().getYear(), record.getDate().getMonthOfYear(),
						record.getDate().getDayOfMonth(), record.getDate().getHourOfDay(),
						record.getDate().getMinuteOfHour());
				mutableTick = new MutableTick(Minutes.minutes(1).toPeriod(),time,time.plusMinutes(1), record.getPriceValue(), record.getPriceValue(),
						record.getPriceValue(), record.getPriceValue(), Decimal.valueOf(0));
			}
		}
		tickList.add(createTick(mutableTick));
		return new TimeSeries(tickList);
	}

	public TimeSeries generateTimeSeries15min(StockDataSet dataSet, Integer interval) {
		List<Tick> tickList = new LinkedList();
		MutableTick mutableTick = null;
		for (StockRecord record : dataSet.getRecords()) {
			if (mutableTick != null
//					&& mutableTick.getStartDateTime().getMinuteOfHour() == record.getDate().getMinuteOfHour()
					&& Minutes.minutesBetween(mutableTick.getStartDateTime(), record.getDate()).getMinutes() < 15) {
				mutableTick.setHighPrice(record.getPriceValue().isGreaterThan(mutableTick.getHighPrice())
						? record.getPriceValue() : mutableTick.getHighPrice());
				mutableTick.setLowPrice(record.getPriceValue().isLessThan(mutableTick.getLowPrice())
						? record.getPriceValue() : mutableTick.getLowPrice());
				mutableTick.setClosePrice(record.getPriceValue());
			} else {
				if (mutableTick != null) {
					tickList.add(createTick(mutableTick));
				}
				DateTime time = new DateTime(record.getDate().getYear(), record.getDate().getMonthOfYear(),
						record.getDate().getDayOfMonth(), record.getDate().getHourOfDay(),
						record.getDate().getMinuteOfHour());
				mutableTick = new MutableTick(Minutes.minutes(15).toPeriod(),time,time.plusMinutes(15), record.getPriceValue(), record.getPriceValue(),
						record.getPriceValue(), record.getPriceValue(), Decimal.valueOf(0));
			}
		}
		tickList.add(createTick(mutableTick));
		return new TimeSeries(tickList);
	}

	public TimeSeries generateTimeSeries30min(StockDataSet dataSet, Integer interval) {
		List<Tick> tickList = new LinkedList();
		MutableTick mutableTick = null;
		for (StockRecord record : dataSet.getRecords()) {
			if (mutableTick != null
//					&& mutableTick.getStartDateTime().getMinuteOfHour() == record.getDate().getMinuteOfHour()
					&& Minutes.minutesBetween(mutableTick.getStartDateTime(), record.getDate()).getMinutes() < 30) {
				mutableTick.setHighPrice(record.getPriceValue().isGreaterThan(mutableTick.getHighPrice())
						? record.getPriceValue() : mutableTick.getHighPrice());
				mutableTick.setLowPrice(record.getPriceValue().isLessThan(mutableTick.getLowPrice())
						? record.getPriceValue() : mutableTick.getLowPrice());
				mutableTick.setClosePrice(record.getPriceValue());
			} else {
				if (mutableTick != null) {
					tickList.add(createTick(mutableTick));
				}
				DateTime time = new DateTime(record.getDate().getYear(), record.getDate().getMonthOfYear(),
						record.getDate().getDayOfMonth(), record.getDate().getHourOfDay(),
						record.getDate().getMinuteOfHour());
				mutableTick = new MutableTick(Minutes.minutes(30).toPeriod(),time,time.plusMinutes(30), record.getPriceValue(), record.getPriceValue(),
						record.getPriceValue(), record.getPriceValue(), Decimal.valueOf(0));
			}
		}
		tickList.add(createTick(mutableTick));
		return new TimeSeries(tickList);
	}

	private Tick createTick(MutableTick mutableTick){
		return new Tick(mutableTick.getPeriod(), mutableTick.getEndDateTime(),
				mutableTick.getOpenPrice(), mutableTick.getHighPrice(), mutableTick.getLowPrice(),
				mutableTick.getClosePrice(), mutableTick.getVolume());
	}

	private class MutableTick {
		Period period;
		DateTime startDateTime;
		DateTime endDateTime;
		Decimal openPrice;
		Decimal highPrice;
		Decimal lowPrice;
		Decimal closePrice;
		Decimal volume;

		public MutableTick(Period period,DateTime startDateTime, DateTime endDateTime, Decimal openPrice, Decimal highPrice, Decimal lowPrice,
				Decimal closePrice, Decimal volume) {
			this.startDateTime = startDateTime;
			this.period = period;
			this.endDateTime = endDateTime;
			this.openPrice = openPrice;
			this.highPrice = highPrice;
			this.lowPrice = lowPrice;
			this.closePrice = closePrice;
			this.volume = volume;
		}

		public DateTime getStartDateTime() {
			return startDateTime;
		}

		public void setStartDateTime(DateTime startDateTime) {
			this.startDateTime = startDateTime;
		}

		public Period getPeriod() {
			return period;
		}

		public void setPeriod(Period period) {
			this.period = period;
		}

		public Decimal getOpenPrice() {
			return openPrice;
		}

		public void setOpenPrice(Decimal openPrice) {
			this.openPrice = openPrice;
		}

		public Decimal getHighPrice() {
			return highPrice;
		}

		public void setHighPrice(Decimal highPrice) {
			this.highPrice = highPrice;
		}

		public Decimal getLowPrice() {
			return lowPrice;
		}

		public void setLowPrice(Decimal lowPrice) {
			this.lowPrice = lowPrice;
		}

		public Decimal getClosePrice() {
			return closePrice;
		}

		public void setClosePrice(Decimal closePrice) {
			this.closePrice = closePrice;
		}

		public Decimal getVolume() {
			return volume;
		}

		public void setVolume(Decimal volume) {
			this.volume = volume;
		}

		public DateTime getEndDateTime() {
			return endDateTime;
		}

		public void setEndDateTime(DateTime endDateTime) {
			this.endDateTime = endDateTime;
		}
	}
}
