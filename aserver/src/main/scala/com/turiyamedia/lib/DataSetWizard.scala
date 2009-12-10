/*
 * DataSetWizard.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.lib

import net.liftweb._
import wizard._
import util._
import common._
import http._
import mapper._

import model._

import scala.xml._

object DataSetWizard extends Wizard {
  override def calcFirstScreen = 
  if (DataSetName.find(By(DataSetName.user, User.currentUser)).isEmpty) Full(newSet) else Full(choose)

  val choose = new Screen {
    val chooseField = new Field {
      type ValueType = Box[DataSetName]

      def default = Empty

      def title = S ?? "Choose Data Set"

      lazy val manifest = buildIt[ValueType]

      override def toForm =
      SHtml.selectObj[ValueType]((Empty -> "New Data Set") ::
                                 DataSetName.findAll(By(DataSetName.user, User.currentUser),
                                                     OrderBy(DataSetName.name, Ascending)).map(ds => (Full(ds) -> ds.name.is)),
                                 Full(is), newValue => set(newValue))
    }

    override def nextScreen = if (chooseField.is.isEmpty) Full(newSet) else Full(uploadData)
  }

  val newSet = new Screen {
    val name = new Field with StringField {
      def title = S ?? "Enter the name of the new data set"

      private def checkDup(str: String): List[FieldError] = 
      DataSetName.find(By(DataSetName.user, User.currentUser), By(DataSetName.name, str)).
      toList.map(_ => FieldError(this, Text("Duplicate data set name")))

      override def validation = minLen(2, S ?? "Name Too Short") ::
      maxLen(80, S ?? "Name Too Long") :: checkDup _ :: super.validation
    }
  }

  val uploadData = new Screen {

  }

  def finish() {
    val dataSet: DataSetName = choose.chooseField.is openOr DataSetName.create.name(newSet.name.is).user(User.currentUser).saveMe
    ()
  }
  
}

trait BooleanField extends FieldIdentifier {
  self: Wizard#Screen#Field =>
  type ValueType = Boolean

  def default = false

  lazy val manifest = buildIt[Boolean]
}