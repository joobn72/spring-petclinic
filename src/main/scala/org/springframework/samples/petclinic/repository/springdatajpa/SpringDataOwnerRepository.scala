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
package org.springframework.samples.petclinic.repository.springdatajpa

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.repository.OwnerRepository

import scala.collection.JavaConversions._

/**
 * Spring Data JPA specialization of the {@link OwnerRepository} interface
 *
 * @author Michael Isvy
 * @since 15.1.2013
 */
trait SpringDataOwnerRepository extends OwnerRepository with Repository[Owner, java.lang.Integer] {

  @Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
  override def findByLastName(@Param("lastName") lastName:String):List[Owner]

  @Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
  override def findById(@Param("id") id:Int):Owner
}