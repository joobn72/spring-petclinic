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

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.samples.petclinic.model.Vet
import org.springframework.samples.petclinic.model.Vets
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView

import com.sun.syndication.feed.atom.Content
import com.sun.syndication.feed.atom.Entry
import com.sun.syndication.feed.atom.Feed

import scala.collection.JavaConversions._

/**
 * A view creating a Atom representation from a list of Visit objects.
 *
 * @author Alef Arendsen
 * @author Arjen Poutsma
 */
class VetsAtomView extends AbstractAtomFeedView {

  //@Override
  def buildFeedMetadata(model: Map[String, Object], feed: Feed, request: HttpServletRequest) {
    feed.setId("tag:springsource.org")
    feed.setTitle("Veterinarians")
    //feed.setUpdated(date);
  }

  //@Override
  override protected def buildFeedEntries(model: java.util.Map[String, Object],
    request: HttpServletRequest, response: HttpServletResponse) = {

    val vets = model.get("vets").asInstanceOf[Vets]
    val vetList = vets.getVetList
    val entries = List[Entry]()

    vetList.foreach(vet => {
      val entry = new Entry()
      // see http://diveintomark.org/archives/2004/05/28/howto-atom-id#other
      entry.setId(String.format("tag:springsource.org,%s", vet.id.toString))
      entry.setTitle(String.format("Vet: %s %s", vet.firstName, vet.lastName))
      //entry.setUpdated(visit.getDate().toDate());

      val summary = new Content()
      summary.setValue(vet.getSpecialties.toString())
      entry.setSummary(summary)

      entries.add(entry)
    })
    response.setContentType("blabla")
    entries

  }

}
