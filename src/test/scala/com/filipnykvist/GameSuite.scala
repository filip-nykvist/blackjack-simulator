package com.filipnykvist

import com.filipnykvist.Game._
import com.filipnykvist.Players.Status._
import com.filipnykvist.Players._

class GameSuite extends munit.FunSuite {
  val sortedDeck = DeckOfCards.create

  val blackJack   = List(Card(Suite.Hearts, Rank.Ace), Card(Suite.Hearts, Rank.King))
  val bust        = List(Card(Suite.Hearts, Rank.Ace), Card(Suite.Hearts, Rank.Seven), Card(Suite.Hearts, Rank.Eight))
  val handScore19 = List(Card(Suite.Hearts, Rank.Ace), Card(Suite.Hearts, Rank.Eight))
  val handScore17 = List(Card(Suite.Hearts, Rank.Ace), Card(Suite.Hearts, Rank.Six))
  val handScore15 = List(Card(Suite.Hearts, Rank.Ace), Card(Suite.Hearts, Rank.Four))
  val handScore10 = List(Card(Suite.Hearts, Rank.Eight), Card(Suite.Hearts, Rank.Two))

  test("Correctly deal cards to player and dealer") {
    val initState: GameState = Game.deal(sortedDeck).evalLeft

    assert(initState.player.hand == List(Card(Suite.Hearts, Rank.Four), Card(Suite.Hearts, Rank.Two)), "Player did not get correct cards")
    assert(initState.dealer.hand == List(Card(Suite.Hearts, Rank.Five), Card(Suite.Hearts, Rank.Three)), "Dealer did not get correct cards")
  }

  test("Game should end after 'deal' with player as winner after getting blackjack") {
    val deck = DeckOfCards(
      List(
        Card(Suite.Hearts, Rank.Ace),
        Card(Suite.Spades, Rank.Ace),
        Card(Suite.Hearts, Rank.King),
        Card(Suite.Spades, Rank.King)
      )
    )

    val result: GameOver = Game.deal(deck).evalRight

    assert(result.winner.name == "Sam", "Sam should win")
    assert(result.player.status() == BlackJack, "Sam should win with blackjack")
    assert(result.dealer.status() == BlackJack, "Sam should win with blackjack even though dealer has blackjack")
  }

  test("Game should end after 'deal' with dealer as winner after getting blackjack") {
    val deck = DeckOfCards(
      List(
        Card(Suite.Hearts, Rank.Queen),
        Card(Suite.Spades, Rank.Ace),
        Card(Suite.Hearts, Rank.King),
        Card(Suite.Spades, Rank.King)
      )
    )

    val result: GameOver = Game.deal(deck).evalRight

    assert(result.winner.name == "Dealer", "Dealer should win")
    assert(result.dealer.status() == BlackJack, "Dealer should win with blackjack")
  }

  test("Game should end after 'deal' with dealer as winner if both dealer and player are bust") {
    val deck = DeckOfCards(
      List(
        Card(Suite.Hearts, Rank.Ace),
        Card(Suite.Spades, Rank.Ace),
        Card(Suite.Diamonds, Rank.Ace),
        Card(Suite.Clubs, Rank.Ace)
      )
    )

    val result: GameOver = Game.deal(deck).evalRight

    assert(result.winner.name == "Dealer", "Dealer should win")
    assert(result.player.status() == Bust, "Dealer should win with when player bust")
    assert(result.dealer.status() == Bust, "Dealer should win with when bust if player is bust")
  }

  test("Player should draw cards until player has at least score 17") {
    val initState = Game.deal(sortedDeck)
    val result    = play(initState)

    assert(result.player.score >= 17, "Player did not draw until score >= 17")
  }

  test("Dealer should draw cards until dealer has higher score than player") {
    val deck   = DeckOfCards(List(Card(Suite.Spades, Rank.Seven), Card(Suite.Hearts, Rank.Three)))
    val state  = PlayersTurn(deck, Player(handScore19), Dealer(handScore10)).lift
    val result = play(state)

    assert(result.dealer.score > result.player.score, "Dealer should stop when has higher score than player")
  }

  test("End game with player as winner if player draws blackjack") {
    val deck   = DeckOfCards(List(Card(Suite.Spades, Rank.Ace)))
    val state  = PlayersTurn(deck, Player("Filip", handScore10), Dealer(handScore10)).lift
    val result = play(state)

    assert(result.winner.name == "Filip", "Dealer should not win when player gets blackjack")
    assert(result.player.status() == BlackJack, "Player did not get blackjack")
  }

  test("End game with dealer as winner if dealer draws blackjack") {
    val deck   = DeckOfCards(List(Card(Suite.Spades, Rank.Ace)))
    val state  = PlayersTurn(deck, Player(handScore17), Dealer(handScore10)).lift
    val result = play(state)

    assert(result.winner.name == "Dealer", "Player should not win when dealer gets blackjack")
    assert(result.dealer.status() == BlackJack, "Dealer did not get blackjack")
  }

  test("End game with dealer as winner if player goes bust") {
    val deck   = DeckOfCards(List(Card(Suite.Spades, Rank.Ace)))
    val state  = PlayersTurn(deck, Player(handScore15), Dealer(handScore10)).lift
    val result = play(state)

    assert(result.winner.name == "Dealer", "Dealer should win when player goes bust")
    assert(result.player.status() == Bust, "Player did not go bust")
  }

  test("End game with player as winner if dealer goes bust") {
    val deck   = DeckOfCards(List(Card(Suite.Spades, Rank.Ace)))
    val state  = PlayersTurn(deck, Player("Filip", handScore17), Dealer(handScore15)).lift
    val result = play(state)

    assert(result.winner.name == "Filip", "Player should win when dealer goes bust")
    assert(result.dealer.status() == Bust, "Dealer did not go bust")
  }

  extension (state: Game) {
    def evalLeft: GameState = state match {
      case Left(s)  => s
      case Right(_) => fail("Could not eval left")
    }
    def evalRight: GameOver = state match {
      case Right(s) => s
      case Left(_)  => fail("Could not eval left")
    }
  }
}
