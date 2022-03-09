package com.example;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.domain.ATMWithCashIn;
import com.example.domain.BanknoteAcceptable;
import com.example.domain.impl.ATMWithCashInImpl;
import com.example.domain.impl.BanknoteCassetteImpl;
import com.example.dto.BanknoteBundleDTO;
import com.example.exceptions.NotEnoughFreeSpaceInATMForDenominationException;
import com.example.exceptions.NotExistsCassetteForDenominationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@DisplayName("Проверка функционала по приему банкнот BanknoteAcceptable")
class BanknoteAcceptableTest {
    @DisplayName("Проверка баланса после приема банкнот")
    @Test
    void checkBalanceAfterAcceptBanknotes() {
        ATMWithCashIn atm = new ATMWithCashInImpl();
        BanknoteAcceptable banknoteAcceptable = atm;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 50, 100));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 25, 50));

        Collection<BanknoteBundleDTO> banknoteBundles = new ArrayList<>();
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_500, 10));
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_1000, 5));
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_2000, 3));

        banknoteAcceptable.acceptBanknotes(banknoteBundles);

        long expectedBalance = BANKNOTE_DENOMINATION.DENOMITATION_500.getValue() * 100L
                + BANKNOTE_DENOMINATION.DENOMITATION_1000.getValue() * 50L
                + BANKNOTE_DENOMINATION.DENOMITATION_2000.getValue() * 25L
                + BANKNOTE_DENOMINATION.DENOMITATION_500.getValue() * 10L
                + BANKNOTE_DENOMINATION.DENOMITATION_1000.getValue() * 5L
                + BANKNOTE_DENOMINATION.DENOMITATION_2000.getValue() * 3L;

        Assertions.assertThat(atm.getBalance()).isEqualTo(expectedBalance);
    }

    @Test
    @DisplayName("Метод внесения суммы возбуждает исключение IllegalArgumentException при передаче null в качестве " +
            "списка пачек банкнот")
    void checkIllegalArgumentExceptionOnNullBanknoteBundles() {
        ATMWithCashIn atm = new ATMWithCashInImpl();
        BanknoteAcceptable banknoteAcceptable = atm;
        Assertions.assertThatThrownBy(() -> banknoteAcceptable.acceptBanknotes(null))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("Метод внесения суммы возбуждает исключение IllegalArgumentException при передаче Отрицательного количества банкнот в пачке")
    void checkIllegalArgumentExceptionOnNegativeBanknoteCountInBundle() {
        ATMWithCashIn atm = new ATMWithCashInImpl();
        BanknoteAcceptable banknoteAcceptable = atm;
        Assertions
                .assertThatThrownBy(() -> banknoteAcceptable.acceptBanknotes(Collections.singleton(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_1000, -1))))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("Метод внесения суммы возбуждает исключение NotExistsCassetteForDenominationException при передаче купюр номиналом, " +
            "для которой не предусмотрено кассеты в банкомате")
    void throwNotExistsCassetteForDenomination() {
        ATMWithCashIn atm = new ATMWithCashInImpl();
        BanknoteAcceptable banknoteAcceptable = atm;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 50, 100));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 25, 50));

        Collection<BanknoteBundleDTO> banknoteBundles = new ArrayList<>();
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_5000, 10));
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_1000, 5));
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_2000, 3));

        Assertions.assertThatThrownBy(() -> banknoteAcceptable.acceptBanknotes(banknoteBundles))
                .isInstanceOf(NotExistsCassetteForDenominationException.class);

    }

    @Test
    @DisplayName("Метод внесения суммы возбуждает исключение NotExistsCassetteForDenominationException при передаче купюр номиналом, " +
            "для которой не предусмотрено кассеты в банкомате")
    void throwNotEnoughFreeSpaceInATMForDenominationException() {
        ATMWithCashIn atm = new ATMWithCashInImpl();
        BanknoteAcceptable banknoteAcceptable = atm;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 50, 100));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 25, 50));

        Collection<BanknoteBundleDTO> banknoteBundles = new ArrayList<>();
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_500, 10));
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_1000, 5));
        banknoteBundles.add(new BanknoteBundleDTO(BANKNOTE_DENOMINATION.DENOMITATION_2000, 26));

        Assertions.assertThatThrownBy(() -> banknoteAcceptable.acceptBanknotes(banknoteBundles))
                .isInstanceOf(NotEnoughFreeSpaceInATMForDenominationException.class);

    }
}




