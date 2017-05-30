package com.pl.pb.predictor.weka.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pl.pb.predictor.weka.WekaProperties;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

@Service
public class WekaService {

	public Instances getInstancesFromCsv(Path path){
		return getInstancesFromCsv(path.toFile());
	}
	
	public Instances getInstancesFromCsv(File file){
		CSVLoader cnv = new CSVLoader();
		try {
			cnv.setSource(file);
			return cnv.getDataSet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAvaliableClassifiersForInstances(Instances instances){
		List<String> classList = new LinkedList<>();
		for(String classname : WekaProperties.classifiersNamesTable){
			try {
				Class myClass = Class.forName("weka.classifiers.meta."+classname);
				Class[] types = {};
				Constructor constructor = myClass.getConstructor(types);
				Object[] parameters = {new Double(0), this};
				Object instanceOfMyClass = constructor.newInstance(parameters);
				if(instanceOfMyClass instanceof Classifier){
					((Classifier)instanceOfMyClass).buildClassifier(instances);
					classList.add(classname);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return classList;
	}
}
