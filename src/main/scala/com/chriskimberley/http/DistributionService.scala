package com.chriskimberley.http

import zio.http.*
import zio.http.Method.GET
import zio.json.EncoderOps

import com.chriskimberley.service.SongService

object DistributionService {
  val routes: Routes[SongService, Nothing] = Routes(
    GET / "search" / string("title") -> handler((title: String, request: Request) =>
      for {
        songs <- SongService.searchReleasedSongs(title).orDie
      } yield Response.json(songs.toJson)
    )
  )
}
