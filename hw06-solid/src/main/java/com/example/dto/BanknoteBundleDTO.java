package com.example.dto;

import com.example.constant.BANKNOTE_DENOMINATION;
import lombok.Getter;

@Getter
public class BanknoteBundleDTO {
    private final BANKNOTE_DENOMINATION banknoteDenomination;
    private final int banknoteCount;

    public BanknoteBundleDTO(BANKNOTE_DENOMINATION banknoteDenomination, int banknoteCount) {
        if (banknoteDenomination == null) {
            throw new IllegalArgumentException("Passed incorrect denomination value (null)");
        }

        if (banknoteCount < 0) {
            throw new IllegalArgumentException(String.format("Passed incorrect banknote count (%d)", banknoteCount));
        }

        this.banknoteDenomination = banknoteDenomination;
        this.banknoteCount = banknoteCount;
    }
}
