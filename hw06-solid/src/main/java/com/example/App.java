package com.example;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.domain.ATM;
import com.example.domain.ATMWithCashIn;
import com.example.domain.BanknoteAcceptable;
import com.example.domain.impl.ATMWithCashInImpl;
import com.example.domain.impl.BanknoteCassetteImpl;
import com.example.dto.BanknoteBundleDTO;

import java.util.Collection;

public class App {
    public static void main(String[] args) {
        ATMWithCashIn atm = new ATMWithCashInImpl();

        printATMBalance(atm);
        addBanknoteCassetteAndPrintCassetteParams(atm, BANKNOTE_DENOMINATION.DENOMITATION_500, 1000, 1000);
        printATMBalance(atm);
        addBanknoteCassetteAndPrintCassetteParams(atm, BANKNOTE_DENOMINATION.DENOMITATION_500, 1000, 1000);
        printATMBalance(atm);
        receiveMoneyAndPrintBanknoteBundleStructure(atm, (1000 + 500) * BANKNOTE_DENOMINATION.DENOMITATION_500.getValue());
        printATMBalance(atm);

        addBanknoteCassetteAndPrintCassetteParams(atm, BANKNOTE_DENOMINATION.DENOMITATION_1000, 500, 500);
        printATMBalance(atm);
        addBanknoteCassetteAndPrintCassetteParams(atm, BANKNOTE_DENOMINATION.DENOMITATION_2000, 500, 500);
        printATMBalance(atm);
        addBanknoteCassetteAndPrintCassetteParams(atm, BANKNOTE_DENOMINATION.DENOMITATION_5000, 300, 300);
        printATMBalance(atm);
        addBanknoteCassetteAndPrintCassetteParams(atm, BANKNOTE_DENOMINATION.DENOMITATION_10000, 100, 100);
        printATMBalance(atm);

        Collection<BanknoteBundleDTO> banknoteBundles = receiveMoneyAndPrintBanknoteBundleStructure(atm, 59500);
        printATMBalance(atm);

        addBanknotesIntoATM(atm, banknoteBundles);
        printATMBalance(atm);
    }

    private static void addBanknoteCassetteAndPrintCassetteParams(ATM atm, BANKNOTE_DENOMINATION banknoteDenomination, int banknoteCount, int cassetteCapacity) {
        atm.addBanknoteCassette(new BanknoteCassetteImpl(banknoteDenomination, banknoteCount, cassetteCapacity));
        System.out.println(
                String.format("Added cassette: banknote denomination: %d, banknote count: %d, cassette capacity: %d, amount of money in cassette: %d",
                        banknoteDenomination.getValue(), banknoteCount, cassetteCapacity, banknoteDenomination.getValue() * banknoteCount
                )
        );
    }

    private static void printATMBalance(ATM atm) {
        System.out.println(String.format("ATM balance: %d", atm.getBalance()));
    }

    private static Collection<BanknoteBundleDTO> receiveMoneyAndPrintBanknoteBundleStructure(ATM atm, long requestedAmountOfMoney) {
        System.out.println("--------------------------------------------------");
        System.out.println(String.format("Requested amount of money: %d", requestedAmountOfMoney));
        System.out.println("Received banknotes: ");
        Collection<BanknoteBundleDTO> banknoteBundles = atm.giveMoney(requestedAmountOfMoney);
        banknoteBundles.forEach(item -> {
                    System.out.println(String.format("Denomination: %d, banknote count: %d", item.getBanknoteDenomination().getValue(), item.getBanknoteCount()));
                });
        System.out.println("--------------------------------------------------");

        return banknoteBundles;
    }

    private static void addBanknotesIntoATM(BanknoteAcceptable banknoteAcceptable, Collection<BanknoteBundleDTO> banknoteBundles) {
        banknoteAcceptable.acceptBanknotes(banknoteBundles);

        long addedAmountOfMoney = banknoteBundles.stream()
                .reduce(
                        0,
                        (amount, banknoteBundle) -> amount + banknoteBundle.getBanknoteCount() * banknoteBundle.getBanknoteDenomination().getValue(),
                        Integer::sum
                );

        System.out.println("--------------------------------------------------");
        System.out.println(String.format("Added amount of money: %d", addedAmountOfMoney));
        System.out.println("Added banknotes: ");
        banknoteBundles.forEach(item -> {
            System.out.println(String.format("Denomination: %d, banknote count: %d", item.getBanknoteDenomination().getValue(), item.getBanknoteCount()));
        });
        System.out.println("--------------------------------------------------");
    }
}
