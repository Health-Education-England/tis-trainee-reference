/*
 * The MIT License (MIT)
 *
 * Copyright 2020 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.nhs.hee.tis.trainee.reference;

import static springfox.documentation.builders.PathSelectors.regex;

import com.github.cloudyrock.spring.v5.EnableMongock;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableMongock
@SpringBootApplication
@EnableSwagger2
public class TisTraineeReferenceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TisTraineeReferenceApplication.class);
  }

  /**
   * Configure swagger.
   *
   * @return The swagger configuration {@link Docket}.
   */
  @Bean
  public Docket swaggerConfiguration() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .paths(regex("/api.*"))
        .apis(RequestHandlerSelectors.basePackage("uk.nhs.hee.tis.trainee.reference"))
        .build()
        .apiInfo(apiDetails());
  }

  private ApiInfo apiDetails() {
    return new ApiInfo(
        "Trainee Reference API",
        "The TIS-TRAINEE-REFERENCE microservice's APIs",
        "1.0",
        "These will be consumed by React UI or other microservices",
        new springfox.documentation.service.Contact("Contact", "TIS Dev Team", "aaaa@hee.nhs.uk"),
        "API Licence",
        "TIS",
        Collections.emptyList());
  }
}
