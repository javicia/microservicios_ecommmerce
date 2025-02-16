package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import exception.EmailSendingException;
import model.Notification;
import repository.INotificationRepository;

@Service
public class NotificationServiceImpl implements INotificationService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private INotificationRepository notificationRepository;

	// URL de la API de Mailtrap
	private static final String MAILTRAP_API_URL = "https://send.api.mailtrap.io/api/send";
	// Token de autorización de Mailtrap (reemplaza con el tuyo real)
	private static final String MAILTRAP_BEARER_TOKEN = "758f0b2728f02b21daa87b764def00a2";

	@Override
	public Notification sendEmailNotification(Notification notification) {
		// Construir el payload en formato JSON
		Map<String, Object> payload = new HashMap<>();
		Map<String, String> from = new HashMap<>();
		from.put("email", "hello@demomailtrap.com"); // Dirección de remitente permitida por Mailtrap
		from.put("name", "Mailtrap Test");
		payload.put("from", from);

		// Configurar el destinatario
		List<Map<String, String>> toList = new ArrayList<>();
		Map<String, String> to = new HashMap<>();
		to.put("email", notification.getRecipient());
		toList.add(to);
		payload.put("to", toList);

		payload.put("subject", notification.getSubject());
		payload.put("text", notification.getMessage());
		payload.put("category", "Integration Test");

		// Configurar los headers, incluyendo el header Authorization
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + MAILTRAP_BEARER_TOKEN);

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(MAILTRAP_API_URL, requestEntity, String.class);
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new EmailSendingException("Error al enviar correo vía API, status: " + response.getStatusCode(),
						null);
			}
		} catch (Exception ex) {
			throw new EmailSendingException("Error al enviar el correo a " + notification.getRecipient(), ex);
		}

		// Actualiza la notificación y guárdala en la base de datos
		notification.setSent(true);
		notification.setSentDate(LocalDateTime.now());
		return notificationRepository.save(notification);
	}
}
