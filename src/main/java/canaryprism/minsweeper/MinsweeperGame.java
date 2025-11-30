/*
 *    Copyright 2024 Canary Prism <canaryprsn@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package canaryprism.minsweeper;

import canaryprism.minsweeper.solver.Solver;

/// # Main class of minsweeper game
///
/// This is the primary implementation of [Minsweeper] and it has the most complete features
///
/// ## How use and whatever
///
/// You start by creating a [BoardSize]
/// ```
/// BoardSize size = new BoardSize(9, 9, 10);
/// ```
/// or use a predefined size in [ConventionalSize]
/// ```
/// BoardSize size = ConventionalSize.BEGINNER.size
/// ```
///
/// Then you can create a MinsweeperGame instance
/// ```
/// MinsweeperGame game = new MinsweeperGame(size);
/// ```
///
/// The created MinsweeperGame has not started yet, its [#gamestate]'s [GameState#status] is [GameStatus#NEVER].
/// Any move attempted like [#reveal(int, int)] [#clearAround(int, int)] or [#setFlagged(int, int, boolean)]
/// will simply not do anything
///
/// To start the game just call [#start()] or [#start(Solver)]
/// ```
/// game.start(Solver.getDefault()); // starts the game with the default solver
/// ```
/// oh right speaking of
///
/// ### [Solvers][Solver]
///
/// Solvers can be used to start a game by using [#start(Solver)] instead of [#start()].
/// This guarantees that the game you play is solvable by the provided solver.
/// This is done by repeatedly generating new random games until one is found that is solvable,
/// when the player first [#reveal(int, int)]s a cell.
///
/// Since this means the generation of a game is postponed until the first [reveal][#reveal(int, int)],
/// [#setFlagged(int, int, boolean)] doesn't do anything before a reveal as the board technically isn't even made yet
///
/// Also, since the first reveal is only able to processed once a solvable board is randomly found,
/// the [#reveal(int, int)] and [#leftClick(int, int)] methods may block for an extended period of time
/// as it keeps generating boards until one is solvable by the solver.
/// To make this easier to work with, the board generation process may be [interrupted][Thread#interrupt()],
/// where a [GenerationInterruptedException] is thrown
///
/// ## Weirdness
///
/// Since this is the main class i figured i'd just throw in all of the more different bits of this minesweeper
/// implementation compared to other common implementations so nobody gets confused or whatever
///
/// - after winning a game, since the true [#gamestate] is returned directly,
///   MinsweeperGame doesn't pretend leftover mines are flagged when they weren't
/// - continuing from the last weirdness, this means the [GameState#remainingMines]
///   is also not set to 0 if not all mines were flagged
/// - the first move is *still* not guaranteed safe or a zero, to achieve this the Solver api may be abused
///   see [canaryprism.minsweeper.solver.impl.start]
///
public final class MinsweeperGame extends AbstractRandomMinsweeper {
    
    private Solver solver;
    
    /// Constructs a new MinsweeperGame with given size and Runnables invoked for winning and losing
    ///
    /// @param sizes the size of the board
    /// @param on_win Runnable to be invoked on win
    /// @param on_lose Runnable to be invoked on lose
    public MinsweeperGame(BoardSize sizes, Runnable on_win, Runnable on_lose) {
        super(sizes, on_win, on_lose);
    }
    
    /// Constructs a new MinsweeperGame with given size
    ///
    /// @param sizes the size of the board
    /// @see #MinsweeperGame(BoardSize, Runnable, Runnable)
    public MinsweeperGame(BoardSize sizes) {
        this(sizes, () -> {}, () -> {});
    }
    
    /// Constructs a new MinsweeperGame with given size and Runnables invoked for winning and losing
    ///
    /// The size of the board will be the [ConventionalSize#size] of the passed `size`
    ///
    /// @param size the size of the board
    /// @param on_win Runnable to be invoked on win
    /// @param on_lose Runnable to be invoked on lose
    /// @see #MinsweeperGame(BoardSize, Runnable, Runnable)
    public MinsweeperGame(ConventionalSize size, Runnable on_win, Runnable on_lose) {
        this(size.size, on_win, on_lose);
    }
    
    /// Constructs a new MinsweeperGame with given size
    ///
    /// The size of the board will be the [ConventionalSize#size] of the passed `size`
    ///
    /// @param size the size of the board
    /// @see #MinsweeperGame(BoardSize)
    public MinsweeperGame(ConventionalSize size) {
        this(size.size);
    }
    
    @Override
    public GameState start() {
        return start(null);
    }
    
    /// Start or restart a Minsweeper game with a given [Solver]
    ///
    /// The game will be guaranteed solvable by the passed `solver`
    ///
    ///
    public GameState start(Solver solver) {
        this.solver = solver;
        
        this.gamestate = new GameState(GameStatus.PLAYING, new Board(sizes), sizes.mines());
        
        this.first = true;
        
        return getGameState();
    }
    
    private volatile boolean first;
    
    /// {@inheritDoc}
    ///
    /// @throws GenerationInterruptedException if the thread is interrupted
    ///                                        while the board generation process
    ///                                        is ongoing
    @Override
    public GameState reveal(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return getGameState();
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return getGameState();
        if (this.first) {
            this.first = false;
            
            if (this.solver != null) {
                this.first = false;
//            this.gamestate = generateGame(x, y);
                var solver = this.solver;
//            var future = new CompletableFuture<GameState>();
                final var thread_batch = 100;
                final var loop_batch = 1;
//            try (var pool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism())) {
//                solver_loop:
//                while (true) {
//
//                    var futures = new ArrayList<Future<GameState>>(thread_batch);
//                    try {
//
//                        for (int i = 0; i < thread_batch; i++) {
////                    throw new RuntimeException();
//                            futures.add(pool.submit(ForkJoinTask.adapt(() -> {
//                                for (int j = 0; j < loop_batch; j++) {
//                                    var original_state = generateGame();
//                                    var game = new SetMinsweeperGame(original_state.clone());
//                                    var state = game.reveal(x, y);
//                                    var result = solver.solve(game, state);
//
//                                    if (result == Solver.Result.WON) {
////                                        System.out.println("solution");
//                                        return original_state;
////                                        this.gamestate = original_state;
////                                    break solver_loop;
//                                    }
//                                }
//                                return null;
//                            })));
//                        }
//
//                        for (var future : futures) {
//                            try {
//                                var result = future.get();
//                                if (result != null) {
//                                    System.out.println("solution");
//                                    this.gamestate = result;
//                                    break solver_loop;
//                                }
//                            } catch (InterruptedException | ExecutionException | IllegalStateException ignored) {}
//                        }
//                    } finally {
//                        futures.forEach(future -> future.cancel(true));
//                    }
//
//                }
//            }
                
                while (true) {
                    if (Thread.interrupted())
                        throw new GenerationInterruptedException(new InterruptedException());
                    var original_state = generateGame();
                    var game = new SetMinsweeperGame(original_state.clone());
                    game.reveal(x, y);
                    var result = solver.solve(game);
                    
                    if (result == Solver.Result.WON) {
                        this.gamestate = original_state;
//                                        this.gamestate = original_state;
                        break;
                    }
                }
            } else {
                this.gamestate = generateGame();
            }
        }
        
        return super.reveal(x, y);
    }
    
    /// {@inheritDoc}
    ///
    /// Does nothing if there hasn't been a [#reveal(int, int)] yet this game
    @Override
    public GameState setFlagged(int x, int y, boolean flagged) {
        if (first)
            return getGameState();
        return super.setFlagged(x, y, flagged);
    }
}
