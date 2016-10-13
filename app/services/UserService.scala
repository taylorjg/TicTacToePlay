package services

import models.User

import scala.concurrent.Future

trait UserService {
  def lookupUsername(username: String): Future[Option[User]]
  def login(username: String, password: String): Future[Option[User]]
  def registerUser(username: String, password: String): Future[Option[User]]
}
