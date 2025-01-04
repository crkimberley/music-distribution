import zio.ZIO.logInfo
import zio.http.Server
import zio.{Task, ZIO, ZIOAppDefault}

import com.chriskimberley.http.DistributionService.routes
import com.chriskimberley.persistence.PrefilledSongRepository
import com.chriskimberley.service.SongServiceImpl

object Main extends ZIOAppDefault {
  override def run: Task[Nothing] =
    logInfo("Music distribution service starting up") *> Server
      .serve(routes)
      .provide(Server.default, PrefilledSongRepository.layer, SongServiceImpl.layer)
}
