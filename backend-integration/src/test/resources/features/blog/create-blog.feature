Feature: Blog creation

  Background:
    Given the admin logs in with username "<validUsername>" and password "<validPassword>"

  Scenario Outline: Admin creates a blog post with multilingual support and optional image
    When the admin submits a new blog post with:
      | language | <language> |
      | title    | <title>    |
      | content  | <content>  |
      | image    | <filename> |
    Then the blog should be created successfully with:
      | title   | <title>     |
      | content | <content>   |
      | language | <language> |

    Examples:
      | language | title        | content       | filename           |
      | EN       | Test Blog    | Hello world   | Gonorrhea.Test.png |