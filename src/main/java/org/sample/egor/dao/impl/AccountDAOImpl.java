package org.sample.egor.dao.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sample.egor.dao.AccountDAO;
import org.sample.egor.dto.Account;
import org.sample.egor.utils.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@NoArgsConstructor
public class AccountDAOImpl implements AccountDAO {
    private static final String TABLE_NAME = "account";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String AMOUNT = "amount";
    private final static Logger logger = LoggerFactory.getLogger(AccountDAOImpl.class);
    @Getter
    private final DataSource dataSource = Database.getDataSource();

    @Override
    public void createAccount(Account account) {
        try (Connection con = getDataSource().getConnection()) {
            PreparedStatement statement = con.prepareStatement("insert into " + TABLE_NAME + "(" + ACCOUNT_NUMBER + ", " + AMOUNT + ") values(?,?)");
            statement.setString(1, account.getAccountNumber());
            statement.setBigDecimal(2, account.getAmount());
            statement.execute();
            logger.info(TABLE_NAME + ":[{}] created", account);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public Account getAccount(String accountNumber) {
        try (Connection con = getDataSource().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("select " + ACCOUNT_NUMBER + ", " + AMOUNT + " from " + TABLE_NAME + " where " + ACCOUNT_NUMBER + " = ?")) {
                statement.setString(1, accountNumber);
                statement.execute();
                ResultSet resultSet = statement.getResultSet();
                if (resultSet.next()) {
                    return new Account(resultSet.getString(ACCOUNT_NUMBER), resultSet.getBigDecimal(AMOUNT));
                } else {
                    return null;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }

    }

    @Override
    public void changeAmount(String accountNumber, BigDecimal amount) throws SQLException {
        try (Connection con = getDataSource().getConnection()) {
            PreparedStatement stmt = con.prepareStatement("update " + TABLE_NAME + " set " + AMOUNT + "= amount + ? where " + ACCOUNT_NUMBER + " = ? ");
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
            logger.info("changed account: {}, for a value: {}", accountNumber, amount);
        }
    }
}
