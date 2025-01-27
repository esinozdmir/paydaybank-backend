package com.infonal.paydaybank.service;

import com.infonal.paydaybank.model.Product;
import com.infonal.paydaybank.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> getAllProductRepository(){
        return productRepository.findAll();
    }

    public double topla(){
        List<Double> prices = productRepository.getAllPrices();
        return prices.stream().mapToDouble(Double::doubleValue).sum();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setAvailable(product.getAvailable());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImage(product.getImage());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}
