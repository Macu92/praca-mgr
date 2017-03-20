package pl.com.stock.work.util;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import eu.verdelhan.ta4j.Tick;
import pl.com.stock.work.model.StockDataSet;

public class TimeSeriesGenerator {
	
	public void generateTimeSeries(StockDataSet dataSet, Integer interval){
		List<Tick> tickList =  new LinkedList();
		dataSet.getRecords().forEach(record -> {
			Tick tick;
//			if(tickList.size()>0){
//				tick = tickList.get(tickList.size()-1);
//			}else{
				DateTime time = record.getDate();
				tick =  new Tick(time,
						record.getPriceValue().doubleValue(),
						record.getPriceValue().doubleValue(),
						record.getPriceValue().doubleValue(),
						record.getPriceValue().doubleValue(),
						(double)0);
//			}
				tickList.add(tick);
		});
		System.out.println("OK");
	}
}
