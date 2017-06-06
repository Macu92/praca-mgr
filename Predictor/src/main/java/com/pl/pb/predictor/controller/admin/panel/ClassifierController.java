package com.pl.pb.predictor.controller.admin.panel;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.pl.pb.predictor.storage.StorageService;
import com.pl.pb.predictor.weka.service.WekaService;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;

@Controller
public class ClassifierController {
	private final StorageService storageService;
	private final WekaService wekaService;

	@Autowired
	public ClassifierController(StorageService storageService, WekaService wekaService) {
		this.storageService = storageService;
		this.wekaService = wekaService;
	}

	@GetMapping("/admin-panel/classifiers")
	public String manageClassififers(Model model) throws Exception {
		Path path = storageService.load("best1-10.csv");
		Instances instances = wekaService.getInstancesFromCsv(path);
		instances.setClassIndex(instances.numAttributes()-1);
		RandomForest randomForest = new RandomForest();
		randomForest.buildClassifier(instances);
		Evaluation eval = new Evaluation(instances);
		eval.evaluateModel(randomForest,instances);
		System.out.println(eval.toSummaryString());


//		wekaService.getAvaliableClassifiersForInstances(instances);
//		model.addAttribute("files",
//				storageService.loadAll()
//						.map(p -> MvcUriComponentsBuilder
//								.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
//								.build().toString())
//						.collect(Collectors.toList()));

		return "admin/classifiers";
	}
}
