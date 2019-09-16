package com.bank.objects;

import java.math.BigDecimal;
import java.sql.Date;

//Currency with exchange rate
public class Currency {
	
	int id;	
	String code;	
	BigDecimal exchangeRate;	
	Date exchangeDate;
	
	public Currency() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Date getExchangeDate() {
		return exchangeDate;
	}

	public void setExchangeDate(Date exchangeDate) {
		this.exchangeDate = exchangeDate;
	}

	@Override
	public String toString() {
		return "Currency [ID=" + id + ", code=" + code + "excahngeRate=" + exchangeRate + ", exchangeDate=" + exchangeDate +"]";
	}

}
