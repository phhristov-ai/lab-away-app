package com.labaway.integration.steps;

import io.cucumber.java.ParameterType;

public class StepParameterTypes {

    @ParameterType("succeed|fail")
    public Boolean loginOutcome(String word) {
        return "succeed".equals(word);
    }
}