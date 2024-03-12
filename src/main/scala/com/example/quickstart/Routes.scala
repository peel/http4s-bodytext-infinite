package com.example.quickstart

import cats.implicits._
import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object Routes {

  def routes[F[_]: Sync](service: Service[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / vendor / version =>
        val res = service.cookie(req.bodyText.compile.string.map(Some(_)), s"$vendor/$version", req)
        Ok(res)
      case GET -> Root / "health" =>
        Ok("ok")
    }
  }
}
