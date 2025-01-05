package com.chriskimberley.musicdistribution.domain

final case class ReleaseIn(
  title: String,
  artist: Artist,
  releaseDate: ReleaseDate,
  songs: Seq[Song]
)
