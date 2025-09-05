Feature: Hello world test

  Scenario: Saying hello
    Given the system is running
    When I say hello
    Then I should get "Hello, Labaway!"