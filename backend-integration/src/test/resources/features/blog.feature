Feature: Blog API

  Scenario: Get blog by slug with default language
    Given the blog with slug "test-blog" exists with title "Test Blog Title" and content "This is test content"
    When I send a GET request to "/api/blogs/test-blog"
    Then the response status should be 200
    And the response JSON should have title "Test Blog Title"
    And the response JSON should have content "This is test content"

  Scenario: Get blog by slug with French language
    Given the blog with slug "test-blog" exists with title "Titre du blog de test" and content "Ceci est un contenu de test" in language "FR"
    When I send a GET request to "/api/blogs/test-blog?lang=FR"
    Then the response status should be 200
    And the response JSON should have title "Titre du blog de test"
    And the response JSON should have content "Ceci est un contenu de test"
