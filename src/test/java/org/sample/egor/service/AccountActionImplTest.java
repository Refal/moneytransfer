package org.sample.egor.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sample.egor.dao.AccountDAO;
import org.sample.egor.dto.Account;
import org.sample.egor.exception.BusinessCheckException;
import org.sample.egor.exception.NoAccountException;
import org.sample.egor.exception.NotEnoughMoneyException;
import org.sample.egor.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountActionImplTest {
    @Mock
    Connection connection;

    @Mock
    AccountDAO persistence;

    @InjectMocks
    AccountServiceImpl action;

    @Test
    void shouldTransferMoney() throws BusinessCheckException, SQLException {
        Account source = new Account("a", BigDecimal.valueOf(2.0));
        Account target = new Account("b", BigDecimal.valueOf(0.0));
        BigDecimal transferAmount = BigDecimal.valueOf(1.0);

        when(persistence.getAccount(anyString())).thenReturn(source).thenReturn(target);

        action.transferAmount(source.getAccountNumber(), target.getAccountNumber(), transferAmount);
        verify(persistence).changeAmount(eq(source.getAccountNumber()), eq(transferAmount.negate()));
        verify(persistence).changeAmount(eq(target.getAccountNumber()), eq(transferAmount));
    }

    @Test
    void shouldNotTransferMoneyIfBalanceNotEnough() throws SQLException {
        Account source = new Account("a", BigDecimal.valueOf(2.0));
        Account target = new Account("b", BigDecimal.valueOf(0.0));
        BigDecimal transferAmount = BigDecimal.valueOf(3.0);

        when(persistence.getAccount(anyString())).thenReturn(source).thenReturn(target);

        Assertions.assertThrows(NotEnoughMoneyException.class, () ->
                action.transferAmount(source.getAccountNumber(), target.getAccountNumber(), transferAmount));

        verify(persistence, never()).changeAmount(anyString(), any(BigDecimal.class));
    }

    @Test
    void shouldThrowExceptionIfNoAccount() throws SQLException {
        Account source = new Account("a", BigDecimal.valueOf(2.0));
        Account target = new Account("b", BigDecimal.valueOf(0.0));
        BigDecimal transferAmount = BigDecimal.valueOf(3.0);

        when(persistence.getAccount(anyString())).thenReturn(null).thenReturn(target);

        Assertions.assertThrows(NoAccountException.class, () ->
                action.transferAmount(source.getAccountNumber(), target.getAccountNumber(), transferAmount));
        verify(persistence, never()).changeAmount(anyString(), any(BigDecimal.class));
    }

}