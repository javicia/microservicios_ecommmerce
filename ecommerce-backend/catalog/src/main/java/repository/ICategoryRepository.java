package repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
	
	Category findByName(String name);


}
