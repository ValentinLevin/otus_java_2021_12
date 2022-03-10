package com.example.domain.impl;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.domain.ATM;
import com.example.domain.BanknoteCassette;
import com.example.dto.BanknoteBundleDTO;
import com.example.exceptions.CassetteNotFoundException;
import com.example.exceptions.NotEnoughBanknoteCountInATMException;
import com.example.exceptions.NotEnoughBanknoteCountInATMToGiveRequestedAmountException;
import com.example.exceptions.RequestedAmountOfMoneyIsTooSmallException;

import java.util.*;

public class ATMImpl implements ATM {
    private final Map<BANKNOTE_DENOMINATION, Collection<BanknoteCassette>> cassettesByBanknoteDenomination;

    @Override
    public Collection<BanknoteCassette> getCassettesByBanknoteDenomination(BANKNOTE_DENOMINATION banknoteDenomination) {
        if (banknoteDenomination == null) {
            throw new IllegalArgumentException("Passed incorrect banknote denomination value (null)");
        }

        return this.cassettesByBanknoteDenomination.containsKey(banknoteDenomination) ?
                Collections.unmodifiableCollection(this.cassettesByBanknoteDenomination.get(banknoteDenomination))
                :
                null;
    }

    @Override
    public void addBanknoteCassette(BanknoteCassette banknoteCassette) {
        if (banknoteCassette == null) {
            throw new IllegalArgumentException("Passed incorrect banknote cassette value (null)");
        }

        Collection<BanknoteCassette> singleDenominationBanknoteCassettes =
                Optional.ofNullable(cassettesByBanknoteDenomination.get(banknoteCassette.getBanknoteDenomination()))
                        .orElseGet(() ->
                                {
                                    Collection<BanknoteCassette> cassettes = new ArrayList<>();
                                    this.cassettesByBanknoteDenomination.put(banknoteCassette.getBanknoteDenomination(), cassettes);
                                    return cassettes;
                                }
                        );
        singleDenominationBanknoteCassettes.add(banknoteCassette);
    }

    @Override
    public void removeBanknoteCassette(BanknoteCassette banknoteCassette) {
        if (banknoteCassette == null) {
            throw new IllegalArgumentException("Passed incorrect banknote cassette value (null)");
        }

        Collection<BanknoteCassette> singleDenominationBanknoteCassette =
                Optional.ofNullable(this.cassettesByBanknoteDenomination.get(banknoteCassette.getBanknoteDenomination()))
                        .orElseThrow(CassetteNotFoundException::new);

        if (!singleDenominationBanknoteCassette.remove(banknoteCassette)) {
            throw new CassetteNotFoundException();
        }
    }

    @Override
    public long getBalance() {
        return this.cassettesByBanknoteDenomination.values().stream()
                .reduce(0L,
                        (atmBalance, singleDenominationCassettes) ->
                                singleDenominationCassettes.stream()
                                    .reduce(
                                            atmBalance,
                                            (balanceBySingleDenomination, cassette) -> balanceBySingleDenomination + cassette.getBalance(),
                                            Long::sum
                                    ),
                        Long::sum
                        );
    }

    private int getAvailableBanknoteCountByDenomination(BANKNOTE_DENOMINATION banknoteDenomination) {
        return Optional.ofNullable(this.getCassettesByBanknoteDenomination(banknoteDenomination))
                .orElse(Collections.emptyList())
                .stream()
                .reduce(0,
                        (banknoteCount, cassette) -> banknoteCount + cassette.getCurrentBanknoteCount(),
                        Integer::sum
                );
    }

    private Collection<BanknoteBundleDTO> generateBanknoteBundleCombinationsForRequestedAmount(long requestedAmountOfMoney) {
        long notProcessedAmountOfMoney = requestedAmountOfMoney;
        Collection<BanknoteBundleDTO> banknoteBundles = new ArrayList<>();
        BANKNOTE_DENOMINATION currentBanknoteDenomination = BANKNOTE_DENOMINATION.getNearestSmallerDenomination(notProcessedAmountOfMoney);

        while (notProcessedAmountOfMoney > 0 && currentBanknoteDenomination != null) {
            int availableBanknoteCountByDenomination = getAvailableBanknoteCountByDenomination(currentBanknoteDenomination);
            if (availableBanknoteCountByDenomination > 0) {
                int banknoteCount =
                        Integer.min((int) notProcessedAmountOfMoney / currentBanknoteDenomination.getValue(), availableBanknoteCountByDenomination);
                if (banknoteCount > 0) {
                    banknoteBundles.add(new BanknoteBundleDTO(currentBanknoteDenomination, banknoteCount));
                    notProcessedAmountOfMoney -= (long) banknoteCount * currentBanknoteDenomination.getValue();
                }
            }
            currentBanknoteDenomination = BANKNOTE_DENOMINATION.getPreviousDenomination(currentBanknoteDenomination);
        }

        if (notProcessedAmountOfMoney > 0) {
            throw new NotEnoughBanknoteCountInATMToGiveRequestedAmountException(requestedAmountOfMoney);
        }

        return banknoteBundles;
    }

    private void removeBanknotesFromCassettes(Collection<BanknoteBundleDTO> banknoteBundles) {
        for (BanknoteBundleDTO banknoteBundle: banknoteBundles) {
            Collection<BanknoteCassette> singleDenominationCassettes =
                    getCassettesByBanknoteDenomination(banknoteBundle.getBanknoteDenomination());
            int banknoteCountToRemove = banknoteBundle.getBanknoteCount();
            Iterator<BanknoteCassette> banknoteCassetteIterator = singleDenominationCassettes.iterator();
            while (banknoteCassetteIterator.hasNext() && banknoteCountToRemove > 0) {
                BanknoteCassette cassette = banknoteCassetteIterator.next();
                int banknoteCountToRemoteFromCassette = Integer.min(banknoteCountToRemove, cassette.getCurrentBanknoteCount());
                banknoteCountToRemove -= banknoteCountToRemoteFromCassette;
                cassette.removeBanknotes(banknoteCountToRemoteFromCassette);
            }
        }
    }

    @Override
    public Collection<BanknoteBundleDTO> giveMoney(long requestedAmountOfMoney) {
        if (requestedAmountOfMoney <= 0) {
            throw new IllegalArgumentException(String.format("Passed incorrect value of requested amount of money (%d)", requestedAmountOfMoney));
        }

        if (getBalance() < requestedAmountOfMoney) {
            throw new NotEnoughBanknoteCountInATMException(requestedAmountOfMoney);
        }

        if (requestedAmountOfMoney < BANKNOTE_DENOMINATION.getSmallestDenomination().getValue()) {
            throw new RequestedAmountOfMoneyIsTooSmallException(requestedAmountOfMoney);
        }

        Collection<BanknoteBundleDTO> banknoteBundles =
                generateBanknoteBundleCombinationsForRequestedAmount(requestedAmountOfMoney);
        removeBanknotesFromCassettes(banknoteBundles);

        return banknoteBundles;
    }

    public ATMImpl() {
        this.cassettesByBanknoteDenomination = new EnumMap<>(BANKNOTE_DENOMINATION.class);
    }
}
