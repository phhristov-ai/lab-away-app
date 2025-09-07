Feature: Admin login

  Scenario Outline: Admin logs in
    When the admin logs in with username "<username>" and password "<password>"
    Then the login should <result>

    Examples:
      | username         | password         | result  |
      | <validUsername>  | <validPassword>  | succeed |
      | <validUsername>  | wrongpass        | fail    |
      | wrong            | <validPassword>  | fail    |