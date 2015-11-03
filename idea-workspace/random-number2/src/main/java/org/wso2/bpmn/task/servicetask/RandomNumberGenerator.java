package org.wso2.bpmn.task.servicetask;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class RandomNumberGenerator implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        if(Math.random() < 0.5){
            delegateExecution.setVariable("randomValue", 0L);
        }else {
            delegateExecution.setVariable("randomValue", 1L);
        }
        delegateExecution.setVariable("increment", 0L);
    }
}
