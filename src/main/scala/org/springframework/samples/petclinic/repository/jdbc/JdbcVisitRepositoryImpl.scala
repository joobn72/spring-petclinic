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

import java.sql.ResultSet
import java.sql.SQLException

import javax.sql.DataSource

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.ParameterizedRowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.repository.VisitRepository
import org.springframework.stereotype.Repository

import scala.collection.JavaConversions._

/**
 * A simple JDBC-based implementation of the {@link VisitRepository} interface.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 * @author Michael Isvy
 */
@Repository
@Autowired
class JdbcVisitRepositoryImpl(dataSource:DataSource) extends VisitRepository {

  val jdbcTemplate = new JdbcTemplate(dataSource)

  val insertVisit = new SimpleJdbcInsert(dataSource)
                          .withTableName("visits")
                          .usingGeneratedKeyColumns("id")

  override def save(visit:Visit) {
    if (visit.isNew) {
      val newKey = insertVisit.executeAndReturnKey(createVisitParameterSource(visit))
      visit.id = newKey.intValue()
    } else {
      throw new UnsupportedOperationException("Visit update not supported")
    }
  }

  def deletePet(id:Int) {
    jdbcTemplate.update("DELETE FROM pets WHERE id=?", id:java.lang.Integer)
  }


  /**
   * Creates a {@link MapSqlParameterSource} based on data values from the supplied {@link Visit} instance.
   */
  private def createVisitParameterSource(visit:Visit) = {
    new MapSqlParameterSource()
          .addValue("id", visit.id)
          .addValue("visit_date", visit.date.toDate())
          .addValue("description", visit.description)
          .addValue("pet_id", visit.pet.id)
  }

  override def findByPetId(petId:Int):List[Visit] = {
    val visits:List[Visit] = jdbcTemplate.query(
      "SELECT id, visit_date, description FROM visits WHERE pet_id=?",
      new ParameterizedRowMapper[Visit]() {
        override def mapRow(rs:ResultSet, row:Int) = {
          val visit = new Visit()
          visit.id = rs.getInt("id")
          val visitDate = rs.getDate("visit_date")
          visit.date = new DateTime(visitDate)
          visit.description = rs.getString("description")
          visit
        }
      }, petId:java.lang.Integer).toList
    visits
  }

}