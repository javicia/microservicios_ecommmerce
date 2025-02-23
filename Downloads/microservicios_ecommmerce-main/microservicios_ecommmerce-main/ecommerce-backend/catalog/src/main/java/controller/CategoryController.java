package controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import model.Category;
import service.ICategoryService;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

	private ICategoryService categoryService;

	@Autowired
	public CategoryController(ICategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}

	@PostMapping
	public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
		if (category.getName() == null || category.getName().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("El campo 'name' es obligatorio");
		}
		return ResponseEntity.ok(categoryService.createCategory(category));

	}

	@GetMapping
	public ResponseEntity<List<Category>> getAllcategory() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Category> getCategoryId(@PathVariable Long id) {
		Optional<Category> category = categoryService.getCategoryId(id);
		return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

}
