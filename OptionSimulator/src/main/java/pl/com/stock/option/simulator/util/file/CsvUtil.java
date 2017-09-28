package pl.com.stock.option.simulator.util.file;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pl.com.stock.option.simulator.AssetModel;
import pl.com.stock.option.simulator.AssetValueModel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CsvUtil {
	private static final String NEW_LINE_SEPARATOR = "\n";

	public <T> void writeAssetModelToFile(String path,String name, AssetModel assetModel) {
		FileWriter fileWriter = null;

		CSVPrinter csvFilePrinter = null;

		// Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		// initialize FileWriter object
		try {
			fileWriter = new FileWriter(path + "/"+name);

			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			// Create CSV file header

			List<String> headers = new LinkedList<>();
			headers.add("RealStockBehavior");
			headers.add("ClassifiedAs");
			headers.add("AssetChange");
			headers.add("AssetValue");

			csvFilePrinter.printRecord(headers);

			LinkedList<String> values;
			for (AssetValueModel assetValueModel : assetModel.getAssetValues()) {
				values = new LinkedList<String>();
				values.add(assetValueModel.getClassifiedInstanceModel().getRealStockBehavior());
				values.add(assetValueModel.getClassifiedInstanceModel().getClassifiedAs());
				values.add(String.valueOf(assetValueModel.getAssetChange()));
				values.add(String.valueOf(assetValueModel.getAssetValue()));
				writeLine(fileWriter, values);
			}
			fileWriter.close();
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
