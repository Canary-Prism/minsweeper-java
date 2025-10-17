/*
 *    Copyright 2025 Canary Prism <canaryprsn@gmail.com>
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

public abstract class AbstractMinsweeper implements Minsweeper {
    
    protected final BoardSize sizes;
    
    protected final Runnable on_win;
    protected final Runnable on_lose;
    
    protected GameState gamestate;
    
    
    public AbstractMinsweeper(BoardSize sizes, Runnable on_win, Runnable on_lose) {
        this.sizes = sizes;
        this.gamestate = new GameState(GameStatus.NEVER, new Board(sizes), 0);
        this.on_win = on_win;
        this.on_lose = on_lose;
    }
    
    @Override
    public GameState getGameState() {
        return gamestate;
    }
    
    
    private void revealEmpty(int x, int y, Board board) {
        if (!(board.get(x, y) instanceof Cell(var t, var s)
                && t instanceof CellType.Safe(int n) && n == 0
                && s == CellState.UNKNOWN))
            return;
        
        board.set(x, y, new Cell(CellType.Safe.EMPTY, CellState.REVEALED));
        
        for (int y2 = Math.max(0, y - 1); y2 <= Math.min(sizes.height() - 1, y + 1); y2++)
            for (int x2 = Math.max(0, x - 1); x2 <= Math.min(sizes.width() - 1, x + 1); x2++)
                if (board.get(x2, y2) instanceof Cell(var type, var state)
                        && type instanceof CellType.Safe(var number)
                        && state == CellState.UNKNOWN)
                    if (number == 0) {
                        revealEmpty(x2, y2, board);
                    } else {
                        board.set(x2, y2, new Cell(new CellType.Safe(number), CellState.REVEALED));
                    }
    }
    
    private boolean internalReveal(int x, int y, Board board) {
        if (board.get(x, y).state() != CellState.UNKNOWN)
            return true;
        return switch (board.get(x, y).type()) {
            case CellType.Safe(var number) -> {
                if (number == 0) {
                    revealEmpty(x, y, board);
                } else {
                    board.set(x, y, new Cell(new CellType.Safe(number), CellState.REVEALED));
                }
                
                yield true;
            }
            case CellType.Mine ignored -> {
                // if it's the first move, move the mine to a random location
//                if (first) {
//                    // this is a bit inefficient, but it's not like it's going to be called often
//                    while (true) {
//                        var x2 = ThreadLocalRandom.current().nextInt(sizes.width());
//                        var y2 = ThreadLocalRandom.current().nextInt(sizes.height());
//
//                        if (board.get(x2, y2) instanceof Cell.Unknown) {
//                            board.set(x2, y2, Cell.Mine.INSTANCE);
//                            break;
//                        }
//                    }
//                    board.set(x, y, new Cell.Unknown(0));
//                    generateNmbers(board);
//                    first = false;
//                    yield internalReveal(x, y, board);
//                } else {
//                }
                board.set(x, y, new Cell(CellType.MINE, CellState.REVEALED));
                yield false;
            }
            
            default -> true;
        };
    }
    
    @Override
    public GameState reveal(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return getGameState();
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return getGameState();
        
        var board = gamestate.board().clone();
        
        var success = internalReveal(x, y, board);
        
        this.gamestate = gamestate.withBoard(board);
        
        if (!success) {
            this.gamestate = gamestate.withStatus(GameStatus.LOST);
            
            on_lose.run();
            
            return getGameState();
        }
        
        if (gamestate.board().hasWon()) {
            this.gamestate = gamestate.withStatus(GameStatus.WON);
            
            on_win.run();
            
            return getGameState();
        }
        
        
        return getGameState();
    }
    
    @Override
    public GameState clearAround(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return getGameState();
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return getGameState();
        
        var board = gamestate.board().clone();
        
        if (!(board.get(x, y) instanceof Cell(var type, var state)
                && type instanceof CellType.Safe(var number)
                && state == CellState.REVEALED)) return getGameState();
        
        var marked_mines = 0;
        
        for (int y2 = Math.max(0, y - 1); y2 <= Math.min(sizes.height() - 1, y + 1); y2++)
            for (int x2 = Math.max(0, x - 1); x2 <= Math.min(sizes.width() - 1, x + 1); x2++)
                if (board.get(x2, y2).state() == CellState.FLAGGED)
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
            
            return getGameState();
        }
        
        if (gamestate.board().hasWon()) {
            this.gamestate = gamestate.withStatus(GameStatus.WON);
            
            on_win.run();
            
            return getGameState();
        }
        
        
        return getGameState();
    }
    
    public GameState setFlagged(int x, int y, boolean flagged) {
        if (gamestate.status() != GameStatus.PLAYING) return getGameState();
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return getGameState();
        if (!(gamestate.board().get(x, y) instanceof Cell(var type, var state)
                && state != CellState.REVEALED))
            return getGameState();
        
        var board = gamestate.board().clone();
        var remaining_mines = gamestate.remainingMines();
        
        remaining_mines += (flagged != (state == CellState.FLAGGED)) ?
                ((flagged) ? -1 : 1) : 0;
        
        board.set(x, y, new Cell(type, (flagged) ? CellState.FLAGGED : CellState.UNKNOWN));
        
        this.gamestate = gamestate.withBoard(board).withRemainingMines(remaining_mines);
        
        return getGameState();
    }
}
