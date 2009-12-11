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

  def dataBlobs: List[DataSet] = DataSet.findAll(By(DataSet.name, this), OrderBy(DataSet.id, Ascending))
}

object DataSetName extends DataSetName with LongKeyedMetaMapper[DataSetName]
