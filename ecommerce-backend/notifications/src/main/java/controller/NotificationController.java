package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.Notification;
import service.INotificationService;

@RestController
@Tag(name = "Notificaciones por correo electrónico", description = "Endpoints para enviar notificaciones por correo electrónico")

public class NotificationController {

	@Autowired
	private INotificationService notificationService;

	@Operation(summary = "Enviar notificación", description = "Envía un correo electrónico de notificación")
	@PostMapping
	public ResponseEntity<Notification> sendEmailNotification(@RequestBody Notification notification) {
		Notification notificationSent = notificationService.sendEmailNotification(notification);
		return new ResponseEntity<>(notificationSent, HttpStatus.CREATED);
	}
}
