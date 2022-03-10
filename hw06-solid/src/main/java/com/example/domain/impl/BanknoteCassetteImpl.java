package com.example.domain.impl;

import com.example.constant.BANKNOTE_DENOMINATION;
import com.example.domain.BanknoteCassette;
import com.example.exceptions.NotEnoughBanknoteCountInCassetteException;
import com.example.exceptions.NotEnoughFreeSpaceInCassetteException;

public class BanknoteCassetteImpl implements BanknoteCassette {
    private static final int DEFAULT_CAPACITY = 1000;

    private final BANKNOTE_DENOMINATION banknoteDenomination;
    private final int capacity;
    private int currentBanknoteCount;

    @Override
    public BANKNOTE_DENOMINATION getBanknoteDenomination() {
        return this.banknoteDenomination;
    }

    @Override
    public int getCurrentBanknoteCount() {
        return this.currentBanknoteCount;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public long getBalance() {
        return (long) this.banknoteDenomination.getValue() * this.currentBanknoteCount;
    }

    @Override
    public int getAvailableSpace() {
        return this.capacity - this.currentBanknoteCount;
    }

    @Override
    public void addBanknotes(int banknoteCount) {
        if (banknoteCount < 0) {
            throw new IllegalArgumentException(String.format("Passed incorrect banknote count to append (%d)", banknoteCount));
        }

        if (this.currentBanknoteCount + banknoteCount > this.capacity) {
            throw new NotEnoughFreeSpaceInCassetteException(banknoteCount, this.capacity);
        }

        this.currentBanknoteCount += banknoteCount;
    }

    @Override
    public void removeBanknotes(int banknoteCount) {
        if (banknoteCount < 0) {
            throw new IllegalArgumentException(String.format("Passed incorrect banknote count to remove (%d)", banknoteCount));
        }

        if (this.currentBanknoteCount < banknoteCount) {
            throw new NotEnoughBanknoteCountInCassetteException();
        }
        this.currentBanknoteCount -= banknoteCount;
    }

    public BanknoteCassetteImpl(BANKNOTE_DENOMINATION banknoteDenomination, int currentBanknoteCount, int capacity) {
        if (banknoteDenomination == null) {
            throw new IllegalArgumentException("Passed incorrect value (null) of banknote denomination");
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException(String.format("Incorrect value of cassette capacity (%d)", capacity));
        }

        if (currentBanknoteCount < 0) {
            throw new IllegalArgumentException(String.format("Incorrect value of banknote count in cassette (%d)", currentBanknoteCount));
        }

        if (currentBanknoteCount > capacity) {
            throw new NotEnoughFreeSpaceInCassetteException(currentBanknoteCount, capacity);
        }

        this.banknoteDenomination = banknoteDenomination;
        this.capacity = capacity;
        this.currentBanknoteCount = currentBanknoteCount;
    }

    public BanknoteCassetteImpl(BANKNOTE_DENOMINATION banknoteDenomination, int currentBanknoteCount) {
        this(banknoteDenomination, currentBanknoteCount, DEFAULT_CAPACITY);
    }

    public BanknoteCassetteImpl(BANKNOTE_DENOMINATION banknoteDenomination) {
        this(banknoteDenomination, 0, DEFAULT_CAPACITY);
    }
}
