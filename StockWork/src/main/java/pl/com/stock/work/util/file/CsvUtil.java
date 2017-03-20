package pl.com.stock.work.util.file;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.format.DateTimeParseException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import pl.com.stock.work.model.StockDataSet;
import pl.com.stock.work.model.StockRecord;
import pl.com.stock.work.util.DateParser;

public class CsvUtil {

	public StockDataSet readFile(String path) {
		Reader in;
		try {
			in = new FileReader(
					"C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week1.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
			StockDataSet stockData = new StockDataSet();
			StockRecord sr;
			for (CSVRecord record : records) {
				sr =  new StockRecord();
				sr.setPriceValue(Double.valueOf(record.get("RateBid")));
				sr.setDate(new DateParser().parseDate(record.get("RateDateTime"),stockData.getDateFormats()));
				stockData.addRecord(sr);
			}
			return stockData;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DateTimeParseException e) {
			e.printStackTrace();
		}
		return null;	
	}

}
