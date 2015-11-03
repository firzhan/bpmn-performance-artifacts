package org.camunda.bpm.loanapproval;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ProcessRequestDelegate implements JavaDelegate{

	private final static Logger LOGGER = Logger.getLogger("LOAN-REQUESTS");
	
	public void execute(DelegateExecution arg0) throws Exception {
		// TODO Auto-generated method stub
	    LOGGER.info("Processing request by '"+ arg0.getVariable("customerId")+"'...");

	}

}
