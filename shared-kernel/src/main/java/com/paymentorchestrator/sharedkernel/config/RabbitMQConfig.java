package com.paymentorchestrator.sharedkernel.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange Names
    public static final String EXCHANGE_NAME = "ex.payment-orchestrator.topic";
    public static final String DLX_NAME = "ex.payment-orchestrator.dlx";
    
    // Queue Names
    public static final String Q_CUSTOMER_VALIDATION = "q.customer-validation";
    public static final String Q_FRAUD_DETECTION = "q.fraud-detection";
    public static final String Q_PSP_CONNECTOR = "q.psp-connector";
    public static final String Q_LEDGER_UPDATE = "q.ledger-update";

    // Dead Letter Queue Names
    public static final String Q_CUSTOMER_VALIDATION_DLQ = "q.customer-validation.dlq";
    public static final String Q_FRAUD_DETECTION_DLQ = "q.fraud-detection.dlq";
    public static final String Q_PSP_CONNECTOR_DLQ = "q.psp-connector.dlq";
    public static final String Q_LEDGER_UPDATE_DLQ = "q.ledger-update.dlq";

    // Routing Key Names
    public static final String RK_PAYMENT_INITIATED = "payment.initiated";
    public static final String RK_CUSTOMER_VALIDATED = "customer.validated";
    public static final String RK_FRAUD_CHECK_PASSED = "fraud.check-passed";
    public static final String RK_PSP_SUCCESSFUL = "psp.successful";
    public static final String RK_PAYMENT_PRE_APPROVED = "payment.pre-approved";
    // Outras Routing Keys podem ser adicionadas conforme necessário


    // QUEUES
    @Bean
    public TopicExchange paymentOrchestratorExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME, true, false);
    }

    @Bean
    public Queue customerValidationQueue() {
        return QueueBuilder.durable(Q_CUSTOMER_VALIDATION)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", Q_CUSTOMER_VALIDATION)
                .build();
    }

    @Bean
    public Queue fraudDetectionQueue() {
        return QueueBuilder.durable(Q_FRAUD_DETECTION)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", Q_FRAUD_DETECTION)
                .build();
    }

    @Bean
    public Queue pspConnectorQueue() {
        return QueueBuilder.durable(Q_PSP_CONNECTOR)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", Q_PSP_CONNECTOR)
                .build();
    }

    //DEAD LETTER QUEUES
    @Bean
    public Queue ledgerQueue() {
        return QueueBuilder.durable(Q_LEDGER_UPDATE)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", Q_LEDGER_UPDATE)
                .build();
    }

    @Bean
    public Queue customerValidationDlq() {
        return QueueBuilder.durable(Q_CUSTOMER_VALIDATION_DLQ).build();
    }
    
    @Bean
    public Queue fraudDetectionDlq() {
        return QueueBuilder.durable(Q_FRAUD_DETECTION_DLQ).build();
    }
    
    @Bean
    public Queue pspConnectorDlq() {
        return QueueBuilder.durable(Q_PSP_CONNECTOR_DLQ).build();
    }
    
    @Bean
    public Queue ledgerDlq() {
        return QueueBuilder.durable(Q_LEDGER_UPDATE_DLQ).build();
    }

    // BINDINGS
    @Bean
    public Binding customerValidationBinding() {
        return BindingBuilder
            .bind(customerValidationQueue())
            .to(paymentOrchestratorExchange())
            .with(RK_PAYMENT_INITIATED);
    }

    @Bean
    public Binding fraudDetectionBinding() {
        return BindingBuilder
            .bind(fraudDetectionQueue())
            .to(paymentOrchestratorExchange())
            .with(RK_PAYMENT_INITIATED);
    }

    @Bean
    public Binding pspConnectorBinding() {
        return BindingBuilder
            .bind(pspConnectorQueue())
            .to(paymentOrchestratorExchange())
            .with(RK_PAYMENT_PRE_APPROVED);
    }

    @Bean
    public Binding ledgerBinding() {
        return BindingBuilder
        .bind(ledgerQueue())
        .to(paymentOrchestratorExchange())
        .with(RK_PSP_SUCCESSFUL);
    }

    // DEAD LETTER BINDINGS
    @Bean public Binding customerValidationDlqBinding() { 
        return BindingBuilder.
        bind(customerValidationDlq())
        .to(deadLetterExchange())
        .with(Q_CUSTOMER_VALIDATION); 
    }

    @Bean public Binding fraudDetectionDlqBinding() { 
        return BindingBuilder
        .bind(fraudDetectionDlq())
        .to(deadLetterExchange())
        .with(Q_FRAUD_DETECTION); 
    }

    @Bean public Binding pspConnectorDlqBinding() { 
        return BindingBuilder
        .bind(pspConnectorDlq())
        .to(deadLetterExchange())
        .with(Q_PSP_CONNECTOR); 
    }

    @Bean public Binding ledgerDlqBinding() { 
        return BindingBuilder
        .bind(ledgerDlq()).
        to(deadLetterExchange())
        .with(Q_LEDGER_UPDATE); 
    }

}
