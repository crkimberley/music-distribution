package com.chriskimberley.musicdistribution.service

import java.time.OffsetDateTime

import zio.{IO, URLayer, ZIO, ZLayer}
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError.NotFoundError
import PaymentService.MonetizationThreshold
import com.chriskimberley.musicdistribution.domain.{ArtistId, SongStreamCount, StreamReport}
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError
import com.chriskimberley.musicdistribution.persistence.{ArtistRepository, SongRepository, SongStreamRepository}

trait ReportService {
  def generateStreamReport(
    artistId: ArtistId,
    startTime: Option[OffsetDateTime],
    endTime: Option[OffsetDateTime]
  ): IO[MusicServiceError, StreamReport]
}

object ReportService {
  def generateStreamReport(
    artistId: ArtistId,
    startTime: Option[OffsetDateTime],
    endTime: Option[OffsetDateTime]
  ): ZIO[ReportService, MusicServiceError, StreamReport] =
    ZIO.serviceWithZIO[ReportService](_.generateStreamReport(artistId, startTime, endTime))
}

case class ReportServiceImpl(
  songStreamRepository: SongStreamRepository,
  songRepository: SongRepository,
  artistRepository: ArtistRepository,
  paymentService: PaymentService
) extends ReportService {
  def generateStreamReport(
    artistId: ArtistId,
    startTimeOpt: Option[OffsetDateTime] = None,
    endTimeOpt: Option[OffsetDateTime] = None
  ): IO[MusicServiceError, StreamReport] =
    for {
      artist <- artistRepository
                  .findById(artistId)
                  .flatMap(
                    ZIO
                      .fromOption(_)
                      .orElseFail(NotFoundError(s"Artist ID $artistId not found"))
                  )

      (startTime, endTime) <- getPaymentPeriod(artistId, startTimeOpt, endTimeOpt)

      songToStreamsMap <- songStreamRepository
                            .findStreamsByArtistAndTime(artistId, startTime, endTime)
                            .map(_.groupBy(_.songId))

      songDetails <- ZIO.foreach(songToStreamsMap.keys.toSeq)(songId =>
                       songRepository
                         .findById(songId)
                         .flatMap(
                           ZIO
                             .fromOption(_)
                             .orElseFail(NotFoundError(s"Song ID $songId not found"))
                         )
                     )

      songStreamCounts = songDetails.map { song =>
                           val songStreams = songToStreamsMap(song.id)
                           val monetized = songStreams
                             .count(_.duration > MonetizationThreshold)
                           val notMonetized = songStreams.size - monetized
                           SongStreamCount(song, monetized, notMonetized)
                         }
    } yield StreamReport(artist, startTime, endTime, songStreamCounts)

  private def getPaymentPeriod(
    artistId: ArtistId,
    startOpt: Option[OffsetDateTime],
    endOpt: Option[OffsetDateTime]
  ) = (startOpt, endOpt) match {
    case (Some(start), Some(end)) => ZIO.succeed((start, end))
    case _                        => paymentService.getCurrentPaymentPeriod(artistId)
  }
}

object ReportServiceImpl {
  val layer: URLayer[SongStreamRepository & SongRepository & ArtistRepository & PaymentService, ReportServiceImpl] =
    ZLayer.fromFunction(ReportServiceImpl(_, _, _, _))
}
