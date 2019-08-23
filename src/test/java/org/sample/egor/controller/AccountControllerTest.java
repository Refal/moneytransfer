package org.sample.egor.controller;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sample.egor.dao.impl.AccountDAOImpl;
import org.sample.egor.dto.Account;
import org.sample.egor.dto.Transfer;
import org.sample.egor.provider.MyObjectMapperProvider;
import org.sample.egor.service.impl.AccountServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountControllerTest extends JerseyTest {
    private final static Logger logger = LoggerFactory.getLogger(AccountControllerTest.class);

    @BeforeEach
    void before() throws Exception {
        super.setUp();
    }

    // do not name this tearDown()
    @AfterEach
    void after() throws Exception {
        super.tearDown();
    }

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        return new ResourceConfig().packages("org.glassfish.jersey.examples.jackson")
                .packages("jersey.jetty.embedded")
                .register(MyObjectMapperProvider.class)  // No need to register this provider if no special configuration is required.
                .register(JacksonFeature.class)
                .register(new AccountController(new AccountServiceImpl(new AccountDAOImpl())));
    }

    @Test
    void createAccount() {
        Response resp = target("/account/").request().put(Entity.entity(new Account("3", new BigDecimal("20.0")), MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Account acc = getAccountValue("3");
        assertNotNull(acc);
        assertEquals("3", acc.getAccountNumber());
    }

    @Test
    void getAccount() {
        Account acc1 = getAccountValue("1");
        Account acc2 = getAccountValue("2");
        assertNotNull(acc2);
        assertNotNull(acc1);
        assertEquals("2", acc2.getAccountNumber());
        assertEquals("1", acc1.getAccountNumber());
    }

    @Test
    void transferAmountFrom2To1() {
        Account before = getAccountValue("2");
        BigDecimal transferAmount = new BigDecimal(1.0);
        Response response = target("/account/transfer").request()
                .post(Entity.entity(new Transfer("2", "1", transferAmount), MediaType.APPLICATION_JSON_TYPE));
        Account after = getAccountValue("2");
        assertEquals(before.getAmount().subtract(transferAmount), after.getAmount());
    }

    @Test
    void multipleTransferAmountFrom2To1() {
        BigDecimal transferAmount = new BigDecimal(1.0);
        for (int i = 0; i < 15; i++) {
            Account before = getAccountValue("2");
            Response response = target("/account/transfer").request()
                    .post(Entity.entity(new Transfer("2", "1", transferAmount), MediaType.APPLICATION_JSON_TYPE));
            assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());
            Account after = getAccountValue("2");
            assertEquals(before.getAmount().subtract(transferAmount), after.getAmount());
        }
    }


    private Account getAccountValue(String s) {
        return target("/account/" + s).request().get(Account.class);
    }
}