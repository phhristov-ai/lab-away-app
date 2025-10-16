// src/utils/langMatcher.ts
export const supportedLanguages = ['en', 'de', 'bg'];

export const matchLang = {
  path: ':lang(en|de|bg)', // Regex match only supported languages
};
