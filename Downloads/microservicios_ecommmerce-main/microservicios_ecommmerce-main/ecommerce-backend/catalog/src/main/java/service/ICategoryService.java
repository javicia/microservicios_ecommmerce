package service;

import java.util.List;
import java.util.Optional;

import model.Category;



public interface ICategoryService {
	
	Category createCategory(Category category);
	List<Category> getAllCategories();
	Optional<Category> getCategoryId(Long id);
	void deleteCategory(Long id);

}
