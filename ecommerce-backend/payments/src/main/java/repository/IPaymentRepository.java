package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import model.Payment;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

}
