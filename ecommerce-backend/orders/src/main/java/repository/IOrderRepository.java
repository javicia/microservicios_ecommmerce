package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import model.Orders;

@Repository
public interface IOrderRepository extends JpaRepository<Orders, Long> {

}
