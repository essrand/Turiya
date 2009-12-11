/*
 * RawData.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.model

import net.liftweb._
import mapper._

/**
* A place to put raw data (streams of bytes)
*/
class RawData extends LongKeyedMapper[RawData] with IdPK {
  def getSingleton = RawData

  object data extends MappedBinary(this)
}

object RawData extends RawData with LongKeyedMetaMapper[RawData]
