Feature: Get random blog previews

  Scenario Outline: A user requests random blog previews in a given language
    When the user requests random blog previews in language "<lang>"
    Then the response should contain at least one blog preview
    And each blog should have a non-empty title
    And each blog should include at least one category

    Examples:
      | lang |
      | EN   |