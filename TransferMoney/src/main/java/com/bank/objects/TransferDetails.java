package com.bank.objects;

import java.math.BigDecimal;

//TransferDetails control the input types allowed to the Transfer Money API
public class TransferDetails {

	int fromAccountNumber;
	int toAccountNumber;
	BigDecimal amount;
	String reference;
	
	public TransferDetails(){		
	}
	
	public int getFromAccountNumber() {
		return fromAccountNumber;
	}
	
	public void setFromAccountNumber(int fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}
	
	public int getToAccountNumber() {
		return toAccountNumber;
	}
	
	public void setToAccountNumber(int toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "TransferDetails [fromAccountNumber=" + fromAccountNumber + ", toAccountNumber=" + toAccountNumber + ", amount=" + amount + ", reference=" + reference + "]";
	}
	
}
