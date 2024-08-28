package ru.chiniakin.model.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class LogModel implements Serializable {

    private String serviceName;

    private String methodName;

    private String methodArguments;

    private String returnMeaning;

    private String exception;

    public LogModel(String serviceName, String methodName, String inputArguments, String returnMeaning, String exception) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.methodArguments = inputArguments;
        this.returnMeaning = returnMeaning;
        this.exception = exception;
    }

}
