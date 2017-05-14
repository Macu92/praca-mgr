package pl.com.stock.work.util.file;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import pl.com.stock.work.model.StockDataSet;
import pl.com.stock.work.model.StockRecord;
import pl.com.stock.work.util.DateParser;

public class CsvUtil {
	private static final String NEW_LINE_SEPARATOR = "\n";

	public StockDataSet readFile(String path) {
		Reader in;
		try {
			in = new FileReader(path);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
			StockDataSet stockData = new StockDataSet();
			StockRecord sr;
			for (CSVRecord record : records) {
				sr = new StockRecord();
				sr.setPriceValue(Decimal.valueOf(record.get("RateBid")));
				sr.setDate(new DateParser().parseDate(record.get("RateDateTime"), stockData.getDateFormats()));
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

	public void writeNextColumn(String path) {
	}

	public <T> void writeToFile(String path, Indicator... indicators) {
		FileWriter fileWriter = null;

		CSVPrinter csvFilePrinter = null;

		// Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		// initialize FileWriter object
		try {
			fileWriter = new FileWriter(path + "/file.csv");

			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			// Create CSV file header

			List<String> headers = new LinkedList<String>();
			headers.add("priceValue");

			for (Indicator indicator : indicators) {
				headers.add(indicator.getClass().getSimpleName());
			}
			// headers.add("futureVale");
			headers.add("futureVale2");
			csvFilePrinter.printRecord(headers);

			LinkedList<String> values;
			for (int i = 0; i <= indicators[0].getTimeSeries().getEnd() - 5; i++) {
				values = new LinkedList<String>();
				values.add(indicators[0].getTimeSeries().getTick(i).getClosePrice().toString());
				for (Indicator indicator : indicators) {
					values.add(((Decimal) indicator.getValue(i)).toString());
				}
				// values.add(indicators[0].getTimeSeries().getTick(i+5).getClosePrice().toString());
				// if(indicators[0].getTimeSeries().getTick(i+5).getClosePrice().isGreaterThan(indicators[0].getTimeSeries().getTick(i).getClosePrice())){
				// if (((Decimal)
				// indicators[0].getValue(i)).isLessThan(Decimal.valueOf(20))) {
				// values.add("up");
				// } else if (((Decimal)
				// indicators[0].getValue(i)).isGreaterThan(Decimal.valueOf(80)))
				// {
				// values.add("down");
				// } else {
				// values.add("no");
				// }

				if (indicators[0].getTimeSeries().getTick(i + 5).getClosePrice()
						.isGreaterThan(indicators[0].getTimeSeries().getTick(i).getClosePrice())){
//						&& ((Decimal) indicators[0].getValue(i)).isLessThan(Decimal.valueOf(20))) {
					values.add("up");
				} else if (indicators[0].getTimeSeries().getTick(i + 5).getClosePrice()
						.isLessThanOrEqual(indicators[0].getTimeSeries().getTick(i).getClosePrice())){
						//&& ((Decimal) indicators[0].getValue(i)).isGreaterThan(Decimal.valueOf(80))) {
					values.add("down");
				} else {
					values.add("no");
				}
				writeLine(fileWriter, values);
				// csvFilePrinter.printRecord(indicator.getValue(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> void writeMapToFile(String path, Map<String, List<Object>> map) {
		FileWriter fileWriter = null;

		CSVPrinter csvFilePrinter = null;

		// Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		// initialize FileWriter object
		try {
			fileWriter = new FileWriter(path + "/fileMapValues2.csv");

			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			// Create CSV file header

			List<String> headers = new LinkedList<String>();

			for (String key : map.keySet()) {
				headers.add(key);
			}
			
			csvFilePrinter.printRecord(headers);

			LinkedList<String> values;
			for (int i = 0; i <= map.get(headers.get(0)).size()-1; i++) {
				values = new LinkedList<String>();
				for (String key : map.keySet()) {
					values.add(map.get(key).get(i).toString());
				}
				writeLine(fileWriter, values);
			}
//			writeLine(fileWriter, values); moze dopisanie lini usunie problem z niepelna ostatnia
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final char DEFAULT_SEPARATOR = ',';

	public void writeLine(Writer w, List<String> values) throws IOException {
		writeLine(w, values, DEFAULT_SEPARATOR, ' ');
	}

	public void writeLine(Writer w, List<String> values, char separators) throws IOException {
		writeLine(w, values, separators, ' ');
	}

	private String followCVSformat(String value) {

		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		return result;

	}

	public void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

		boolean first = true;

		// default customQuote is empty

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first) {
				sb.append(separators);
			}
			if (customQuote == ' ') {
				sb.append(followCVSformat(value));
			} else {
				sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
			}

			first = false;
		}
		sb.append("\n");
		w.append(sb.toString());

	}
}
