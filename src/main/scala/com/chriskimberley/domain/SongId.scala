package com.chriskimberley.domain

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.util.UUID

final case class SongId(value: UUID)

object SongId { given JsonCodec[SongId] = DeriveJsonCodec.gen[SongId] }
