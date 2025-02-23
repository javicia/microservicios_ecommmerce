package service;

import java.util.List;

import model.Orders;

public interface IOrderService {

	Orders createOrder(Orders order);

	Orders getOrderById(Long id);

	List<Orders> getAllOrders();

	Orders updateOrder(Long id, Orders order);

	void deleteOrder(Long id);

}
