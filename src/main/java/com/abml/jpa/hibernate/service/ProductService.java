package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ProductService {

  @Autowired private ProductRepository productRepo;

  public List<Product> getAllProducts() {
    return productRepo.findAll();
  }

  public Product getProductById(Long id) {
    return productRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
  }
}
