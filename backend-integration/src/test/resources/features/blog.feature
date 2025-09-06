Feature: Blog

Feature: View blog post by slug

  Scenario Outline: A user views a blog post in a given language
    When the user views the blog post with slug "<slug>" in language "<lang>"
    Then the blog post title should be "<title>"
    And the blog post content should be "<content>"

    Examples:
      | slug                                           | lang |  title                                               | content                      |
      | recognizing-treating-and-preventing-gonorrhea  | EN   |  Recognizing, Treating, and preventing Gonorrhea     | This is test content         |
      | recognizing-treating-and-preventing-gonorrhea  | DE   |  Recognizing, Treating, and preventing Gonorrhea     | Ceci est un contenu de test  |


