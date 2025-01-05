package com.chriskimberley.musicdistribution.service

import java.time.LocalDate

import zio.{IO, URLayer, ZIO, ZLayer}

import com.chriskimberley.musicdistribution.domain.ReleaseStatus.{Approved, Suspended}
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError
import com.chriskimberley.musicdistribution.domain.{
  Release,
  ReleaseDate,
  ReleaseId,
  ReleaseIn,
  ReleaseStatus,
  Song,
  SongIn
}
import com.chriskimberley.musicdistribution.persistence.{ReleaseRepository, SongRepository}

trait ReleaseService {
  def addRelease(release: ReleaseIn): IO[MusicServiceError, Release]
  def addNewSongToRelease(releaseId: ReleaseId, song: SongIn): IO[MusicServiceError, Song]
  def addNewSongsToRelease(releaseId: ReleaseId, songs: Seq[SongIn]): IO[MusicServiceError, Seq[Song]]
  def addExistingSongToRelease(releaseId: ReleaseId, song: Song): IO[MusicServiceError, Release]
  def addExistingSongsToRelease(releaseId: ReleaseId, songs: Seq[Song]): IO[MusicServiceError, Release]
  def proposeReleaseDate(releaseId: ReleaseId, date: LocalDate): IO[MusicServiceError, Unit]
  def approveReleaseDate(releaseId: ReleaseId, date: LocalDate): IO[MusicServiceError, Release]
  def rejectReleaseDate(releaseId: ReleaseId, date: LocalDate): IO[MusicServiceError, Unit]
  def suspendRelease(releaseId: ReleaseId): IO[MusicServiceError, Release]
}

object ReleaseService {
  def addRelease(release: ReleaseIn): ZIO[ReleaseService, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseService](_.addRelease(release))

  def addNewSongToRelease(
    releaseId: ReleaseId,
    song: SongIn
  ): ZIO[ReleaseService, MusicServiceError, Song] =
    ZIO.serviceWithZIO[ReleaseService](_.addNewSongToRelease(releaseId, song))

  def addNewSongsToRelease(
    releaseId: ReleaseId,
    songs: Seq[SongIn]
  ): ZIO[ReleaseService, MusicServiceError, Seq[Song]] =
    ZIO.serviceWithZIO[ReleaseService](_.addNewSongsToRelease(releaseId, songs))

  def addExistingSongToRelease(
    releaseId: ReleaseId,
    song: Song
  ): ZIO[ReleaseService, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseService](_.addExistingSongToRelease(releaseId, song))

  def addExistingSongsToRelease(
    releaseId: ReleaseId,
    songs: Seq[Song]
  ): ZIO[ReleaseService, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseService](_.addExistingSongsToRelease(releaseId, songs))

  def proposeReleaseDate(
    releaseId: ReleaseId,
    date: LocalDate
  ): ZIO[ReleaseService, MusicServiceError, Unit] =
    ZIO.serviceWithZIO[ReleaseService](_.proposeReleaseDate(releaseId, date))

  def approveReleaseDate(
    releaseId: ReleaseId,
    date: LocalDate
  ): ZIO[ReleaseService, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseService](_.approveReleaseDate(releaseId, date))

  def rejectReleaseDate(
    releaseId: ReleaseId,
    date: LocalDate
  ): ZIO[ReleaseService, MusicServiceError, Unit] =
    ZIO.serviceWithZIO[ReleaseService](_.rejectReleaseDate(releaseId, date))

  def suspendRelease(releaseId: ReleaseId): ZIO[ReleaseService, MusicServiceError, Release] =
    ZIO.serviceWithZIO[ReleaseService](_.suspendRelease(releaseId))
}

class ReleaseServiceImpl(
  releaseRepository: ReleaseRepository,
  songRepository: SongRepository
) extends ReleaseService {

  override def addRelease(release: ReleaseIn): IO[MusicServiceError, Release] =
    releaseRepository.save(release)

  override def addNewSongToRelease(
    releaseId: ReleaseId,
    song: SongIn
  ): IO[MusicServiceError, Song] =
    for {
      savedSong <- songRepository.save(song)
      _ <- releaseRepository.addSongToRelease(releaseId, savedSong)
    } yield savedSong

  override def addNewSongsToRelease(
    releaseId: ReleaseId,
    songs: Seq[SongIn]
  ): IO[MusicServiceError, Seq[Song]] =
    for {
      savedSongs <- songRepository.saveBatch(songs)
      _ <- releaseRepository.addSongsToRelease(releaseId, savedSongs)
    } yield savedSongs

  override def addExistingSongToRelease(
    releaseId: ReleaseId,
    song: Song
  ): IO[MusicServiceError, Release] =
    releaseRepository.addSongToRelease(releaseId, song)

  override def addExistingSongsToRelease(
    releaseId: ReleaseId,
    songs: Seq[Song]
  ): IO[MusicServiceError, Release] =
    releaseRepository.addSongsToRelease(releaseId, songs)

  override def proposeReleaseDate(
    releaseId: ReleaseId,
    date: LocalDate
  ): IO[MusicServiceError, Unit] =
    releaseRepository
      .updateReleaseDate(releaseId, ReleaseDate(date, ReleaseStatus.Pending))

  override def approveReleaseDate(
    releaseId: ReleaseId,
    date: LocalDate
  ): IO[MusicServiceError, Release] =
    updateReleaseAndSongs(
      releaseId = releaseId,
      newStatus = Approved,
      updateSongs = _.map(song =>
        song.copy(releaseDate = song.releaseDate match {
          case Some(existingDate) if existingDate.value.isBefore(date) =>
            song.releaseDate
          case _ => Some(ReleaseDate(date, ReleaseStatus.Approved))
        })
      )
    )

  override def rejectReleaseDate(
    releaseId: ReleaseId,
    date: LocalDate
  ): IO[MusicServiceError, Unit] =
    releaseRepository.updateReleaseDate(releaseId, ReleaseDate(date, ReleaseStatus.Rejected))

  override def suspendRelease(releaseId: ReleaseId): IO[MusicServiceError, Release] =
    updateReleaseAndSongs(
      releaseId = releaseId,
      newStatus = Suspended,
      updateSongs = _.map(_.copy(releaseDate = None))
    )

  private def updateReleaseAndSongs(
    releaseId: ReleaseId,
    newStatus: ReleaseStatus,
    updateSongs: Seq[Song] => Seq[Song]
  ): IO[MusicServiceError, Release] =
    for {
      release <- releaseRepository.findById(releaseId)
      _ <- releaseRepository.updateReleaseStatus(releaseId, newStatus)
      songs = release.songs
      updatedSongs = updateSongs(songs)
      _ <- ZIO.foreachDiscard(updatedSongs)(songRepository.update)
    } yield release
}

object ReleaseServiceImpl {
  val layer: URLayer[ReleaseRepository & SongRepository, ReleaseServiceImpl] =
    ZLayer.fromFunction(ReleaseServiceImpl(_, _))
}
