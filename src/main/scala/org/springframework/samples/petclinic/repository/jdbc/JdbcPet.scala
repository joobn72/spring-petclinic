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
package org.springframework.samples.petclinic.repository.jdbc

import org.springframework.samples.petclinic.model.Pet

/**
 * Subclass of Pet that carries temporary id properties which are only relevant for a JDBC implmentation of the
 * ClinicService.
 *
 * @author Juergen Hoeller
 * @see JdbcClinicImpl
 */
class JdbcPet extends Pet {

  private var _typeId:Int = _

  private var _ownerId:Int = _


  def typeId:Int = _typeId
  def typeId_=(typeId:Int) = _typeId = typeId

  def ownerId:Int = _ownerId
  def ownerId_=(ownerId:Int) = _ownerId = ownerId

}
