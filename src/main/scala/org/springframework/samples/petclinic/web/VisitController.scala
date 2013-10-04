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

import javax.validation.Valid

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.support.SessionStatus
import org.springframework.web.servlet.ModelAndView

import scala.collection.JavaConversions._

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
@SessionAttributes(Array("visit"))
@Autowired
class VisitController(clinicService: ClinicService) {

  @InitBinder
  def setAllowedFields(dataBinder: WebDataBinder) {
    dataBinder.setDisallowedFields("id")
  }

  @RequestMapping(value = Array("/owners/*/pets/{petId}/visits/new"), method = Array(RequestMethod.GET))
  def initNewVisitForm(@PathVariable("petId") petId:Int, model: Map[String, Object]) = {
    val pet = clinicService.findPetById(petId)
    val visit = new Visit()
    pet.addVisit(visit)
    model.put("visit", visit)
    "pets/createOrUpdateVisitForm"
  }

  @RequestMapping(value = Array("/owners/{ownerId}/pets/{petId}/visits/new"), method = Array(RequestMethod.POST))
  def processNewVisitForm(@Valid visit:Visit, result:BindingResult, status:SessionStatus) = {
    if (result.hasErrors()) {
      "pets/createOrUpdateVisitForm"
    } else {
      clinicService.saveVisit(visit)
      status.setComplete()
      "redirect:/owners/{ownerId}"
    }
  }

  @RequestMapping(value = Array("/owners/*/pets/{petId}/visits"), method = Array(RequestMethod.GET))
  def showVisits(@PathVariable petId:Int) = {
    val mav = new ModelAndView("visitList")
    mav.addObject("visits", this.clinicService.findPetById(petId).getVisits)
    mav
  }

}

