## Blackjack Simulator 

This is a project to simulate a blackjack game.

## Build and Run 

To build the jar file:
```bash
make build
```

To run, use either of the following:
```bash
java -jar BeatTheDealer.jar 
java -jar BeatTheDealer.jar path/to/file.txt
```

Or, simply run with sbt:
```bash 
sbt run
sbt "run path/to/file.txt"
```

## Note:
The game rules do not explicitly specify what happens if ONLY the dealer goes bust after the 'deal' phase, 
This may have an impact on the result of the game. 

To reproduce: 
1. Player is dealt: HK, H6
2. Dealer is dealt: SA, CA
3. Player draws: HQ

> #### Question:
> 
> does player win after stage 2? - OR does dealer win after stage 3
