package io.sqlinsight.example.service;

import io.sqlinsight.annotations.DisableQueryTracking;
import io.sqlinsight.annotations.QueryLabel;
import io.sqlinsight.annotations.QueryTrace;
import io.sqlinsight.annotations.SlowQueryAlert;
import io.sqlinsight.example.entity.Product;
import io.sqlinsight.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryTrace("List All Products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @QueryLabel("Fetch Single Product")
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @SlowQueryAlert(threshold = 10)
    public List<Product> getProductsSlowly() {
        // This will be fast but since threshold is 10ms, any DB query might trigger it
        return productRepository.findAll();
    }

    @DisableQueryTracking
    @Transactional
    public Product createProductSilently(String name, double price) {
        return productRepository.save(new Product(name, price));
    }

    @Transactional
    public void triggerNPlusOne() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            // Re-fetching per item to trigger N+1 detection
            productRepository.findById(product.getId());
        }
    }
}
