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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static canaryprism.minsweeper.solver.impl.mia.MiaLogic.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class ExpertSolver extends IntermediateSolver implements Solver {
    
    public static final int BRUTE_FORCE_LIMIT = 20;
    
    @Override
    public Move solve(GameState state) {
        if (super.solve(state) instanceof Move move)
            return move;
        var size = state.board().getSize();
        
        
        var empties = new HashSet<Move.Point>();
        var adjacents = new HashSet<Move.Point>();
        for (int y = 0; y < size.height(); y++) {
            for (int x = 0; x < size.width(); x++) {
                if (state.board().get(x, y).state() == CellState.UNKNOWN) {
                    for (int y2 = max(0, y - 1); y2 <= min(size.height() - 1, y + 1); y2++) {
                        for (int x2 = max(0, x - 1); x2 <= min(size.width() - 1, x + 1); x2++) {
                            if (state.board().get(x2, y2).type() instanceof CellType.Safe(var number) && number > 0) {
                                empties.add(new Move.Point(x, y));
                                adjacents.add(new Move.Point(x2, y2));
                            }
                        }
                    }
                }
            }
        }
//        System.out.println();
//        System.out.println("empties: " + empties.size());
//        System.out.println("adjacents: " + adjacents.size());
        if (empties.size() < BRUTE_FORCE_LIMIT && !adjacents.isEmpty()) {
//            System.out.println("brute forcing");
//            var start = System.nanoTime();
//            try {
            
            var states = bruteForce(adjacents.stream().toList(), 0, state)
                    .collect(Collectors.toSet());
//        System.out.println("possible states");
//        for (var e : states) {
//            e.board().forEach(System.out::println);
//            System.out.println();
//        }
            if (!states.isEmpty()) {
                var clicks = new HashSet<Move.Click>();
                for (var point : empties) {
                    if (states.stream()
                            .allMatch((e) ->
                                    e.board().get(point.x(), point.y()).state() != CellState.FLAGGED)) {
//                            System.out.println("brute force solution");
                        clicks.add(new Move.Click(point, Move.Action.LEFT));
//                            return new Move(point, Move.Action.LEFT, Optional.of(new Reason(BRUTE_FORCE_REVEAL)));
                    }
                    if (states.stream()
                            .allMatch((e) ->
                                    e.board().get(point.x(), point.y()).state() == CellState.FLAGGED)) {
//                            System.out.println("brute force solution");
                        clicks.add(new Move.Click(point, Move.Action.RIGHT));
//                            return new Move(point, Move.Action.RIGHT, Optional.of(new Reason(BRUTE_FORCE_FLAG)));
                    }
                }
                if (!clicks.isEmpty()) {
                    return new Move(clicks, new Reason(BRUTE_FORCE, empties));
                }
                
                if (states.stream().allMatch((e) -> e.remainingMines() == 0)) {
                    for (int y2 = 0; y2 < size.height(); y2++) {
                        for (int x2 = 0; x2 < size.width(); x2++) {
                            int x = x2, y = y2;
                            if (state.board().get(x2, y2).state() == CellState.UNKNOWN
                                    && states.stream()
                                    .allMatch((e) ->
                                            e.board().get(x, y).state() != CellState.FLAGGED)) {
                                clicks.add(new Move.Click(x2, y2, Move.Action.LEFT));
                            }
                        }
                    }
                }
                
                if (!clicks.isEmpty()) {
                    return new Move(clicks, new Reason(BRUTE_FORCE_EXHAUSTION, empties));
                }
            }
//                System.out.println("brute force without solution");
//            } finally {
//                System.out.printf("spent %.6f secs\n", (System.nanoTime() - start) / 1_000_000_000.0);
//            }
        }
        
        
        return null;
    }
    
    
    private GameState simulateRightClick(GameState state, Move.Point point) {
        return simulateRightClick(state, point.x(), point.y());
    }
    private GameState simulateRightClick(GameState state, int x, int y) {
        var board = state.board().clone();
        var cell = board.get(x, y);
        var remaining = state.remainingMines();
        if (cell.state() == CellState.UNKNOWN) {
            board.set(x, y, new Cell(CellType.UNKNOWN, CellState.FLAGGED));
            remaining--;
        } else if (cell.state() == CellState.FLAGGED) {
            board.set(x, y, new Cell(CellType.UNKNOWN, CellState.UNKNOWN));
            remaining++;
        }
        return new GameState(state.status(), board, remaining);
    }
    
    private GameState simulateReveal(GameState state, Move.Point point) {
        return simulateReveal(state, point.x(), point.y());
    }
    private GameState simulateReveal(GameState state, int x, int y) {
        var board = state.board().clone();
        // it is normally illegal to have a revealed cell still be unknown
        // but such are the circumstances we find ourselves in
        board.set(x, y, new Cell(CellType.UNKNOWN, CellState.REVEALED));
        return new GameState(state.status(), board, state.remainingMines());
    }
    
    /// this might actually return partially filled satisfied states since tehy might fail early??
    /// probably not though
    private Stream<GameState> bruteForce(List<Move.Point> points, int index, GameState state) {
        var size = state.board().getSize();
        var empties = new ArrayList<Move.Point>();
        var current = points.get(index);
        var x = current.x();
        var y = current.y();
        var flags = 0;
        var cell = ((CellType.Safe) state.board().get(x, y).type());
        for (int y3 = max(0, y - 1); y3 <= min(size.height() - 1, y + 1); y3++) {
            for (int x3 = max(0, x - 1); x3 <= min(size.width() - 1, x + 1); x3++) {
                if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
                    empties.add(new Move.Point(x3, y3));
                } else if (state.board().get(x3, y3).state() == CellState.FLAGGED) {
                    flags++;
                }
            }
        }
        
        var mines_to_flag = cell.number() - flags;
        
        if (mines_to_flag > state.remainingMines() || mines_to_flag > empties.size())
            return Stream.empty(); // invalid
        
        if (mines_to_flag == 0 || empties.isEmpty()) {
            if (index + 1 == points.size()) {
                return Stream.of(state);
            }
            return bruteForce(points, index + 1, state);
        }
        
        
        var stream = Stream.<Stream<GameState>>builder();
        for (var flag_combination : getFlagCombinations(empties, mines_to_flag)) {
            var state_copy = state.clone();
            for (var point : empties) {
                if (flag_combination.contains(point)) {
                    state_copy = simulateRightClick(state_copy, point);
                } else {
                    state_copy = simulateReveal(state_copy, point);
                }
            }
//            for (var point : flag_combination) {
//                state_copy = simulateRightClick(state_copy, point);
//            }
            if (index + 1 == points.size()) {
                stream.add(Stream.of(state_copy));
            } else {
                stream.add(bruteForce(points, index + 1, state_copy));
            }
            
        }
        return stream.build()
                .flatMap(Function.identity());
    }
    
    private Set<Set<Move.Point>> getFlagCombinations(List<Move.Point> empties, int mines_to_flag) {
        if (empties.size() < mines_to_flag)
            return Set.of();
        
        return Objects.requireNonNull(getFlagCombinations(new HashSet<>(), empties, 0, mines_to_flag))
                .collect(Collectors.toSet());
    }
    
    /// See, the weird thing about this recursing method is that
    @SuppressWarnings("unchecked")
    private Stream<Set<Move.Point>> getFlagCombinations(HashSet<Move.Point> selected, List<Move.Point> empties, int start, int mines_to_flag) {
        if (mines_to_flag < 1)
            return Stream.empty();
        var stream = Stream.<Stream<Set<Move.Point>>>builder();
        for (int i = start; i < empties.size(); i++) {
            var clone = ((HashSet<Move.Point>) selected.clone());
            clone.add(empties.get(i));
            if (mines_to_flag == 1) {
                stream.add(Stream.of(clone));
            } else {
                stream.add(getFlagCombinations(clone, empties, i + 1, mines_to_flag - 1));
            }
        }
        return stream.build()
                .flatMap(Function.identity());
    }
    
    @Override
    public String getName() {
        return "Expert Solver";
    }
    
    @Override
    public String getDescription() {
        return "solver that can brute force through all possible mine configurations within reasonable time constraints";
    }
}
