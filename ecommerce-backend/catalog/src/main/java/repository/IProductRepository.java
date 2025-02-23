package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import model.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
	
	List<Product> findByCategoryId(Long categoryId);

}
