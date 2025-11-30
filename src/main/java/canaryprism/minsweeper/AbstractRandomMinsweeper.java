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

import java.util.concurrent.ThreadLocalRandom;

/// Abstract partial implementation of [Minsweeper]
///
/// is like [AbstractHidingMinsweeper] except it implements [#start()]
///
/// [#start()] generates a random board and that's it. it does **not** prevent death on first move
///
/// @see AbstractHidingMinsweeper
public abstract class AbstractRandomMinsweeper extends AbstractHidingMinsweeper {
    
    /// Constructs an AbstractRandomMinsweeper with the provided sizes
    ///
    /// has win/lose [Runnable]s that are invoked when a game is won or lost respectively
    ///
    /// @param sizes the size of the board
    /// @param on_win Runnable to be invoked on win
    /// @param on_lose Runnable to be invoked on lose
    public AbstractRandomMinsweeper(BoardSize sizes, Runnable on_win, Runnable on_lose) {
        super(sizes, on_win, on_lose);
    }
    
    /// Start or restarts a Minsweeper game
    ///
    /// idk what to tell ya it resets and starts a new game
    ///
    /// The game is completely randomly generated without any special treatment for the first move
    ///
    /// @return the state of the game
    public GameState start() {
        
        this.gamestate = generateGame();
        
        return getGameState();
    }
    
    private static final Cell MINE = new Cell(CellType.MINE, CellState.UNKNOWN);
    
    /// Generates a random Minsweeper game
    ///
    /// @return a randomly generated gamestate
    protected GameState generateGame() {
        var temp_board = new Board(sizes);
        var mines = 0;
        while (mines < sizes.mines()) {
            var x = ThreadLocalRandom.current().nextInt(sizes.width());
            var y = ThreadLocalRandom.current().nextInt(sizes.height());
            
            if (temp_board.get(x, y).type() instanceof CellType.Safe) {
                temp_board.set(x, y, MINE);
                mines++;
            }
        }
        
        generateNmbers(temp_board);
        
        return new GameState(GameStatus.PLAYING, temp_board, sizes.mines());
    }
    
    /// I am not fixing the typo
    ///
    /// @param board board bc yk boards
    private void generateNmbers(Board board) {
        for (int y = 0; y < sizes.height(); y++)
            for (int x = 0; x < sizes.width(); x++)
                if (board.get(x, y).type() instanceof CellType.Safe)
                    board.set(x, y, new Cell(new CellType.Safe(0), CellState.UNKNOWN));
        
        for (int y = 0; y < sizes.height(); y++)
            for (int x = 0; x < sizes.width(); x++)
                if (board.get(x, y).type() instanceof CellType.Mine)
                    for (int y2 = Math.max(0, y - 1); y2 <= Math.min(sizes.height() - 1, y + 1); y2++)
                        for (int x2 = Math.max(0, x - 1); x2 <= Math.min(sizes.width() - 1, x + 1); x2++)
                            if (board.get(x2, y2).type() instanceof CellType.Safe(var number))
                                board.set(x2, y2, new Cell(new CellType.Safe(number + 1), CellState.UNKNOWN));
    }
}
