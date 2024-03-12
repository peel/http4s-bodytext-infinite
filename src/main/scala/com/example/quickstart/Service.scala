package com.example.quickstart

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
      _ <- sinkEvent()
      resp = buildHttpResponse(body)
    } yield resp
    def sinkEvent(): F[Unit] = for {
      _ <- Sync[F].delay(Thread.sleep(1000))
      _ <- Sync[F].delay(Thread.sleep(1000))
    } yield ()
    def buildHttpResponse(body: Option[String]) = body.getOrElse("empty")
  }
}
