package com.labaway.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    @GetMapping
    public List<ProductPreviewDto> getAll(@RequestParam(defaultValue = "EN") Language lang) {
        return productService.getAllProductPreviews(lang);
    }

    @GetMapping("/random")
    public List<ProductPreviewDto> getRandomProducts(@RequestParam(defaultValue = "EN") Language lang) {
        return productService.getRandomProductPreviews(lang);
    }

    @GetMapping("/random-by-category")
    public List<ProductPreviewDto> getRandomProductsByCategory(
            @RequestParam(required = false) String slug,
            @RequestParam(defaultValue = "EN") Language lang) {
        return productService.getRandomProductPreviewsByCategorySlug(slug, lang);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProductDto> getBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "EN") Language lang) {
        ProductDto productDto = productService.getProductBySlug(slug, lang);
        if (productDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(productDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> createProductWithImages(
            @RequestPart(value = "product") String productJson,
            @RequestPart("files") MultipartFile[] files) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            ProductPayloadDto productPayloadDto = mapper.readValue(productJson, ProductPayloadDto.class);
            ProductDto createdProduct = productService.createProduct(productPayloadDto, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String slug,
            @RequestPart("product") String productJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            ProductPayloadDto updateProductDto = mapper.readValue(productJson, ProductPayloadDto.class);

            ProductDto updatedProduct = productService.updateProduct(slug, updateProductDto, files);
            if (updatedProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> delete(@PathVariable String slug) {
        productService.deleteProductBySlug(slug);
        return ResponseEntity.noContent().build();
    }
}
