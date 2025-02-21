package ru.sam47kon.kd.product.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.sam47kon.kd.core.event.ProductCreateEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	String bootstrapServers;

	@Value("${spring.kafka.producer.key-serializer}")
	String keySerializer;

	@Value("${spring.kafka.producer.value-serializer}")
	String valueSerializer;

	@Value("${spring.kafka.producer.acks}")
	String acks;

	@Value("${spring.kafka.producer.retries}")
	String retries;

	@Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
	String deliveryTimeout;

	@Value("${spring.kafka.producer.properties.linger.ms}")
	String linger;

	@Value("${spring.kafka.producer.properties.request.timeout.ms}")
	String requestTimeout;

	@Value("${spring.kafka.producer.properties.enable.idempotence}")
	String enableIdempotence;

	Map<String, Object> producerConfigs() {
		Map<String, Object> config = new HashMap<>();

		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
		config.put(ProducerConfig.ACKS_CONFIG, acks);
		config.put(ProducerConfig.RETRIES_CONFIG, retries);
		config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
		config.put(ProducerConfig.LINGER_MS_CONFIG, linger);
		config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
		config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

		return config;
	}

	@Bean
	public ProducerFactory<String, ProductCreateEvent> producerConfig() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, ProductCreateEvent> kafkaTemplate() {
		return new KafkaTemplate<>(producerConfig());
	}

	@Bean
	public NewTopic createTopic(@Value("${product-created-events-topic}") String topicName) {
		return TopicBuilder.name(topicName)
				.partitions(3)
				.replicas(3)
				.configs(Map.of("min.insync.replicas", "2"))
				.build();
	}
}
