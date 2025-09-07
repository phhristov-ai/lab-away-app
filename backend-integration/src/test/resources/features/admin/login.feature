Feature: Admin login

  Scenario Outline: Admin attempts to log in
    When the admin logs in with username "<username>" and password "<password>"
    Then the response status should be <status>
    And the response should <expectToken>

    Examples:
      | username | password          | status | expectToken      |
      | admin    | Allahepedal911%   | 200    | contain a token  |
      | admin    | wrongpass         | 401    | not contain token|
      | invalid  | password123       | 401    | not contain token|
