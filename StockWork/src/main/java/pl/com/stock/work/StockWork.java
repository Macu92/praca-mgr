package pl.com.stock.work;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.CCIIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.statistics.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandWidthIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsUpperIndicator;
import pl.com.stock.work.model.StockDataSet;
import pl.com.stock.work.util.TimeSeriesGenerator;
import pl.com.stock.work.util.file.CsvUtil;

public class StockWork {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CsvUtil csv = new CsvUtil();
		StockDataSet set = csv.readFile(
				"C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week1.csv");
		TimeSeries ts = new TimeSeriesGenerator().generateTimeSeries(set, 1);
		ClosePriceIndicator closePrice = new ClosePriceIndicator(ts);
		RSIIndicator rsi = new RSIIndicator(closePrice, 14);
		CCIIndicator cci = new CCIIndicator(ts, 20);

		SMAIndicator sma = new SMAIndicator(closePrice, 20);
		StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
		BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
		BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd, Decimal.valueOf(2.5));
		BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd, Decimal.valueOf(2.5));
		// System.out.println(rsi.getTimeSeries().getEnd());
		// for(int i = 0; i<rsi.getTimeSeries().getEnd()+1;i++){
		// System.out.println(i);
		// System.out.println(rsi.getTimeSeries().getTick(i).getClosePrice());
		// }
		csv.writeToFile("C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/generowaneDane", rsi, cci, bbu, bbl);

	}

}
