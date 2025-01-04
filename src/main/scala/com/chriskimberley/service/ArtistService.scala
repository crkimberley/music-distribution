package com.chriskimberley.service

import zio.{IO, URLayer, ZIO, ZLayer}

import com.chriskimberley.domain.error.MusicServiceError
import com.chriskimberley.domain.{Artist, ArtistIn}
import com.chriskimberley.persistence.ArtistRepository

trait ArtistService {
  def addArtist(artist: ArtistIn): IO[MusicServiceError, Artist]
}

object ArtistService {
  def addArtist(artist: ArtistIn): ZIO[ArtistService, MusicServiceError, Artist] =
    ZIO.serviceWithZIO[ArtistService](_.addArtist(artist))
}

case class ArtistServiceImpl(artistRepository: ArtistRepository) extends ArtistService {
  override def addArtist(artist: ArtistIn): IO[MusicServiceError, Artist] =
    artistRepository.save(artist)
}

object ArtistServiceImpl {
  val layer: URLayer[ArtistRepository, ArtistServiceImpl] =
    ZLayer.fromFunction(ArtistServiceImpl(_))
}
