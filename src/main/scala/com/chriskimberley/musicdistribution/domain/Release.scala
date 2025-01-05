package com.chriskimberley.musicdistribution.domain

final case class Release(
  id: ReleaseId,
  title: String,
  artist: Artist,
  releaseDate: ReleaseDate,
  songs: Seq[Song]
)
