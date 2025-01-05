package com.chriskimberley.musicdistribution.domain

import java.time.OffsetDateTime

import scala.concurrent.duration.Duration

final case class SongStream(
  id: SongStreamId,
  songId: SongId,
  duration: Duration,
  streamedAt: OffsetDateTime
)
