package com.chriskimberley.domain

import java.time.OffsetDateTime

final case class PaymentIn(
  artistId: ArtistId,
  amount: BigDecimal,
  startTime: OffsetDateTime,
  endTime: OffsetDateTime,
  calculatedAt: OffsetDateTime = OffsetDateTime.now()
)
