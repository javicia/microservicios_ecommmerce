package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.OrderStatus;
import model.Orders;
import repository.IOrderRepository;

@Service
public class OrderServiceImpl implements IOrderService {

	@Autowired
	private IOrderRepository orderRepository;

	@Override
	public Orders createOrder(Orders order) {
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.CREATED);
		// Cálculo del total basado en los ítems del pedido
		if (order.getItems() != null) {
			BigDecimal total = order.getItems().stream()
					.map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			order.setTotal(total);
		}
		return orderRepository.save(order);
	}

	@Override
	public Orders getOrderById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
	}

	@Override
	public List<Orders> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Orders updateOrder(Long id, Orders order) {
		Orders existinOrder = getOrderById(id);
		// Actualiza los ítems y recalcula el total
		if (order.getItems() != null) {
			BigDecimal total = order.getItems().stream()
					.map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			existinOrder.setTotal(total);
		}
		// Permite actualizar el estado si se envía en la petición
		if (order.getStatus() != null) {
			existinOrder.setStatus(order.getStatus());
		}
		return orderRepository.save(existinOrder);
	}

	@Override
	public void deleteOrder(Long id) {
		Orders order = getOrderById(id);
		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);

	}

}
