package org.wso2.bpmn.task.servicetask;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class Incrementer implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        long count = (Long) delegateExecution.getVariable("increment");
        delegateExecution.setVariable("increment", ++count);
    }
}
