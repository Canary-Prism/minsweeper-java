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

import canaryprism.minsweeper.CellState;
import canaryprism.minsweeper.CellType;
import canaryprism.minsweeper.GameState;
import canaryprism.minsweeper.solver.Move;
import canaryprism.minsweeper.solver.Reason;
import canaryprism.minsweeper.solver.Solver;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static canaryprism.minsweeper.solver.impl.mia.MiaLogic.*;
import static canaryprism.minsweeper.solver.impl.mia.MiaLogic.REGION_DEDUCTION_FLAG;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class IntermediateSolver extends BeginnerSolver implements Solver {
    
    @Override
    public Move solve(GameState state) {
        if (super.solve(state) instanceof Move move)
            return move;
        var size = state.board().getSize();


//            new way of doing multi flags
//            should be better
//            you have to generate all the multi flags first,,
        
        record Flag(int number, HashSet<Move.Point> points) {}
        
        var flags = new LinkedHashSet<Flag>();
        // woah really hate i was too lazy to not use a lambda
        // aren't they like inefficient or something
        
        for (int y2 = 0; y2 < size.height(); y2++) {
            for (int x2 = 0; x2 < size.width(); x2++) {
                if (state.board().get(x2, y2).type() instanceof CellType.Safe(var required)) {
                    
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (state.board().get(x3, y3).state() == CellState.FLAGGED) {
                                required--;
                            }
                        }
                    }
                    
                    if (required <= 0)
                        continue;
                    
                    var neighbours = new HashSet<Move.Point>();
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
                                neighbours.add(new Move.Point(x3, y3));
                            }
                        }
                    }
                    
                    if (neighbours.isEmpty())
                        continue;
                    
                    flags.add(new Flag(required, neighbours));
                }
            }
        }
        
        var changed = true;
        while (changed) {
            
            var to_add = new HashSet<Flag>();
            for (var flag : flags) {
                // entirely contained flags and stuff
                {
                    var contained_flags = flags.stream()
                            .filter((e) -> flag.points.containsAll(e.points))
                            .collect(Collectors.toSet());
                    
                    for (var contained : contained_flags) {
                        var remaining_number = flag.number - contained.number;
                        var remaining_neighbours = new HashSet<>(flag.points);
                        remaining_neighbours.removeAll(contained.points);
                        
                        if (remaining_neighbours.isEmpty())
                            continue;
                        
                        
                        if (remaining_number == 0) {
                            // all the flags are accounted for, just reveal everything remaining
                            return new Move(
                                    remaining_neighbours.stream()
                                            .map((point) -> new Move.Click(point, Move.Action.LEFT))
                                            .collect(Collectors.toSet()),
                                    new Reason(REGION_DEDUCTION_REVEAL, contained.points));
                        } else if (remaining_number == remaining_neighbours.size()) {
                            // all flags can be accounted for if everything left is flagged yippee
                            return new Move(
                                    remaining_neighbours.stream()
                                            .map((point) -> new Move.Click(point, Move.Action.RIGHT))
                                            .collect(Collectors.toSet()),
                                    new Reason(REGION_DEDUCTION_FLAG, contained.points));
                        }
//                            it.remove();
//                            to_add.add(flag);
                        to_add.add(new Flag(remaining_number, remaining_neighbours));
                    }
                }
                
                // not entirely contained stuffs
                {
                    var touching_flags = flags.stream()
                            .filter((e) -> e.points.stream()
                                    .anyMatch(flag.points::contains))
                            .collect(Collectors.toSet());
                    
                    
                    for (var touching : touching_flags) {
                        var remaining_number = flag.number - touching.number;
                        var remaining_neighbours = new HashSet<>(flag.points);
                        remaining_neighbours.removeAll(touching.points);
                        
                        if (remaining_neighbours.isEmpty())
                            continue;
                        
                        
                        if (remaining_number == remaining_neighbours.size()) {
                            // all flags can be accounted for if everything left is flagged yippee
                            return new Move(
                                    remaining_neighbours.stream()
                                            .map((point) -> new Move.Click(point, Move.Action.RIGHT))
                                            .collect(Collectors.toSet()),
                                    new Reason(REGION_DEDUCTION_FLAG, touching.points));
                        }
                    }
                }
            }
            
            changed = flags.addAll(to_add);
        }
        
        if (state.remainingMines() == 0) {
            var clicks = new HashSet<Move.Click>();
            for (int y2 = 0; y2 < size.height(); y2++) {
                for (int x2 = 0; x2 < size.width(); x2++) {
                    if (state.board().get(x2, y2).state() == CellState.UNKNOWN) {
                        clicks.add(new Move.Click(x2, y2, Move.Action.LEFT));
                    }
                }
            }
            if (!clicks.isEmpty()) {
                return new Move(clicks, new Reason(ZERO_MINES_REMAINING));
            }
        }
        
        return null;
    }
    
    @Override
    public String getName() {
        return "Intermediate Solver";
    }
    
    @Override
    public String getDescription() {
        return "solver that can keep track of the amount of mines in individual regions and make logical deductions";
    }
}
