package pl.com.stock.option.simulator.weka;

import lombok.AllArgsConstructor;
import lombok.Data;
import weka.core.Instance;

@Data
public class ClassifiedInstanceModel {
    private String rsiExtreme;
    private String cciExtreme;
    private String macdIndicator;
    private String bbSizeBand;
    private String bbPricePosition;
    private String realStockBehavior;
    private String classifiedAs;

    public ClassifiedInstanceModel(Instance instance, String classifiedAs) {
        rsiExtreme = String.valueOf(instance.value(0));
        cciExtreme = String.valueOf(instance.value(1));
        macdIndicator = String.valueOf(instance.value(2));
        bbSizeBand = String.valueOf(instance.value(3));
        bbPricePosition = String.valueOf(instance.value(4));
        realStockBehavior = String.valueOf(instance.classAttribute().value((int)instance.value(5)));
        this.classifiedAs = classifiedAs;
    }
}
