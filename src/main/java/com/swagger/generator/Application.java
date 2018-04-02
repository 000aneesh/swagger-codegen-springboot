package com.swagger.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	/**
	 * This method used to generate service api
	 *
	 * @return docket
	 */
	@Bean
	public Docket propertyServiceApi() {
		// formatter:off
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(propertyServiceInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.swagger.generator.controller")).paths(PathSelectors.any()).build()
				.pathMapping("/");
		// formatter:on
	}

	/**
	 * This method used to display student service information
	 *
	 * @return apiInfo
	 */
	private ApiInfo propertyServiceInfo() {
		return new ApiInfoBuilder().title("Swagger Code Generator API").description("Creates Project using Codegen")
				.version("1.0").build();
	}
}
