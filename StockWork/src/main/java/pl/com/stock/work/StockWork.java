package pl.com.stock.work;

import java.io.IOException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
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
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import pl.com.stock.work.indicators.CCI;
import pl.com.stock.work.indicators.RSI;
import pl.com.stock.work.model.StockDataSet;
import pl.com.stock.work.strategy.RsiPremiumStrategy;
import pl.com.stock.work.util.DateParser;
import pl.com.stock.work.util.TimeSeriesGenerator;
import pl.com.stock.work.util.file.CsvToArff;
import pl.com.stock.work.util.file.CsvUtil;

public class StockWork {
    static CsvUtil csv;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        csv = new CsvUtil();
        StockDataSet set;
//        CsvToArff converter = new CsvToArff();
//        try {
//                converter.convert("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/5min-CCi/daily");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        set = csv.readFile("F:/dev-workspace/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week1.csv");
        set = set.append(csv.readFile("F:/dev-workspace/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week2.csv"));
        set = set.append(csv.readFile("F:/dev-workspace/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week3.csv"));
        set = set.append(csv.readFile("F:/dev-workspace/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week4.csv"));

//        set = set.append(csv.readFile("F:/dev-workspace/UCZELNIA/praca-mgr/StockWork/src/main/resources/trainData/Feb/EUR_USD_Week5.csv"));

//        StockDataSet set = csv.readFile("F:/dev-workspace/dane/luty/EUR_USD_Week1.csv");
//        set = set.append(csv.readFile("F:/dev-workspace/dane/luty/EUR_USD_Week2.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/luty/EUR_USD_Week3.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/luty/EUR_USD_Week4.csv"));
//        set = csv.readFile("F:/dev-workspace/dane/marzec/EUR_USD_Week1.csv");
//        set = set.append(csv.readFile("F:/dev-workspace/dane/marzec/EUR_USD_Week2.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/marzec/EUR_USD_Week3.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/marzec/EUR_USD_Week4.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/kwiecien/EUR_USD_Week1.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/kwiecien/EUR_USD_Week2.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/kwiecien/EUR_USD_Week3.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/kwiecien/EUR_USD_Week4.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/maj/EUR_USD_Week1.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/maj/EUR_USD_Week2.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/maj/EUR_USD_Week3.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/maj/EUR_USD_Week4.csv"));
//        set = set.append(csv.readFile("F:/dev-workspace/dane/maj/EUR_USD_Week5.csv"));

        TimeSeries ts = new TimeSeriesGenerator().generateTimeSeries(set, 1);

//        buildDailyData(ts);
//        buildWeeklyData(ts);
//        buildMonthlyData(ts);

        buildSingleDocData(ts);
        System.out.println("end");
    }

    private static void buildDailyData(TimeSeries ts) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(ts);
        SMAIndicator sma = new SMAIndicator(closePrice, 20);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd, Decimal.valueOf(2.5));
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd, Decimal.valueOf(2.5));

        RSI rsi = new RSI(Decimal.valueOf(20), Decimal.valueOf(80), closePrice, 16);
        CCI cci = new CCI(ts, 21, Decimal.valueOf(160), Decimal.valueOf(-160));


        EMAIndicator ema = new EMAIndicator(closePrice, 11);
        MACDIndicator macd = new MACDIndicator(ema, 16, 28);

        RsiPremiumStrategy rsiPrem = new RsiPremiumStrategy(rsi, cci, bbu, bbl);
        rsiPrem.setBbMiddle(bbm);
        rsiPrem.setMacd(macd);;
        Map<String, List<Object>> map = rsiPrem.genBestDataSetDaily(5);//rsiPrem.createIndicatorValuesMap(5);

    }

    private static void buildWeeklyData(TimeSeries ts) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(ts);
        SMAIndicator sma = new SMAIndicator(closePrice, 20);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd, Decimal.valueOf(2.5));
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd, Decimal.valueOf(2.5));

        RSI rsi = new RSI(Decimal.valueOf(20), Decimal.valueOf(80), closePrice, 16);
        CCI cci = new CCI(ts, 21, Decimal.valueOf(160), Decimal.valueOf(-160));


        EMAIndicator ema = new EMAIndicator(closePrice, 11);
        MACDIndicator macd = new MACDIndicator(ema, 16, 28);

        RsiPremiumStrategy rsiPrem = new RsiPremiumStrategy(rsi, cci, bbu, bbl);
        rsiPrem.setBbMiddle(bbm);
        rsiPrem.setMacd(macd);
        Map<String, List<Object>> map = rsiPrem.genBestDataSetWeekly(5);//rsiPrem.createIndicatorValuesMap(5);

    }

    private static void buildMonthlyData(TimeSeries ts) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(ts);
        SMAIndicator sma = new SMAIndicator(closePrice, 20);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd, Decimal.valueOf(2.5));
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd, Decimal.valueOf(2.5));

        RSI rsi = new RSI(Decimal.valueOf(20), Decimal.valueOf(80), closePrice, 16);
        CCI cci = new CCI(ts, 21, Decimal.valueOf(160), Decimal.valueOf(-160));


        EMAIndicator ema = new EMAIndicator(closePrice, 11);
        MACDIndicator macd = new MACDIndicator(ema, 16, 28);

        RsiPremiumStrategy rsiPrem = new RsiPremiumStrategy(rsi, cci, bbu, bbl);
        rsiPrem.setBbMiddle(bbm);
        rsiPrem.setMacd(macd);
        Map<String, List<Object>> map = rsiPrem.genBestDataSetMonthly(5);//rsiPrem.createIndicatorValuesMap(5);

    }

    private static void buildSingleDocData(TimeSeries ts) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(ts);
//		RSIIndicator rsi = new RSIIndicator(closePrice, 14);
//		CCIIndicator cci = new CCIIndicator(ts, 20);

        SMAIndicator sma = new SMAIndicator(closePrice, 20);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(sma);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd, Decimal.valueOf(2.8));
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd, Decimal.valueOf(2.2));

        RSI rsi = new RSI(Decimal.valueOf(30), Decimal.valueOf(70), closePrice, 14);
        CCI cci = new CCI(ts, 20, Decimal.valueOf(150), Decimal.valueOf(-150));


        EMAIndicator ema = new EMAIndicator(closePrice, 16);
        MACDIndicator macd = new MACDIndicator(ema, 16, 30);

        RsiPremiumStrategy rsiPrem = new RsiPremiumStrategy(rsi, cci, bbu, bbl);
        rsiPrem.setBbMiddle(bbm);
        rsiPrem.setMacd(macd);
        Map<String, List<Object>> map = rsiPrem.genBestDataSet(30);//rsiPrem.createIndicatorValuesMap(5);
//		csv.writeToFile("C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/generowaneDane", rsi, cci, bbu,bbm, bbl, macd);
//		csv.writeMapToFile("C:/Users/Maciek/Documents/UCZELNIA/praca-mgr/generowaneDane", map);
        csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/30-minCCi", "train.csv", map);
    }

    private static Map<LocalDate, TimeSeries> generateDailyTimeSeries(TimeSeries timeSeries) {
        LinkedHashMap<LocalDate, TimeSeries> timeSeriesMap = new LinkedHashMap<>();
        List<TimeSeries> daily;
        List<DateTimeFormatter> format = new LinkedList<DateTimeFormatter>();
        format.add(DateTimeFormat.forPattern("HH:mm dd/MM/yyyy"));
        Tick tick;
        LocalDate date;
        TimeSeries newTimeSeries;
        for (int i = 0; i < timeSeries.getEnd(); i++) {
            tick = timeSeries.getTick(i);
            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();
            if (timeSeriesMap.containsKey(date)) {
                timeSeriesMap.get(date).addTick(tick);
            } else {
                newTimeSeries = new TimeSeries(date.toString(), Period.minutes(1));
                newTimeSeries.addTick(tick);
                timeSeriesMap.put(date, newTimeSeries);
            }
        }
        return timeSeriesMap;
    }
    public static final WeekFields US_WEEK_FIELDS = WeekFields.of(DayOfWeek.SUNDAY, 4);

    private static List<TimeSeries> generateWeeklyTimeSeries(TimeSeries timeSeries) {
        List<DateTimeFormatter> format = new LinkedList<DateTimeFormatter>();
        format.add(DateTimeFormat.forPattern("HH:mm dd/MM/yyyy"));
        Tick tick;
        LocalDate date;
        for (int i = 0; i < timeSeries.getEnd(); i++) {
            tick = timeSeries.getTick(i);
            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();
            System.out.println(date.getWeekOfWeekyear());
            System.out.println(java.time.LocalDate.of(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth()).get(US_WEEK_FIELDS.weekOfWeekBasedYear()));
            System.out.println(date);
        }

        return new LinkedList<>();
    }

    private List<TimeSeries> generateMonthlyTimeSeries(TimeSeries timeSeries) {

        return new LinkedList<>();
    }

}
