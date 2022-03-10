package com.example;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.domain.BanknoteCassette;
import com.example.domain.impl.BanknoteCassetteImpl;
import com.example.exceptions.NotEnoughBanknoteCountInCassetteException;
import com.example.exceptions.NotEnoughFreeSpaceInCassetteException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка BanknoteCassette")
class BanknoteCassetteTest {
    @Test
    @DisplayName("Проверка корректности номинала купюр кассеты, присвоенного при создании")
    void banknoteDenominationTest() {
        BANKNOTE_DENOMINATION expectedValue = BANKNOTE_DENOMINATION.DENOMITATION_5000;

        BanknoteCassette banknoteCassette = new BanknoteCassetteImpl(expectedValue);

        Assertions.assertThat(banknoteCassette.getBanknoteDenomination())
                .isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Проверка корректности сохранения в объекте кассеты вместимости кассеты, переданной при создании")
    void capacityTest() {
        int expectedValue = 999;
        BanknoteCassette banknoteCassette = new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 0, expectedValue);
        Assertions.assertThat(banknoteCassette.getCapacity())
                .isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Проверка корректности сохранения в объекте кассеты текущего количества банкнот, переданного при создании")
    void currentBanknoteCountTest() {
        int expectedValue = 999;
        BanknoteCassette banknoteCassette = new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, expectedValue, 1000);
        Assertions.assertThat(banknoteCassette.getCurrentBanknoteCount())
                .isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Проверка корректности подсчета доступного места для новых банкнот")
    void availableSpaceTest() {
        int expectedValue = 50;
        BanknoteCassette banknoteCassette = new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 150, 200);
        Assertions.assertThat(banknoteCassette.getAvailableSpace())
                .isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Проверка корректности подсчета баланса наличности в кассете")
    void balanceTest() {
        BANKNOTE_DENOMINATION banknoteDenomination = BANKNOTE_DENOMINATION.DENOMITATION_2000;
        int banknoteCount = 35;

        BanknoteCassette banknoteCassette = new BanknoteCassetteImpl(banknoteDenomination, banknoteCount, 1000);

        Assertions.assertThat(banknoteCassette.getBalance())
                .isEqualTo((long) banknoteDenomination.getValue() * banknoteCount);
    }

    @Test
    @DisplayName("Проверка на корректность параметров создания объекта")
    void throwIllegalArgumentExceptionOnIncorrectConstructorValues() {
        Assertions.assertThatThrownBy(() -> new BanknoteCassetteImpl(null))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, -1))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 10, 5))
                .isInstanceOf(NotEnoughFreeSpaceInCassetteException.class);
    }

    @Test
    @DisplayName("Проверка доступности добавления купюр более чем позволяет кассета")
    void appendBanknoteCountMoreThanCapacity() {
        BanknoteCassette banknoteCassette = new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 10, 20);
        Assertions.assertThatThrownBy(() -> banknoteCassette.addBanknotes(15))
                .isInstanceOf(NotEnoughFreeSpaceInCassetteException.class);
    }

    @Test
    @DisplayName("Проверка доступности извлечения купюр более чем есть в кассете")
    void removeBanknoteCountMoreThanExists() {
        BanknoteCassette banknoteCassette = new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 10, 20);
        Assertions.assertThatThrownBy(() -> banknoteCassette.removeBanknotes(15))
                .isInstanceOf(NotEnoughBanknoteCountInCassetteException.class);
    }

    @Test
    @DisplayName("Проверка баланса кассеты после добавления купюр")
    void checkBalanceAfterAppendBanknotes() {
        BanknoteCassette banknoteCassette =
                new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 10, 20);
        banknoteCassette.addBanknotes(7);
        Assertions.assertThat(banknoteCassette.getBalance())
                .isEqualTo(BANKNOTE_DENOMINATION.DENOMITATION_2000.getValue() * 17);
    }

    @Test
    @DisplayName("Проверка баланса кассеты после извлечения купюр")
    void checkBalanceAfterRemoveBanknotes() {
        BanknoteCassette banknoteCassette =
                new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 10, 20);
        banknoteCassette.removeBanknotes(7);
        Assertions.assertThat(banknoteCassette.getBalance())
                .isEqualTo(BANKNOTE_DENOMINATION.DENOMITATION_2000.getValue() * 3);
    }

}
