package com.example.BankApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info=@Info(
				title="Bank Application",
				description="First App for a Bank",
				version = "v1.0",
				contact = @Contact(
						name="Moufok Sabrina",
						email="sabrina10moufok@gmail.com",
						url="https://github.com/1sabrina10"
				)
		)
)
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
