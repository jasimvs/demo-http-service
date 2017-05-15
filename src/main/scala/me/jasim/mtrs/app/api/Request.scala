package me.jasim.mtrs.app.api

/**
  * Created by jsulaiman on 5/15/17.
  */
case class RegisterShowRequest(imdbId: String, availableSeats: Int, screenId: String)

case class ReserveTicketRequest(imdbId: String, screenId: String)
