package com.labaway.backend.util;

import com.labaway.backend.dto.category.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonParsingUtils {

    private final ObjectMapper objectMapper;

    public List<CategoryDto> parseCategoryList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}