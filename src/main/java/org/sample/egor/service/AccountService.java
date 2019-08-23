package org.sample.egor.service;

import org.sample.egor.dto.Account;
import org.sample.egor.exception.BusinessCheckException;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface AccountService {
    void transferAmount(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount) throws SQLException, BusinessCheckException;

    Account getAccount(String accountNumber) throws SQLException;

    void createAccount(Account account);

}
