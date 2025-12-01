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

package canaryprism.minsweeper.solver.impl.start;

import canaryprism.minsweeper.GameState;
import canaryprism.minsweeper.GameStatus;
import canaryprism.minsweeper.Minsweeper;
import canaryprism.minsweeper.solver.Move;
import canaryprism.minsweeper.solver.Solver;
import org.jetbrains.annotations.Nullable;

/// SafeStart ensures the first move in [canaryprism.minsweeper.MinsweeperGame] is [safe][canaryprism.minsweeper.CellType.Safe]
///
/// It does not do anything else and isn't a real [Solver]
/// @see ZeroStart
public final class SafeStart implements Solver {
    
    @Override
    public @Nullable Move solve(GameState gameState) {
        return null;
    }
    
    @Override
    public Result solve(Minsweeper minsweeper) {
        return (minsweeper.getGameState().status() != GameStatus.LOST) ? Result.WON : Result.LOST;
    }
    
    @Override
    public String getName() {
        return "Safe Start";
    }
    
    @Override
    public String getDescription() {
        return "fake solver that only ensures the first move is safe";
    }
}
