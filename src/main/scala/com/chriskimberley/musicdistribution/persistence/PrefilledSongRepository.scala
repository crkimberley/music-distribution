package com.chriskimberley.musicdistribution.persistence

import java.time.LocalDate
import java.time.LocalDate.now
import java.util.UUID

import zio.{IO, Ref, ULayer, ZLayer}

import com.chriskimberley.musicdistribution.domain.ReleaseStatus.Approved
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError
import com.chriskimberley.musicdistribution.domain.{ReleaseDate, Song, SongId, SongIn}

case class PrefilledSongRepository(ref: Ref[Seq[Song]]) extends SongRepository {
  override def save(song: SongIn): IO[MusicServiceError, Song] = ???

  override def findById(songId: SongId): IO[MusicServiceError, Option[Song]] = ???

  override def saveBatch(songs: Seq[SongIn]): IO[MusicServiceError, Seq[Song]] = ???

  override def update(song: Song): IO[MusicServiceError, Unit] = ???

  override def findAllReleased: IO[MusicServiceError, Seq[Song]] =
    for {
      songs <- ref.get
    } yield songs.filter(s =>
      s.releaseDate.isDefined &&
        s.releaseDate.get.status == Approved &&
        !s.releaseDate.get.value.isAfter(now())
    )
}

object PrefilledSongRepository {
  val approvedReleaseDateOfToday = Some(ReleaseDate(now(), Approved))

  val songIds: Seq[SongId] = Seq(
    "5146714c-4053-407d-b86e-aaf2426523e6",
    "ad1a90c4-fcc0-491e-b74e-93d1c67bd5d1",
    "c5c06303-e556-4c5d-a75a-b33419a23b63",
    "24096055-6b41-4409-87cc-2cf1f8ad1b6b",
    "96aa839d-600b-4a27-971e-924a47c147a0",
    "17703bd5-5bb3-469d-aca3-17a6f9db0cd6",
    "22aef79c-7d98-4411-845b-344de75dcaaa",
    "5009d384-e59b-439d-88ff-fd92ed5ad732",
    "631aa5a5-d88a-49aa-ad93-a99e3938b236",
    "3da65135-c38f-4804-9110-2fe5b32873dd"
  ).map(s => SongId(UUID.fromString(s)))

  val layer: ULayer[SongRepository] = ZLayer {
    Ref
      .make(
        Seq(
          Song(songIds(0), "ann", approvedReleaseDateOfToday),
          Song(songIds(1), "bella", approvedReleaseDateOfToday),
          Song(songIds(2), "claire", approvedReleaseDateOfToday),
          Song(songIds(3), "danni", approvedReleaseDateOfToday),
          Song(songIds(4), "ella", approvedReleaseDateOfToday),
          Song(songIds(5), "fiona", approvedReleaseDateOfToday),
          Song(songIds(6), "grace", approvedReleaseDateOfToday),
          Song(songIds(7), "heidi", approvedReleaseDateOfToday),
          Song(songIds(8), "ida", approvedReleaseDateOfToday),
          Song(songIds(9), "jenny", approvedReleaseDateOfToday)
        )
      )
      .map(PrefilledSongRepository(_))
  }
}
