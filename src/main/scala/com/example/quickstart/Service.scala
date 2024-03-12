package com.example.quickstart

import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.implicits._
import cats.effect._
import org.http4s._

trait Service[F[_]] {
  def cookie(
      body: F[Option[String]],
      path: String,
      request: Request[F],
  ): F[String]
}

object Service {
  def impl[F[_]: Sync]: Service[F] = new Service[F] {
    override def cookie(
        body: F[Option[String]],
        path: String,
        request: Request[F],
    ): F[String] = for {
      body <- body
      _ <- sinkEvent(body)
      resp = buildHttpResponse(body)
    } yield resp
    def sinkEvent(body: Option[String]): F[Unit] = for {
      logger <- Slf4jLogger.create[F]
      _ <- logger.info("Slowly sinking event")
      _ <- Sync[F].delay(Thread.sleep(1000))
      _ <- logger.info(s"The event is: $body")
      _ <- Sync[F].delay(Thread.sleep(1000))
    } yield ()
    def buildHttpResponse(body: Option[String]) = body.getOrElse("empty")
  }
}
