package com.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;

import com.bank.TransferMoneyController;

// Main application - starts Jetty Web server and creates in memory database with test data
public class App {
	
	private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static Slf4jLog logger;
   
	
    public static void main(String[] args){
    	
    	try {
    		
    		logger = new Slf4jLog();
    		Log.setLog(logger);
    		
    		setupDatabase();
    		logger.debug("Setup Database");
    	
    		configureServer();
    		logger.debug("Configured Server");
    	
    	} catch (Exception e) {
    		logger.info("Main Error");
            e.printStackTrace();
    	}
    }
    
    private static void configureServer() throws Exception {
        
        Server jettyServer = new Server(8080);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder jerseyServlet = context.addServlet(
             org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           TransferMoneyController.class.getCanonicalName());
        
        jettyServer.setHandler(context);

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
        	logger.info("Configure Server Error");
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }
    
    private static void setupDatabase(){
    	
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
        	logger.info(e.getMessage());
        }
        try {
        	Connection dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        	setupTestData(dbConnection);
        } catch (Exception e) {
        	logger.info("Setup Database Error");
        	logger.info(e.getMessage());
        }
    }
    
    private static void setupTestData(Connection dbConnection) throws SQLException {
    	 Statement stmt = null;
         try {
        	 logger.debug("About to create data");
             stmt = dbConnection.createStatement();
             stmt.execute("CREATE TABLE Currency(id int primary key, code varchar(3), exchangeRate decimal(12,10), exchangeDate Date)");
             stmt.execute("INSERT INTO Currency(id, code, exchangeRate, exchangeDate) VALUES(1, 'GBP', 1, '2019-09-12')");
             stmt.execute("INSERT INTO Currency(id, code, exchangeRate, exchangeDate) VALUES(2, 'EUR', 1.1246063878, '2019-09-12')");
             stmt.execute("INSERT INTO Currency(id, code, exchangeRate, exchangeDate) VALUES(3, 'USD', 1.2329059829, '2019-09-12')");
             logger.debug("Setup Currency data");
             
             stmt.execute("CREATE TABLE Account(number int primary key, currencyID int, balance decimal(10, 2))");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(1111111, 1, 1000.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(2222222, 1, 500.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(3333333, 2, 250.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(4444444, 2, 200.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(5555555, 3, 100.00)");
             stmt.execute("INSERT INTO Account(number, currencyID, balance) VALUES(6666666, 3, 50.00)");
             logger.debug("Setup Account data");
             
             stmt.execute("CREATE TABLE Transaction(id int primary key AUTO_INCREMENT, fromAccountNumber int, toAccountNumber int, amount decimal(10, 2), "
             		+ "reference varchar(100), timestamp timestamp, successful boolean, exchangeRate decimal(12,10), toAmount decimal(10,2), "
            		+ "fromBalance decimal(10, 2), toBalance decimal(10, 2))");
             logger.debug("Created Transaction table");
             
             stmt.close();
         } catch (SQLException e) {
        	 logger.info("Exception Message " + e.getLocalizedMessage());
         } catch (Exception e) {
        	 logger.info("Setup Data Error");
             e.printStackTrace();
         } finally {
             dbConnection.close();
         }
    }
}
    
