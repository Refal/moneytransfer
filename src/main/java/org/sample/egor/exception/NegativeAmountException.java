package org.sample.egor.exception;

import java.math.BigDecimal;

public class NegativeAmountException extends BusinessCheckException {
    public NegativeAmountException(BigDecimal amount) {
        super("tried to transfer negative amount:" + amount);
    }
}
