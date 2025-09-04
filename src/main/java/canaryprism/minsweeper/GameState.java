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

/// Represents the state of the game
public record GameState(GameStatus status, Board board, int remainingMines) implements Cloneable {
    
    GameState withStatus(GameStatus status) {
        return new GameState(status, board, remainingMines);
    }
    
    GameState withBoard(Board board) {
        return new GameState(status, board, remainingMines);
    }
    
    GameState withRemainingMines(int remainingMines) {
        return new GameState(status, board, remainingMines);
    }

    GameState hideMines() {
        return new GameState(status, board.hideMines(), remainingMines);
    }
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public GameState clone() {
        return new GameState(status, board.clone(), remainingMines);
    }
}
