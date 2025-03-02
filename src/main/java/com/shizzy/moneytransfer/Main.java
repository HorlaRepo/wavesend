package com.shizzy.moneytransfer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shizzy.moneytransfer.dto.FlutterwaveWithdrawalRequest;
import com.shizzy.moneytransfer.service.CountryService;
import com.shizzy.moneytransfer.service.EmailService;
import com.shizzy.moneytransfer.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableAsync
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean
	CommandLineRunner runner(@Qualifier("mailerSendService") EmailService emailService) {
		return args -> {
			LocalDateTime ldt = LocalDateTime.parse(LocalDateTime.now().toString(), DateTimeFormatter.ISO_DATE_TIME);
			System.out.println(ldt.format(DateTimeFormatter.ofPattern("h:mm a")));
		};
	}

}
