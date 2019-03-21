package org.isc.certanalysis.service;

import org.isc.certanalysis.config.ApplicationProperties;
import org.isc.certanalysis.domain.User;
import org.isc.certanalysis.service.dto.CertificateDTO;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

/**
 * @author p.dzeviarylin
 */
@Service
public class MailService {

	private static final String CERTIFICATE = "certificate";
	private static final String BASE_URL = "baseUrl";

	private final JavaMailSender javaMailSender;
	private final SpringTemplateEngine templateEngine;
	private final ApplicationProperties applicationProperties;

	public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, ApplicationProperties applicationProperties) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.applicationProperties = applicationProperties;
	}

	@Async
	public Future<Boolean> sendEmail(CertificateDTO certificateDTO, User user) {
		return sendEmailFromTemplate(certificateDTO, user, "mail/email");
	}

	public AsyncResult<Boolean> sendEmailFromTemplate(CertificateDTO certificateDTO, User user, String templateName) {
		Context context = new Context();
		context.setVariable(CERTIFICATE, certificateDTO);
		context.setVariable(BASE_URL, applicationProperties.getMail().getBaseUrl());
		String content = templateEngine.process(templateName, context);
		String subject = "Репозиторий сертификатов, предупреждение:";
		return sendEmail(user.getEmail(), subject, content, false, true);
	}

	public AsyncResult<Boolean> sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
			message.setTo(to);
			message.setFrom(applicationProperties.getMail().getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			return new AsyncResult<>(true);
		} catch (Exception ignored) {
			return new AsyncResult<>(false);
		}
	}

}
