package com.chriskimberley.musicdistribution.app

import com.chriskimberley.musicdistribution.http.DistributionService.routes
import com.chriskimberley.musicdistribution.persistence.PrefilledSongRepository
import com.chriskimberley.musicdistribution.service.SongServiceImpl
import zio.ZIO.logInfo
import zio.http.Server
import zio.{Task, ZIO, ZIOAppDefault}

object Main extends ZIOAppDefault {
  override def run: Task[Nothing] =
    logInfo("Music distribution service starting up") *> Server
      .serve(routes)
      .provide(Server.default, PrefilledSongRepository.layer, SongServiceImpl.layer)
}
