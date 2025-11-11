package com.abml.jpa.hibernate.service;

import com.abml.jpa.hibernate.repository.ProductRepository;


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
