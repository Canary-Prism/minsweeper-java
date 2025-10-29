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

import canaryprism.minsweeper.Cell;
import canaryprism.minsweeper.CellState;
import canaryprism.minsweeper.CellType;
import canaryprism.minsweeper.GameState;
import canaryprism.minsweeper.solver.Move;
import canaryprism.minsweeper.solver.Reason;
import canaryprism.minsweeper.solver.Solver;

import java.util.HashSet;

import static canaryprism.minsweeper.solver.impl.mia.MiaLogic.CHORD;
import static canaryprism.minsweeper.solver.impl.mia.MiaLogic.FLAG_CHORD;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class BeginnerSolver implements Solver {
    
    @Override
    public Move solve(GameState state) {
        var size = state.board().getSize();
        
        for (int y2 = 0; y2 < size.height(); y2++) {
            for (int x2 = 0; x2 < size.width(); x2++) {
                
                if (!(state.board().get(x2, y2) instanceof Cell(var t, var ignored)
                        && t instanceof CellType.Safe(var number)))
                    continue;
                
                var marked_mines = new HashSet<Move.Point>();
                var empty_spaces = new HashSet<Move.Point>();
                
                for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                    for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                        if (state.board().get(x3, y3).state() == CellState.FLAGGED) {
                            marked_mines.add(new Move.Point(x3, y3));
                            empty_spaces.add(new Move.Point(x3, y3));
                        } else if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
                            empty_spaces.add(new Move.Point(x3, y3));
                        }
                    }
                }
                
                if (number == marked_mines.size() && empty_spaces.size() > marked_mines.size()) {
//                            try? await Task.sleep(nanoseconds: 50_000_000)
                    return new Move(x2, y2, Move.Click.LEFT, new Reason(CHORD, marked_mines));
                } else if (number == empty_spaces.size()) {
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
                                empty_spaces.add(new Move.Point(x2, y2));
                                return new Move(x3, y3, Move.Click.RIGHT, new Reason(FLAG_CHORD, empty_spaces));
                            }
                        }
                    }
                } else if (number < marked_mines.size()) {
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (state.board().get(x3, y3).state() == CellState.FLAGGED) {
                                
                                return new Move(x3, y3, Move.Click.RIGHT);
                            }
                        }
                    }
                }
                
                
            }
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "Beginner Solver";
    }
    
    @Override
    public String getDescription() {
        return "solver that only knows how to flag all neighbours and chord";
    }
}
