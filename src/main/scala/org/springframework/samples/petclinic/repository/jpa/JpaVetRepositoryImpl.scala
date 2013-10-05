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
package org.springframework.samples.petclinic.repository.jpa

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import org.springframework.cache.annotation.Cacheable
import org.springframework.samples.petclinic.model.Vet
import org.springframework.samples.petclinic.repository.VetRepository
import org.springframework.stereotype.Repository

import scala.collection.JavaConversions._

/**
 * JPA implementation of the {@link VetRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @since 22.4.2006
 */
@Repository
class JpaVetRepositoryImpl extends VetRepository {

  @PersistenceContext
  private var em:EntityManager = _


  @Cacheable(value = Array("vets"))
  @SuppressWarnings(Array("unchecked"))
  override def findAll:List[Vet] = {
    em.createQuery("SELECT distinct vet FROM Vet vet left join fetch vet.specialties ORDER BY vet.lastName, vet.firstName")
      .getResultList.asInstanceOf[List[Vet]]
  }

}