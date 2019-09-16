# TransferMoney Test application

To execute the application you can run the transfer-money-1.0.jar file. (This can be built using maven but I have included the JAR here).

The API is located at http://localhost:8080/bank/transfer-money

You can pass in the following JSON object to test a money transfer:

```JSON
{
	"fromAccountNumber": "1111111",
	"toAccountNumber": "2222222",
	"amount": "50.55",
	"reference":"test"
}
```
# There are 6 test accounts:

| Account Number | Currency | Balance |
| -------------  | ------------- | ------------- |
| 1111111   | GBP  | 1000.00 |
| 2222222   | GBP  |  500.00 |
| 3333333   | EUR  | 1000.00 |
| 4444444   | EUR  |  500.00 |
| 5555555   | USD  | 1000.00 |
| 6666666   | USD  |  500.00 |

The API is using the following currency conversion:

£1 = €1.1246063878 = $1.2329059829

(Note this is static information but could easily be updated regularly by calling a currency exchange rate api.)

# The Test application uses the following libraries

Jetty - embedded web server.

Jersey - JAX-RS API implementation.

Jackson - JSON parser

H2 - in memory database

Log4j - logging

Junit - testing

# High level logic
When the Jar is executed the Jetty server is initialised and test accounts are loaded into the H2 in memory database.
The transfer money API needs to provide a JSON object which is parsed by Jackson to convert it into a Java object. This ensure the data is in the correct format. There is further data validation on the reference field to ensure no special characters can be entered. This prevents SQL injection attacks.
Transactions are used to ensure there is data integrity during the account transfer.
The account numbers are checked to ensure they are valid.
The account balance is verified to ensure there is sufficient funds in the account.
The Exchange rate is calculated between the accounts.
Finally the money is transfered and the transaction history is stored in the database.

# Automated tests
There are automated tests to validate the Rest API and the Money Transfer logic. 
