package com.shizzy.moneytransfer.kafka;

import com.shizzy.moneytransfer.dto.TransactionNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, TransactionNotification> kafkaTemplate;

    public void sendNotification(String topic, TransactionNotification transactionNotification) {

        logger.info("Sending notification with body = < {} >", transactionNotification);

        kafkaTemplate.send(topic, transactionNotification);
    }
}

