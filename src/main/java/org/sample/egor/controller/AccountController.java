package org.sample.egor.controller;

import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.sample.egor.dto.Account;
import org.sample.egor.dto.Transfer;
import org.sample.egor.exception.BusinessCheckException;
import org.sample.egor.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Api
@Path("/account")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService action;

    public AccountController(AccountService action) {
        this.action = action;
    }


    @POST
    @Path("transfer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(@ApiParam @RequestBody Transfer transfer) throws BusinessCheckException, SQLException {
        logger.info("transfer from: {}, to: {}, amount: [{}]", transfer.getSourceAccount(), transfer.getTargetAccount(), transfer.getAmount());
        action.transferAmount(transfer.getSourceAccount(), transfer.getTargetAccount(), transfer.getAmount());
        return Response.ok().build();
    }

    @GET
    @Path("/{accountNumber}")
    @JSONP
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountNumber") String accountNumber) throws SQLException {
        logger.debug("getAccount: {}", accountNumber);
        Account account = action.getAccount(accountNumber);
        logger.debug("account is: {}", account);
        return account;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(@ApiParam @RequestBody Account account) {
        logger.info("will create account: {}", account);
        action.createAccount(account);
        return Response.ok().build();
    }
}
