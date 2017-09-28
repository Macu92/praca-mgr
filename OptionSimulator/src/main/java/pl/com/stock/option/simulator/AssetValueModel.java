package pl.com.stock.option.simulator;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.com.stock.option.simulator.weka.ClassifiedInstanceModel;

@Data
@AllArgsConstructor
public class AssetValueModel {
    private ClassifiedInstanceModel classifiedInstanceModel;
    private Double assetValue;
    private Double assetChange;
}
