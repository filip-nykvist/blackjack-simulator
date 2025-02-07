package com.filipnykvist

import scala.io.Source
import scala.util.{Random, Using}

case class DeckOfCards(cards: List[Card]) {
  def shuffle(rnd: Random): DeckOfCards = DeckOfCards(rnd.shuffle(cards))
  def draw: (Card, DeckOfCards)         = cards.head -> DeckOfCards(cards.tail)
}

object DeckOfCards {

  def create: DeckOfCards = {
    val cards = for {
      suite <- Suite.All
      rank  <- Rank.All
    } yield Card(suite, rank)

    DeckOfCards(cards)
  }

  def fromFile(path: String): Option[DeckOfCards] = {
    for {
      input <- Using(Source.fromFile(path))(_.getLines.mkString).toOption
      cs     = input.split(",").map(_.trim).toList
      cards <- parseToCards(cs)
      _     <- if (cards.toSet.size == 52) Some(()) else None
    } yield DeckOfCards(cards.reverse)
  }

  private def parseToCards(ss: List[String]): Option[List[Card]] = {
    ss
      .map(parseToCard)
      .foldLeft(Some(List.empty): Option[List[Card]]) {
        case (None, _)                 => None
        case (_, None)                 => None
        case (Some(cards), Some(card)) => Some(card :: cards)
      }
  }

  private def parseToCard(s: String): Option[Card] = {
    for {
      suite <- s.headOption.flatMap(Suite.fromChar)
      rank  <- Rank.fromString(s.tail)
    } yield Card(suite, rank)
  }
}

case class Card(suite: Suite, rank: Rank) {
  override def toString: String = s"${suite.symbol}${rank.symbol}"
}

sealed trait Suite { def symbol: Char }

object Suite {
  case object Hearts   extends Suite { def symbol = 'H' }
  case object Spades   extends Suite { def symbol = 'S' }
  case object Diamonds extends Suite { def symbol = 'D' }
  case object Clubs    extends Suite { def symbol = 'C' }
  val All: List[Suite] = List(Hearts, Spades, Diamonds, Clubs)

  def fromChar(c: Char): Option[Suite] = All.find(_.symbol == c)
}

sealed trait Rank {
  def symbol: String
  def value: Int
}

object Rank {
  case object Two   extends Rank { val symbol = "2"; val value = 2   }
  case object Three extends Rank { val symbol = "3"; val value = 3   }
  case object Four  extends Rank { val symbol = "4"; val value = 4   }
  case object Five  extends Rank { val symbol = "5"; val value = 5   }
  case object Six   extends Rank { val symbol = "6"; val value = 6   }
  case object Seven extends Rank { val symbol = "7"; val value = 7   }
  case object Eight extends Rank { val symbol = "8"; val value = 8   }
  case object Nine  extends Rank { val symbol = "9"; val value = 9   }
  case object Ten   extends Rank { val symbol = "10"; val value = 10 }
  case object Jack  extends Rank { val symbol = "J"; val value = 10  }
  case object Queen extends Rank { val symbol = "Q"; val value = 10  }
  case object King  extends Rank { val symbol = "K"; val value = 10  }
  case object Ace   extends Rank { val symbol = "A"; val value = 11  }
  val All: List[Rank] = List(Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace)

  def fromString(s: String): Option[Rank] = All.find(_.symbol == s)
}
