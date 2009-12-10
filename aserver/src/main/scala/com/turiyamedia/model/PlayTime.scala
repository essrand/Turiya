/*
* Contains play time for given player ids
 */

package com.turiyamedia.model

import net.liftweb._
import mapper._

class PlayTime extends LongKeyedMapper[PlayTime] with IdPK {
  def getSingleton = PlayTime

  object set extends MappedLongForeignKey(this, DataSetName)
  object playerId extends MappedPoliteString(this, 256)
  object startPlay extends MappedDateTime(this)
  object endPlay extends MappedDateTime(this)
}

object PlayTime extends PlayTime with LongKeyedMetaMapper[PlayTime]
