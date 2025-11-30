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

/// Abstract partial implementation of [Minsweeper]
///
/// is like [AbstractMinsweeper] except [#getGameState()] hides the true [GameState] from players until they win or lose
///
/// @see AbstractHidingMinsweeper
/// @see AbstractRandomMinsweeper
public abstract class AbstractHidingMinsweeper extends AbstractMinsweeper {
    
    /// Constructs an AbstractHidingMinsweeper with the provided sizes
    ///
    /// has win/lose [Runnable]s that are invoked when a game is won or lost respectively
    ///
    /// @param sizes the size of the board
    /// @param on_win Runnable to be invoked on win
    /// @param on_lose Runnable to be invoked on lose
    public AbstractHidingMinsweeper(BoardSize sizes, Runnable on_win, Runnable on_lose) {
        super(sizes, on_win, on_lose);
    }
    
    /// {@inheritDoc}
    ///
    /// If [#gamestate]'s [status][GameState#status] is [GameStatus#PLAYING],
    /// the returned state hides [CellType] information for [UNKNOWN][CellState#UNKNOWN] [Cell]s
    /// by replacing the [CellType] with [CellType.Unknown]
    ///
    /// @return the state of the game
    @Override
    public final GameState getGameState() {
        if (super.getGameState().status() == GameStatus.PLAYING)
            return super.getGameState().hideMines();
        return super.getGameState();
    }
}
