package com.example.productservice.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final Map<Long, Product> products = new ConcurrentHashMap<>(Map.of(
        1L, new Product(1L, "Laptop"),
        2L, new Product(2L, "Keyboard")));

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Product> list() {
        return products.values().stream().sorted(java.util.Comparator.comparing(Product::id)).toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return products.remove(id) == null ? ResponseEntity.notFound().build() : ResponseEntity.ok().build();
    }

    public record Product(long id, String name) {}
}
