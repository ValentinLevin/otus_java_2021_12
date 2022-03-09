package com.example.domain;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.dto.BanknoteBundleDTO;

import java.util.Collection;

public interface ATM {
    Collection<BanknoteCassette> getCassettesByBanknoteDenomination(BANKNOTE_DENOMINATION banknoteDenomination);
    void addBanknoteCassette(BanknoteCassette banknoteCassette);
    void removeBanknoteCassette(BanknoteCassette banknoteCassette);
    long getBalance();
    Collection<BanknoteBundleDTO> giveMoney(long requestedAmountOfMoney);
}
