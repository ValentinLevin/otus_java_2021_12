package com.example.domain.impl;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.domain.ATMWithCashIn;
import com.example.domain.BanknoteCassette;
import com.example.dto.BanknoteBundleDTO;
import com.example.exceptions.NotEnoughFreeSpaceInATMForDenominationException;
import com.example.exceptions.NotExistsCassetteForDenominationException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ATMWithCashInImpl extends ATMImpl implements ATMWithCashIn {

    @Override
    public void acceptBanknotes(Collection<BanknoteBundleDTO> banknoteBundlesByDenomination) {
        if (banknoteBundlesByDenomination == null) {
            throw new IllegalArgumentException("Passed incorrect banknote bundles list (null)");
        }

        checkFreeSpaceForBanknotes(banknoteBundlesByDenomination);
        appendBanknotesIntoCassettes(banknoteBundlesByDenomination);
    }

    private void checkFreeSpaceForBanknotes(Collection<BanknoteBundleDTO> banknoteBundlesByDenomination) {
        for (var banknoteBundleDTO: banknoteBundlesByDenomination) {
            if (getCassettesByBanknoteDenomination(banknoteBundleDTO.getBanknoteDenomination()) == null) {
                throw new NotExistsCassetteForDenominationException(banknoteBundleDTO.getBanknoteDenomination());
            }

            int banknoteAmount = banknoteBundleDTO.getBanknoteCount();
            if (getAvailableSpaceForBanknoteDenomination(banknoteBundleDTO.getBanknoteDenomination()) < banknoteAmount) {
                throw new NotEnoughFreeSpaceInATMForDenominationException(banknoteBundleDTO.getBanknoteDenomination(), banknoteAmount);
            }
        }
    }

    private void appendBanknotesIntoCassettes(Collection<BanknoteBundleDTO> banknoteBundlesByDenomination) {
        for (var banknoteBundle: banknoteBundlesByDenomination) {
            int banknoteAmount = banknoteBundle.getBanknoteCount();

            if (banknoteAmount > 0) {
                do {
                    BanknoteCassette banknoteCassetteImplWithFreeSpace =
                            getNextBanknoteCassetteWithFreeSpace(banknoteBundle.getBanknoteDenomination());
                    int banknoteCountToAppend = Integer.min(banknoteAmount, banknoteCassetteImplWithFreeSpace.getAvailableSpace());
                    banknoteCassetteImplWithFreeSpace.addBanknotes(banknoteCountToAppend);
                    banknoteAmount -= banknoteCountToAppend;
                } while (banknoteAmount > 0);
            }
        }
    }

    private BanknoteCassette getNextBanknoteCassetteWithFreeSpace(BANKNOTE_DENOMINATION banknoteDenomination) {
        return Optional.ofNullable(getCassettesByBanknoteDenomination(banknoteDenomination))
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> item.getAvailableSpace() > 0)
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getAvailableSpaceForBanknoteDenomination(BANKNOTE_DENOMINATION banknoteDenomination) {
        return Optional.ofNullable(this.getCassettesByBanknoteDenomination(banknoteDenomination))
                .orElse(Collections.emptyList())
                .stream()
                .reduce(
                        0,
                        (availableSpace, cassette) -> availableSpace + cassette.getAvailableSpace(),
                        Integer::sum
                );
    }
}