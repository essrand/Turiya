package com.turiyamedia.model

import _root_.net.liftweb._
import mapper._
import util._
import common._
import http._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def beforeCreate = maybeMakeSuper _ :: super.beforeCreate

  override def afterCreate = registerCreateEvent _ :: super.afterCreate

  /**
   * If the user we're about to create the first user in the database
   * make the user a super user
   */
  private def maybeMakeSuper(who: User) {
    if (User.find().isEmpty) {
      who.superUser(true)
    }
  }

  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
      <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
                                 locale, timezone, password, textArea)

  // comment this line out to require email validations in dev mode
  override def skipEmailValidation = Props.devMode

  // constructor
  onLogIn ::= loginEvent
  onLogOut ::= logoutEvent

  private var whosThere: Map[Long, List[LiftSession]] = Map()

  /**
  * Given a user id, get all the sessions for that user
  */
 def sessionListForId(id: Long): List[LiftSession] = User.this.synchronized {
   whosThere.getOrElse(id, Nil)
 }

  private object randomSessionId extends SessionVar(Helpers.randomString(20))

  private def loginEvent(who: User) {
    EventInfo.create.user(who).event(Event.login).data(randomSessionId).saveMe

    for {
      sess <- S.session
    } User.this.synchronized {
      whosThere += (who.id.is -> (sess :: whosThere.getOrElse(who.id.is, Nil)))
    }

  }

  private def logoutEvent(who: Box[User]) {
    EventInfo.create.user(who).event(Event.logout).data(randomSessionId).saveMe


    for {
      sess <- S.session
      user <- who
    } User.this.synchronized {
      whosThere += (user.id.is -> (whosThere.getOrElse(user.id.is, Nil).filter(_ ne sess)))
    }
  }

  private def registerCreateEvent(who: User) {
    EventInfo.create.user(who).event(Event.create).saveMe
  }
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}
