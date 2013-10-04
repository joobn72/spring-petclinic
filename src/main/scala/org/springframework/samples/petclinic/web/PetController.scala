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
package org.springframework.samples.petclinic.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.model.PetType
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.bind.support.SessionStatus

import scala.collection.JavaConversions._

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@SessionAttributes(Array("pet"))
@Autowired
class PetController(clinicService: ClinicService) {

  @ModelAttribute("types")
  def populatePetTypes = clinicService.findPetTypes()

  @InitBinder
  def setAllowedFields(dataBinder: WebDataBinder) {
    dataBinder.setDisallowedFields("id")
  }

  @RequestMapping(value = Array("/owners/{ownerId}/pets/new"), method = Array(RequestMethod.GET))
  def initCreationForm(@PathVariable("ownerId") ownerId:Int, model: Map[String, Object]) = {
    val owner = clinicService.findOwnerById((ownerId))
    val pet = new Pet()
    owner.addPet(pet)
    model.put("pet", pet)
    "pets/createOrUpdatePetForm"
  }

  @RequestMapping(value = Array("/owners/{ownerId}/pets/new"), method = Array(RequestMethod.POST))
  def processCreationForm(@ModelAttribute("pet") pet: Pet, result: BindingResult, status: SessionStatus) = {
    new PetValidator().validate(pet, result)
    if (result.hasErrors()) {
      "pets/createOrUpdatePetForm"
    } else {
      clinicService.savePet(pet)
      status.setComplete()
      "redirect:/owners/{ownerId}"
    }
  }

  @RequestMapping(value = Array("/owners/*/pets/{petId}/edit"), method = Array(RequestMethod.GET))
  def initUpdateForm(@PathVariable("petId") petId: Int, model: Map[String, Object]) = {
    val pet = clinicService.findPetById(petId)
    model.put("pet", pet)
    "pets/createOrUpdatePetForm"
  }

  @RequestMapping(value = Array("/owners/{ownerId}/pets/{petId}/edit"), method = Array(RequestMethod.PUT, RequestMethod.POST))
  def processUpdateForm(@ModelAttribute("pet") pet: Pet, result: BindingResult, status: SessionStatus) = {
    // we're not using @Valid annotation here because it is easier to define such validation rule in Java
    new PetValidator().validate(pet, result)
    if (result.hasErrors()) {
      "pets/createOrUpdatePetForm"
    } else {
      clinicService.savePet(pet)
      status.setComplete()
      "redirect:/owners/{ownerId}"
    }
  }

}

