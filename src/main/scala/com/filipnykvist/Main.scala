package com.filipnykvist

import scala.util.Random
import scala.util.chaining.scalaUtilChainingOps

@main def main(args: String*): Unit = {
  lazy val rng = new Random(133333337L)

  val result = args.headOption
    .flatMap(DeckOfCards.fromFile)
    .getOrElse(DeckOfCards.create.shuffle(rng))
    .pipe(Game.deal)
    .pipe(Game.play)

  println(result.report)
}
