package model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Dirección de correo del destinatario
	@Column(nullable = false)
	private String recipient;

	// Asunto del correo
	@Column(nullable = false)
	private String subject;

	// Contenido del correo
	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	// Fecha y hora en que se envió el correo
	private LocalDateTime sentDate;

	// Indicador de si el correo se envió correctamente
	private boolean sent;
}
