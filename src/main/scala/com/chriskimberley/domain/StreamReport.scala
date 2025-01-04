package com.chriskimberley.domain

import java.time.OffsetDateTime

final case class StreamReport(
  artist: Artist,
  startTime: OffsetDateTime,
  endTime: OffsetDateTime,
  streams: Seq[SongStreamCount]
)
