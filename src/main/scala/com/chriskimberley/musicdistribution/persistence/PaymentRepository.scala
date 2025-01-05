package com.chriskimberley.musicdistribution.persistence

import com.chriskimberley.musicdistribution.domain.{ArtistId, Payment, PaymentIn}
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError
import zio.{IO, ZIO}

trait PaymentRepository {
  def savePayment(payment: PaymentIn): IO[MusicServiceError, Payment]

  def findLastPaymentForArtist(artistId: ArtistId): IO[MusicServiceError, Option[Payment]]
}

object PaymentRepository {
  def savePayment(payment: PaymentIn): ZIO[PaymentRepository, MusicServiceError, Payment] =
    ZIO.serviceWithZIO[PaymentRepository](_.savePayment(payment))

  def findLastPaymentForArtist(
    artistId: ArtistId
  ): ZIO[PaymentRepository, MusicServiceError, Option[Payment]] =
    ZIO.serviceWithZIO[PaymentRepository](_.findLastPaymentForArtist(artistId))
}
