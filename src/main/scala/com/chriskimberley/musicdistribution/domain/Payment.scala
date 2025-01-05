package com.chriskimberley.musicdistribution.domain

import java.time.OffsetDateTime

final case class Payment(
  id: PaymentId,
  artistId: ArtistId,
  amount: BigDecimal,
  startTime: OffsetDateTime,
  endTime: OffsetDateTime,
  calculatedAt: OffsetDateTime
)
