Feature: View blog post by slug

  Scenario Outline: A user views a blog post in a given language
    When the user views the blog post with slug "<slug>" in language "<lang>"
    Then the blog post title should be "<title>"
    And the blog post content should contain "<contentSnippet>"
    And the blog post should include category with name "<categoryName>"

    Examples:
      | slug                                          | lang | title                                           | contentSnippet                         | categoryName |
      | recognizing-treating-and-preventing-gonorrhea | EN   | Recognizing, Treating, and preventing Gonorrhea | Gonorrhea is a sexually transmitted    | STD Tests    |



