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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static canaryprism.minsweeper.solver.impl.mia.MiaLogic.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class IntermediateSolver extends BeginnerSolver implements Solver {
    
    @Override
    public Move solve(GameState state) {
        if (super.solve(state) instanceof Move move)
            return move;
        var size = state.board().getSize();
        
        class Logic {
            
            Move logic(int x2, int y2, int de, Set<Move.Point> grid) {
                if (!(state.board().get(x2, y2).type() instanceof CellType.Safe(var this_num)))
                    return null;
                
                var index = 0;
                var claimed_surroundings = new ArrayList<Integer>();
                for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                    for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                        if (x3 == x2 && y3 == y2) {
                            index += 1;
                            continue;
                        }
                        for (var item : grid) {
                            if (item.x() == x3 && item.y() == y3) {
                                claimed_surroundings.add(index);
                            }
                        }
                        index += 1;
                    }
                }
                var strong_match = claimed_surroundings.size() == grid.size();
                var flagged = 0;
                var empty = 0;
                index = 0;
                for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                    for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                        if (x3 == x2 && y3 == y2) {
                            index += 1;
                            continue;
                        }
                        if (claimed_surroundings.contains(index)) {
                            index += 1;
                            continue;
                        }
                        switch (state.board().get(x3, y3).state()) {
                            case FLAGGED -> flagged++;
                            case UNKNOWN -> empty++;
                            default -> {}
                        }
                        index++;
                    }
                }
                
                if (strong_match && flagged + de == this_num && empty > 0) {
                    index = 0;
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (x3 == x2 && y3 == y2) {
                                index++;
                                continue;
                            }
                            if (claimed_surroundings.contains(index)) {
                                index++;
                                continue;
                            }
                            if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
//                                        try? await Task.sleep(nanoseconds: 50_000_000)
                                
                                return new Move(x3, y3, Move.Click.LEFT, new Reason(MULTLI_FLAG_REVEAL, grid));
                            }
                            index += 1;
                        }
                    }
                } else if (flagged + de + empty == this_num) {
                    index = 0;
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (x3 == x2 && y3 == y2) {
                                index += 1;
                                continue;
                            }
                            if (claimed_surroundings.contains(index)) {
                                index += 1;
                                continue;
                            }
                            if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
                                return new Move(x3, y3, Move.Click.RIGHT, new Reason(MULTLI_FLAG_FLAG, grid));
                            }
                            index += 1;
                        }
                    }
                }
                
                return null;
            }
        }
        
        var logic = new Logic();
        
        for (int y2 = 0; y2 < size.height(); y2++) {
            for (int x2 = 0; x2 < size.width(); x2++) {
                if (state.board().get(x2, y2).type() instanceof CellType.Safe(var this_num)) {
                    if (this_num <= 0)
                        continue;
                    
                    var flagged = 0;
                    var empty = 0;
                    var grid = new HashSet<Move.Point>();
                    
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (state.board().get(x3, y3).state() == CellState.FLAGGED) {
                                flagged += 1;
                            } else if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
                                grid.add(new Move.Point(x3, y3));
                                empty += 1;
                            }
                        }
                    }
                    if (!(flagged < this_num && empty > 0))
                        continue;
                    
                    final var de = this_num - flagged;
                    
                    
                    for (int y3 = max(0, y2 - 2); y3 <= min(size.height() - 1, y2 + 2); y3++) {
                        for (int x3 = max(0, x2 - 2); x3 <= min(size.width() - 1, x2 + 2); x3++) {
                            if (state.board().get(x3, y3).state() == CellState.REVEALED) {
                                if (logic.logic(x3, y3, de, grid) instanceof Move move) {
                                    return move;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (state.remainingMines() == 0) {
            for (int y2 = 0; y2 < size.height(); y2++) {
                for (int x2 = 0; x2 < size.width(); x2++) {
                    if (state.board().get(x2, y2).state() == CellState.UNKNOWN) {
                        
                        return new Move(x2, y2, Move.Click.LEFT, new Reason(ZERO_MINES_REMAINING));
                    }
                }
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
