package com.labaway.backend.bdd.steps;

import com.labaway.backend.bdd.TestApiClient;
import com.labaway.backend.dto.product.main.ProductPreviewDto;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSteps {

    @Autowired
    private TestApiClient api;

    @When("I get all products")
    public void i_get_all_products() {

        ResponseEntity<ProductPreviewDto[]> res =
                api.get(
                        "/api/products",
                        new HttpHeaders(),
                        ProductPreviewDto[].class);

        Arrays.stream(res.getBody())
                .forEach(System.out::println);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
