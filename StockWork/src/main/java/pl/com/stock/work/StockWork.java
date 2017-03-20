package pl.com.stock.work;

import pl.com.stock.work.model.StockDataSet;
import pl.com.stock.work.util.TimeSeriesGenerator;
import pl.com.stock.work.util.file.CsvUtil;

public class StockWork {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StockDataSet set = new CsvUtil().readFile("tt");
		new TimeSeriesGenerator().generateTimeSeries(set, 1);
	}

}
