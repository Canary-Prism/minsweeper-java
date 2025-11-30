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

/// Minsweeper game with a preset given state
///
/// This implementation takes a [GameState] directly and does not support [#start()]
///
/// All other moves work normally tho
public class SetMinsweeperGame extends AbstractHidingMinsweeper {
    
    /// Constructs a new SetMinsweeperGame with given [GameState] and Runnables invoked for winning and losing
    ///
    /// @param state the state of the game
    /// @param on_win Runnable to be invoked on win
    /// @param on_lose Runnable to be invoked on lose
    public SetMinsweeperGame(GameState state, Runnable on_win, Runnable on_lose) {
        super(state.board().getSize(), on_win, on_lose);
        this.gamestate = state;
    }
    
    /// Constructs a new SetMinsweeperGame with given [GameState]
    ///
    /// @param state the state of the game
    public SetMinsweeperGame(GameState state) {
        this(state, () -> {}, () -> {});
    }
    
    /// Not supported :3
    /// @throws UnsupportedOperationException hehe :3
    @Override
    public GameState start() {
        throw new UnsupportedOperationException("start() is unsupported for SetMinsweeperGame");
    }
}
