package org.sample.egor.exception;

import org.sample.egor.dto.Account;

import java.math.BigDecimal;


public class NotEnoughMoneyException extends BusinessCheckException {

    public NotEnoughMoneyException(Account account, BigDecimal deductionSum) {
        super("Not enough money on account: [" + account.toString() + "to deduct: " + deductionSum);
    }
}
