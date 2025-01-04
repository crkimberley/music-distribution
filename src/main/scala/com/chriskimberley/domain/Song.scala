package com.chriskimberley.domain

import zio.json.{DeriveJsonCodec, JsonCodec}

final case class Song(id: SongId, title: String, releaseDate: Option[ReleaseDate] = None)

object Song { given JsonCodec[Song] = DeriveJsonCodec.gen[Song] }
