package com.ytech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sender;

	@Async
	@Override
	public void sendSimpleMail(String receipient, String item, Integer quantity) {
		try {

			SimpleMailMessage mailMessage = new SimpleMailMessage();

			mailMessage.setFrom(sender);
			mailMessage.setTo(receipient);
			mailMessage.setSubject("Congratulations! Your order was successfuly completed!");
			mailMessage.setText("Item:" + item + "\nquantity:" + quantity);

			// Sending the mail
			javaMailSender.send(mailMessage);
			log.info("E-mail sent to " + receipient);
		} catch (Exception e) {
			log.error("Error sending e-mail to " + receipient, e);
		}
	}
}
