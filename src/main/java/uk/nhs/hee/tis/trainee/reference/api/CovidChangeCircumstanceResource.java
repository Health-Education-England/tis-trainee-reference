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

package uk.nhs.hee.tis.trainee.reference.api;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.trainee.reference.dto.CovidChangeCircumstanceDto;
import uk.nhs.hee.tis.trainee.reference.mapper.CovidChangeCircumstanceMapper;
import uk.nhs.hee.tis.trainee.reference.model.CovidChangeCircumstance;
import uk.nhs.hee.tis.trainee.reference.service.CovidChangeCircumstanceService;

@RestController
@RequestMapping("/api")
@XRayEnabled
public class CovidChangeCircumstanceResource {

  private static final Logger log = LoggerFactory.getLogger(CovidChangeCircumstanceResource.class);

  private final CovidChangeCircumstanceService changeCircumstanceService;
  private final CovidChangeCircumstanceMapper changeCircumstanceMapper;

  public CovidChangeCircumstanceResource(CovidChangeCircumstanceService changeCircumstanceService,
      CovidChangeCircumstanceMapper changeCircumstanceMapper) {
    this.changeCircumstanceService = changeCircumstanceService;
    this.changeCircumstanceMapper = changeCircumstanceMapper;
  }

  /**
   * Get CovidChangeCircumstance options from reference table.
   *
   * @return list of CovidChangeCircumstances.
   */
  @GetMapping("/covid-change-circs")
  public List<CovidChangeCircumstanceDto> getCovidChangeCircumstance() {
    log.trace("Get all CovidChangeCircumstance");
    List<CovidChangeCircumstance> changeCircumstances = changeCircumstanceService
        .getCovidChangeCircumstances();
    return changeCircumstanceMapper.toDtos(changeCircumstances);
  }

}
