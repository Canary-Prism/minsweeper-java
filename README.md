# minsweeper-java

Minsweeper but like Java and stuffs

it's on maven central i think, can't be bothered telling u where

okay i ended up putting much more effort into this than i expected to so

## Solvers

the main thing about this minesweeper implementation is that it has pluggable solvers 
that can also be used in conjuction with `MinsweeperGame` to generate boards that are guaranteed solvable
by the given solver

this is done by generating random games and then sending them to be solved by the provided solver,
if the solver succeeds then the board is presented to the player, otherwise, another board is generated

**NOTE: this generate loop is single threaded, but *is* interruptible. if you pick a board size with a mine density astronomically unlikely to end up randomly generating as a solvable board, the thread will block forever until you interrupt the thread**

Solver is a SPI that has methods for solving a particular state or solving an entire game,
the library comes with a couple default solvers that have different skill levels
in order to generate boards with specific a specific "difficulty"

Solvers with "Only" in their names will not accept games that are solvable by the previous level of solver