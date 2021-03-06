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

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.xml.bind.annotation.XmlElement

import scala.collection.JavaConversions._

/**
 * Simple JavaBean domain object representing a veterinarian.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Arjen Poutsma
 */
@Entity
@Table(name = "vets")
class Vet extends Person {

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "vet_specialties", joinColumns = Array(new JoinColumn(name = "vet_id")),
  inverseJoinColumns = Array(new JoinColumn(name = "specialty_id")))
  private var _specialties:java.util.Set[Specialty] = _

  /* protected */
  def setSpecialtiesInternal_=(specialties:Set[Specialty]) = _specialties = specialties
  /* protected */
  def getSpecialtiesInternal = {
    if (_specialties == null) {
      _specialties = Set[Specialty]()
    }
    _specialties
  }

  @XmlElement
  def getSpecialties = {
    val sortedSpecs:List[Specialty] = getSpecialtiesInternal.toList
    asJavaCollection(sortedSpecs.sortBy(x => x.name))
  }

  def getNrOfSpecialties = getSpecialtiesInternal.size

  def addSpecialty(specialty:Specialty) {
    getSpecialtiesInternal + specialty
  }
}
