/*
 * ProcessLoginDates.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.turiyamedia.lib

import net.liftweb._
import mapper._
import common._
import util._
import Helpers._

import model._

import scala.xml._
import scala.collection.mutable.ListBuffer

import java.util.Date

object ProcessLoginDates {
  def runJob(job: Job): Elem = {
    try {
      // wrap in a single transaction
      DB.use(DefaultConnectionIdentifier) {
        ignore =>

        val badLines = new ListBuffer[String]()
        // remove any old data sets
        PlayTime.bulkDelete_!!(By(PlayTime.set, job.data))

        for {
          dataName <- job.data.obj
          dataBlob <- dataName.dataBlobs
          rawData <- dataBlob.data.obj
          bytes <- Box !! rawData.data.is
          line <- lines(bytes)
        } {
          line.roboSplit("\t") match {
            case id :: ADate(start) :: ADate(end) :: _ =>
              PlayTime.create.set(dataName).playerId(id).startPlay(start).endPlay(end).save
            case bad =>
              println("bad is "+bad)
              badLines += line
          }
        }

        badLines.toList match {
          case Nil =>
            val (cols, data) = DB.runQuery("select playerid, sum(endplay - startplay) from playtime where set_c = "+job.data.is+" group by playerid order by sum desc limit 20")

            <div>
              {data.length} most popular players:
              <table>
                <tr><td>player</td><td>Hours</td></tr>
                {
                  data.map {
                    case name :: time :: _ => <tr><td>{name}</td><td>{time}</td></tr>
                  }
                }
              </table>
            </div>

          case xs =>
            <div>Bad data:
              <ul>
                {
                  xs.map(i => <li>{i}</li>)
                }
              </ul>
            </div>
        }
      }
    } catch {
      case e => <div>Fail: <pre>{e.toString}</pre></div>
    }
  }

  private object ADate {
    def unapply(in: String): Option[Date] = Helpers.boxParseInternetDate(in).toOption
  }

  def lines(in: Array[Byte]): Iterator[String] = {
    import java.io._
    val is = new ByteArrayInputStream(in)
    val reader = new BufferedReader(new InputStreamReader(is))

    new Iterator[String] {
      private var _next: String = null
      private var done = false
      
      def hasNext(): Boolean = {
        done || {
          _next = reader.readLine
          if (_next eq null) {
            done = true
            reader.close
            is.close
            false
          } else true
        }
      }
      
      def next(): String = _next
    }
  }
}
