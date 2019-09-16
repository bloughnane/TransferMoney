package com.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

//Tests for Transfer Money Rest API
public class TransferMoneyControllerTests extends JerseyTest{
	
	private static String DB_DRIVER = "org.h2.Driver";
    private static String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static String DB_USER = "";
    private static String DB_PASSWORD = "";
    
    @Override
    protected Application configure() {
        return new ResourceConfig(TransferMoneyController.class);
    }
	
	@BeforeClass
	public static void setup() {
		setupDatabase();
	}
	
	@Test
	public void transactionSuccessful() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1010101\",\"toAccountNumber\":\"2020202\""
		        		+ ",\"amount\":\"5.55\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 200 ", 200, response.getStatus());
		assertTrue(response.readEntity(String.class).equals("{\"transactionID\":\"1\", \"fromAccountNumber\":\"1010101\", \"toAccountNumber\":\"2020202\", \"fromAmount\":\"5.55\", \"exchangeRate\":\"1.0000000000\", \"toAmount\":\"5.55\"\", \"reference\":\"test\"}"));
	}
	
	@Test
	public void insufficientFunds() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1010101\",\"toAccountNumber\":\"2020202\""
		        		+ ",\"amount\":\"1200.55\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 200 ", 200, response.getStatus());
		assertTrue(response.readEntity(String.class).equals("{\"errorMessage\":\"Insufficient funds\", \"fromAccountNumber\":\"1010101\", \"toAccountNumber\":\"2020202\", \"amount\":\"1200.55\", \"reference\":\"test\"}"));
	}
	
	@Test
	public void unableToTransferToSameAccount() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1010101\",\"toAccountNumber\":\"1010101\""
		        		+ ",\"amount\":\"500.55\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 200 ", 200, response.getStatus());
		assertTrue(response.readEntity(String.class).equals("{\"errorMessage\":\"Not allowed to transfer to the same account\", \"fromAccountNumber\":\"1010101\", \"toAccountNumber\":\"1010101\", \"amount\":\"500.55\", \"reference\":\"test\"}"));
	}
	
	@Test
	public void fromAccountDoesNotExist() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1234567\",\"toAccountNumber\":\"2020202\""
		        		+ ",\"amount\":\"500.55\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 200 ", 200, response.getStatus());
		assertTrue(response.readEntity(String.class).equals("{\"errorMessage\":\"Invalid from account number entered\", \"fromAccountNumber\":\"1234567\", \"toAccountNumber\":\"2020202\", \"amount\":\"500.55\", \"reference\":\"test\"}"));
	}
	
	@Test
	public void toAccountDoesNotExist() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1010101\",\"toAccountNumber\":\"2345678\""
		        		+ ",\"amount\":\"500.55\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 200 ", 200, response.getStatus());
		assertTrue(response.readEntity(String.class).equals("{\"errorMessage\":\"Invalid to account number entered\", \"fromAccountNumber\":\"1010101\", \"toAccountNumber\":\"2345678\", \"amount\":\"500.55\", \"reference\":\"test\"}"));
	}
	
	@Test
	public void invalidInputFormatFromAccount() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"a\",\"toAccountNumber\":\"2020202\""
		        		+ ",\"amount\":\"500.55\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 400 ", 400, response.getStatus());
	}
	
	@Test
	public void invalidInputFormatToAccount() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1010101\",\"toAccountNumber\":\"b\""
		        		+ ",\"amount\":\"500.55\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 400 ", 400, response.getStatus());
	}
	
	@Test
	public void invalidInputFormatAmount() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1010101\",\"toAccountNumber\":\"2020202\""
		        		+ ",\"amount\":\"a\",\"reference\":\"test\"}"));
		 
		assertEquals("Http Response should be 400 ", 400, response.getStatus());
	}
	
	@Test
	public void invalidInputFormatReference() {
		Response response = target("bank/transfer-money").request()
		        .post(Entity.json("{\"fromAccountNumber\":\"1010101\",\"toAccountNumber\":\"2020202\""
		        		+ ",\"amount\":\"500.55\",\"reference\":\"test *\"}"));
		 
		assertEquals("Http Response should be 200 ", 200, response.getStatus());
		assertTrue(response.readEntity(String.class).equals("{\"errorMessage\":\"Only alphanumerics and spaces allowed in reference field\", \"fromAccountNumber\":\"1010101\", \"toAccountNumber\":\"2020202\", \"amount\":\"500.55\", \"reference\":\"test *\"}"));
	}
	
	
	private static void setupDatabase(){
    	
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        }
        try {
        	Connection dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        	setupTestData(dbConnection);
        } catch (Exception e) { }
    }
    
    private static void setupTestData(Connection dbConnection) throws SQLException {
    	 Statement stmt = null;
         try {
             stmt = dbConnection.createStatement();
             stmt.execute("CREATE TABLE Currency(id int primary key, code varchar(3), exchangeRate decimal(12,10), exchangeDate Date)");
             stmt.execute("INSERT INTO Currency(id, code, exchangeRate, exchangeDate) VALUES(1, 'GBP', 1, '2019-09-12')");
             stmt.execute("INSERT INTO Currency(id, code, exchangeRate, exchangeDate) VALUES(2, 'EUR', 1.1246063878, '2019-09-12')");
             stmt.execute("INSERT INTO Currency(id, code, exchangeRate, exchangeDate) VALUES(3, 'USD', 1.2329059829, '2019-09-12')");
             
             stmt.execute("CREATE TABLE Account(number int primary key, currencyID int, balance decimal(10, 2))");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(1010101, 1, 1000.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(2020202, 1, 500.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(1111111, 1, 1000.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(2222222, 1, 500.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(3333333, 1, 250.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(4444444, 1, 200.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(5555555, 1, 100.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(6666666, 1, 50.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(7777777, 2, 1000.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(8888888, 2, 500.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(9999999, 2, 250.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(1212121, 2, 200.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(2323232, 2, 100.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(3434343, 2, 50.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(4545454, 3, 1000.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(5656565, 3, 500.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(6767676, 3, 250.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(7878787, 3, 200.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(8989898, 3, 100.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(9090909, 3, 50.00)");
             
             stmt.execute("CREATE TABLE Transaction(id int primary key AUTO_INCREMENT, fromAccountNumber int, toAccountNumber int, amount decimal(10, 2), "
             		+ "reference varchar(100), timestamp timestamp, successful boolean, exchangeRate decimal(12,10), toAmount decimal(10,2), "
            		+ "fromBalance decimal(10, 2), toBalance decimal(10, 2))");
             
             stmt.close();
         } catch (SQLException e) {
        	 
         } catch (Exception e) {
        	 
         } finally {
             dbConnection.close();
         }
    }


}
