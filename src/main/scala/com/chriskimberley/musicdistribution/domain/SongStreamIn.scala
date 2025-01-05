package com.chriskimberley.musicdistribution.domain

import java.time.OffsetDateTime

import scala.concurrent.duration.Duration

final case class SongStreamIn(
  songId: SongId,
  duration: Duration,
  streamedAt: OffsetDateTime
)
