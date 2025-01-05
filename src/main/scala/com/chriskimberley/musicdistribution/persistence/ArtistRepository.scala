package com.chriskimberley.musicdistribution.persistence

import com.chriskimberley.musicdistribution.domain.{Artist, ArtistId, ArtistIn}
import com.chriskimberley.musicdistribution.domain.error.MusicServiceError
import zio.{IO, ZIO}

trait ArtistRepository {
  def save(artist: ArtistIn): IO[MusicServiceError, Artist]

  def findById(artistId: ArtistId): IO[MusicServiceError, Option[Artist]]
}

object ArtistRepository {
  def save(artist: ArtistIn): ZIO[ArtistRepository, MusicServiceError, Artist] =
    ZIO.serviceWithZIO[ArtistRepository](_.save(artist))

  def findById(artistId: ArtistId): ZIO[ArtistRepository, MusicServiceError, Option[Artist]] =
    ZIO.serviceWithZIO[ArtistRepository](_.findById(artistId))
}
