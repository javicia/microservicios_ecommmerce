package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.Orders;
import service.IOrderService;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Endpoints para la gestión de órdenes de compra")
public class OrderController {

	@Autowired
	private IOrderService orderService;

	@Operation(summary = "Crear pedido", description = "Crea una nueva orden de compra")
	@PostMapping
	public ResponseEntity<Orders> createOrder(@RequestBody Orders order) {
		Orders newOrder = orderService.createOrder(order);
		return ResponseEntity.ok(newOrder);
	}

	@Operation(summary = "Obtener pedido", description = "Recupera el pedido por su ID")
	@GetMapping("/{id}")
	public ResponseEntity<Orders> getOrder(@PathVariable Long id) {
		Orders order = orderService.getOrderById(id);
		return ResponseEntity.ok(order);
	}

	@Operation(summary = "Listar pedidos", description = "Recupera todos los pedidos")
	@GetMapping
	public ResponseEntity<Iterable<Orders>> getOrders() {
		Iterable<Orders> orders = orderService.getAllOrders();
		return ResponseEntity.ok(orders);
	}

	@Operation(summary = "Actualizar pedido", description = "Actualiza un pedido existente")
	@PostMapping("/{id}")
	public ResponseEntity<Orders> updateOrder(@PathVariable Long id, @RequestBody Orders order) {
		Orders updatedOrder = orderService.updateOrder(id, order);
		return ResponseEntity.ok(updatedOrder);
	}

	@Operation(summary = "Eliminar pedido", description = "Elimina un pedido existente")
	@PostMapping("/{id}/delete")
	public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
		orderService.deleteOrder(id);
		return ResponseEntity.ok("Pedido eliminado");
	}
}
