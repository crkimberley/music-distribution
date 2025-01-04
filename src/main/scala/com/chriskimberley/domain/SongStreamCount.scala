package com.chriskimberley.domain

final case class SongStreamCount(
  song: Song,
  monetizedPlayCount: Long,
  nonMonetizedPlayCount: Long
)
