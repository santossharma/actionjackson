package de.baro.actionjackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class ActionJacksonApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActionJacksonApplication.class, args);
	}

	@Bean
	public CommandLineRunner getObjectMapperConfig(ObjectMapper objectMapper) {
		return args -> {
			System.out.println(objectMapper.getRegisteredModuleIds());
			System.out.println("Pretified : " +
					objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT));
			System.out.println("Fail on unknown properties : " +
					objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
			System.out.println("Write Date as timestamp properties : " +
					objectMapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
		};
	}

	/* Centralized way to customize Object Mapper default behaviour. */
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customizer() {
		return jacksonObjectMapperBuilder -> {
			jacksonObjectMapperBuilder.failOnUnknownProperties(true); // it should 'fail' for better contract!
			//jacksonObjectMapperBuilder.indentOutput(true);
		};
	}

	@Bean
	/* Registers a serializer by type so that whenever we have an offset date time, we don't want the default way
	* that these string are serialized.
	* It actually truncates the value to seconds first before it marshals out, ignoring the milliseconds. */
	public Jackson2ObjectMapperBuilderCustomizer offsetDateTimeSerializationCustomization() {
		return objectMapperBuilder -> {
			objectMapperBuilder.serializerByType(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
				@Override
				public void serialize(OffsetDateTime offsetDateTime,
									  JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
					jsonGenerator.writeString(offsetDateTime.truncatedTo(ChronoUnit.SECONDS)
							.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
				}
			});
			// used to serialize Long types to String, which solves the problem of lost precision of JSs numeric types in the browser client.
			objectMapperBuilder.serializerByType(BigInteger.class, ToStringSerializer.instance);
			objectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);

		};
	}

}
