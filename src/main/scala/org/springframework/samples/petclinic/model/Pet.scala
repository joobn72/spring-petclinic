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

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

import org.hibernate.annotations.Type
import org.joda.time.DateTime
import org.springframework.format.annotation.DateTimeFormat

import org.springframework.samples.petclinic.util.Joda._
import scala.collection.JavaConversions._

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name = "pets")
class Pet extends NamedEntity {

  @Column(name = "birth_date")
  @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @DateTimeFormat(pattern = "yyyy/MM/dd")
  private var _birthDate:DateTime = _

  @ManyToOne
  @JoinColumn(name = "type_id")
  private var _type:PetType = _

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private var _owner:Owner = _

  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "_pet", fetch = FetchType.EAGER)
  private var visits:java.util.Set[Visit] = _

  def birthDate:DateTime = _birthDate
  def birthDate_=(birthDate:DateTime) = _birthDate = birthDate

  def `type`:PetType = _type
  def type_=(pt:PetType) = _type = pt

  /* protected */ def owner:Owner = _owner
  def owner_=(owner:Owner) = _owner = owner

  protected def setVisitsInternal(visits:Set[Visit]) {
    this.visits = visits
  }

  protected def getVisitsInternal = {
    if (visits == null) {
      visits = Set[Visit]()
    }
    visits
  }

  def getVisits {
    val sortedVisits = getVisitsInternal.toList
    sortedVisits.sortBy(x => x.date)
  }

  def addVisit(visit:Visit) {
    visits = getVisitsInternal + visit
    visit.pet = this
  }
}
