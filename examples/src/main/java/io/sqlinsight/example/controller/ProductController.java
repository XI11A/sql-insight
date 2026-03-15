package io.sqlinsight.example.controller;

import io.sqlinsight.example.entity.Product;
import io.sqlinsight.example.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/slow")
    public List<Product> getSlow() {
        return productService.getProductsSlowly();
    }

    @PostMapping
    public Product create(@RequestParam String name, @RequestParam double price) {
        return productService.createProductSilently(name, price);
    }

    @GetMapping("/n-plus-one")
    public String triggerNPlusOne() {
        productService.triggerNPlusOne();
        return "N+1 triggered";
    }
}
