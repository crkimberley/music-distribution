package com.chriskimberley.musicdistribution.domain

import zio.json.{DeriveJsonCodec, JsonCodec}

enum ReleaseStatus { case Approved, Pending, Rejected, Suspended }

object ReleaseStatus {
  given JsonCodec[ReleaseStatus] = DeriveJsonCodec.gen[ReleaseStatus]
}
