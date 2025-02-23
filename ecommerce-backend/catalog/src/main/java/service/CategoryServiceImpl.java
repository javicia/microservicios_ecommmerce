package service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Category;
import repository.ICategoryRepository;

@Service
public class CategoryServiceImpl implements ICategoryService {
	
	private final ICategoryRepository categoryRepository;
	
	@Autowired
	public CategoryServiceImpl(ICategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
		
	}

	@Override
	public Category createCategory(Category category) {
		 if (category == null) {
	            throw new IllegalArgumentException("Category cannot be null");
	        }
	        return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();	
		
	}

	@Override
    public Optional<Category> getCategoryId(Long id) {
        return Optional.ofNullable(categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría con ID " + id + " no encontrada")));
    }

	@Override
	public void deleteCategory(Long id) {
		if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("No se puede eliminar, la categoría con ID " + id + " no existe");
        }
        categoryRepository.deleteById(id);
    }

}
