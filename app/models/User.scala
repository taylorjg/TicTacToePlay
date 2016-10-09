package models

case class User(username: String, passwordHash: String) extends stamina.Persistable
