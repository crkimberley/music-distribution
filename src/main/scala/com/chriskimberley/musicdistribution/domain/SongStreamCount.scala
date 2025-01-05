package com.chriskimberley.musicdistribution.domain

final case class SongStreamCount(
  song: Song,
  monetizedPlayCount: Long,
  nonMonetizedPlayCount: Long
)
