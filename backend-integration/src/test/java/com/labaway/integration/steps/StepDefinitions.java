package com.labaway.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinitions {

    private String greeting;

    @Given("the system is running")
    public void systemIsRunning() {
        // Simulate system up (could call your Spring Boot app context)
    }

    @When("I say hello")
    public void iSayHello() {
        greeting = "Hello, Labaway!";
    }

    @Then("I should get {string}")
    public void iShouldGet(String expected) {
        assertEquals(expected, greeting);
    }
}
