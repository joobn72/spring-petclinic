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

import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.model.PetType
import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.repository.OwnerRepository
import org.springframework.samples.petclinic.repository.PetRepository
import org.springframework.samples.petclinic.repository.VisitRepository
import org.springframework.samples.petclinic.util.EntityUtils
import org.springframework.stereotype.Repository

import scala.collection.JavaConversions._

/**
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 */
@Repository
@Autowired
class JdbcPetRepositoryImpl(
              dataSource:DataSource,
              ownerRepository:OwnerRepository,
              visitRepository:VisitRepository) extends PetRepository with EntityUtils {

  private val namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource)

  private val insertPet = new SimpleJdbcInsert(dataSource)
                                .withTableName("pets")
                                .usingGeneratedKeyColumns("id")

  override def findPetTypes:List[PetType] = {
    val params = scala.collection.mutable.Map[String, Object]()
    namedParameterJdbcTemplate.query(
      "SELECT id, name FROM types ORDER BY name",
      params,
      ParameterizedBeanPropertyRowMapper.newInstance(classOf[PetType])).asInstanceOf[List[PetType]]
  }

  override def findById(id:Int):Pet = {
    var pet:JdbcPet = null
    try {
      val params = scala.collection.mutable.Map[String, Int]()
      params += "id" -> id
      pet = namedParameterJdbcTemplate.queryForObject(
        "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id=:id",
        params,
        new JdbcPetRowMapper()
      )
    } catch {
      case e:EmptyResultDataAccessException => throw new ObjectRetrievalFailureException(classOf[Pet], id)
    }
    val owner = ownerRepository.findById(pet.ownerId)
    owner.addPet(pet)
    pet.type_=(getById(findPetTypes(), classOf[PetType], pet.typeId))
    // FIXME
    //pet.`type` = EntityUtils.getById(findPetTypes(), classOf[PetType], pet.typeId)

    val visits = visitRepository.findByPetId(pet.id)
    visits.foreach(visit => pet.addVisit(visit))
    pet
  }

  override def save(pet:Pet) {
    if (pet.isNew) {
      val newKey = insertPet.executeAndReturnKey(createPetParameterSource(pet))
      pet.id = newKey.intValue()
    } else {
      namedParameterJdbcTemplate.update(
        "UPDATE pets SET name=:name, birth_date=:birth_date, type_id=:type_id, " +
        "owner_id=:owner_id WHERE id=:id",
         createPetParameterSource(pet))
    }
  }

  /**
   * Creates a {@link MapSqlParameterSource} based on data values from the supplied {@link Pet} instance.
   */
  private def createPetParameterSource(pet:Pet) = {
    new MapSqlParameterSource()
          .addValue("id", pet.id)
          .addValue("name", pet.name)
          .addValue("birth_date", pet.birthDate.toDate())
          .addValue("type_id", pet.`type`.id)
          .addValue("owner_id", pet.owner.id)
  }

}