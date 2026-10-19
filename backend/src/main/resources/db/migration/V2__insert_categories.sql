
START TRANSACTION;

-- Insert categories
INSERT INTO categories (id, slug, created_at, updated_at)
VALUES
  (UNHEX(REPLACE(UUID(), '-', '')), 'drug-tests', NOW(6), NOW(6)),
  (UNHEX(REPLACE(UUID(), '-', '')), 'fertility-tests', NOW(6), NOW(6)),
  (UNHEX(REPLACE(UUID(), '-', '')), 'bundles', NOW(6), NOW(6)),
  (UNHEX(REPLACE(UUID(), '-', '')), 'single-tests', NOW(6), NOW(6)),
  (UNHEX(REPLACE(UUID(), '-', '')), 'std-tests', NOW(6), NOW(6)),
  (UNHEX(REPLACE(UUID(), '-', '')), 'vitamin-tests', NOW(6), NOW(6));

-- Insert all category translations
INSERT INTO category_translations (id, language, name, category_id)
SELECT UNHEX(REPLACE(UUID(), '-', '')), translations.language,
       translations.name, categories.id
FROM categories
JOIN (
    SELECT 'drug-tests' AS slug, 'EN' AS language, 'Drug Tests' AS name
    UNION ALL SELECT 'drug-tests', 'DE', 'Drogentests'
    UNION ALL SELECT 'drug-tests', 'BG', 'Тестове за наркотици'

    UNION ALL SELECT 'fertility-tests', 'EN', 'Fertility Tests'
    UNION ALL SELECT 'fertility-tests', 'DE', 'Fruchtbarkeitstests'
    UNION ALL SELECT 'fertility-tests', 'BG', 'Тестове за плодовитост'

    UNION ALL SELECT 'bundles', 'EN', 'Bundles'
    UNION ALL SELECT 'bundles', 'DE', 'Pakete'
    UNION ALL SELECT 'bundles', 'BG', 'Пакети'

    UNION ALL SELECT 'single-tests', 'EN', 'Single Tests'
    UNION ALL SELECT 'single-tests', 'DE', 'Einzeltests'
    UNION ALL SELECT 'single-tests', 'BG', 'Единични тестове'

    UNION ALL SELECT 'std-tests', 'EN', 'STD Tests'
    UNION ALL SELECT 'std-tests', 'DE', 'STD-Tests'
    UNION ALL SELECT 'std-tests', 'BG', 'Тестове за ППИ'

    UNION ALL SELECT 'vitamin-tests', 'EN', 'Vitamin Tests'
    UNION ALL SELECT 'vitamin-tests', 'DE', 'Vitamin-Tests'
    UNION ALL SELECT 'vitamin-tests', 'BG', 'Тестове за витамини'
) AS translations ON translations.slug = categories.slug;

COMMIT;
