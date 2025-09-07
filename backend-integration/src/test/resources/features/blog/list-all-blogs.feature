Feature: List all blog previews

  Scenario Outline: A user requests all blog previews in a given language
    When the user requests all blog previews in language "<lang>"
    Then the response should contain a blog with title "<title>"
    And the blog should have category with name "<categoryName>"

    Examples:
      | lang | title                                               | categoryName |
      | EN   | Recognizing, Treating, and preventing Gonorrhea     | STD Tests    |
