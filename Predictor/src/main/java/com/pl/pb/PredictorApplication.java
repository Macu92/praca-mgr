//package com.pl.pb.predictor;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//
//import com.pl.pb.predictor.storage.StorageProperties;
//import com.pl.pb.predictor.storage.StorageService;
//
//import weka.classifiers.evaluation.Evaluation;
//import weka.classifiers.meta.FilteredClassifier;
//import weka.classifiers.trees.J48;
//import weka.core.Attribute;
//import weka.core.DenseInstance;
//import weka.core.Instances;
//import weka.filters.Filter;
//import weka.filters.unsupervised.attribute.Discretize;
//import weka.filters.unsupervised.attribute.NominalToBinary;
//import weka.filters.unsupervised.attribute.Remove;
//import weka.filters.unsupervised.attribute.Standardize;
//
//@SpringBootApplication
//@EnableConfigurationProperties(StorageProperties.class)
//public class PredictorApplication {
//
//	public static void main(String[] args) {
////		SpringApplication.run(PredictorApplication.class, args);
//		try {
//			Zad2b();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Bean
//	CommandLineRunner init(StorageService storageService) {
//		return (args) -> {
//            storageService.deleteAll();
//            storageService.init();
//		};
//	}
//
//		private static void Zad1a() throws Exception{
//			BufferedReader reader = new BufferedReader(new FileReader("D://data//iris.arff"));
//			Instances data = new Instances(reader);
//			reader.close();
//			data.setClassIndex(data.numAttributes() - 1);
//
//			Remove filter = new Remove();
//	        // Filtrowanie po indeksach atrybutów
//	        filter.setAttributeIndices("2,3");
//	        filter.setInputFormat(data);
//	        Instances filteredData = Filter.useFilter(data, filter);
//
//	        J48 tree = new J48();
//	        tree.buildClassifier(filteredData);
//
//
//			Attribute first = new Attribute("sepallength");
//			Attribute second = new Attribute("sepalwidth");
//			Attribute third = new Attribute("petallength");
//			Attribute fourth = new Attribute("petalwidth");
//			List<String> classAtt = new ArrayList<String>();
//			classAtt.add("Iris-setosa");
//			classAtt.add("Iris-versicolor");
//			classAtt.add("Iris-virginica");
//			Attribute cls = new Attribute("class", classAtt);
//			ArrayList<Attribute> irisAttributes = new ArrayList<Attribute>();
//			irisAttributes.add(first);
//			irisAttributes.add(second);
//			irisAttributes.add(third);
//			irisAttributes.add(fourth);
//			irisAttributes.add(cls);
//			Instances dataset = new Instances("testowa", irisAttributes, 0);
//
//
//			for(int i=0; i<10; i++){
//
//				double[] values = new double[] { Math.random()*10, Math.random()*10 , Math.random()*10 , Math.random()*10 , 0 };
//
//				dataset.add(new DenseInstance(1.0, values));
//				System.out.println("Rezultat klasyfikacji: ");
//				dataset.setClassIndex(data.numAttributes() - 1);
//
//				double clsLabel = tree.classifyInstance(dataset.instance(0));
//				dataset.instance(0).setClassValue(clsLabel);
//				System.out.println(dataset.lastInstance());
//			}
//		}
//			private static void Zad1b() throws Exception{
//				BufferedReader reader = new BufferedReader(new FileReader("D://data//iris.arff"));
//				Instances data = new Instances(reader);
//				reader.close();
//				data.setClassIndex(data.numAttributes() - 1);
//
//				Discretize filter = new Discretize();
//		        filter.setAttributeIndices("2, 3");
//		        filter.setInputFormat(data);
//		        Instances filteredData = Filter.useFilter(data, filter);
//
//		        J48 tree = new J48();
//		        tree.buildClassifier(filteredData);
//
//
//				Attribute first = new Attribute("sepallength");
//				Attribute second = new Attribute("sepalwidth");
//				Attribute third = new Attribute("petallength");
//				Attribute fourth = new Attribute("petalwidth");
//				List<String> classAtt = new ArrayList<String>();
//				classAtt.add("Iris-setosa");
//				classAtt.add("Iris-versicolor");
//				classAtt.add("Iris-virginica");
//				Attribute cls = new Attribute("class", classAtt);
//				ArrayList<Attribute> irisAttributes = new ArrayList<Attribute>();
//				irisAttributes.add(first);
//				irisAttributes.add(second);
//				irisAttributes.add(third);
//				irisAttributes.add(fourth);
//				irisAttributes.add(cls);
//				Instances dataset = new Instances("testowa", irisAttributes, 0);
//
//
//				for(int i=0; i<10; i++){
//
//					double[] values = new double[] { Math.random()*10, Math.random()*10 , Math.random()*10 , Math.random()*10 , 0 };
//
//					dataset.add(new DenseInstance(1.0, values));
//					System.out.println("Rezultat klasyfikacji: ");
//					dataset.setClassIndex(data.numAttributes() - 1);
//
//					double clsLabel = tree.classifyInstance(dataset.instance(0));
//					dataset.instance(0).setClassValue(clsLabel);
//					System.out.println(dataset.lastInstance());
//				}
//
//
//		}
//
//			public static void Zad2a() throws Exception {
//		        Instances test = new Instances(new BufferedReader(new BufferedReader(new FileReader("D:/data//weather.nominal.test.arff"))));
//		        Instances train = new Instances(new BufferedReader(new BufferedReader(new FileReader("D://data//weather.nominal.train.arff"))));
//		        test.setClassIndex(test.numAttributes() - 1);
//		        train.setClassIndex(train.numAttributes() - 1);
//
//		        J48 j48 = new J48();
//		        j48.setUnpruned(true);
//
//		        // Z filtrowaniem
//		        NominalToBinary filter = new NominalToBinary(); // filter
//		        filter.setAttributeIndices("first-last");
//		        filter.setInputFormat(train);
//		        FilteredClassifier fc = new FilteredClassifier();
//		        fc.setFilter(filter);
//		        fc.setClassifier(j48);
//		        fc.buildClassifier(train);
//
//		        Evaluation eval = new Evaluation(test);
//				eval.evaluateModel(fc, test);
//				System.out.println(eval.toSummaryString());
//		   }
//
//		    public static void Zad2b() throws Exception {
//		    	 Instances test = new Instances(new BufferedReader(new BufferedReader(new FileReader("D://data//weather.nominal.test.arff"))));
//			        Instances train = new Instances(new BufferedReader(new BufferedReader(new FileReader("D://data//weather.nominal.train.arff"))));
//			        test.setClassIndex(test.numAttributes() - 1);
//			        train.setClassIndex(train.numAttributes() - 1);
//
//			        J48 j48 = new J48();
//			        j48.setUnpruned(true);
//
//		        // Z filtrowaniem
//		        Standardize filter = new Standardize(); // filter
//		        filter.setInputFormat(train);
//		        FilteredClassifier fc = new FilteredClassifier();
//		        fc.setFilter(filter);
//		        fc.setClassifier(j48);
//		        fc.buildClassifier(train);
//
//		        System.out.println(fc);
//
//		        Evaluation eval = new Evaluation(test);
//				eval.evaluateModel(fc, test);
//				System.out.println(eval.toSummaryString());
//		    }
//
//
//
//}
