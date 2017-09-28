package pl.com.stock.option.simulator.util.file;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CsvToArff {

    public void     convert(String folderPath) throws IOException {
        FastVector atts = new FastVector();
        atts.addElement("up");
        atts.addElement("down");
        atts.addElement("no");
        for (File file : getAllFilesPathsInFolder(new File(folderPath))) {
            CSVLoader loader = new CSVLoader();
            loader.setSource(file);
            Instances data = loader.getDataSet();//get instances object

            Attribute attr = new Attribute(data.attribute(data.numAttributes()-1).name(),atts);

//            Attribute att = data.attribute(data.numAttributes()-1);
//            data.renameAttributeValue(att,att.value(0),"up");
//            data.renameAttributeValue(att,att.value(1),"down");
//            data.renameAttributeValue(att,att.value(2),"no");

            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);//set the dataset we want to convert
            //and save as ARFF
            File arrf = new File(getFilePathWithoutExtension(file.getPath()) + ".arff");
            saver.setFile(arrf);
            saver.writeBatch();
            List<String> lines = Files.readAllLines(arrf.toPath());
            lines.set(7,"@attribute Prediction {down,up,no}");
            Files.write(arrf.toPath(), lines);
        }
    }

    private String getFilePathWithoutExtension(String path) {
        Integer dotIndex = path.lastIndexOf(".");
        return path.subSequence(0, dotIndex).toString();
    }

    private List<File> getAllFilesPathsInFolder(File folder) {
        if (folder.isDirectory()) {
            List<File> results = new ArrayList<>();
            File[] files = folder.listFiles();

            for (File file : files) {
                if (file.isFile()&&file.getPath().contains(".csv")) {
                    results.add(file);
                }

            }
            return results;
        }
        return null;
    }
}
