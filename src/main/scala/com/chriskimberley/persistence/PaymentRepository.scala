package com.chriskimberley.persistence

import zio.{IO, ZIO}

import com.chriskimberley.domain.error.MusicServiceError
import com.chriskimberley.domain.{ArtistId, Payment, PaymentIn}

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
