package com.bank.objects;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.bank.objects.TransferDetails;

//Transaction details used to store successful transactions and to respond with success or failed transaction result
public class Transaction extends TransferDetails {
	
	int id;
	Timestamp timeStamp;
	boolean successful;
	BigDecimal exchangeRate;
	BigDecimal toAmount;
	BigDecimal fromBalance;
	BigDecimal toBalance;
	String errorMessage;
	
	public Transaction(){
	}
	
	public Transaction(TransferDetails transferDetails) {
		id = 0;
		successful = false;
		timeStamp = new Timestamp(System.currentTimeMillis());
		fromAccountNumber = transferDetails.getFromAccountNumber();
		toAccountNumber = transferDetails.getToAccountNumber();
		amount = transferDetails.getAmount();
		reference = transferDetails.getReference();
		exchangeRate = new BigDecimal(0);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getToAmount() {
		return toAmount;
	}

	public void setToAmount(BigDecimal toAmount) {
		this.toAmount = toAmount;
	}

	public BigDecimal getFromBalance() {
		return fromBalance;
	}

	public void setFromBalance(BigDecimal fromBalance) {
		this.fromBalance = fromBalance;
	}

	public BigDecimal getToBalance() {
		return toBalance;
	}

	public void setToBalance(BigDecimal toBalance) {
		this.toBalance = toBalance;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	@Override
	public String toString() {
		//successful response
		if(successful) {
			return "{\"transactionID\":\"" + id + "\", \"fromAccountNumber\":\"" + fromAccountNumber + "\", \"toAccountNumber\":\"" + toAccountNumber + "\", \"fromAmount\":\"" + amount + 
					"\", \"exchangeRate\":\"" + exchangeRate + "\", \"toAmount\":\"" + toAmount +"\"" + "\", \"reference\":\"" + reference + "\"}";
		//failed response
		}else {
			return "{\"errorMessage\":\"" + errorMessage + "\", \"fromAccountNumber\":\"" + fromAccountNumber + "\", \"toAccountNumber\":\"" + toAccountNumber + "\", \"amount\":\"" + amount + 
					"\", \"reference\":\"" + reference + "\"}";
		}
	}


}
