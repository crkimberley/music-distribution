package com.chriskimberley.persistence

import zio.{IO, ZIO}

import com.chriskimberley.domain.error.MusicServiceError
import com.chriskimberley.domain.{Artist, ArtistId, ArtistIn}

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
