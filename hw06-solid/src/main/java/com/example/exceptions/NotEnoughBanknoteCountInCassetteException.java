package com.example.exceptions;

import java.io.Serial;

public class NotEnoughBanknoteCountInCassetteException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -660381738127026041L;
    private static final String MESSAGE_TEMPLATE = "There are not enough banknotes in the cassette";

    public NotEnoughBanknoteCountInCassetteException() {
        super(MESSAGE_TEMPLATE);
    }
}
