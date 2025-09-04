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

import canaryprism.minsweeper.solver.Move;
import canaryprism.minsweeper.solver.Solver;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;

/// Main class of minsweeper game
public class Minsweeper {
    
    private final BoardSize sizes;
    
    private final Runnable on_win;
    private final Runnable on_lose;
    
    private GameState gamestate;
    
    private Solver solver;
    
    private Minsweeper(GameState state) {
        this.sizes = state.board().getSize();
        this.on_win = () -> {};
        this.on_lose = () -> {};
        this.gamestate = state;
        this.first = false;
    }
    
    public Minsweeper(BoardSize sizes, Runnable on_win, Runnable on_lose) {
        this.sizes = sizes;
        this.gamestate = new GameState(GameStatus.NEVER, new Board(sizes), -1);
        this.on_win = on_win;
        this.on_lose = on_lose;
    }
    
    public Minsweeper(BoardSize sizes) {
        this(sizes, () -> {}, () -> {});
    }
    
    public Minsweeper(ConventionalSize size, Runnable on_win, Runnable on_lose) {
        this(size.size, on_win, on_lose);
    }
    
    public Minsweeper(ConventionalSize size) {
        this(size.size);
    }
    
    public GameState start() {
        return start(null);
    }
    public GameState start(Solver solver) {
        this.solver = solver;
        
        this.gamestate = generateGame();
        
        this.first = true;
        
        return gamestate.hideMines();
    }
    
    private GameState generateGame() {
        var temp_board = new Board(sizes);
        var mines = 0;
        while (mines < sizes.mines()) {
            var x = ThreadLocalRandom.current().nextInt(sizes.width());
            var y = ThreadLocalRandom.current().nextInt(sizes.height());
            
            if (temp_board.get(x, y) instanceof Cell.Unknown) {
                temp_board.set(x, y, Cell.Mine.INSTANCE);
                mines++;
            }
        }
        
        generateNmbers(temp_board);
        
        return new GameState(GameStatus.PLAYING, temp_board, sizes.mines());
    }
    
    private GameState generateGame(int x, int y) {
        generation_attempt:
        while (true) {
            var board = new Board(sizes);
            var mines = 0;
            var points = new HashSet<Move.Point>();
            while (mines < sizes.mines()) {
                var x2 = ThreadLocalRandom.current().nextInt(sizes.width());
                var y2 = ThreadLocalRandom.current().nextInt(sizes.height());
                if (x2 == x || y2 == y)
                    continue;
                
                if (!points.add(new Move.Point(x2, y2))) {
                    if (points.size() == sizes.mines())
                        continue generation_attempt;
                    continue;
                }
                var board_copy = board.clone();
                if (board_copy.get(x2, y2) instanceof Cell.Unknown) {
                    board_copy.set(x2, y2, Cell.Mine.INSTANCE);
                    generateNmbers(board_copy);
                    if (this.solver != null) {
                        var state = new GameState(GameStatus.PLAYING, board_copy, mines);
                        var game = new Minsweeper(state);
                        state = game.leftClick(x, y);
                        if (solver.solve(game, state) == Solver.Result.WON) {
                            board = board_copy;
                            points.clear();
                            mines++;
                        }
                    } else {
                        board = board_copy;
                        points.clear();
                        mines++;
                    }
                }
            }
            
            return new GameState(GameStatus.PLAYING, board, sizes.mines());
        }
    }
    
    private void generateNmbers(Board board) {
        for (int y = 0; y < sizes.height(); y++)
            for (int x = 0; x < sizes.width(); x++)
                if (board.get(x, y) instanceof Cell.Unknown)
                    board.set(x, y, new Cell.Unknown(0));
        
        for (int y = 0; y < sizes.height(); y++)
            for (int x = 0; x < sizes.width(); x++)
                if (board.get(x, y) instanceof Cell.Mine)
                    for (int y2 = Math.max(0, y - 1); y2 <= Math.min(sizes.height() - 1, y + 1); y2++)
                        for (int x2 = Math.max(0, x - 1); x2 <= Math.min(sizes.width() - 1, x + 1); x2++)
                            if (board.get(x2, y2) instanceof Cell.Unknown(var number))
                                board.set(x2, y2, new Cell.Unknown(number + 1));
    }
    
    
    private volatile boolean first;
    
    private void revealEmpty(int x, int y, Board board) {
        if (!(board.get(x, y) instanceof Cell.Unknown(var n) && n == 0)) return;
        
        board.set(x, y, new Cell.Revealed(0));
        
        for (int y2 = Math.max(0, y - 1); y2 <= Math.min(sizes.height() - 1, y + 1); y2++)
            for (int x2 = Math.max(0, x - 1); x2 <= Math.min(sizes.width() - 1, x + 1); x2++)
                if (board.get(x2, y2) instanceof Cell.Unknown(var number))
                    if (number == 0) {
                        revealEmpty(x2, y2, board);
                    } else {
                        board.set(x2, y2, new Cell.Revealed(number));
                    }
    }
    
    private boolean internalReveal(int x, int y, Board board) {
        return switch (board.get(x, y)) {
            case Cell.Unknown(var number) -> {
                if (number == 0) {
                    revealEmpty(x, y, board);
                } else {
                    board.set(x, y, new Cell.Revealed(number));
                }
                first = false;
                
                yield true;
            }
            case Cell.Mine ignored -> {
                // if it's the first move, move the mine to a random location
                if (first) {
                    // this is a bit inefficient, but it's not like it's going to be called often
                    while (true) {
                        var x2 = ThreadLocalRandom.current().nextInt(sizes.width());
                        var y2 = ThreadLocalRandom.current().nextInt(sizes.height());
                        
                        if (board.get(x2, y2) instanceof Cell.Unknown) {
                            board.set(x2, y2, Cell.Mine.INSTANCE);
                            break;
                        }
                    }
                    board.set(x, y, new Cell.Unknown(0));
                    generateNmbers(board);
                    first = false;
                    yield internalReveal(x, y, board);
                } else {
                    board.set(x, y, Cell.ExplodedMine.INSTANCE);
                    yield false;
                }
            }
            
            default -> true;
        };
    }
    
    public GameState reveal(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return gamestate;
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return gamestate.hideMines();
        if (this.solver != null && this.first) {
            this.first = false;
//            this.gamestate = generateGame(x, y);
            var solver = this.solver;
            var future = new CompletableFuture<GameState>();
            final var batch_amount = 30;
            var ecs = new ExecutorCompletionService<GameState>(ForkJoinPool.commonPool());
            solver_loop:
            while (true) {
                for (int i = 0; i < batch_amount; i++) {
                    ecs.submit(() -> {
                        var original_state = generateGame();
                        var game = new Minsweeper(original_state.clone());
                        var state = game.reveal(x, y);
                        var result = solver.solve(game, state);

                        if (result == Solver.Result.WON) {
                            return original_state;
                        }
                        throw new RuntimeException();
                    });
                }

                for (int i = 0; i < batch_amount; i++) {
                    try {
                        this.gamestate = ecs.take().resultNow();
                        break solver_loop;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalStateException e) {}
                }
            }
        }
        
        var board = gamestate.board().clone();
        
        var success = internalReveal(x, y, board);
        
        this.gamestate = gamestate.withBoard(board);
        
        if (!success) {
            this.gamestate = gamestate.withStatus(GameStatus.LOST);
            
            on_lose.run();
            
            return gamestate;
        }
        
        if (gamestate.board().hasWon()) {
            this.gamestate = gamestate.withStatus(GameStatus.WON);
            
            on_win.run();
            
            return gamestate;
        }
        
        
        return gamestate.hideMines();
    }
    
    public GameState clearAround(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return gamestate;
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return gamestate.hideMines();
        
        var board = gamestate.board().clone();
        
        if (!(board.get(x, y) instanceof Cell.Revealed(var number))) return gamestate.hideMines();
        
        var marked_mines = 0;
        
        for (int y2 = Math.max(0, y - 1); y2 <= Math.min(sizes.height() - 1, y + 1); y2++)
            for (int x2 = Math.max(0, x - 1); x2 <= Math.min(sizes.width() - 1, x + 1); x2++)
                if (board.get(x2, y2) instanceof Cell.MarkedMine || board.get(x2, y2) instanceof Cell.FalseMine)
                    marked_mines += 1;
            
        var success = true;
        
        if (marked_mines == number)
            for (int y2 = Math.max(0, y - 1); y2 <= Math.min(sizes.height() - 1, y + 1); y2++)
                for (int x2 = Math.max(0, x - 1); x2 <= Math.min(sizes.width() - 1, x + 1); x2++)
                    success = internalReveal(x2, y2, board) && success;
        
        this.gamestate = gamestate.withBoard(board);
        
        if (!success) {
            this.gamestate = gamestate.withStatus(GameStatus.LOST);
            
            on_lose.run();
            
            return gamestate;
        }
        
        if (gamestate.board().hasWon()) {
            this.gamestate = gamestate.withStatus(GameStatus.WON);
            
            on_win.run();
            
            return gamestate;
        }
        
        
        return gamestate.hideMines();
    }
    
    public GameState toggleFlag(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return gamestate;
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return gamestate.hideMines();
        
        var board = gamestate.board().clone();
        var remaining_mines = gamestate.remainingMines();
        
        remaining_mines += switch (board.get(x, y)) {
            case Cell.FalseMine ignored -> 1;
            case Cell.MarkedMine ignored -> 1;
            case Cell.Unknown ignored -> -1;
            case Cell.Mine ignored -> -1;
            default -> 0;
        };
        
        var new_value = switch (board.get(x, y)) {
            case Cell.Mine ignored -> Cell.MarkedMine.INSTANCE;
            case Cell.FalseMine(var i) -> new Cell.Unknown(i);
            case Cell.MarkedMine ignored -> Cell.Mine.INSTANCE;
            case Cell.Unknown(var i) -> new Cell.FalseMine(i);
            default -> board.get(x, y);
        };
        
        board.set(x, y, new_value);
        
        this.gamestate = gamestate.withBoard(board).withRemainingMines(remaining_mines);
        
        return gamestate.hideMines();
    }
    
    public GameState leftClick(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return gamestate;
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return gamestate.hideMines();
        
        return switch (gamestate.board().get(x, y)) {
            case Cell.Revealed ignored -> clearAround(x, y);
            case Cell.FalseMine ignored -> gamestate.hideMines();
            case Cell.MarkedMine ignored -> gamestate.hideMines();
            default -> reveal(x, y);
        };
    }
    
    public GameState rightClick(int x, int y) {
        return toggleFlag(x, y);
    }
}
