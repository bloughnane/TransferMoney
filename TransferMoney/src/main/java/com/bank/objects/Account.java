package com.bank.objects;

import java.math.BigDecimal;

//Basic bank account details
public class Account {
	
	int number;
	
	int currencyID;
	
	BigDecimal balance;
	
	public Account() {
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(int currencyID) {
		this.currencyID = currencyID;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "Account [number=" + number + ", currencyID=" + currencyID + ", balance=" + balance + "]";
	}

}
