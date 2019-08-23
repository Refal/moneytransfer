package org.sample.egor.service.impl;

import org.sample.egor.dao.AccountDAO;
import org.sample.egor.dao.Database;
import org.sample.egor.dto.Account;
import org.sample.egor.exception.*;
import org.sample.egor.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountServiceImpl implements AccountService {
    private final static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountDAO accountDAO;

    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public void transferAmount(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount) throws BusinessCheckException {
        try {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new NegativeAmountException(amount);
            }
            //will lock accounts in a sorted order to prevent deadlock
            String key = Stream.of(sourceAccountNumber, targetAccountNumber).sorted().collect(Collectors.joining(","));
            logger.debug("will lock by key: {}", key);
            Database.lock(key, () -> {
                logger.debug("accounts: {} locked", key);
                try {
                    Account sourceAccount = getAccount(sourceAccountNumber);
                    Account targetAccount = getAccount(targetAccountNumber);
                    if (sourceAccount != null && targetAccount != null) {
                        if (sourceAccount.getAmount().compareTo(amount) >= 0) {
                            accountDAO.changeAmount(sourceAccountNumber, amount.negate());
                            accountDAO.changeAmount(targetAccountNumber, amount);
                        } else {
                            throw new NotEnoughMoneyException(sourceAccount, amount);
                        }
                    } else {
                        logger.error("No accounts source: {}, target: {}", sourceAccountNumber, targetAccountNumber);
                        throw new NoAccountException(sourceAccount == null ? sourceAccountNumber : targetAccountNumber);
                    }
                } catch (SQLException ex) {
                    throw new GeneralException(ex.getMessage(), ex);
                }
            });
        } catch (InterruptedException ex1) {
            logger.error(ex1.getMessage(), ex1);
            throw new GeneralException(ex1.getMessage(), ex1);
        } finally {
            logger.info("connection released");
        }
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountDAO.getAccount(accountNumber);
    }

    @Override
    public void createAccount(Account account) {
        accountDAO.createAccount(account);
    }
}
