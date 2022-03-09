package com.example.exceptions;

import com.example.constant.BANKNOTE_DENOMINATION;

import java.io.Serial;

public class NotExistsCassetteForDenominationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1025153483610314099L;
    private final static String MESSAGE_TEMPLATE = "Not exists cassette in ATM for denomination (%d)";

    public NotExistsCassetteForDenominationException(BANKNOTE_DENOMINATION banknoteDenomination) {
        super(String.format(MESSAGE_TEMPLATE, banknoteDenomination.getValue()));
    }
}
