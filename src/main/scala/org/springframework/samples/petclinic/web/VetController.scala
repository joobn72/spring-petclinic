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
import org.springframework.samples.petclinic.model.Vet
import org.springframework.samples.petclinic.model.Vets
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

import scala.collection.JavaConversions._
import org.springframework.ui.Model

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController @Autowired() (clinicService:ClinicService) {

  @RequestMapping(Array("/vets"))
  def showVetList(model:Model) = {
    // Here we are returning an object of type 'Vets' rather than a collection of Vet objects
    // so it is simpler for Object-Xml mapping
    val vets = new Vets
    vets.vets = vets.getVetList.toList ::: clinicService.findVets()
    model.addAttribute("vets", vets)
    "vets/vetList"
  }

}
