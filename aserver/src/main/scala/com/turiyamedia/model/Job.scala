/*
 * Job.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.model

import net.liftweb._
import mapper._
import util._

class Job extends LongKeyedMapper[Job] with IdPK {
  def getSingleton = Job

  object queuedWhen extends MappedDateTime(this) {
    override def defaultValue = Helpers.now
  }
  
  object startedWhen extends MappedDateTime(this) {
    override def defaultValue = Helpers.now
  }
  
  object finishedWhen extends MappedDateTime(this) {
    override def defaultValue = Helpers.now
  }

  object user extends MappedLongForeignKey(this, User)
  
  object data extends MappedLongForeignKey(this, DataSetName)

  object answerData extends MappedLongForeignKey(this, RawData)

  object status extends MappedEnum(this, JobStatus)
}

object Job extends Job with LongKeyedMetaMapper[Job] 

object JobStatus extends Enumeration {
  val queued = Value(1, "Queued")
  val inProcess = Value(2, "In Process")
  val complete = Value(3, "Complete")
}