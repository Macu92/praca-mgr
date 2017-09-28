package pl.com.stock.option.simulator;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AssetModel {
    private Double assetValue;
    private List<AssetValueModel> assetValues;
}
