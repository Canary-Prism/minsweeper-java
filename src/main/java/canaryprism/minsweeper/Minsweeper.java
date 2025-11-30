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

/// # Minsweeper base interface
///
/// This interface defines all the standard moves of a Minsweeper game
///
/// The main implementation of this is [MinsweeperGame]
///
/// @apiNote All methods return a [GameState] representing the state of the game after the operation is complete.
///          The [GameState] returned is identical to what would be returned
///          if [#getGameState()] were invoked after the operation and before the next operation
/// @see MinsweeperGame
public interface Minsweeper {
    
    /// Start or restarts a Minsweeper game
    ///
    /// idk what to tell ya it resets and starts a new game
    ///
    /// @return the state of the game
    GameState start();

    /// Gets the current state of the game
    ///
    /// @return the state of the game
    GameState getGameState();
    
    /// Reveals a [Cell] at the specified coordinates
    ///
    /// Revealing only works on [UNKNOWN][CellState#UNKNOWN] cells,
    /// turning them into [REVEALED][CellState#REVEALED] cells.
    /// Otherwise, this method does nothing
    ///
    /// If the revealed cell is [CellType.Safe(0)][CellType.Safe#EMPTY] (an empty cell)
    /// then all surrounding cells are also revealed (this continues recursively)
    ///
    /// If the revealed cell is a [CellType.Mine] then the game is lost
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the state of the game
    GameState reveal(int x, int y);
    
    /// Clears around (chords) a [Cell] at the specified coordinates
    ///
    /// Clearing around only works on [CellType.Safe] cells where their [number][CellType.Safe#number]
    /// equals the amount of [FLAGGED][CellState#FLAGGED] neighbours.
    /// Otherwise, this method does nothing
    ///
    /// This [reveal][#reveal(int, int)]s all [UNKNOWN][CellState#UNKNOWN] neighbours
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the state of the game
    /// @see #reveal(int, int)
    GameState clearAround(int x, int y);
    
    /// Sets a [Cell]'s flagged state
    ///
    /// Setting flag state is actually represented as switching the cell's state
    /// between [CellState#UNKNOWN] and [CellState#FLAGGED].
    /// As such, if the cell's state is [CellState#REVEALED] this method does nothing
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the state of the game
    /// @see #toggleFlag(int, int)
    GameState setFlagged(int x, int y, boolean flagged);
    
    /// Toggles a [Cell]'s flagged state
    ///
    /// delegates to [#setFlagged(int, int, boolean)]
    /// where `flagged` is `false` if the current cell's state is [FLAGGED][CellState#FLAGGED] and `true` otherwise
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the state of the game
    /// @see #setFlagged(int, int, boolean)
    default GameState toggleFlag(int x, int y) {
        return setFlagged(x, y, getGameState().board().get(x, y).state() != CellState.FLAGGED);
    }
    
    /// Performs a "left click" by the conventional minesweeper input scheme
    ///
    /// if the current cell's state is [UNKNOWN][CellState#UNKNOWN] then tries to [reveal][#reveal(int, int)] the cell
    /// if the current cell's state is [REVEALED][CellState#REVEALED] then tries to [chord][#clearAround(int, int)] it
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the state of the game
    default GameState leftClick(int x, int y) {
        if (getGameState().status() != GameStatus.PLAYING) return getGameState();
        if (!(x >= 0 && x < getGameState().board().getSize().width() && y >= 0 && y < getGameState().board().getSize().height()))
            return getGameState();
        
        var cell = getGameState().board().get(x, y);
        
        if (cell.type() instanceof CellType.Safe && cell.state() == CellState.REVEALED)
            return clearAround(x, y);
        if (cell.state() == CellState.UNKNOWN)
            return reveal(x, y);
        return getGameState();
    }
    
    /// Performs a "right click" by the conventional minesweeper input scheme
    ///
    /// identical to [#toggleFlag(int, int)]
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the state of the game
    /// @see #toggleFlag(int, int)
    default GameState rightClick(int x, int y) {
        return toggleFlag(x, y);
    }
}
