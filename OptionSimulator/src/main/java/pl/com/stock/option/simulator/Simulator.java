package pl.com.stock.option.simulator;

import pl.com.stock.option.simulator.util.file.CsvUtil;
import pl.com.stock.option.simulator.weka.ClassifiedInstanceModel;
import pl.com.stock.option.simulator.weka.Weka;

import java.util.List;
import java.util.logging.Logger;

public class Simulator {
    final private static Logger log = Logger.getLogger(Simulator.class.getName());

    public static void main(String[] args) {

        Weka weka = new Weka("F:\\dev-workspace\\UCZELNIA\\praca-mgr\\generowaneDane\\POPRAWIONE\\1h-CCi\\result3m.model",
                "F:\\dev-workspace\\UCZELNIA\\praca-mgr\\generowaneDane\\POPRAWIONE\\1h-CCi\\monthly\\2017-05-31.arff");

        List<ClassifiedInstanceModel> instances = weka.classifyInstances();
        SimulationAsset simulationAsset = new SimulationAsset(10000.0,10.0,90.0);
        AssetModel assetModel = simulationAsset.calculateInvestmentAsset(instances);
        new CsvUtil().writeAssetModelToFile("F:\\dev-workspace\\UCZELNIA\\praca-mgr\\generowaneDane\\POPRAWIONE\\Symulacje-wyniki","1h-sim-result-mo2.csv",assetModel);
        log.info("DONE");
    }
}
