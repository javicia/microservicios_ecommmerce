package service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Product;
import repository.IProductRepository;

@Service
public class ProductServiceImpl implements IProductService {

	private final IProductRepository productRepository;

	@Autowired
	public ProductServiceImpl(IProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public Product createProduct(Product product) {
		if (product == null) {
			throw new IllegalArgumentException("El producto no puede ser nulo");
		}
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Optional<Product> getProductById(Long id) {
		return Optional.ofNullable(productRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Producto con ID " + id + " no encontrado")));
	}

	@Override
	public List<Product> getProductsByCategory(Long categoryId) {
		return productRepository.findByCategoryId(categoryId);
	}

	@Override
	public Product updateProduct(Long id, Product updatedProduct) {
		return productRepository.findById(id).map(existingProduct -> {
			existingProduct.setName(updatedProduct.getName());
			existingProduct.setDescription(updatedProduct.getDescription());
			existingProduct.setPrice(updatedProduct.getPrice());
			existingProduct.setStock(updatedProduct.getStock());
			existingProduct.setCategory(updatedProduct.getCategory());
			return productRepository.save(existingProduct);
		}).orElseThrow(() -> new NoSuchElementException("Producto con ID " + id + " no encontrado"));
	}

	@Override
	public void deleteProduct(Long id) {
		if (!productRepository.existsById(id)) {
			throw new NoSuchElementException("No se puede eliminar, el producto con ID " + id + " no existe");
		}
		productRepository.deleteById(id);
	}

}
