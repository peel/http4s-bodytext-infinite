package com.example.quickstart

import scala.concurrent.duration._
import cats.effect.Async
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.{Logger, HSTS}
import org.http4s.headers.`Strict-Transport-Security`
import java.net.InetSocketAddress

object Server {

  def run[F[_]: Async]: F[Nothing] = {
    val httpApp = Routes.routes[F](Service.impl[F]).orNotFound
    val hstsApp =
      HSTS(httpApp, `Strict-Transport-Security`.unsafeFromDuration(360.days))
    val finalApp = Logger.httpApp(true, true)(hstsApp)

    BlazeServerBuilder[F]
      .bindSocketAddress(new InetSocketAddress(3001))
      .withHttpApp(finalApp)
      .withIdleTimeout(610.seconds)
      .withMaxConnections(8126)
      .resource
      .useForever
  }
}
