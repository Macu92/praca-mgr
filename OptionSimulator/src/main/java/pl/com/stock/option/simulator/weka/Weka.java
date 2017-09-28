package pl.com.stock.option.simulator.weka;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Weka {
    final private static Logger log = Logger.getLogger(Weka.class.getName());

    RandomForest randomForestModel;
    Instances dataSetToSimulation;

    public Weka(String modelPath,String arffFilePath) {
        loadModel(modelPath);
        loadArffData(arffFilePath);
        log.info("Data loaded");

    }

    public List<ClassifiedInstanceModel> classifyInstances(){
        List<ClassifiedInstanceModel> classifiedInstanceModels = new LinkedList<>();
        try {
            for(Instance instance : dataSetToSimulation.subList(0, dataSetToSimulation.size()-1)){
                double v = randomForestModel.classifyInstance(instance);
                classifiedInstanceModels.add(new ClassifiedInstanceModel(instance,dataSetToSimulation.classAttribute().value((int)v).toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classifiedInstanceModels;
    }

    private void loadModel(String pathToModelFile){
        try {
            randomForestModel = (RandomForest) SerializationHelper.read((new FileInputStream(pathToModelFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadArffData(String pathToDataFile) {
        DataSource source = null;
        try {
            source = new DataSource(pathToDataFile);
            dataSetToSimulation = source.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (dataSetToSimulation.classIndex() == -1)
            dataSetToSimulation.setClassIndex(dataSetToSimulation.numAttributes() - 1);
    }
}
