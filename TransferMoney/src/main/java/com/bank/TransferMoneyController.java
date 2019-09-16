package com.bank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.util.log.Slf4jLog;

import com.bank.jdbc.TransferMoneyJDBC;
import com.bank.objects.Transaction;
import com.bank.objects.TransferDetails;

@Path("/bank")
//Transfer Money Rest API
public class TransferMoneyController {

	@POST
    @Path("transfer-money")
	@Consumes(MediaType.APPLICATION_JSON)
	//Transfer Money API accepts TransferDetails JSON Object as POST body
    public Response TransferMoneyBetweenAccounts(TransferDetails transferDetails) {
		try {
			Slf4jLog logger = new Slf4jLog();
			Transaction transaction = null;
			//Only Alphanumerics and spaces allowed in reference field
			if(!StringValidation(transferDetails.getReference())) {
				transaction = new Transaction(transferDetails);
				logger.debug("Special Characters detected in reference field");
				transaction.setErrorMessage("Only alphanumerics and spaces allowed in reference field");
			}else {
				TransferMoneyJDBC transferMoneyJDBC = new TransferMoneyJDBC();
				transaction = transferMoneyJDBC.TransferMoney(transferDetails);
			}
			
			String result;
			if(transaction.isSuccessful()) {
				result = transaction.toString();
	        }else {
	        	result = transaction.toString();
	        }
	        logger.debug(transaction.toString());
			return Response.status(200).entity(result).build();
		}catch (Exception e) {
			System.out.println("Unexpected error occured");
			e.printStackTrace();
			return Response.status(400).entity("Unexpected error occured").build();
		}
    }
	
	//Validate reference input field does not contain special characters
	private boolean StringValidation(String reference) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9 ]*");
		
		 Matcher matcher = pattern.matcher(reference);
		 
		 return matcher.matches();
		 
	}
	
}
