package com.example.exceptions;

import java.io.Serial;

public class NotEnoughFreeSpaceInCassetteException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4637399794763357256L;
    private static final String MESSAGE_TEMPLATE = "Not enough space for %d banknote(s) in cassette with capacity %d";

    public NotEnoughFreeSpaceInCassetteException(int banknoteCount, int cassetteCapacity) {
        super(String.format(MESSAGE_TEMPLATE, banknoteCount, cassetteCapacity));
    }
}
