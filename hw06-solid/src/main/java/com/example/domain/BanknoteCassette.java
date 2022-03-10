package com.example.domain;

import com.example.constant.BANKNOTE_DENOMINATION;

public interface BanknoteCassette {
    BANKNOTE_DENOMINATION getBanknoteDenomination();

    void addBanknotes(int banknoteCount);
    void removeBanknotes(int banknoteCount);

    int getCurrentBanknoteCount();
    int getCapacity();
    int getAvailableSpace();
    long getBalance();
}
