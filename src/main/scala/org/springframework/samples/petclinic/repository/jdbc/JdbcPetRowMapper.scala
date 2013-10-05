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

import org.joda.time.DateTime
import org.springframework.jdbc.core.simple.ParameterizedRowMapper

import scala.collection.JavaConversions._

/**
 * {@link ParameterizedRowMapper} implementation mapping data from a {@link ResultSet} to the corresponding properties
 * of the {@link JdbcPet} class.
 */
class JdbcPetRowMapper extends ParameterizedRowMapper[JdbcPet] {

  override def mapRow(rs:ResultSet, rownum:Int) = {
    val pet = new JdbcPet()
    pet.id = rs.getInt("id")
    pet.name = rs.getString("name")
    val birthDate = rs.getDate("birth_date")
    pet.birthDate = new DateTime(birthDate)
    pet.typeId = rs.getInt("type_id")
    pet.ownerId = rs.getInt("owner_id")
    pet
  }
}