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

import java.util.concurrent.ThreadLocalRandom;

/// Main class of minsweeper game
public class Minsweeper {
    
    private final BoardSize sizes;
    
    private final Runnable on_win;
    private final Runnable on_lose;
    
    private GameState gamestate = new GameState(GameStatus.NEVER, new Board(), -1);
    
    public Minsweeper(BoardSize sizes, Runnable on_win, Runnable on_lose) {
        this.sizes = sizes;
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
        var temp_board = new Board(sizes.width(), sizes.height());
        
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
        
        this.gamestate = new GameState(GameStatus.PLAYING, temp_board, sizes.mines());
        
        this.first = true;
        
        return gamestate.hideMines();
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
