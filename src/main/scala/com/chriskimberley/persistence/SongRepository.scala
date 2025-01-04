package com.chriskimberley.persistence

import zio.{IO, ZIO}

import com.chriskimberley.domain.error.MusicServiceError
import com.chriskimberley.domain.{Song, SongId, SongIn}

trait SongRepository {
  def save(song: SongIn): IO[MusicServiceError, Song]
  def saveBatch(songs: Seq[SongIn]): IO[MusicServiceError, Seq[Song]]
  def findAllReleased: IO[MusicServiceError, Seq[Song]]
  def findById(songId: SongId): IO[MusicServiceError, Option[Song]]
  def update(song: Song): IO[MusicServiceError, Unit]
}

object SongRepository {
  def save(song: SongIn): ZIO[SongRepository, MusicServiceError, Song] =
    ZIO.serviceWithZIO[SongRepository](_.save(song))

  def saveBatch(songs: Seq[SongIn]): ZIO[SongRepository, MusicServiceError, Seq[Song]] =
    ZIO.serviceWithZIO[SongRepository](_.saveBatch(songs))

  def findAllReleased: ZIO[SongRepository, MusicServiceError, Seq[Song]] =
    ZIO.serviceWithZIO[SongRepository](_.findAllReleased)

  def findById(songId: SongId): ZIO[SongRepository, MusicServiceError, Option[Song]] =
    ZIO.serviceWithZIO[SongRepository](_.findById(songId))

  def update(song: Song): ZIO[SongRepository, MusicServiceError, Unit] =
    ZIO.serviceWithZIO[SongRepository](_.update(song))
}
