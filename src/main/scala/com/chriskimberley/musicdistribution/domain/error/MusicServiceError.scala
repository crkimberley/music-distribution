package com.chriskimberley.musicdistribution.domain.error

// Just to give an idea of some errors the service might need to handle
enum MusicServiceError(val message: String, val cause: Option[Throwable] = None) extends Throwable {
  case ValidationError(msg: String) extends MusicServiceError(msg)
  case NotFoundError(msg: String) extends MusicServiceError(msg)
  case DatabaseError(msg: String, dbCause: Option[Throwable]) extends MusicServiceError(msg, dbCause)
  case ExternalServiceError(msg: String, extCause: Option[Throwable]) extends MusicServiceError(msg, extCause)
  case UnknownError(msg: String, unknownCause: Option[Throwable]) extends MusicServiceError(msg, unknownCause)

  override def getCause: Throwable = cause.orNull
}
