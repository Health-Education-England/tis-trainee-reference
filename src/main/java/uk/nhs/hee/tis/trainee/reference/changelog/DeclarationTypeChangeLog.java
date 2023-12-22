/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Crown Copyright (Health Education England)
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

package uk.nhs.hee.tis.trainee.reference.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import java.util.Set;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.nhs.hee.tis.trainee.reference.model.DeclarationType;

/**
 * Change logs for the DeclarationType collection.
 *
 * @deprecated This approach was deprecated by Mongock in version 5.
 */
@Deprecated(since = "1.0.0")
@ChangeLog
public class DeclarationTypeChangeLog {

  /**
   * Insert the initial data for the {@link DeclarationType} collection.
   *
   * @param mongoTemplate The mongo template for the dotabase.
   */
  @ChangeSet(order = "001", id = "insertInitialDeclarationTypes", author = "")
  public void insertInitialDeclarationTypes(MongoTemplate mongoTemplate) {
    DeclarationType significantEvent = new DeclarationType();
    significantEvent.setLabel("Significant event");

    DeclarationType complaint = new DeclarationType();
    complaint.setLabel("Complaint");

    DeclarationType otherInvestigation = new DeclarationType();
    otherInvestigation.setLabel("Other investigation");

    mongoTemplate.insertAll(Set.of(significantEvent, complaint, otherInvestigation));
  }
}
