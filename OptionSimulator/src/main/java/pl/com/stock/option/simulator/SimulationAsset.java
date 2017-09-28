package pl.com.stock.option.simulator;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.com.stock.option.simulator.weka.ClassifiedInstanceModel;

import java.util.LinkedList;
import java.util.List;


@Data
public class SimulationAsset {
    private Double startAsset;
    private Double singleInvestmentValue;
    private Double rateOfReturn;
    private Double actualAsset = 0.0;
    private List<AssetValueModel> assetValueModels;

    public SimulationAsset(Double startAsset, Double singleInvestmentValue, Double rateOfReturn) {
        this.startAsset = startAsset;
        this.singleInvestmentValue = singleInvestmentValue;
        this.rateOfReturn = rateOfReturn;
    }

    public AssetModel calculateInvestmentAsset(List<ClassifiedInstanceModel> classifiedInstances) {
        assetValueModels = new LinkedList<>();
        actualAsset = startAsset;
        for (ClassifiedInstanceModel instanceModel : classifiedInstances) {
            try {
                calculateInvestment(instanceModel);
            } catch (NoMoreMoneyException e) {
                e.printStackTrace();
                break;
            }
        }
        return new AssetModel(actualAsset, assetValueModels);
    }

    private void calculateInvestment(ClassifiedInstanceModel instanceModel) throws NoMoreMoneyException {
        Double assetBeforeChange = actualAsset;
        if (instanceModel.getClassifiedAs().equals(instanceModel.getRealStockBehavior()) && (instanceModel.getClassifiedAs().equals("up")||instanceModel.getClassifiedAs().equals("down"))) {
            actualAsset += singleInvestmentValue * (rateOfReturn / 100);
        } else if (!instanceModel.getClassifiedAs().equals(instanceModel.getRealStockBehavior())) {
            actualAsset -= singleInvestmentValue;
        }
        assetValueModels.add(new AssetValueModel(instanceModel, actualAsset, actualAsset - assetBeforeChange));
        if (actualAsset < 0) {
            throw new NoMoreMoneyException("Skonczyla sie kaska");
        }
    }
}
