package com.chriskimberley.persistence

import zio.{IO, ZIO}

import com.chriskimberley.domain.*
import com.chriskimberley.domain.error.MusicServiceError

trait ReleaseRepository {
  def save(release: ReleaseIn): IO[MusicServiceError, Release]

  def addSongToRelease(releaseId: ReleaseId, song: Song): IO[MusicServiceError, Release]

  def addSongsToRelease(releaseId: ReleaseId, songs: Seq[Song]): IO[MusicServiceError, Release]

  def updateReleaseDate(releaseId: ReleaseId, date: ReleaseDate): IO[MusicServiceError, Unit]

  def updateReleaseStatus(releaseId: ReleaseId, status: ReleaseStatus): IO[MusicServiceError, Unit]

  def findById(releaseId: ReleaseId): IO[MusicServiceError, Release]
}

object ReleaseRepository {
  def save(
    release: ReleaseIn
  ): ZIO[ReleaseRepository, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseRepository](_.save(release))

  def addSongToRelease(
    releaseId: ReleaseId,
    song: Song
  ): ZIO[ReleaseRepository, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseRepository](_.addSongToRelease(releaseId, song))

  def addSongsToRelease(
    releaseId: ReleaseId,
    songs: Seq[Song]
  ): ZIO[ReleaseRepository, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseRepository](_.addSongsToRelease(releaseId, songs))

  def updateReleaseDate(
    releaseId: ReleaseId,
    date: ReleaseDate
  ): ZIO[ReleaseRepository, MusicServiceError, Unit] =
    ZIO.serviceWithZIO[ReleaseRepository](_.updateReleaseDate(releaseId, date))

  def updateReleaseStatus(
    releaseId: ReleaseId,
    status: ReleaseStatus
  ): ZIO[ReleaseRepository, MusicServiceError, Unit] =
    ZIO.serviceWithZIO[ReleaseRepository](_.updateReleaseStatus(releaseId, status))

  def findById(
    releaseId: ReleaseId
  ): ZIO[ReleaseRepository, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseRepository](_.findById(releaseId))
}
