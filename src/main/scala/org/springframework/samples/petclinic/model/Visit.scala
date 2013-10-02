/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

import org.hibernate.annotations.Type
import org.hibernate.validator.constraints.NotEmpty
import org.joda.time.DateTime
import org.springframework.format.annotation.DateTimeFormat

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 */
@Entity
@Table(name = "visits")
class Visit extends BaseEntity {

  /**
   * Holds value of property date.
   */
  @Column(name = "visit_date")
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @DateTimeFormat(pattern = "yyyy/MM/dd")
  private var _date:DateTime = _

  /**
   * Holds value of property description.
   */
  @NotEmpty
  @Column(name = "description")
  private var _description:String = _

  /**
   * Holds value of property pet.
   */
  @ManyToOne
  @JoinColumn(name = "pet_id")
  private var _pet:Pet = _

  /**
   * Creates a new instance of Visit for the current date
   */
  _date = new DateTime()

  /**
   * Getter for property date.
   *
   * @return Value of property date.
   */
  def date:DateTime = _date

  /**
   * Setter for property date.
   *
   * @param date New value of property date.
   */
  def date_=(date:DateTime) = _date = date

  /**
   * Getter for property description.
   *
   * @return Value of property description.
   */
  def description:String = _description

  /**
   * Setter for property description.
   *
   * @param description New value of property description.
   */
  def description_=(description:String) = _description = description

  /**
   * Getter for property pet.
   *
   * @return Value of property pet.
   */
  def pet:Pet = _pet

  /**
   * Setter for property pet.
   *
   * @param pet New value of property pet.
   */
  def pet_=(pet:Pet) = _pet = pet

}




