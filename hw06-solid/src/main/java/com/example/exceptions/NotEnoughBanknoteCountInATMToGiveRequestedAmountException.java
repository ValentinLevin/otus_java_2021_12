package com.example.exceptions;

import java.io.Serial;

public class NotEnoughBanknoteCountInATMToGiveRequestedAmountException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1025153483610314099L;
    private final static String MESSAGE_TEMPLATE = "Not enough banknote count in ATM for requested amount of money (%d)";

    public NotEnoughBanknoteCountInATMToGiveRequestedAmountException(long requestedAmountOfMoney) {
        super(String.format(MESSAGE_TEMPLATE, requestedAmountOfMoney));
    }
}
