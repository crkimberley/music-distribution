package com.chriskimberley.musicdistribution.http

import com.chriskimberley.musicdistribution.service.SongService
import zio.http.*
import zio.http.Method.GET
import zio.json.EncoderOps

object DistributionService {
  val routes: Routes[SongService, Nothing] = Routes(
    GET / "search" / string("title") -> handler((title: String, request: Request) =>
      for {
        songs <- SongService.searchReleasedSongs(title).orDie
      } yield Response.json(songs.toJson)
    )
  )
}
