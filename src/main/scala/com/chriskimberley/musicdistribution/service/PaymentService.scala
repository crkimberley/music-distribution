package com.chriskimberley.musicdistribution.service

import java.time.OffsetDateTime

import scala.concurrent.duration.{DurationInt, FiniteDuration}

import zio.{IO, URLayer, ZIO, ZLayer}
import PaymentService.MonetizationThreshold
import com.chriskimberley.musicdistribution.domain.{ArtistId, Payment, PaymentIn, SongStream}
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError
import com.chriskimberley.musicdistribution.persistence.{PaymentRepository, SongStreamRepository}

trait PaymentService {
  def calculateAndSavePayment(artistId: ArtistId): IO[MusicServiceError, Payment]
  def getCurrentPaymentPeriod(artistId: ArtistId): IO[MusicServiceError, (OffsetDateTime, OffsetDateTime)]
}

object PaymentService {
  val MonetizationThreshold: FiniteDuration = 30.seconds

  def calculateAndSavePayment(
    artistId: ArtistId
  ): ZIO[PaymentService, MusicServiceError, Payment] =
    ZIO.serviceWithZIO[PaymentService](_.calculateAndSavePayment(artistId))

  def getCurrentPaymentPeriod(
    artistId: ArtistId
  ): ZIO[PaymentService, MusicServiceError, (OffsetDateTime, OffsetDateTime)] =
    ZIO.serviceWithZIO[PaymentService](_.getCurrentPaymentPeriod(artistId))

}

class PaymentServiceImpl(
  streamRepository: SongStreamRepository,
  paymentRepository: PaymentRepository
) extends PaymentService {
  def calculateAndSavePayment(artistId: ArtistId): IO[MusicServiceError, Payment] =
    for {
      (startTime, endTime) <- getCurrentPaymentPeriod(artistId)
      streams <- streamRepository.findStreamsByArtistAndTime(artistId, startTime, endTime)
      monetizedStreams = filterMonetizedStreams(streams)
      totalAmount = calculatePayment(monetizedStreams)
      payment = PaymentIn(artistId, totalAmount, startTime, endTime)
      savedPayment <- paymentRepository.savePayment(payment)
    } yield savedPayment

  def getCurrentPaymentPeriod(
    artistId: ArtistId
  ): IO[MusicServiceError, (OffsetDateTime, OffsetDateTime)] =
    for {
      lastPayment <- paymentRepository
                       .findLastPaymentForArtist(artistId)
                       .map(_.map(_.endTime))
    } yield (lastPayment.getOrElse(OffsetDateTime.MIN), OffsetDateTime.now)

  private def filterMonetizedStreams(streams: Seq[SongStream]): Seq[SongStream] =
    streams.filter(_.duration > MonetizationThreshold)

  // TODO: replace placeholder method
  private def calculatePayment(streams: Seq[SongStream]): BigDecimal = {
    val ratePerStream = BigDecimal(0.01)
    streams.size * ratePerStream
  }
}

object PaymentServiceImpl {
  val layer: URLayer[SongStreamRepository & PaymentRepository, PaymentServiceImpl] =
    ZLayer.fromFunction(PaymentServiceImpl(_, _))
}
