package com.example.exceptions;

import java.io.Serial;

public class NotEnoughBanknoteCountInATMException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1025153483610314099L;
    private final static String MESSAGE_TEMPLATE = "Not enough money in ATM for requested amount (%d)";

    public NotEnoughBanknoteCountInATMException(long requestedAmountOfMoney) {
        super(String.format(MESSAGE_TEMPLATE, requestedAmountOfMoney));
    }
}
