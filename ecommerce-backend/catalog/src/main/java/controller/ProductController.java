package controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.Product;
import service.IProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final IProductService productService;

	@Autowired
	public ProductController(IProductService productService) {
		super();
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody Product product) {
		System.out.println("Producto recibido: " + product);
		if (product.getCategory() == null || product.getCategory().getId() == null) {
			return ResponseEntity.badRequest().body("La categoría es obligatoria y debe tener un ID válido");
		}
		return new ResponseEntity<>(productService.createProduct(product), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		Optional<Product> product = productService.getProductById(id);
		return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updaProduct) {
		return ResponseEntity.ok(productService.updateProduct(id, updaProduct));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

}
