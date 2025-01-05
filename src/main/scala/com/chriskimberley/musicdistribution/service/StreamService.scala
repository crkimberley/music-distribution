package com.chriskimberley.musicdistribution.service

import com.chriskimberley.musicdistribution.domain.{SongStream, SongStreamIn}
import zio.{IO, URLayer, ZIO, ZLayer}
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError
import com.chriskimberley.musicdistribution.persistence.SongStreamRepository

trait StreamService {
  def addStream(songStream: SongStreamIn): IO[MusicServiceError, SongStream]
}

object StreamService {
  def addStream(songStream: SongStreamIn): ZIO[StreamService, MusicServiceError, SongStream] =
    ZIO.serviceWithZIO[StreamService](_.addStream(songStream))
}

case class StreamServiceImpl(songStreamRepository: SongStreamRepository) extends StreamService {
  override def addStream(songStream: SongStreamIn): IO[MusicServiceError, SongStream] =
    songStreamRepository.save(songStream)
}

object StreamServiceImpl {
  val layer: URLayer[SongStreamRepository, StreamServiceImpl] =
    ZLayer.fromFunction(StreamServiceImpl(_))
}
