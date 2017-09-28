package pl.com.stock.option.simulator;

import lombok.Data;

@Data
public class NoMoreMoneyException extends Throwable {
    String message;
    public NoMoreMoneyException(String message) {
        message = message;
    }
}
