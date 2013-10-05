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
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.SessionAttributes
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
@SessionAttributes(types = Array(classOf[Owner]))
class OwnerController @Autowired() (clinicService:ClinicService) {


  @InitBinder
  def setAllowedFields(dataBinder: WebDataBinder) {
    dataBinder.setDisallowedFields("id")
  }

  @RequestMapping(value = Array("/owners/new"), method = Array(RequestMethod.GET))
  def initCreationForm(model: Model) = {
    val owner = new Owner()
    model.addAttribute("owner", owner)
    "owners/createOrUpdateOwnerForm"
  }

  @RequestMapping(value = Array("/owners/new"), method = Array(RequestMethod.POST))
  def processCreationForm(@Valid owner:Owner, result:BindingResult, status:SessionStatus) = {
    if (result.hasErrors()) {
      "owners/createOrUpdateOwnerForm"
    } else {
      this.clinicService.saveOwner(owner)
      status.setComplete()
      "redirect:/owners/" + owner.id
    }
  }

  @RequestMapping(value = Array("/owners/find"), method = Array(RequestMethod.GET))
  def initFindForm(model: Model) = {
    model.addAttribute("owner", new Owner())
    "owners/findOwners"
  }

  @RequestMapping(value = Array("/owners"), method = Array(RequestMethod.GET))
  def processFindForm(owner: Owner, result:BindingResult, model: Model):String = {

    // allow parameterless GET request for /owners to return all records
    if (owner.lastName == null) {
      owner.lastName = "" // empty string signifies broadest possible search
    }

    // find owners by last name
    val results = clinicService.findOwnerByLastName(owner.lastName)

    if (results.size < 1) {
      // no owners found
      result.rejectValue("lastName", "notFound", "not found")
      return "owners/findOwners"
    }
    if (results.size > 1) {
      // multiple owners found
      model.addAttribute("selections", results)
      "owners/ownersList"
    } else {
      // 1 owner found
      val nextOwner = results.iterator.next()
      "redirect:/owners/" + nextOwner.id
    }
  }

  @RequestMapping(value = Array("/owners/{ownerId}/edit"), method = Array(RequestMethod.GET))
  def initUpdateOwnerForm(@PathVariable("ownerId") ownerId:Int, model:Model) = {
    val owner = clinicService.findOwnerById((ownerId))
    model.addAttribute(owner)
    "owners/createOrUpdateOwnerForm"
  }

  @RequestMapping(value = Array("/owners/{ownerId}/edit"), method = Array(RequestMethod.PUT))
  def processUpdateOwnerForm(@Valid owner: Owner, result: BindingResult, status: SessionStatus) = {
    if (result.hasErrors()) {
      "owners/createOrUpdateOwnerForm"
    } else {
      clinicService.saveOwner(owner)
      status.setComplete()
      "redirect:/owners/{ownerId}"
    }
  }

  /**
   * Custom handler for displaying an owner.
   *
   * @param ownerId the ID of the owner to display
   * @return a ModelMap with the model attributes for the view
   */
  @RequestMapping(Array("/owners/{ownerId}"))
  def showOwner(@PathVariable("ownerId") ownerId:Int) = {
    val mav = new ModelAndView("owners/ownerDetails")
    mav.addObject(this.clinicService.findOwnerById(ownerId))
    mav
  }

}
