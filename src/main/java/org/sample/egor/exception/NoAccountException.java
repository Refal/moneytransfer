package org.sample.egor.exception;


public class NoAccountException extends BusinessCheckException {

    public NoAccountException(String accountNumber) {
        super("No account found: [" + accountNumber + "]");
    }
}
