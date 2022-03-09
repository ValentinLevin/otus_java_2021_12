package com.example.constant;

public enum BANKNOTE_DENOMINATION {
    DENOMITATION_500(500),
    DENOMITATION_1000(1_000),
    DENOMITATION_2000(2_000),
    DENOMITATION_5000(5_000),
    DENOMITATION_10000(10_000),
    DENOMITATION_20000(20_000);

    private final int value;

    public static BANKNOTE_DENOMINATION getNearestSmallerDenomination(long fromValue) {
        for (var i = BANKNOTE_DENOMINATION.values().length - 1; i >= 0; i--) {
            if (BANKNOTE_DENOMINATION.values()[i].getValue() < fromValue) {
                return BANKNOTE_DENOMINATION.values()[i];
            }
        }

        return null;
    }

    public static BANKNOTE_DENOMINATION getPreviousDenomination(BANKNOTE_DENOMINATION fromDenomination) {
        if (fromDenomination != null && fromDenomination.ordinal() > 0) {
            return BANKNOTE_DENOMINATION.values()[fromDenomination.ordinal() - 1];
        }
        return null;
    }

    public static BANKNOTE_DENOMINATION getSmallestDenomination() {
        return DENOMITATION_500;
    }

    public int getValue() {
        return this.value;
    }

    BANKNOTE_DENOMINATION(int value) {
        this.value = value;
    }
}
