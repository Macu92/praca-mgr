package pl.com.stock.work.strategy;

import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xpath.internal.operations.Bool;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsUpperIndicator;
import lombok.Data;
import lombok.NonNull;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import pl.com.stock.work.indicators.CCI;
import pl.com.stock.work.indicators.RSI;
import pl.com.stock.work.util.DateParser;
import pl.com.stock.work.util.file.CsvUtil;

@Data
public class RsiPremiumStrategy {

    @NonNull
    private RSI rsi;
    @NonNull
    private CCI cci;
    @NonNull
    private BollingerBandsUpperIndicator bbUpper;
    @NonNull
    private BollingerBandsLowerIndicator bbLower;
    private BollingerBandsMiddleIndicator bbMiddle;
    private MACDIndicator macd;

    public Map<String, List<Object>> createIndicatorValuesMap(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> priceCloseValue = new LinkedList<>();
        List<Object> rsiValues = new LinkedList<>();
        List<Object> cciValues = new LinkedList<>();
        List<Object> bbuValues = new LinkedList<>();
        List<Object> bbmValues = new LinkedList<>();
        List<Object> bblValues = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();

        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            priceCloseValue.add(ts.getTick(i).getClosePrice());
            rsiValues.add(rsi.getValue(i));
            cciValues.add(cci.getValue(i));
            bbuValues.add(bbUpper.getValue(i));
            bbmValues.add(bbMiddle.getValue(i));
            bblValues.add(bbLower.getValue(i));
            macdValues.add(macd.getValue(i));
            if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("up");
            } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                    .isLessThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("down");
            } else {
                predictionVal.add("no");
            }
        }

        indicatorsValuesMap.put("CloseValue", priceCloseValue);
        indicatorsValuesMap.put(rsi.getClass().getSimpleName(), rsiValues);
        indicatorsValuesMap.put(cci.getClass().getSimpleName(), cciValues);
        indicatorsValuesMap.put(bbUpper.getClass().getSimpleName(), bbuValues);
        indicatorsValuesMap.put(bbMiddle.getClass().getSimpleName(), bbmValues);
        indicatorsValuesMap.put(bbLower.getClass().getSimpleName(), bblValues);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("Prediction", predictionVal);
        return indicatorsValuesMap;
    }

    public Map<String, List<Object>> createMixedindicatorsMap(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> priceCloseValue = new LinkedList<>();
        List<Object> rsiValues = new LinkedList<>();
        List<Object> rsiOverBoughtOrSoldSignalList = new LinkedList<>();
        List<Object> cciValues = new LinkedList<>();
        List<Object> cciChannelBreakSignalList = new LinkedList<>();
        List<Object> bbuValues = new LinkedList<>();
        List<Object> bbmValues = new LinkedList<>();
        List<Object> bblValues = new LinkedList<>();
        List<Object> bbBreakSignalList = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();

        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            priceCloseValue.add(ts.getTick(i).getClosePrice());
            rsiValues.add(rsi.getValue(i));
            cciValues.add(cci.getValue(i));
            bbuValues.add(bbUpper.getValue(i));
            bbmValues.add(bbMiddle.getValue(i));
            bblValues.add(bbLower.getValue(i));
            macdValues.add(macd.getValue(i));
            if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("up");
            } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                    .isLessThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("down");
            } else {
                predictionVal.add("no");
            }

            rsiOverBoughtOrSoldSignalList.add(makeRsiSignal(rsi.getValue(i)).toString());
            cciChannelBreakSignalList.add(makeCCiSignal(cci.getValue(i)).toString());
            bbBreakSignalList.add(
                    makeBbSignal(bbUpper.getValue(i), bbLower.getValue(i), ts.getTick(i).getClosePrice()).toString());
        }

        indicatorsValuesMap.put("CloseValue", priceCloseValue);
        indicatorsValuesMap.put(rsi.getClass().getSimpleName(), rsiValues);
        indicatorsValuesMap.put(cci.getClass().getSimpleName(), cciValues);
        indicatorsValuesMap.put(bbUpper.getClass().getSimpleName(), bbuValues);
        indicatorsValuesMap.put(bbMiddle.getClass().getSimpleName(), bbmValues);
        indicatorsValuesMap.put(bbLower.getClass().getSimpleName(), bblValues);
        indicatorsValuesMap.put("RSI OVERBOUGHT OR SOLD", rsiOverBoughtOrSoldSignalList);
        indicatorsValuesMap.put("CCI CHANNEL BREAK", cciChannelBreakSignalList);
        indicatorsValuesMap.put("BB BREAK", bbBreakSignalList);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("Prediction", predictionVal);
        return indicatorsValuesMap;
    }

    public Map<String, List<Object>> createIndicatorsSignalsMap(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> priceCloseValue = new LinkedList<>();
        List<Object> rsiValues = new LinkedList<>();
        List<Object> cciValues = new LinkedList<>();
        List<Object> bbValues = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();

        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            priceCloseValue.add(ts.getTick(i).getClosePrice().toString());
            rsiValues.add(makeRsiSignal(rsi.getValue(i)).toString());
            cciValues.add(makeCCiSignal(cci.getValue(i)).toString());
            bbValues.add(
                    makeBbSignal(bbUpper.getValue(i), bbLower.getValue(i), ts.getTick(i).getClosePrice()).toString());
            macdValues.add(macdTrendSignal(macd.getValue(i)));
            if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("up");
            } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                    .isLessThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("down");
            } else {
                predictionVal.add("no");
            }
        }

        indicatorsValuesMap.put("CloseValue", priceCloseValue);
        indicatorsValuesMap.put(rsi.getClass().getSimpleName(), rsiValues);
        indicatorsValuesMap.put(cci.getClass().getSimpleName(), cciValues);
        indicatorsValuesMap.put(bbUpper.getClass().getSimpleName(), bbValues);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("Prediction", predictionVal);
        return indicatorsValuesMap;
    }

    public Map<String, List<Object>> createIndicatorsScaledSignalsMap(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> priceCloseValue = new LinkedList<>();
        List<Object> rsiValues = new LinkedList<>();
        List<Object> cciValues = new LinkedList<>();
        List<Object> bbValues = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();

        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            priceCloseValue.add(ts.getTick(i).getClosePrice().toString());
            rsiValues.add(scaleRsiValue(rsi.getValue(i)).toString());
            cciValues.add(scaleCCiValue(cci.getValue(i)).toString());
            bbValues.add(
                    scaleBBSignal(bbUpper.getValue(i), bbLower.getValue(i), ts.getTick(i).getClosePrice()).toString());
            macdValues.add(scaleMacdValues(macd.getValue(i)));
            if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("up");
            } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                    .isLessThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("down");
            } else {
                predictionVal.add("no");
            }
        }

        indicatorsValuesMap.put("CloseValue", priceCloseValue);
        indicatorsValuesMap.put(rsi.getClass().getSimpleName(), rsiValues);
        indicatorsValuesMap.put(cci.getClass().getSimpleName(), cciValues);
        indicatorsValuesMap.put(bbUpper.getClass().getSimpleName(), bbValues);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("Prediction", predictionVal);
        return indicatorsValuesMap;
    }

    public Map<String, List<Object>> dataSetVol3(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> priceBodyValue = new LinkedList<>();
        List<Object> priceUpperShadow = new LinkedList<>();
        List<Object> priceDownShadow = new LinkedList<>();
        List<Object> isBullish = new LinkedList<>(); //positice candle
//		List<Object> isBearish = new LinkedList<>(); //negative candle
//        List<Object> bbuScaledValues = new LinkedList<>();
//        List<Object> bbmScaledValues = new LinkedList<>();
//        List<Object> bblScaledValues = new LinkedList<>();
//        List<Object> bbuMinusValues = new LinkedList<>();
//        List<Object> bbmMinusValues = new LinkedList<>();
//        List<Object> bblMinusValues = new LinkedList<>();
        List<Object> bbSizeBand = new LinkedList<>();
        List<Object> bbPricePosition = new LinkedList<>();
        List<Object> cciChannelBreakSignalList = new LinkedList<>();
        List<Object> rsiOverBoughtOrSoldSignalList = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledList = new LinkedList<>();
        List<Object> cciBreakScaledList = new LinkedList<>();
        List<Object> bbBreakSignalList = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();
        List<Object> rsiValues = new LinkedList<>();
        List<Object> cciValues = new LinkedList<>();
        List<Object> rsiValuesScaled = new LinkedList<>();
        List<Object> cciValuesScaled = new LinkedList<>();

        TimeSeries ts = rsi.getTimeSeries();
        Tick tick = null;
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            tick = ts.getTick(i);
            priceBodyValue.add(getCandleBody(tick));
            priceUpperShadow.add(getUpShadow(tick));
            priceDownShadow.add(getDownShadow(tick));
            isBullish.add(getBinaryBoolean(tick.isBullish()));
//            bbuScaledValues.add(bbDividedUpValue(bbUpper.getValue(i),tick.getClosePrice()));
//            bbmScaledValues.add(bbDividedMiddleValue(bbMiddle.getValue(i),tick.getClosePrice()));
//            bblScaledValues.add(bbDividedDownValue(bbLower.getValue(i),tick.getClosePrice()));
//            bbuMinusValues.add(bbMinusUpValue(bbUpper.getValue(i),tick.getClosePrice()));
//            bbmMinusValues.add(bbMinusMiddleValue(bbMiddle.getValue(i),tick.getClosePrice()));
//            bblMinusValues.add(bbMinusDownValue(bbLower.getValue(i),tick.getClosePrice()));
            bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
            bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
            macdValues.add(macd.getValue(i));
            rsiValues.add(rsi.getValue(i));
            cciValues.add(cci.getValue(i));
            rsiValuesScaled.add(scaleRsiValue(rsi.getValue(i)).toString());
            cciValuesScaled.add(scaleCCiValue(cci.getValue(i)).toString());
            if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(tick.getClosePrice())) {
                predictionVal.add("up");
            } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                    .isLessThan(tick.getClosePrice())) {
                predictionVal.add("down");
            } else {
                predictionVal.add("no");
            }

            rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
            cciChannelBreakSignalList.add(makeCciScaledSignal(cci.getValue(i), 0.2).toString());
            rsiOverBoughtOrSoldSignalList.add(makeRsiSignal(rsi.getValue(i)).toString());
            cciBreakScaledList.add(makeCCiSignal(cci.getValue(i)).toString());
            bbBreakSignalList.add(
                    makeBbSignal(bbUpper.getValue(i), bbLower.getValue(i), tick.getClosePrice()));
        }


        indicatorsValuesMap.put("Candle body", priceBodyValue);
        indicatorsValuesMap.put("Upper shadow", priceUpperShadow);
        indicatorsValuesMap.put("Lower shadow", priceDownShadow);
        indicatorsValuesMap.put("is Bullish", isBullish);
//        indicatorsValuesMap.put("BBUScaled", bbuScaledValues);
//        indicatorsValuesMap.put("BBMScaled", bbmScaledValues);
//        indicatorsValuesMap.put("BBLScaled", bblScaledValues);
//        indicatorsValuesMap.put("BBUMinus", bbuMinusValues);
//        indicatorsValuesMap.put("BBMMinus", bbmMinusValues);
//        indicatorsValuesMap.put("BBLMinus", bblMinusValues);
        indicatorsValuesMap.put("BB BREAK SCALED", bbBreakSignalList);
        indicatorsValuesMap.put("RSI", rsiValues);
        indicatorsValuesMap.put("RSI SCALED", rsiValuesScaled);
        indicatorsValuesMap.put("RSI EXTREME TOUCHED", rsiOverBoughtOrSoldSignalList);
        indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
        indicatorsValuesMap.put("CCI", cciValues);
        indicatorsValuesMap.put("CCI SCALED", cciValuesScaled);
        indicatorsValuesMap.put("CCI CHANNEL BREAK", cciChannelBreakSignalList);
        indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
        indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
        indicatorsValuesMap.put("Prediction", predictionVal);
        return indicatorsValuesMap;
    }

    public Map<String, List<Object>> genBestDataSet(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
//        List<Object> bbuValues = new LinkedList<>();
//        List<Object> bbmValues = new LinkedList<>();
//        List<Object> bblValues = new LinkedList<>();
        List<Object> bbSizeBand = new LinkedList<>();
        List<Object> bbPricePosition = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledList = new LinkedList<>();
        List<Object> cciBreakScaledList = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();
//        List<Object> rsiValues = new LinkedList<>();
//        List<Object> cciValues = new LinkedList<>();

        TimeSeries ts = rsi.getTimeSeries();
        Tick tick = null;
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            tick = ts.getTick(i);
//            bbuValues.add(bbUpper.getValue(i));
//            bbmValues.add(bbMiddle.getValue(i));
//            bblValues.add(bbLower.getValue(i));
            macdValues.add(macd.getValue(i));
//            rsiValues.add(rsi.getValue(i));
//            cciValues.add(cci.getValue(i));
            if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("up");
            } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                    .isLessThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("down");
            } else {
                predictionVal.add("no");
            }
            bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
            bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
            rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
            cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());
        }

//        indicatorsValuesMap.put("BBU", bbuValues);
//        indicatorsValuesMap.put("BBM", bbmValues);
//        indicatorsValuesMap.put("BBL", bblValues);
//        indicatorsValuesMap.put("RSI", rsiValues);
        indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
//        indicatorsValuesMap.put("CCI", cciValues);
        indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
        indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
        indicatorsValuesMap.put("Prediction", predictionVal);
        return indicatorsValuesMap;
    }


    public Map<String, List<Object>> genBestDataSetfor3(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> bbSizeBand = new LinkedList<>();
        List<Object> bbPricePosition = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledList = new LinkedList<>();
        List<Object> cciBreakScaledList = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();


        List<Object> bbSizeBandDaily = new LinkedList<>();
        List<Object> bbPricePositionDaily = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledListDaily = new LinkedList<>();
        List<Object> cciBreakScaledListDaily = new LinkedList<>();
        List<Object> macdValuesDaily = new LinkedList<>();
        List<Object> predictionValDaily = new LinkedList<>();


        List<Object> bbSizeBandWeekly = new LinkedList<>();
        List<Object> bbPricePositionWeekly = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledListWeekly = new LinkedList<>();
        List<Object> cciBreakScaledListWeekly = new LinkedList<>();
        List<Object> macdValuesWeekly = new LinkedList<>();
        List<Object> predictionValWeekly = new LinkedList<>();


        List<Object> bbSizeBandMonthly = new LinkedList<>();
        List<Object> bbPricePositionMonthly = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledListMonthly = new LinkedList<>();
        List<Object> cciBreakScaledListMonthly = new LinkedList<>();
        List<Object> macdValuesMonthly = new LinkedList<>();
        List<Object> predictionValMonthly = new LinkedList<>();



        LinkedHashMap<LocalDate, TimeSeries> timeSeriesMapDaily = new LinkedHashMap<>();
        LinkedHashMap<Integer, TimeSeries> timeSeriesMapMonthly = new LinkedHashMap<>();
        LocalDate date = null;
        Tick tick;
        List<DateTimeFormatter> format = new LinkedList<DateTimeFormatter>();
        format.add(DateTimeFormat.forPattern("HH:mm dd/MM/yyyy"));
        CsvUtil csv = new CsvUtil();
        String weeknum;
        WeekFields US_WEEK_FIELDS = WeekFields.of(DayOfWeek.SUNDAY, 4);

        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            tick = ts.getTick(i);

            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();
            if (!timeSeriesMapDaily.containsKey(date)) {
                Map<String, List<Object>> indicatorsMap = new LinkedHashMap<>();
                timeSeriesMapDaily.put(date, new TimeSeries(Period.minutes(1)));
                indicatorsMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
                indicatorsMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
                indicatorsMap.put(macd.getClass().getSimpleName(), macdValues);
                indicatorsMap.put("bbSizeBand", bbSizeBand);
                indicatorsMap.put("bbPricePosition", bbPricePosition);
                indicatorsMap.put("Prediction", predictionVal);
                if (timeSeriesMapDaily.size() > 0 && timeSeriesMapDaily.containsKey(date.minusDays(1))) {
                    csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/30-minCCi/daily", date.minusDays(1).toString() + ".csv", indicatorsValuesMap);
                }
                rsiExtremeSignalScaledList.clear();
                macdValues.clear();
                predictionVal.clear();
                bbSizeBand.clear();
                bbPricePosition.clear();
            }

//            weeknum = String.valueOf(java.time.LocalDate.of(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth()).get(US_WEEK_FIELDS.weekOfWeekBasedYear()));
//            if (timeSeriesMap.containsKey(weeknum)){
//
//            }

            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();
            if (timeSeriesMapMonthly.containsKey(date.getMonthOfYear())){
                timeSeriesMapMonthly.put(date.getMonthOfYear(), new TimeSeries(Period.minutes(1)));
                indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
                indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
                indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
                indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
                indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
                indicatorsValuesMap.put("Prediction", predictionVal);
                if (timeSeriesMapMonthly.size() > 0 && timeSeriesMapMonthly.containsKey(date.getMonthOfYear()-1)) {
                    csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/30-minCCi/monthly", date.minusDays(1).toString() + ".csv", indicatorsValuesMap);
                }
                rsiExtremeSignalScaledList.clear();
                macdValues.clear();
                predictionVal.clear();
                indicatorsValuesMap = new LinkedHashMap<>();
            }

            macdValues.add(macd.getValue(i));
            if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("up");
            } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                    .isLessThan(ts.getTick(i).getClosePrice())) {
                predictionVal.add("down");
            } else {
                predictionVal.add("no");
            }
            bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
            bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
            rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
            cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());

            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();

        }

        indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
        indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
        indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
        indicatorsValuesMap.put("Prediction", predictionVal);
        return indicatorsValuesMap;
    }



    public Map<String, List<Object>> genBestDataSetDaily(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> bbSizeBand = new LinkedList<>();
        List<Object> bbPricePosition = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledList = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> cciBreakScaledList = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();

        LinkedHashMap<LocalDate, TimeSeries> timeSeriesMap = new LinkedHashMap<>();
        LocalDate date = null;
        Tick tick;
        List<DateTimeFormatter> format = new LinkedList<DateTimeFormatter>();
        format.add(DateTimeFormat.forPattern("HH:mm dd/MM/yyyy"));
        CsvUtil csv = new CsvUtil();
        LocalDate last = LocalDate.now();
        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            tick = ts.getTick(i);
            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();
            if (timeSeriesMap.containsKey(date)) {
                last = date;
                macdValues.add(macd.getValue(i));
                if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("up");
                } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                        .isLessThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("down");
                } else {
                    predictionVal.add("no");
                }

                rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
                bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
                bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
                cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());
                 } else {
                indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
                indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
                indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
                indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
                indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
                indicatorsValuesMap.put("Prediction", predictionVal);
                if (timeSeriesMap.size() > 0 && timeSeriesMap.containsKey(last)) {
                    csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/1h-CCi/daily", last.toString() + ".csv", indicatorsValuesMap);
                }
                timeSeriesMap.put(date, new TimeSeries(Period.minutes(1)));
                rsiExtremeSignalScaledList.clear();
                macdValues.clear();
                predictionVal.clear();
                cciBreakScaledList.clear();
                bbSizeBand.clear();
                bbPricePosition.clear();
                indicatorsValuesMap = new LinkedHashMap<>();

                macdValues.add(macd.getValue(i));
                if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("up");
                } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                        .isLessThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("down");
                } else {
                    predictionVal.add("no");
                }

                cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());
                rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
                bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
                bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
            }

        }
        indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
        indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
        indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
        indicatorsValuesMap.put("Prediction", predictionVal);
        csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/1h-CCi/daily", last.toString() + ".csv", indicatorsValuesMap);
        return indicatorsValuesMap;
    }


    public Map<String, List<Object>> genBestDataSetWeekly(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> bbSizeBand = new LinkedList<>();
        List<Object> bbPricePosition = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledList = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();

        List<Object> cciBreakScaledList = new LinkedList<>();

        WeekFields US_WEEK_FIELDS = WeekFields.of(DayOfWeek.SUNDAY, 4);
        LinkedHashMap<String, TimeSeries> timeSeriesMap = new LinkedHashMap<>();
        LocalDate date = null;
        Tick tick;
        List<DateTimeFormatter> format = new LinkedList<DateTimeFormatter>();
        format.add(DateTimeFormat.forPattern("HH:mm dd/MM/yyyy"));
        CsvUtil csv = new CsvUtil();
        String weeknum;

        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            tick = ts.getTick(i);

            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();
            weeknum = String.valueOf(java.time.LocalDate.of(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth()).get(US_WEEK_FIELDS.weekOfWeekBasedYear()));
            if (timeSeriesMap.containsKey(weeknum)){
                macdValues.add(macd.getValue(i));
                if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("up");
                } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                        .isLessThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("down");
                } else {
                    predictionVal.add("no");
                }
                cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());
                rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
                bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
                bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
            } else {
                timeSeriesMap.put(String.valueOf(java.time.LocalDate.of(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth()).get(US_WEEK_FIELDS.weekOfWeekBasedYear())), new TimeSeries(Period.minutes(1)));
                indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
                indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
                indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
                indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
                indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
                indicatorsValuesMap.put("Prediction", predictionVal);
                if (timeSeriesMap.size() > 0 && timeSeriesMap.containsKey(String.valueOf(Integer.valueOf(weeknum)-1))) {
                    csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/1h-CCi/weekly", date.minusDays(1).toString() + ".csv", indicatorsValuesMap);
                }
                rsiExtremeSignalScaledList.clear();
                macdValues.clear();
                predictionVal.clear();
                cciBreakScaledList.clear();
                bbSizeBand.clear();
                bbPricePosition.clear();
                indicatorsValuesMap = new LinkedHashMap<>();

                macdValues.add(macd.getValue(i));
                if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("up");
                } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                        .isLessThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("down");
                } else {
                    predictionVal.add("no");
                }
                cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());
                rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
                bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
                bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
            }

        }
        indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
        indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
        indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
        indicatorsValuesMap.put("Prediction", predictionVal);
        csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/1h-CCi/weekly", date.toString() + ".csv", indicatorsValuesMap);
        return indicatorsValuesMap;
    }

    public Map<String, List<Object>> genBestDataSetMonthly(int predictionTimeMinutes) {
        Map<String, List<Object>> indicatorsValuesMap = new LinkedHashMap<>();
        List<Object> bbSizeBand = new LinkedList<>();
        List<Object> bbPricePosition = new LinkedList<>();
        List<Object> rsiExtremeSignalScaledList = new LinkedList<>();
        List<Object> macdValues = new LinkedList<>();
        List<Object> predictionVal = new LinkedList<>();
        List<Object> cciBreakScaledList = new LinkedList<>();


        WeekFields US_WEEK_FIELDS = WeekFields.of(DayOfWeek.SUNDAY, 4);
        LinkedHashMap<Integer, TimeSeries> timeSeriesMap = new LinkedHashMap<>();
        LocalDate date = null;
        Tick tick;
        List<DateTimeFormatter> format = new LinkedList<DateTimeFormatter>();
        format.add(DateTimeFormat.forPattern("HH:mm dd/MM/yyyy"));
        CsvUtil csv = new CsvUtil();
        String weeknum;

        TimeSeries ts = rsi.getTimeSeries();
        for (int i = 0; i < ts.getEnd() - predictionTimeMinutes; i++) {
            tick = ts.getTick(i);

            date = new DateParser().parseDate(tick.getDateName(), format).toLocalDate();
            if (timeSeriesMap.containsKey(date.getMonthOfYear())){
                macdValues.add(macd.getValue(i));
                if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("up");
                } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                        .isLessThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("down");
                } else {
                    predictionVal.add("no");
                }

                rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
                bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
                bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
                cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());
            } else {
                timeSeriesMap.put(date.getMonthOfYear(), new TimeSeries(Period.minutes(1)));
                indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
                indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
                indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
                indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
                indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
                indicatorsValuesMap.put("Prediction", predictionVal);
                if (timeSeriesMap.size() > 0 && timeSeriesMap.containsKey(date.getMonthOfYear()-1)) {
                    csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/1h-CCi/monthly", date.minusDays(1).toString() + ".csv", indicatorsValuesMap);
                }
                rsiExtremeSignalScaledList.clear();
                cciBreakScaledList.clear();
                bbPricePosition.clear();
                bbSizeBand.clear();
                macdValues.clear();
                predictionVal.clear();
                indicatorsValuesMap = new LinkedHashMap<>();

                macdValues.add(macd.getValue(i));
                if (ts.getTick(i + predictionTimeMinutes).getClosePrice().isGreaterThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("up");
                } else if (ts.getTick(i + predictionTimeMinutes).getClosePrice()
                        .isLessThan(ts.getTick(i).getClosePrice())) {
                    predictionVal.add("down");
                } else {
                    predictionVal.add("no");
                }

                cciBreakScaledList.add(makeCciScaledSignal(cci.getValue(i),0.2).toString());
                rsiExtremeSignalScaledList.add(makeRsiScaledSignal(rsi.getValue(i), 0.2).toString());
                bbSizeBand.add(bbBandSize(bbUpper.getValue(i),bbLower.getValue(i)));
                bbPricePosition.add(bbWithScaledPricePosition(bbUpper.getValue(i),bbLower.getValue(i),bbMiddle.getValue(i),tick.getClosePrice()));
            }
        }
        indicatorsValuesMap.put("RSI EXTREME SCALED", rsiExtremeSignalScaledList);
        indicatorsValuesMap.put("CCI CHANNEL BREAK SCALED", cciBreakScaledList);
        indicatorsValuesMap.put(macd.getClass().getSimpleName(), macdValues);
        indicatorsValuesMap.put("bbSizeBand", bbSizeBand);
        indicatorsValuesMap.put("bbPricePosition", bbPricePosition);
        indicatorsValuesMap.put("Prediction", predictionVal);
        csv.writeMapToFile("F:/dev-workspace/UCZELNIA/praca-mgr/generowaneDane/POPRAWIONE/1h-CCi/monthly", date.toString() + ".csv", indicatorsValuesMap);
        return indicatorsValuesMap;
    }


    //for(int i=0; i<timeSeries.getEnd(); i++){
//		tick = timeSeries.getTick(i);
//		date = new DateParser().parseDate(tick.getDateName(),format).toLocalDate();
//		if(timeSeriesMap.containsKey(date)){
//			timeSeriesMap.get(date).addTick(tick);
//		}else{
//			newTimeSeries = new TimeSeries(date.toString(), Period.minutes(1));
//			newTimeSeries.addTick(tick);
//			timeSeriesMap.put(date,newTimeSeries);
//		}
//	}

    private Integer makeRsiSignal(Decimal rsiValue) {
        if (rsiValue.isGreaterThanOrEqual(rsi.getOverBoughtLevel())) {
            return 1;
        } else if (rsiValue.isLessThanOrEqual(rsi.getOverSoldLevel())) {
            return -1;
        } else {
            return 0;
        }
    }

    private Integer makeRsiScaledSignal(Decimal rsiValue, Double scale) {
        if (rsiValue.isGreaterThanOrEqual(rsi.getOverBoughtLevel())) {
            if (rsiValue.isGreaterThanOrEqual(rsi.getOverBoughtLevel().plus(rsi.getOverBoughtLevel().multipliedBy(Decimal.valueOf(scale))))) {
                return 2;
            } else if (rsiValue.isGreaterThanOrEqual(rsi.getOverBoughtLevel().plus(rsi.getOverBoughtLevel().multipliedBy(Decimal.valueOf(scale * 2))))) {
                return 3;
            } else if (rsiValue.isGreaterThanOrEqual(rsi.getOverBoughtLevel().plus(rsi.getOverBoughtLevel().multipliedBy(Decimal.valueOf(scale * 3))))) {
                return 4;
            } else if (rsiValue.isGreaterThanOrEqual(rsi.getOverBoughtLevel().plus(rsi.getOverBoughtLevel().multipliedBy(Decimal.valueOf(scale * 4))))) {
                return 5;
            } else {
                return 1;
            }
        } else if (rsiValue.isLessThanOrEqual(rsi.getOverSoldLevel())) {
            if (rsiValue.isLessThanOrEqual(rsi.getOverSoldLevel().minus(rsi.getOverSoldLevel().multipliedBy(Decimal.valueOf(scale))))) {
                return -2;
            } else if (rsiValue.isLessThanOrEqual(rsi.getOverSoldLevel().minus(rsi.getOverSoldLevel().multipliedBy(Decimal.valueOf(scale * 2))))) {
                return -3;
            } else if (rsiValue.isLessThanOrEqual(rsi.getOverSoldLevel().minus(rsi.getOverSoldLevel().multipliedBy(Decimal.valueOf(scale * 3))))) {
                return -4;
            } else if (rsiValue.isLessThanOrEqual(rsi.getOverSoldLevel().minus(rsi.getOverSoldLevel().multipliedBy(Decimal.valueOf(scale * 4))))) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }

    private Integer makeCCiSignal(Decimal rsiValue) {
        if (rsiValue.isGreaterThanOrEqual(cci.getHighMeasuredAverageLevel())) {
            return 1;
        } else if (rsiValue.isLessThanOrEqual(cci.getLowMeasuredAverageLevel())) {
            return -1;
        } else {
            return 0;
        }
    }

    private Integer makeCciScaledSignal(Decimal cciValue, Double scale) {
        if (cciValue.isGreaterThanOrEqual(cci.getHighMeasuredAverageLevel())) {
            if (cciValue.isGreaterThanOrEqual(cci.getHighMeasuredAverageLevel().plus(cci.getHighMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale))))) {
                return 2;
            } else if (cciValue.isGreaterThanOrEqual(cci.getHighMeasuredAverageLevel().plus(cci.getHighMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale * 2))))) {
                return 3;
            } else if (cciValue.isGreaterThanOrEqual(cci.getHighMeasuredAverageLevel().plus(cci.getHighMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale * 3))))) {
                return 4;
            } else if (cciValue.isGreaterThanOrEqual(cci.getHighMeasuredAverageLevel().plus(cci.getHighMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale * 4))))) {
                return 5;
            } else {
                return 1;
            }
        } else if (cciValue.isLessThanOrEqual(cci.getLowMeasuredAverageLevel())) {
            if (cciValue.isLessThanOrEqual(cci.getLowMeasuredAverageLevel().minus(cci.getLowMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale))))) {
                return -2;
            } else if (cciValue.isLessThanOrEqual(cci.getLowMeasuredAverageLevel().minus(cci.getLowMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale * 2))))) {
                return -3;
            } else if (cciValue.isLessThanOrEqual(cci.getLowMeasuredAverageLevel().minus(cci.getLowMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale * 3))))) {
                return -4;
            } else if (cciValue.isLessThanOrEqual(cci.getLowMeasuredAverageLevel().minus(cci.getLowMeasuredAverageLevel().multipliedBy(Decimal.valueOf(scale * 4))))) {
                return -5;
            }
            return -1;
        } else {
            return 0;
        }
    }

    private Integer makeBbSignal(Decimal bbuValue, Decimal bblValue, Decimal priceValue) {
        if (priceValue.isGreaterThanOrEqual(bbuValue.minus(bbuValue.multipliedBy(Decimal.valueOf(0.05))))) {
            return 1;
        } else if (priceValue.isLessThanOrEqual(bblValue.plus(bblValue.multipliedBy(Decimal.valueOf(0.05))))) {
            return -1;
        } else {
            return 0;
        }
    }

    private Integer makeScaledBbSignal(Decimal bbuValue, Decimal bblValue, Decimal priceValue) {
        if (priceValue.isGreaterThan(bbuValue.plus(bbuValue.multipliedBy(Decimal.valueOf(0.0001))))) {
            return 2;
        } else if (priceValue.isGreaterThan(bbuValue.plus(bbuValue.multipliedBy(Decimal.valueOf(0.0002))))) {
            return 3;
        } else if (priceValue.isGreaterThan(bbuValue.plus(bbuValue.multipliedBy(Decimal.valueOf(0.0003))))) {
            return 4;
        } else if (priceValue.isGreaterThan(bbuValue.plus(bbuValue.multipliedBy(Decimal.valueOf(0.0004))))) {
            return 5;
        } else if (priceValue.isLessThanOrEqual(bblValue.minus(bblValue.multipliedBy(Decimal.valueOf(0.0001))))) {
            return -2;
        } else if (priceValue.isLessThanOrEqual(bblValue.minus(bblValue.multipliedBy(Decimal.valueOf(0.0002))))) {
            return -3;
        } else if (priceValue.isLessThanOrEqual(bblValue.minus(bblValue.multipliedBy(Decimal.valueOf(0.0003))))) {
            return -4;
        } else if (priceValue.isLessThanOrEqual(bblValue.minus(bblValue.multipliedBy(Decimal.valueOf(0.0004))))) {
            return -5;
        } else {
            return 0;
        }
    }

    private Integer macdTrendSignal(Decimal macdVal) {
        if (macdVal.isGreaterThan(Decimal.valueOf(new Double(0.00005)))) {
            return 1;
        } else if (macdVal.isLessThan(Decimal.valueOf(new Double(-0.00005)))) {
            return -1;
        } else {
            return 0;
        }
    }

    private Integer scaleRsiValue(Decimal rsiValue) {
        if (rsiValue.isLessThanOrEqual(Decimal.valueOf("20"))) {
            return 1;
        } else if (rsiValue.isLessThanOrEqual(Decimal.valueOf("40")) && rsiValue.isGreaterThan(Decimal.valueOf("20"))) {
            return 2;
        } else if (rsiValue.isLessThanOrEqual(Decimal.valueOf("60")) && rsiValue.isGreaterThan(Decimal.valueOf("40"))) {
            return 3;
        } else if (rsiValue.isLessThanOrEqual(Decimal.valueOf("80")) && rsiValue.isGreaterThan(Decimal.valueOf("60"))) {
            return 4;
        } else if (rsiValue.isLessThanOrEqual(Decimal.valueOf("100"))
                && rsiValue.isGreaterThan(Decimal.valueOf("80"))) {
            return 5;
        }
        return 0;
    }

    private Integer scaleCCiValue(Decimal rsiValue) { // 5>300 4>200 3>150 2>100
        // 1>50 50>0>-50
        if (rsiValue.isGreaterThanOrEqual(Decimal.valueOf(300))) {
            return 5;
        } else if (rsiValue.isGreaterThanOrEqual(Decimal.valueOf(200)) && rsiValue.isLessThan(Decimal.valueOf(300))) {
            return 4;
        } else if (rsiValue.isGreaterThanOrEqual(Decimal.valueOf(150)) && rsiValue.isLessThan(Decimal.valueOf(200))) {
            return 3;
        } else if (rsiValue.isGreaterThanOrEqual(Decimal.valueOf(100)) && rsiValue.isLessThan(Decimal.valueOf(150))) {
            return 2;
        } else if (rsiValue.isGreaterThanOrEqual(Decimal.valueOf(50)) && rsiValue.isLessThan(Decimal.valueOf(100))) {
            return 1;
        } else if (rsiValue.isGreaterThan(Decimal.valueOf(-100)) && rsiValue.isLessThanOrEqual(Decimal.valueOf(-50))) {
            return -1;
        } else if (rsiValue.isGreaterThan(Decimal.valueOf(-150)) && rsiValue.isLessThanOrEqual(Decimal.valueOf(-100))) {
            return -2;
        } else if (rsiValue.isGreaterThan(Decimal.valueOf(-200)) && rsiValue.isLessThanOrEqual(Decimal.valueOf(-150))) {
            return -3;
        } else if (rsiValue.isGreaterThan(Decimal.valueOf(-300)) && rsiValue.isLessThanOrEqual(Decimal.valueOf(-200))) {
            return -4;
        } else if (rsiValue.isLessThanOrEqual(Decimal.valueOf(-300))) {
            return -5;
        } else {
            return 0;
        }
    }

    private Integer scaleBBSignal(Decimal bbuValue, Decimal bblValue, Decimal priceValue) {
        if (priceValue.minus(bbuValue).isGreaterThan(Decimal.valueOf(0.0005))) {
            return 2;
        } else if (priceValue.minus(bbuValue).isGreaterThan(Decimal.valueOf(0.0001))
                && priceValue.minus(bbuValue).isLessThan(Decimal.valueOf(0.0005))) {
            return 1;
        } else if (priceValue.minus(bblValue).isLessThanOrEqual(Decimal.valueOf(-0.0001))
                && priceValue.minus(bblValue).isGreaterThan(Decimal.valueOf(-0.0005))) {
            return -1;
        } else if (priceValue.minus(bblValue).isLessThanOrEqual(Decimal.valueOf(-0.0005))) {
            return -2;
        } else {
            return 0;
        }
    }

    private Decimal bbDividedUpValue (Decimal bbuValue, Decimal priceValue) {
        return bbuValue.dividedBy(priceValue);
    }

    private Decimal bbDividedMiddleValue (Decimal bbmValue, Decimal priceValue) {
        return bbmValue.dividedBy(priceValue);
    }

    private Decimal bbDividedDownValue ( Decimal bblValue, Decimal priceValue) {
        return bblValue.dividedBy(priceValue);
    }

    private Decimal bbMinusUpValue (Decimal bbuValue, Decimal priceValue) {
        return priceValue.minus(bbuValue);
    }

    private Decimal bbMinusMiddleValue (Decimal bbmValue, Decimal priceValue) {
        return priceValue.minus(bbmValue);
    }

    private Decimal bbMinusDownValue ( Decimal bblValue, Decimal priceValue) {
        return priceValue.dividedBy(bblValue);
    }

    private Decimal bbBandSize (Decimal bbuValue,Decimal bblValue){
        return bbuValue.minus(bblValue);
    }

    private Decimal bbWithScaledPricePosition(Decimal bbuValue,Decimal bblValue, Decimal bbmValue,Decimal price){
        if(price.isGreaterThanOrEqual(bbmValue)){
            Decimal priceBand = price.minus(bbmValue);
            Decimal upperBand = bbuValue.minus(bbmValue);
            Decimal pricePosition = priceBand.dividedBy(upperBand);
            return pricePosition.multipliedBy(Decimal.valueOf(100));
        }else {
            Decimal priceBand = bbmValue.minus(price);
            Decimal lowerBand = bbmValue.minus(bblValue);
            Decimal pricePosition = priceBand.dividedBy(lowerBand);
            return pricePosition.multipliedBy(Decimal.valueOf(-100));
        }
    }

    private Integer scaleMacdValues(Decimal macdVal) { // 3>0.0001 2>0.000075
        // 1>0.00005 0
        if (macdVal.isGreaterThanOrEqual(Decimal.valueOf(0.0001))) {
            return 3;
        } else if (macdVal.isGreaterThanOrEqual(Decimal.valueOf(0.000075))
                && macdVal.isLessThan(Decimal.valueOf(0.0001))) {
            return 2;
        } else if (macdVal.isGreaterThanOrEqual(Decimal.valueOf(0.00005))
                && macdVal.isLessThan(Decimal.valueOf(0.000075))) {
            return 1;
        } else if (macdVal.isGreaterThan(Decimal.valueOf(-0.000075))
                && macdVal.isLessThanOrEqual(Decimal.valueOf(-0.00005))) {
            return -1;
        } else if (macdVal.isGreaterThan(Decimal.valueOf(-0.0001))
                && macdVal.isLessThanOrEqual(Decimal.valueOf(-0.000075))) {
            return -2;
        } else if (macdVal.isLessThanOrEqual(Decimal.valueOf(-0.0001))) {
            return -3;
        } else {
            return 0;
        }
    }

    private Decimal getCandleBody(Tick tick) {
        return tick.getOpenPrice().minus(tick.getClosePrice()).abs();
    }

    private Decimal getUpShadow(Tick tick) {
        return tick.getMaxPrice().minus(tick.getOpenPrice()).abs();
    }

    private Decimal getDownShadow(Tick tick) {
        return tick.getMinPrice().minus(tick.getClosePrice()).abs();
    }

    private Integer getBinaryBoolean(boolean bool) {
        return Boolean.valueOf(bool).compareTo(true);
    }


}
