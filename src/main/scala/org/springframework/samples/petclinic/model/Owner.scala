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
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Digits

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.core.style.ToStringCreator

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Entity
@Table(name = "owners")
class Owner extends Person {
  @Column(name = "address")
  @NotEmpty
  private var _address:String = _

  @Column(name = "city")
  @NotEmpty
  private var _city:String = _

  @Column(name = "telephone")
  @NotEmpty
  @Digits(fraction = 0, integer = 10)
  private var _telephone:String = _

  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "owner")
  private var _pets:Set[Pet] = _

  def address:String = _address
  def address_=(address:String) = _address = address

  def city:String = _city
  def city_=(city:String) = _city = city

  def telephone:String = _telephone
  def telephone_=(telephone:String) = _telephone = telephone

  /*protected*/ def setPetsInternal(pets:Set[Pet]) { _pets = pets }
  /*protected*/ def getPetsInternal = {
    if (_pets == null) {
      _pets = Set[Pet]()
    }
    _pets
  }

  def getPets {
    val sortedPets:List[Pet] = getPetsInternal.toList
    sortedPets.sortBy(x => x.name)
  }

  def addPet(pet:Pet) {
    _pets = getPetsInternal + pet
    pet.owner = this
  }

  /**
   * Return the Pet with the given name, or null if none found for this Owner.
   *
   * @param name to test
   * @return true if pet name is already in use
   */
  def getPet(name:String):Pet = {
    getPet(name, false)
  }

  /**
   * Return the Pet with the given name, or null if none found for this Owner.
   *
   * @param name to test
   * @return true if pet name is already in use
   */
  def getPet(name:String, ignoreNew:Boolean):Pet = {
    getPetsInternal.foreach(pet => {
      if (!ignoreNew || !pet.isNew) {
        val compName  = pet.name.toLowerCase
        if (compName == name.toLowerCase) {
          return pet
        }
      }
    })
    null
  }

  override def toString = {
    new ToStringCreator(this)

      .append("id", this.getId())
      .append("new", this.isNew)
      .append("lastName", this.getLastName())
      .append("firstName", this.getFirstName())
      .append("address", this.address)
      .append("city", this.city)
      .append("telephone", this.telephone)
      .toString()
  }
}
