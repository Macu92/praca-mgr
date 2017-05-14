package pl.com.stock.work;

import java.util.List;
import java.util.Map;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.CCIIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.statistics.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsUpperIndicator;
import pl.com.stock.work.indicators.CCI;
import pl.com.stock.work.indicators.RSI;
import pl.com.stock.work.model.StockDataSet;
import pl.com.stock.work.strategy.RsiPremiumStrategy;
import pl.com.stock.work.util.TimeSeriesGenerator;
import pl.com.stock.work.util.file.CsvUtil;

public class StockWork {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CsvUtil csv = new CsvUtil();
		StockDataSet set = csv.readFile(
				"C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week1.csv");
		set = set.append(csv.readFile(
				"C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week2.csv"));
		set = set.append(csv.readFile(
				"C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week3.csv"));
		set = set.append(csv.readFile(
				"C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week4.csv"));
		set = set.append(csv.readFile(
				"C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week5.csv"));
		TimeSeries ts = new TimeSeriesGenerator().generateTimeSeries(set, 1);
		ClosePriceIndicator closePrice = new ClosePriceIndicator(ts);
//		RSIIndicator rsi = new RSIIndicator(closePrice, 14);
//		CCIIndicator cci = new CCIIndicator(ts, 20);

		SMAIndicator sma = new SMAIndicator(closePrice, 20);
		StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
		BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
		BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd, Decimal.valueOf(2.5));
		BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd, Decimal.valueOf(2.5));
		
		EMAIndicator ema = new EMAIndicator(closePrice, 9);
		MACDIndicator macd = new MACDIndicator(ema, 13, 26);
		
		RSI rsi = new RSI(Decimal.valueOf(20), Decimal.valueOf(80), closePrice, 14);
		CCI cci = new CCI(ts, 20, Decimal.valueOf(150), Decimal.valueOf(-150));
		
		RsiPremiumStrategy rsiPrem = new RsiPremiumStrategy(rsi,cci,bbu,bbl);
		rsiPrem.setBbMiddle(bbm);
		rsiPrem.setMacd(macd);
		Map<String, List<Object>> map = rsiPrem.createIndicatorsScaledSignalsMap(5);//rsiPrem.createIndicatorValuesMap(5);
		
//		csv.writeToFile("C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/generowaneDane", rsi, cci, bbu,bbm, bbl, macd);
		csv.writeMapToFile("C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/generowaneDane", map);
		System.out.println(rsiPrem.getHighest()+" h || "+ rsiPrem.getLowest());
		System.out.println("end");
	}

}
