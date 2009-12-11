/*
 * Results.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.lib

import net.liftweb._
import http._
import mapper._
import sitemap._
import common._
import util._
import Helpers._

import model._

import scala.xml._

import java.io.ByteArrayInputStream

object Results extends Loc[Job] {
  // the name of the page
  def name = "Results"

  // the default parameters (used for generating the menu listing)
  def defaultValue = Empty

  // no extra parameters
  def params = List(Loc.Snippet("results", showResults _))

  private def showResults(in: NodeSeq): NodeSeq =
  (for {
      job <- requestValue.is
      answerData <- job.answerData.obj
      answerBytes <- Box !! answerData.data.is
      xml <- PCDataXmlParser(new ByteArrayInputStream(answerBytes))
    } yield xml) openOr NodeSeq.Empty

  /**
   * Generate a link based on the current page
   */
  val link =
  new Loc.Link[Job](List("results"), false) {
    override def createLink(in: Job) = {
      Full(Text("/results/"+urlEncode(in.id.toString)))
    }
  }

  /**
   * What's the text of the link?
   */
  val text = new Loc.LinkText(calcLinkText _)


  def calcLinkText(in: Job): NodeSeq =
  Text("Results for "+(in.data.obj.map(_.name.is) openOr ""))

  private object jobMemo extends RequestMemoize[String, Box[Job]]

  object CheckJob {
    def unapply(in: String): Option[Job] =
    jobMemo(in, 
        for {
          id <- Helpers.asLong(in)
          job <- Job.find(By(Job.id, id), PreCache(Job.data)) if job.status.is == JobStatus.complete
          data <- job.data.obj
          curUser <- User.currentUser if curUser.id.is == data.user.is
        } yield {
          job
        })
  }

  /**
   * Rewrite the request and emit the type-safe parameter
   */
  override val rewrite: LocRewrite =
  Full({
      case RewriteRequest(ParsePath("results" :: CheckJob(page) :: Nil, _, _,_),
                          _, _) =>
        (RewriteResponse("results" :: Nil), page)
    })


}

