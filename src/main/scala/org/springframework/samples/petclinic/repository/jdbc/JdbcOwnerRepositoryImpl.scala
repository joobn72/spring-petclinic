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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.model.PetType
import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.repository.OwnerRepository
import org.springframework.samples.petclinic.repository.VisitRepository
import org.springframework.samples.petclinic.util.EntityUtils
import org.springframework.stereotype.Repository

import scala.collection.JavaConversions._

/**
 * A simple JDBC-based implementation of the {@link OwnerRepository} interface.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 */
@Repository
@Autowired
class JdbcOwnerRepositoryImpl(
    dataSource:DataSource,
    var namedParameterJdbcTemplate:NamedParameterJdbcTemplate,
    visitRepository:VisitRepository) extends OwnerRepository with EntityUtils {


  private var insertOwner:SimpleJdbcInsert = _

  insertOwner = new SimpleJdbcInsert(dataSource)
                      .withTableName("owners")
                      .usingGeneratedKeyColumns("id")

  namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource)


  /**
   * Loads {@link Owner Owners} from the data store by last name, returning all owners whose last name <i>starts</i> with
   * the given name; also loads the {@link Pet Pets} and {@link Visit Visits} for the corresponding owners, if not
   * already loaded.
   */
  override def findByLastName(lastName:String):List[Owner] = {
    val params = scala.collection.mutable.Map[String, Object]()
    params += "lastName" -> (lastName + "%")
    val owners = namedParameterJdbcTemplate.query(
      "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name like :lastName",
      params,
      ParameterizedBeanPropertyRowMapper.newInstance(classOf[Owner])
    ).toList
    loadOwnersPetsAndVisits(owners)
    owners
  }

  /**
   * Loads the {@link Owner} with the supplied <code>id</code>; also loads the {@link Pet Pets} and {@link Visit Visits}
   * for the corresponding owner, if not already loaded.
   */
  override def findById(id:Int) = {
    var owner:Owner = null
    try {
      val params = scala.collection.mutable.Map[String, Int]()
      params += "id" -> id
      owner = namedParameterJdbcTemplate.queryForObject(
        "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id= :id",
        params,
        ParameterizedBeanPropertyRowMapper.newInstance(classOf[Owner])
      )
    } catch {
      case e:EmptyResultDataAccessException => throw new ObjectRetrievalFailureException(classOf[Owner], id)
    }
    loadPetsAndVisits(owner)
    owner
  }

  def loadPetsAndVisits(owner:Owner) {
    val params = scala.collection.mutable.Map[String, Int]()
    params += "id" -> owner.id
    val pets = namedParameterJdbcTemplate.query(
      "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id=:id",
      params,
      new JdbcPetRowMapper()
    )
    pets.foreach(pet => {
      owner.addPet(pet)
      pet.type_=(getById(getPetTypes, classOf[PetType], pet.typeId))
      val visits = visitRepository.findByPetId(pet.id)
      visits.foreach(visit => {
        pet.addVisit(visit)
      })

    })
  }

  override def save(owner:Owner) {
    val parameterSource = new BeanPropertySqlParameterSource(owner)
    if (owner.isNew) {
      val newKey = insertOwner.executeAndReturnKey(parameterSource)
      owner.id = newKey.intValue()
    } else {
      namedParameterJdbcTemplate.update(
        "UPDATE owners SET first_name=:firstName, last_name=:lastName, address=:address, " +
        "city=:city, telephone=:telephone WHERE id=:id",
        parameterSource
      )
    }
  }

  def getPetTypes:List[PetType] = {
    val params = scala.collection.mutable.Map[String, Object]()
    namedParameterJdbcTemplate.query(
      "SELECT id, name FROM types ORDER BY name",
      params,
      ParameterizedBeanPropertyRowMapper.newInstance(classOf[PetType])).asInstanceOf[List[PetType]]
  }

  /**
   * Loads the {@link Pet} and {@link Visit} data for the supplied {@link List} of {@link Owner Owners}.
   *
   * @param owners the list of owners for whom the pet and visit data should be loaded
   * @see #loadPetsAndVisits(Owner)
   */
  private def loadOwnersPetsAndVisits(owners:List[Owner]) {
    owners.foreach(owner => {
      loadPetsAndVisits(owner)
    })
  }


}
