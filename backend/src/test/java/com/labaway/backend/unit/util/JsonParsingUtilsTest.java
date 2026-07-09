package com.labaway.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labaway.backend.dto.category.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonParsingUtilsTest {

    private JsonParsingUtils jsonParsingUtils;

    @BeforeEach
    void setUp() {
        jsonParsingUtils = new JsonParsingUtils(new ObjectMapper());
    }

    @Test
    void parseCategoryList_returnsEmptyList_forNullInput() {
        List<CategoryDto> result = jsonParsingUtils.parseCategoryList(null);
        assertThat(result).isEmpty();
    }

    @Test
    void parseCategoryList_returnsEmptyList_forEmptyString() {
        List<CategoryDto> result = jsonParsingUtils.parseCategoryList("");
        assertThat(result).isEmpty();
    }

    @Test
    void parseCategoryList_parsesValidJsonCorrectly() {
        String json = """
            [
              {"name": "Technology", "slug": "technology"},
              {"name": "Science", "slug": "science"}
            ]
            """;

        List<CategoryDto> result = jsonParsingUtils.parseCategoryList(json);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getName()).isEqualTo("Technology");
        assertThat(result.get(0).getSlug()).isEqualTo("technology");

        assertThat(result.get(1).getName()).isEqualTo("Science");
        assertThat(result.get(1).getSlug()).isEqualTo("science");
    }

    @Test
    void parseCategoryList_returnsEmptyList_forMalformedJson() {
        String malformedJson = "[{name: \"Tech\", slug: \"tech\"}";
        List<CategoryDto> result = jsonParsingUtils.parseCategoryList(malformedJson);

        assertThat(result).isEmpty();
    }
}