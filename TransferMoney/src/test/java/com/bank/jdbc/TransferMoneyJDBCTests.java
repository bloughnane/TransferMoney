package com.bank.jdbc;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import com.bank.objects.Transaction;
import com.bank.objects.TransferDetails;
import com.bank.jdbc.TransferMoneyJDBC;

//Test money transfer between various accounts and currencies
public class TransferMoneyJDBCTests {
	
	private TransferMoneyJDBC transferMoneyJDBC;
	private TransferDetails transferDetails;
	private Transaction transaction;
	
	@Before
	public void setup() {
		transferMoneyJDBC = new TransferMoneyJDBC();
		transferDetails = new TransferDetails();
	}
	
	@Test
	//Transfer £555.55 from account balance £1000 to account balance £500
	public void sameCurrencyMoneyTransferGBP() {
		transferDetails.setFromAccountNumber(1111111);
		transferDetails.setToAccountNumber(2222222);
		transferDetails.setAmount(new BigDecimal("555.55"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("444.45"));
		assertEquals(transaction.getToBalance(), new BigDecimal("1055.55"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("1.0000000000"));
	}
	
	@Test
	//Transfer €444.44 from account balance €1000 to account balance €500
	public void sameCurrencyMoneyTransferEUR() {
		transferDetails.setFromAccountNumber(7777777);
		transferDetails.setToAccountNumber(8888888);
		transferDetails.setAmount(new BigDecimal("444.44"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("555.56"));
		assertEquals(transaction.getToBalance(), new BigDecimal("944.44"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("1.0000000000"));
	}
	
	@Test
	//Transfer $666.66 from account balance $1000 to account balance $500
	public void sameCurrencyMoneyTransferUSD() {
		transferDetails.setFromAccountNumber(4545454);
		transferDetails.setToAccountNumber(5656565);
		transferDetails.setAmount(new BigDecimal("666.66"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("333.34"));
		assertEquals(transaction.getToBalance(), new BigDecimal("1166.66"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("1.0000000000"));
	}
	
	@Test
	//Transfer £111.11 from account balance £250 to account balance €250
	public void MoneyTransferFromGBPToEUR() {
		transferDetails.setFromAccountNumber(3333333);
		transferDetails.setToAccountNumber(9999999);
		transferDetails.setAmount(new BigDecimal("111.11"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("138.89"));
		assertEquals(transaction.getToBalance(), new BigDecimal("374.96"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("1.1246063878"));
	}
	
	@Test
	//Transfer €99.99 from account balance €200 to account balance £200
	public void MoneyTransferFromEURToGBP() {
		transferDetails.setFromAccountNumber(1212121);
		transferDetails.setToAccountNumber(4444444);
		transferDetails.setAmount(new BigDecimal("99.99"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("100.01"));
		assertEquals(transaction.getToBalance(), new BigDecimal("288.91"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("0.8892000000"));
	}
	
	@Test
	//Transfer £77.77 from account balance £100 to account balance $250
	public void MoneyTransferFromGBPToUSD() {
		transferDetails.setFromAccountNumber(5555555);
		transferDetails.setToAccountNumber(6767676);
		transferDetails.setAmount(new BigDecimal("77.77"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("22.23"));
		assertEquals(transaction.getToBalance(), new BigDecimal("345.88"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("1.2329059829"));
	}
	
	@Test
	//Transfer $129.99 from account balance $200 to account balance £50
	public void MoneyTransferFromUSDToGBP() {
		transferDetails.setFromAccountNumber(7878787);
		transferDetails.setToAccountNumber(6666666);
		transferDetails.setAmount(new BigDecimal("129.99"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("70.01"));
		assertEquals(transaction.getToBalance(), new BigDecimal("155.43"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("0.8110918544"));
	}
	
	@Test
	//Transfer €76.98 from account balance €100 to account balance $100
	public void MoneyTransferFromEURToUSD() {
		transferDetails.setFromAccountNumber(2323232);
		transferDetails.setToAccountNumber(8989898);
		transferDetails.setAmount(new BigDecimal("76.98"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("23.02"));
		assertEquals(transaction.getToBalance(), new BigDecimal("184.39"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("1.0963000000"));
	}
	
	@Test
	//Transfer $39.95 from account balance $50 to account balance €50
	public void MoneyTransferFromUSDToEUR() {
		transferDetails.setFromAccountNumber(9090909);
		transferDetails.setToAccountNumber(3434343);
		transferDetails.setAmount(new BigDecimal("39.95"));
		try {
			transaction = transferMoneyJDBC.TransferMoney(transferDetails);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(transaction.getFromBalance(), new BigDecimal("10.05"));
		assertEquals(transaction.getToBalance(), new BigDecimal("86.44"));
		assertEquals(transaction.getExchangeRate(), new BigDecimal("0.9121590806"));
	}
}
