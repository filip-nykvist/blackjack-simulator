package com.filipnykvist

import scala.io.Source
import scala.util.Using

class DeckOfCardsSuite extends munit.FunSuite {
  private def loadFileContents(path: String) = {
    Using(Source.fromFile(path))(_.getLines().mkString)
  }

  test("Create a deck of cards") {
    val deck = DeckOfCards.create

    assert(deck.cards.size == 52, "Deck does not contain 52 cards")
    assert(deck.cards.toSet.size == 52, "Cards are not unique")
  }

  test("Correctly load deck from file") {
    val path    = "src/test/resources/completeDeck.txt"
    val deck    = DeckOfCards.fromFile(path)
    val content = loadFileContents(path)

    assert(deck.nonEmpty, "Could not parse file to deck")
    assert(deck.get.cards.size == 52, "Does not contain 52 cards")
    assert(deck.get.cards.toSet.size == 52, "Cards are not unique")
    assert(deck.get.cards.mkString(", ") == content.get)
  }

  test("Fail to make deck from file with incomplete set of cards") {
    val path    = "src/test/resources/incompleteDeck.txt"
    val deck    = DeckOfCards.fromFile(path)
    val content = loadFileContents(path)

    assert(content.isSuccess, "File is empty")
    assert(deck.isEmpty, "Created a deck")
  }

  test("Fail to make deck from file with duplicate cards") {
    val path    = "src/test/resources/duplicateDeck.txt"
    val deck    = DeckOfCards.fromFile(path)
    val content = loadFileContents(path)

    assert(content.isSuccess, "File is empty")
    assert(deck.isEmpty, "Created a deck")
  }
}
