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
import javax.persistence.Query

import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.repository.VisitRepository
import org.springframework.stereotype.Repository

/**
 * JPA implementation of the ClinicService interface using EntityManager.
 * <p/>
 * <p>The mappings are defined in "orm.xml" located in the META-INF directory.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @since 22.4.2006
 */
@Repository
class JpaVisitRepositoryImpl extends VisitRepository {

  @PersistenceContext
  private var em:EntityManager = _


  override def save(visit:Visit) {
    if (visit.id == 0) {
      em.persist(visit)
    } else {
      em.merge(visit)
    }
  }


  @SuppressWarnings(Array("unchecked"))
  override def findByPetId(petId:Int):List[Visit] = {
    val query = em.createQuery("SELECT visit FROM Visit v where v._pets._id= :id")
    query.setParameter("id", petId)
    query.getResultList.asInstanceOf[List[Visit]]
  }

}