package com.filipnykvist

import scala.annotation.tailrec
import scala.util.chaining.scalaUtilChainingOps

import com.filipnykvist.Players.Status.{BlackJack, Bust, Draw, Stop}
import com.filipnykvist.Players.{Dealer, Player, PlayerBase}

object Game {

  type Game = Either[GameState, GameOver]
  def deal(deck: DeckOfCards): Game = {
    (1 to 4)
      .foldLeft((deck, Player(), Dealer())) { case ((deck, sam, dealer), i) =>
        val (c, d) = deck.draw
        if (i % 2 == 0) (d, sam, dealer.deal(c))
        else (d, sam.deal(c), dealer)
      }
      .pipe { case (deck, player, dealer) =>
        (player.status(), dealer.status()) match {
          case (BlackJack, _) => Right(GameOver(player, player, dealer))
          case (_, BlackJack) => Right(GameOver(dealer, player, dealer))
          case (Bust, _)      => Right(GameOver(dealer, player, dealer))
          case (_, Bust)      => Right(GameOver(player, player, dealer))
          case _              => Left(PlayersTurn(deck, player, dealer))
        }
      }
  }

  @tailrec
  def play(game: Game): GameOver = game match {
    case Left(state)     => play(state.playRound)
    case Right(gameOver) => gameOver
  }

  case class GameOver(winner: PlayerBase, player: Player, dealer: Dealer) {
    def lift: Game = Right(this)

    def report: String = s"""
      |${winner.name}
      |${player.name}: ${player.hand.mkString(", ")}
      |Dealer: ${dealer.hand.mkString(", ")}
      |""".stripMargin
  }

  sealed trait GameState {
    def deck: DeckOfCards
    def player: Player
    def dealer: Dealer
    def playRound: Game

    def lift: Game = Left(this)
  }

  case class PlayersTurn(deck: DeckOfCards, player: Player, dealer: Dealer) extends GameState {
    def playRound: Game = player.status() match {
      case BlackJack => GameOver(player, player, dealer).lift
      case Bust      => GameOver(dealer, player, dealer).lift
      case Stop(_)   => DealersTurn(deck, player, dealer).lift
      case Draw      => deck.draw.pipe { case (card, deck) => PlayersTurn(deck, player.deal(card), dealer) }.lift
    }
  }

  case class DealersTurn(deck: DeckOfCards, player: Player, dealer: Dealer) extends GameState {
    def playRound: Game = dealer.status(player.score) match {
      case BlackJack   => GameOver(dealer, player, dealer).lift
      case Bust        => GameOver(player, player, dealer).lift
      case Draw        => deck.draw.pipe { case (card, deck) => DealersTurn(deck, player, dealer.deal(card)) }.lift
      case Stop(score) =>
        if (player.score > score) GameOver(player, player, dealer).lift
        else GameOver(dealer, player, dealer).lift
    }
  }
}
