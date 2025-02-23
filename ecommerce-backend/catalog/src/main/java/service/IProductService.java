package service;

import java.util.List;
import java.util.Optional;

import model.Product;

public interface IProductService {
	
	Product createProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    List<Product> getProductsByCategory(Long categoryId);
    Product updateProduct(Long id, Product updatedProduct);
    void deleteProduct(Long id);

}
