/*
 * DataSet.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.model

import net.liftweb._
import mapper._

/**
* Many different data sets for each data set name.
*/
class DataSet extends LongKeyedMapper[DataSet] with IdPK {
  def getSingleton = DataSet

  object data extends MappedLongForeignKey(this, RawData)
  object name extends MappedLongForeignKey(this, DataSetName)
}

object DataSet extends DataSet with LongKeyedMetaMapper[DataSet]
