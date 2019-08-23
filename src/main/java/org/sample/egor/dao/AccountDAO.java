package org.sample.egor.dao;

import org.sample.egor.dto.Account;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface AccountDAO {

    void createAccount(Account account);

    Account getAccount(String accountNumber);

    void changeAmount(String accountNumber, BigDecimal amount) throws SQLException;

}
