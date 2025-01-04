package com.chriskimberley.domain

import java.time.LocalDate

import zio.json.{DeriveJsonCodec, JsonCodec}

final case class ReleaseDate(value: LocalDate, status: ReleaseStatus)

object ReleaseDate { given JsonCodec[ReleaseDate] = DeriveJsonCodec.gen[ReleaseDate] }
