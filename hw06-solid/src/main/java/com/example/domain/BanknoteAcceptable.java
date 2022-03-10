package com.example.domain;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.dto.BanknoteBundleDTO;

import java.util.Collection;

public interface BanknoteAcceptable {
    void acceptBanknotes(Collection<BanknoteBundleDTO> banknoteBundlesByDenomination);
    int getAvailableSpaceForBanknoteDenomination(BANKNOTE_DENOMINATION banknoteDenomination);
}
