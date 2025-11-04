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

package canaryprism.minsweeper.solver.impl.mia;

import canaryprism.minsweeper.GameStatus;
import canaryprism.minsweeper.Minsweeper;
import canaryprism.minsweeper.solver.Logic;
import canaryprism.minsweeper.solver.Move;
import canaryprism.minsweeper.solver.Reason;
import canaryprism.minsweeper.solver.Solver;

import java.util.Set;

public class ExpertOnlySolver extends ExpertSolver implements Solver {
    
    public static final Set<Logic> EXPERT_LOGIC = Set.of(
            MiaLogic.BRUTE_FORCE_FLAG, MiaLogic.BRUTE_FORCE_REVEAL);
    
    @Override
    public Result solve(Minsweeper minsweeper) {
        var state = minsweeper.getGameState();
        var expert_logic_used = false;
        while (state.status() == GameStatus.PLAYING) {
            var move = solve(state);
            if (move instanceof Move(var clicks, var optional_reason)) {
                if (optional_reason.orElse(null) instanceof Reason reason && EXPERT_LOGIC.contains(reason.logic()))
                    expert_logic_used = true;
                for (var click : clicks)
                    switch (click.action()) {
                        case LEFT -> state = minsweeper.leftClick(click.point().x(), click.point().y());
                        case RIGHT -> state = minsweeper.rightClick(click.point().x(), click.point().y());
                    }
            } else {
                break;
            }
        }
        if (!expert_logic_used)
            return Result.RESIGNED;
        return switch (state.status()) {
            case WON -> Result.WON;
            case LOST -> Result.LOST;
            case PLAYING -> Result.RESIGNED;
            case NEVER -> throw new IllegalArgumentException();
        };
    }
    
    @Override
    public String getName() {
        return "Expert Only Solver";
    }
    
    @Override
    public String getDescription() {
        return "Expert Solver that resigns if a game is so easy Intermediate Solver could've solved it";
    }
}
