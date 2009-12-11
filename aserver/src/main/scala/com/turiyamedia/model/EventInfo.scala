/*
 * EventInfo.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.model

import net.liftweb._
import mapper._
import util._

class EventInfo extends LongKeyedMapper[EventInfo] with IdPK {
  def getSingleton = EventInfo

  object user extends MappedLongForeignKey(this, User)
  object event extends MappedEnum(this, Event)
  object data extends MappedText(this)
  object when extends MappedDateTime(this) {
    override def defaultValue = Helpers.now
  }
}

object EventInfo extends EventInfo with LongKeyedMetaMapper[EventInfo]

object Event extends Enumeration {
  val create = Value(1, "Create Account")
  val login = Value(2, "Login")
  val logout = Value(3, "Logout")
  val uploadData = Value(4, "Upload Data")
}