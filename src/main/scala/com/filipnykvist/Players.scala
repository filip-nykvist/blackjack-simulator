package com.filipnykvist

object Players {
  import Status._

  trait PlayerBase {
    def name: String
    def hand: List[Card]
    lazy val score: Int = hand.map(_.rank.value).sum

    def status(compScore: Int = 16): Status = score match {
      case v if v <= compScore => Draw
      case v if v > 21         => Bust
      case v                   => Stop(v)
    }
  }

  object Player {
    def apply(): Player                 = Player("Sam", List.empty)
    def apply(hand: List[Card]): Player = Player("Sam", hand)
    def apply(name: String): Player     = Player(name, List.empty)
  }
  case class Player(name: String, hand: List[Card]) extends PlayerBase {
    def deal(card: Card): Player = copy(hand = card :: hand)
  }

  object Dealer {
    def apply(): Dealer = Dealer(List.empty)
  }
  case class Dealer(hand: List[Card]) extends PlayerBase {
    val name                     = "Dealer"
    def deal(card: Card): Dealer = copy(hand = card :: hand)
  }

  sealed trait Status
  object Status {
    case object Bust            extends Status
    case object Draw            extends Status
    case class Stop(value: Int) extends Status
  }
}
