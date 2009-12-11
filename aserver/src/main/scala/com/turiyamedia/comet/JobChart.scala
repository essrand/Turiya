/*
 * JobChart.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.comet

import net.liftweb._
import http._
import util._
import mapper._

import model._

class JobChart extends CometActor {
  def render = {
    Job.findAll(By(Job.user, User.currentUser), PreCache(Job.data), OrderBy(Job.id, Descending)) match {
      case Nil => <div>No jobs... how about <a href="/upload">uploading some data</a></div>
      case jobList =>
        <div>
          <ul>
            {
              for {
                job <- jobList
                dataSetName <- job.data.obj
              } yield
              <li>
                Data Set: {dataSetName.name}
                {
                  job.status.is match {
                    case JobStatus.complete => <a href={"/results/"+job.id}>Results</a>
                    case s => <span>Status: {s}</span>
                  }
                }
              </li>
            }
          </ul>
        </div>
    }
  }

  override def lowPriority = {
    case UpdateThyself => reRender(false)
  }
}

case object UpdateThyself