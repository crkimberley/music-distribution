package com.chriskimberley.persistence

import java.time.OffsetDateTime

import zio.{IO, ZIO}

import com.chriskimberley.domain.error.MusicServiceError
import com.chriskimberley.domain.{ArtistId, SongStream, SongStreamIn}

trait SongStreamRepository {
  def save(stream: SongStreamIn): IO[MusicServiceError, SongStream]

  def findStreamsByArtistAndTime(
    artistId: ArtistId,
    startTime: OffsetDateTime,
    endTime: OffsetDateTime
  ): IO[MusicServiceError, Seq[SongStream]]
}

object SongStreamRepository {
  def save(stream: SongStreamIn): ZIO[SongStreamRepository, MusicServiceError, SongStream] =
    ZIO.serviceWithZIO[SongStreamRepository](_.save(stream))

  def findStreamsByArtistAndTime(
    artistId: ArtistId,
    startTime: OffsetDateTime,
    endTime: OffsetDateTime
  ): ZIO[SongStreamRepository, MusicServiceError, Seq[SongStream]] =
    ZIO.serviceWithZIO[SongStreamRepository](_.findStreamsByArtistAndTime(artistId, startTime, endTime))
}
