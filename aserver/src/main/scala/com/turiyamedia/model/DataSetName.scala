/*
 * The name of a data set
 */

package com.turiyamedia.model

import net.liftweb._
import mapper._

class DataSetName extends LongKeyedMapper[DataSetName] with IdPK {
  def getSingleton = DataSetName

  object user extends MappedLongForeignKey(this, User)
  object name extends MappedPoliteString(this, 128)
}

object DataSetName extends DataSetName with LongKeyedMetaMapper[DataSetName]
