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

package canaryprism.minsweeper.solver;

import canaryprism.minsweeper.GameState;
import canaryprism.minsweeper.GameStatus;
import canaryprism.minsweeper.Minsweeper;
import canaryprism.minsweeper.MinsweeperGame;
import canaryprism.minsweeper.solver.impl.MiaSolver;

/// Solver is a type that is capable of solving [MinsweeperGame] games
///
/// Solvers are forbidden from making guesses, it will only perform a move
/// if it is 100% certain it is safe
///
/// if a Solver is to be used to generate a game by [MinsweeperGame#start(Solver)], it should prioritise
/// at least a little bit of speed, it's fine if a Solver chooses to resign if continuing would
/// result in too much of a time loss (around 3 seconds is the worst case limit probably)
///
/// it's also a SPI so yk
public interface Solver {
    
    /// Get the global default Solver instance
    ///
    /// @return the default Solver
    static Solver getDefault() {
        class Holder {
            static final Solver INSTANCE = new MiaSolver();
        }
        return Holder.INSTANCE;
    }
    
    /// Solve from a [GameState]
    ///
    /// @param state the state to solve
    /// @return a move to make
    Move solve(GameState state);
    
    default Result solve(Minsweeper minsweeper, GameState state) {
        while (state.status() == GameStatus.PLAYING) {
            var move = solve(state);
            if (move instanceof Move(Move.Point(var x, var y), var action))
                switch (action) {
                    case LEFT -> state = minsweeper.leftClick(x, y);
                    case RIGHT -> state = minsweeper.rightClick(x, y);
                }
            else
                break;
        }
        return switch (state.status()) {
            case WON -> Result.WON;
            case LOST -> Result.LOST;
            case PLAYING -> Result.RESIGNED;
            case NEVER -> throw new IllegalArgumentException();
        };
    }
    
    enum Result {
        WON, LOST, RESIGNED
    }
}
