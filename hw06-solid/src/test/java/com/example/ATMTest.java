package com.example;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.domain.ATM;
import com.example.domain.BanknoteCassette;
import com.example.domain.impl.ATMImpl;
import com.example.domain.impl.BanknoteCassetteImpl;
import com.example.dto.BanknoteBundleDTO;
import com.example.exceptions.CassetteNotFoundException;
import com.example.exceptions.NotEnoughBanknoteCountInATMException;
import com.example.exceptions.NotEnoughBanknoteCountInATMToGiveRequestedAmountException;
import com.example.exceptions.RequestedAmountOfMoneyIsTooSmallException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

@DisplayName("Проверка функциональности интерфейса ATM")
class ATMTest {
    @Test
    @DisplayName("После добавления кассеты, баланс изменяется ровно на сумму банкнот в кассете")
    void banknoteCassetteAdditionTest() {
        ATM atm = new ATMImpl();

        BANKNOTE_DENOMINATION banknoteDenomination = BANKNOTE_DENOMINATION.DENOMITATION_500;
        int banknoteCountInCassette = 100;
        int cassetteCapacity = 200;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(banknoteDenomination, banknoteCountInCassette, cassetteCapacity));

        int expectedValue = banknoteDenomination.getValue() * banknoteCountInCassette;

        Assertions.assertThat(atm.getBalance()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("После добавления двух кассет с банкнотами одинакового номинала, баланс составляет сумму банкнот в обеих кассетах")
    void banknoteCassetteAdditionSameDenominationCassetteTest() {
        ATM atm = new ATMImpl();

        BANKNOTE_DENOMINATION banknoteDenomination1 = BANKNOTE_DENOMINATION.DENOMITATION_500;
        int banknoteCountInCassette1 = 100;
        int cassetteCapacity1 = 200;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(banknoteDenomination1, banknoteCountInCassette1, cassetteCapacity1));

        BANKNOTE_DENOMINATION banknoteDenomination2 = BANKNOTE_DENOMINATION.DENOMITATION_500;
        int banknoteCountInCassette2 = 50;
        int cassetteCapacity2 = 100;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(banknoteDenomination2, banknoteCountInCassette2, cassetteCapacity2));

        int expectedValue = banknoteDenomination1.getValue() * banknoteCountInCassette1
                + banknoteDenomination2.getValue() * banknoteCountInCassette2;

        Assertions.assertThat(atm.getBalance()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("После добавления нескольких кассет с банкнотами разного номинала, баланс составляет сумму банкнот во всех добавленных кассетах")
    void banknoteCassetteAdditionDifferentDenominationCassetteTest() {
        ATM atm = new ATMImpl();

        BANKNOTE_DENOMINATION banknoteDenomination1 = BANKNOTE_DENOMINATION.DENOMITATION_500;
        int banknoteCountInCassette1 = 100;
        int cassetteCapacity1 = 200;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(banknoteDenomination1, banknoteCountInCassette1, cassetteCapacity1));

        BANKNOTE_DENOMINATION banknoteDenomination2 = BANKNOTE_DENOMINATION.DENOMITATION_2000;
        int banknoteCountInCassette2 = 50;
        int cassetteCapacity2 = 100;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(banknoteDenomination2, banknoteCountInCassette2, cassetteCapacity2));

        BANKNOTE_DENOMINATION banknoteDenomination3 = BANKNOTE_DENOMINATION.DENOMITATION_5000;
        int banknoteCountInCassette3 = 75;
        int cassetteCapacity3 = 100;

        atm.addBanknoteCassette(new BanknoteCassetteImpl(banknoteDenomination3, banknoteCountInCassette3, cassetteCapacity3));

        int expectedValue = banknoteDenomination1.getValue() * banknoteCountInCassette1
                + banknoteDenomination2.getValue() * banknoteCountInCassette2
                + banknoteDenomination3.getValue() * banknoteCountInCassette3;

        Assertions.assertThat(atm.getBalance()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Возбуждается исключение IllegalArgumentException при передаче null в качестве кассеты для добавления")
    void banknoteCassetteAdditionIllegalArgumentProcessingTest() {
        ATM atm = new ATMImpl();
        Assertions.assertThatThrownBy(() -> atm.addBanknoteCassette(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Возбуждение исключения CassetteNotFoundException при передаче null в качестве значения кассеты для удаления")
    void banknoteCassetteRemovingIllegalArgumentExceptionThrowsForNullCassetteValueTest() {
        ATM atm = new ATMImpl();
        Assertions.assertThatThrownBy(() -> atm.removeBanknoteCassette(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Возбуждение исключения CassetteNotFoundException при передаче для удаления номинала, которого нет в банкомате ")
    void banknoteCassetteRemovingCassetteNotFoundThrowsForNotExistsDenominationValueTest() {
        ATM atm = new ATMImpl();
        Assertions.assertThatThrownBy(() -> atm.removeBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000)))
                .isInstanceOf(CassetteNotFoundException.class);
    }

    @Test
    @DisplayName("Возбуждение исключения CassetteNotFoundException при передаче для удаления значения кассеты, которой нет в банкомате ")
    void banknoteCassetteRemovingCassetteNotFoundThrowsForNotExistsCassetteValueTest() {
        ATM atm = new ATMImpl();
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500));
        Assertions.assertThatThrownBy(() -> atm.removeBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500)))
                .isInstanceOf(CassetteNotFoundException.class);
    }

    @Test
    @DisplayName("Метод для получения перечня кассетт с банкнотами одного номинала возбуждает исключение IllegalArgumentException " +
            "при передаче null в качестве номинала банкноты")
    void throwsIllegalArgumentExceptionOnCassetteReceivingWithNullDenominationArgument() {
        ATM atm = new ATMImpl();
        Assertions.assertThatThrownBy(() -> atm.getCassettesByBanknoteDenomination(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Метод получения перечня кассет с банкнотами одного номинала возвращает null при запросе кассет номинала," +
            " которого нет в банкомате (за исключением null)")
    void returnsNullForNotExistsBanknoteDenominationOnCassetteRequest() {
        ATM atm = new ATMImpl();

        Assertions.assertThat(atm.getCassettesByBanknoteDenomination(BANKNOTE_DENOMINATION.DENOMITATION_500))
                .isNull();
    }

    @Test
    @DisplayName("Метод получения перечня кассетт с банкнотами одного номинала возвращает корректное значение ранее добавленной кассеты при запросе кассет номинала")
    void returnsCorrectCassetteValueOnCassetteRequest() {
        ATM atm = new ATMImpl();

        BanknoteCassette exceptedValueOfBanknoteCassette = new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500);
        atm.addBanknoteCassette(exceptedValueOfBanknoteCassette);

        Assertions.assertThat(atm.getCassettesByBanknoteDenomination(BANKNOTE_DENOMINATION.DENOMITATION_500).contains(exceptedValueOfBanknoteCassette)).isTrue();
    }

    @Test
    @DisplayName("Проверка выдачи запрошенной суммы минимальным количеством купюр")
    void checkMoneyGivingTest() {
        ATM atm = new ATMImpl();

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 100, 200));

        long requestedAmountOfMoney = 13500;

        Collection<BanknoteBundleDTO> banknoteBundles = atm.giveMoney(requestedAmountOfMoney);

        long expected2000BanknoteCount = requestedAmountOfMoney / 2000;
        long expected1000BanknoteCount = (requestedAmountOfMoney - expected2000BanknoteCount * 2000) / 1000;
        long expected500BanknoteCount = (requestedAmountOfMoney - expected2000BanknoteCount * 2000 - expected1000BanknoteCount * 1000) / 500;
        int expectedBanknoteDenominationCount = 3;

        Assertions.assertThat(banknoteBundles.size()).isEqualTo(expectedBanknoteDenominationCount);

        Assertions.assertThat(banknoteBundles.stream().filter(item -> item.getBanknoteDenomination() == BANKNOTE_DENOMINATION.DENOMITATION_2000).count())
                .isEqualTo(1);

        Assertions.assertThat(banknoteBundles.stream()
                        .filter(item -> item.getBanknoteDenomination() == BANKNOTE_DENOMINATION.DENOMITATION_2000)
                        .map(BanknoteBundleDTO::getBanknoteCount)
                        .findFirst()
                        .orElse(0))
                .isEqualTo(expected2000BanknoteCount);

        Assertions.assertThat(banknoteBundles.stream().filter(item -> item.getBanknoteDenomination() == BANKNOTE_DENOMINATION.DENOMITATION_1000).count())
                .isEqualTo(1);
        Assertions.assertThat(banknoteBundles.stream()
                        .filter(item -> item.getBanknoteDenomination() == BANKNOTE_DENOMINATION.DENOMITATION_1000)
                        .map(BanknoteBundleDTO::getBanknoteCount)
                        .findFirst()
                        .orElse(0))
                .isEqualTo(expected1000BanknoteCount);


        Assertions.assertThat(banknoteBundles.stream().filter(item -> item.getBanknoteDenomination() == BANKNOTE_DENOMINATION.DENOMITATION_500).count())
                .isEqualTo(1);
        Assertions.assertThat(banknoteBundles.stream()
                        .filter(item -> item.getBanknoteDenomination() == BANKNOTE_DENOMINATION.DENOMITATION_500)
                        .map(BanknoteBundleDTO::getBanknoteCount)
                        .findFirst()
                        .orElse(0))
                .isEqualTo(expected500BanknoteCount);

    }

    @Test
    @DisplayName("Проверка выдачи запрошенной суммы минимальным количеством купюр из нескольких кассет с одинаковым номиналом")
    void checkMoneyGivingTestFromSomeCassettes() {
        ATM atm = new ATMImpl();

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));

        long requestedAmountOfMoney = (100 + 50) * BANKNOTE_DENOMINATION.DENOMITATION_500.getValue();

        Collection<BanknoteBundleDTO> banknoteBundles = atm.giveMoney(requestedAmountOfMoney);

        long expected500BanknoteCount = (requestedAmountOfMoney / 500);

        Assertions.assertThat(banknoteBundles.stream()
                        .filter(item -> item.getBanknoteDenomination() == BANKNOTE_DENOMINATION.DENOMITATION_500)
                        .map(BanknoteBundleDTO::getBanknoteCount)
                        .findFirst()
                        .orElse(0))
                .isEqualTo(expected500BanknoteCount);
    }

    @Test
    @DisplayName("Проверка баланса после выдачи запрошенной суммы из нескольких кассет с одинаковым номиналом")
    void checkBalanceAfterMoneyGivingTestFromSomeCassettes() {
        ATM atm = new ATMImpl();

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));

        long requestedAmountOfMoney = (100 + 50) * BANKNOTE_DENOMINATION.DENOMITATION_500.getValue();

        atm.giveMoney(requestedAmountOfMoney);

        long expectedBalance = BANKNOTE_DENOMINATION.DENOMITATION_500.getValue() * (200 - (requestedAmountOfMoney / 500));

        Assertions.assertThat(atm.getBalance()).isEqualTo(expectedBalance);
    }

    @Test
    @DisplayName("Проверка баланса денег в банкомате после выдачи запрошенной суммы")
    void checkBalanceAfterMoneyGiving() {
        ATM atm = new ATMImpl();

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_500, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 100, 200));

        long requestedAmountOfMoney = 13500;

        atm.giveMoney(requestedAmountOfMoney);

        long expectedAmountOfMoney = BANKNOTE_DENOMINATION.DENOMITATION_500.getValue() * 100
                + BANKNOTE_DENOMINATION.DENOMITATION_1000.getValue() * 100
                + BANKNOTE_DENOMINATION.DENOMITATION_2000.getValue() * 100
                - requestedAmountOfMoney;
        Assertions.assertThat(atm.getBalance()).isEqualTo(expectedAmountOfMoney);
    }

    @Test
    @DisplayName("Проверка возбуждения исключения NotEnoughBanknoteCountInATMToGiveRequestedAmountException при отсутствии необходимых банкнот при выдаче запрошенной суммы")
    void throwNotEnoughBanknoteCountInATMToGiveRequestedAmountExceptionOnMoneyGiving() {
        ATM atm = new ATMImpl();

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 100, 200));
        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_2000, 100, 200));

        long requestedAmountOfMoney = 13500;

        Assertions.assertThatThrownBy(() -> atm.giveMoney(requestedAmountOfMoney))
                .isInstanceOf(NotEnoughBanknoteCountInATMToGiveRequestedAmountException.class);
    }

    @Test
    @DisplayName("Проверка возбуждения исключения IllegalArgumentException при передачи некорректной суммы необходимой для снятия")
    void throwIllegalArgumentExceptionOnMoneyGiving() {
        ATM atm = new ATMImpl();

        Assertions.assertThatThrownBy(() -> atm.giveMoney(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Проверка возбуждения исключения NotEnoughBanknoteCountInATMException при запросе слишком большой суммы для снятия")
    void throwNotEnoughBanknoteCountInATMExceptionOnMoneyGiving() {
        ATM atm = new ATMImpl();

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 1, 1));
        Assertions.assertThatThrownBy(() -> atm.giveMoney(2000))
                .isInstanceOf(NotEnoughBanknoteCountInATMException.class);
    }

    @Test
    @DisplayName("Проверка возбуждения исключения RequestedAmountOfMoneyIsTooSmallException при запросе слишком маленькой суммы для снятия")
    void throwRequestedAmountOfMoneyIsTooSmallExceptionOnMoneyGiving() {
        ATM atm = new ATMImpl();

        atm.addBanknoteCassette(new BanknoteCassetteImpl(BANKNOTE_DENOMINATION.DENOMITATION_1000, 1, 1));
        Assertions.assertThatThrownBy(() -> atm.giveMoney(1))
                .isInstanceOf(RequestedAmountOfMoneyIsTooSmallException.class);
    }
}
