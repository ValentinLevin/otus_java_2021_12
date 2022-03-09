package com.example.exceptions;

import java.io.Serial;

public class RequestedAmountOfMoneyIsTooSmallException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 208791319778936945L;
    private static final String MESSAGE_TEMPLATE = "Requested amount of money (%d) is too small";

    public RequestedAmountOfMoneyIsTooSmallException(long requestedAmountOfMoney) {
        super(String.format(MESSAGE_TEMPLATE, requestedAmountOfMoney));
    }
}
