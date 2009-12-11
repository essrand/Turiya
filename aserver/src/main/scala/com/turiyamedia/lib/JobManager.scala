/*
 * JobManager.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.lib

import net.liftweb._
import common._
import util._
import actor._
import mapper._

import comet._
import model._

object JobManager extends LiftActor {
  private var threadCnt = 0

  protected def messageHandler = {
    case NewJob(what) =>
      Job.create.user(what.user).data(what).status(JobStatus.queued).save
      notifyUser(what.user)
      this ! GrabNextJob

    case GrabNextJob =>
      if (threadCnt < 1) {
        for {
          job <- Job.find(By(Job.status, JobStatus.queued), OrderBy(Job.id, Ascending))
        } {
          threadCnt += 1
          job.startedWhen(Helpers.now).status(JobStatus.inProcess).save
          runJob(job)
          this ! GrabNextJob
          notifyUser(job.user)
        }
      }

    case JobFinished(job) =>
      threadCnt -= 1
      job.finishedWhen(Helpers.now).status(JobStatus.complete).save
      notifyUser(job.user)
      this ! GrabNextJob

    case Startup =>
      // make sure all jobs are queued on startup
      DB.use(DefaultConnectionIdentifier) {
        conn =>
        for {
          job <- Job.findAll(By(Job.status, JobStatus.inProcess))
        } job.status(JobStatus.queued).save
      }
      this ! GrabNextJob
  }

  /**
   * Notify all JobChart components for the logged in user that the job status has changed
   */
  private def notifyUser(who: Long) {
    for {
      sess <- User.sessionListForId(who)
      comet <- sess.findComet("JobChart")
    } comet ! UpdateThyself
  }

  private case object Startup
  private case object GrabNextJob
  private case class JobFinished(job: Job)

  private def runJob(job: Job) {
    new Thread("Job "+job.id) {
      override def run() {
        Thread.sleep(10000)
        val raw = RawData.create.data(<span>Finished job {job.id}</span>.toString.getBytes("UTF-8")).saveMe
        job.answerData(raw).save
        JobManager ! JobFinished(job)
      }
    }.start
  }

  this ! Startup

  /**
   * Do nothing, but allow the singleton to be instantiated in Boot.scala
   */
  def touch() {

  }
}

case class NewJob(dataSet: DataSetName)