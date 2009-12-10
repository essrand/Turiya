package com.turiyamedia.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def beforeCreate = maybeMakeSuper _ :: super.beforeCreate

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
