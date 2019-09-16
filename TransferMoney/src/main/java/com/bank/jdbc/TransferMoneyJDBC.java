package com.bank.jdbc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jetty.util.log.Slf4jLog;

import com.bank.objects.Transaction;
import com.bank.objects.TransferDetails;

public class TransferMoneyJDBC {
	
	private Connection dbConnection;
	private String DB_DRIVER = "org.h2.Driver";
	private String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;IFEXISTS=TRUE";
	private String DB_USER = "";
	private String DB_PASSWORD = "";
	private Slf4jLog logger;

	public TransferMoneyJDBC() {
		dbConnection = getDBConnection();
		try {
			logger = new Slf4jLog();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Connection getDBConnection() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }
	
	//Transfers the money between accounts - checks if the account exists, there are sufficient funds and calculates the currency conversion. 
	public Transaction TransferMoney(TransferDetails transferDetails) throws SQLException {
		
		Transaction transaction = new Transaction(transferDetails);
		
		try {
			//prevent multiple account updates at the same time
			dbConnection.setAutoCommit(false);
			
			int i = 0;
			ResultSet resultSet = null;
			BigDecimal balanceFrom = null;
			BigDecimal exchangeRateFrom = null;
			BigDecimal balanceTo = null;
			BigDecimal exchangeRateTo = null;
			
			//check different account
			if(transferDetails.getFromAccountNumber() != transferDetails.getToAccountNumber()) {
				//retrieve from and to account details
				String retrieveAccountDetails = "select Account.number, Account.balance, Currency.exchangeRate from Account "
						+ "INNER JOIN Currency ON Account.CurrencyID=Currency.ID "
						+ "where number=? or number=?";
				PreparedStatement retrieveAccountDetailsPreparedStatement = dbConnection.prepareStatement(retrieveAccountDetails);
				
				retrieveAccountDetailsPreparedStatement.setInt(1, transferDetails.getFromAccountNumber());
				retrieveAccountDetailsPreparedStatement.setInt(2, transferDetails.getToAccountNumber());
				resultSet = retrieveAccountDetailsPreparedStatement.executeQuery();
				
				while (resultSet.next()) {
					logger.debug("Account number " + resultSet.getInt("number") + " balance " + resultSet.getBigDecimal(2)+ " exchangeRate " + resultSet.getBigDecimal(3));
					if(transferDetails.getFromAccountNumber() == resultSet.getInt("number")) {
						balanceFrom = resultSet.getBigDecimal(2);
						exchangeRateFrom = resultSet.getBigDecimal(3);
					}else if(transferDetails.getToAccountNumber() == resultSet.getInt("number")) {
						balanceTo = resultSet.getBigDecimal(2);
						exchangeRateTo = resultSet.getBigDecimal(3);
					}
					i++;
	            }
				retrieveAccountDetailsPreparedStatement.close();
				
			}
			
			// if both accounts exist and the from account has sufficient funds to transfer
			if(i == 2 && transferDetails.getAmount().compareTo(balanceFrom)<=0) {
				//calculate exchange rate between accounts and update account balances
				transaction.setExchangeRate(exchangeRateTo.divide(exchangeRateFrom,10,RoundingMode.HALF_EVEN));
				transaction.setToAmount(transferDetails.getAmount().multiply(transaction.getExchangeRate()).setScale(2,RoundingMode.HALF_EVEN));
				transaction.setToBalance(balanceTo.add(transaction.getToAmount()).setScale(2,RoundingMode.HALF_EVEN));
				transaction.setFromBalance(balanceFrom.subtract(transferDetails.getAmount()));
				
				String updateAccountBalance = "Update Account set balance=? where number=?";
				PreparedStatement updateAccountBalancePreparedStatement = dbConnection.prepareStatement(updateAccountBalance);
				
				updateAccountBalancePreparedStatement.setBigDecimal(1, transaction.getFromBalance());
				updateAccountBalancePreparedStatement.setInt(2, transferDetails.getFromAccountNumber());
				updateAccountBalancePreparedStatement.executeUpdate();
				
				updateAccountBalancePreparedStatement.setBigDecimal(1, transaction.getToBalance());
				updateAccountBalancePreparedStatement.setInt(2, transferDetails.getToAccountNumber());
				updateAccountBalancePreparedStatement.executeUpdate();
				
				updateAccountBalancePreparedStatement.close();
				
				transaction.setSuccessful(true);
				
				//store the transaction details
				Statement storeTransactionStatement = dbConnection.createStatement();
				storeTransactionStatement.execute(("Insert into Transaction(fromAccountNumber, toAccountNumber, amount, reference, timestamp, successful, exchangeRate, toAmount, fromBalance, toBalance)"+
						" values(" + transaction.getFromAccountNumber() + ", " + transaction.getToAccountNumber() + ", " + transaction.getAmount() + ", '" +
						transaction.getReference() + "', '" + transaction.getTimeStamp() + "', " + transaction.isSuccessful() + ", " + transaction.getExchangeRate() +
						", " + transaction.getToAmount() + ", " + transaction.getFromBalance() + ", " + transaction.getToBalance() + ")"),
						+ Statement.RETURN_GENERATED_KEYS);
				//get transaction ID
				resultSet = storeTransactionStatement.getGeneratedKeys();
				while (resultSet.next()) {
					transaction.setId(resultSet.getInt(1));
				}
				storeTransactionStatement.close();
			}else {
				//invalid details
				if(transferDetails.getFromAccountNumber() == transferDetails.getToAccountNumber()) {
					logger.debug("Can't transfer to the same account");
					transaction.setErrorMessage("Not allowed to transfer to the same account");
				}else if(i != 2) {
					if(balanceFrom==null) {
						logger.debug("Invalid from account number entered");
						transaction.setErrorMessage("Invalid from account number entered");
					}else {
						logger.debug("Invalid to account number entered");
						transaction.setErrorMessage("Invalid to account number entered");
					}
				}else {
					logger.debug("Insufficient funds");
					transaction.setErrorMessage("Insufficient funds");
				}
			}
		
			//finish transaction
			dbConnection.commit();
			if(resultSet != null) {
				resultSet.close();
			}
			
		}catch (Exception e) {
			//rollback transaction
			dbConnection.rollback();
			logger.info("Error accessing data");
			e.printStackTrace();
			transaction.setErrorMessage("Unexpected error occured");
			transaction.setSuccessful(false);
		} finally {
            dbConnection.close();
		}
		return transaction;
	}
	
	
}
