package com.example.exceptions;

import com.example.constant.BANKNOTE_DENOMINATION;

import java.io.Serial;

public class NotEnoughFreeSpaceInATMForDenominationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -660381738127026041L;
    private static final String MESSAGE_TEMPLATE = "No free space for %d banknote(s) amount of %d denomination";

    public NotEnoughFreeSpaceInATMForDenominationException(BANKNOTE_DENOMINATION banknoteDenomination, int banknoteCount) {
        super(String.format(MESSAGE_TEMPLATE, banknoteCount, banknoteDenomination.getValue()));
    }
}
