package com.chriskimberley.musicdistribution.persistence

import com.chriskimberley.musicdistribution.domain.SongIn
import com.chriskimberley.musicdistribution.service.{SongService, SongServiceImpl}
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object SongRepositorySpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("SongRepositorySpec")(test("search") {
      for {
        song1 <- SongRepository.save(SongIn("rifle1"))
        song2 <- SongRepository.save(SongIn("title2"))
        foundSongs <- SongService.searchReleasedSongs("rifle")
      } yield assertTrue(foundSongs.map(_.title) == Seq(song1.title, song2.title))
    }).provide(SongServiceImpl.layer, MockSongRepository.layer)
}
