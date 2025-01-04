package com.chriskimberley.service

import zio.{IO, URLayer, ZIO, ZLayer}

import com.github.vickumar1981.stringdistance.LevenshteinDistance
import com.github.vickumar1981.stringdistance.StringDistance.Levenshtein

import com.chriskimberley.domain.error.MusicServiceError
import com.chriskimberley.domain.{Song, SongIn}
import com.chriskimberley.persistence.SongRepository

trait SongService {
  def addSong(song: SongIn): IO[MusicServiceError, Song]
  def addSongs(songs: Seq[SongIn]): IO[MusicServiceError, Seq[Song]]
  def searchReleasedSongs(
    query: String,
    minResults: Int,
    maxThreshold: Int
  ): IO[MusicServiceError, Seq[Song]]
}

object SongService {
  val MinResults = 3
  val MaxThreshold = 3

  def addSong(song: SongIn): ZIO[SongService, MusicServiceError, Song] =
    ZIO.serviceWithZIO[SongService](_.addSong(song))

  def addSongs(songs: Seq[SongIn]): ZIO[SongService, MusicServiceError, Seq[Song]] =
    ZIO.serviceWithZIO[SongService](_.addSongs(songs))

  def searchReleasedSongs(
    query: String,
    minResults: Int = MinResults,
    maxThreshold: Int = MaxThreshold
  ): ZIO[SongService, MusicServiceError, Seq[Song]] =
    ZIO.serviceWithZIO[SongService](_.searchReleasedSongs(query, minResults, maxThreshold))
}

case class SongServiceImpl(songRepository: SongRepository) extends SongService {
  override def addSong(song: SongIn): IO[MusicServiceError, Song] =
    songRepository.save(song)

  override def addSongs(songs: Seq[SongIn]): IO[MusicServiceError, Seq[Song]] =
    songRepository.saveBatch(songs)

  override def searchReleasedSongs(
    query: String,
    minResults: Int,
    maxThreshold: Int
  ): IO[MusicServiceError, Seq[Song]] = {
    def levenshteinDistance(song: Song) =
      ZIO.succeed(Levenshtein.distance(query, song.title) -> song)

    for {
      releasedSongs <- songRepository.findAllReleased
      songs <- ZIO
                 .foreachPar(releasedSongs)(levenshteinDistance)
                 .map(_.groupMap(_._1)(_._2).withDefaultValue(Seq.empty))
                 .map((0 to maxThreshold).flatMap(_).take(minResults))
    } yield songs
  }
}

object SongServiceImpl {
  val layer: URLayer[SongRepository, SongServiceImpl] =
    ZLayer.fromFunction(SongServiceImpl(_))
}
