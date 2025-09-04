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

package canaryprism.minsweeper.solver.impl;

import canaryprism.minsweeper.Cell;
import canaryprism.minsweeper.GameState;
import canaryprism.minsweeper.GameStatus;
import canaryprism.minsweeper.solver.Move;
import canaryprism.minsweeper.solver.Solver;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MiaSolver implements Solver {
    
    public static final int BRUTE_FORCE_LIMIT = 20;
    
    public Move solve(GameState state) {
        var size = state.board().getSize();

        if (state.status() == GameStatus.PLAYING) {
            for (int y2 = 0; y2 < size.height(); y2++) {
                for (int x2 = 0; x2 < size.width(); x2++) {
                    
                    if (!(state.board().get(x2, y2) instanceof Cell.Revealed(var number)))
                        continue;
                    
                    var marked_mines = 0;
                    var empty_spaces = 0;
                    
                    for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                        for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                            if (state.board().get(x3, y3) instanceof Cell.MarkedMine) {
                                marked_mines++;
                                empty_spaces++;
                            } else if (state.board().get(x3, y3) instanceof Cell.Unknown) {
                                empty_spaces++;
                            }
                        }
                    }
                    
                    if (number == marked_mines && empty_spaces > marked_mines) {
//                            try? await Task.sleep(nanoseconds: 50_000_000)
                        return new Move(x2, y2, Move.Click.LEFT);
                    } else if (number == empty_spaces) {
                        for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                            for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                                if (state.board().get(x3, y3) instanceof Cell.Unknown) {
                                    
                                    return new Move(x3, y3, Move.Click.RIGHT);
                                }
                            }
                        }
                    } else if (number < marked_mines) {
                        for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                            for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                                if (state.board().get(x3, y3) instanceof Cell.MarkedMine) {

                                    return new Move(x3, y3, Move.Click.RIGHT);
                                }
                            }
                        }
                    }
                    
                    
                }
            }
            
            //logical deduction time :c
            record Point(int x, int y) {
            
            }
            
            class Logic {
                
                Move logic(int x2, int y2, int de, ArrayList<Point> grid) {
                    if (!(state.board().get(x2, y2) instanceof Cell.Revealed(var this_num)))
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
                                if (item.x == x3 && item.y == y3) {
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
                            switch (state.board().get(x3, y3)) {
                                case Cell.MarkedMine ignored -> flagged++;
                                case Cell.Unknown ignored -> empty++;
                                default -> {
                                }
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
                                if (state.board().get(x3, y3) instanceof Cell.Unknown) {
//                                        try? await Task.sleep(nanoseconds: 50_000_000)
                                    
                                    return new Move(x3, y3, Move.Click.LEFT);
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
                                if (state.board().get(x3, y3) instanceof Cell.Unknown) {
                                    return new Move(x3, y3, Move.Click.RIGHT);
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
                    if (state.board().get(x2, y2) instanceof Cell.Revealed(var this_num)) {
                        if (this_num <= 0)
                            continue;
                        
                        var flagged = 0;
                        var empty = 0;
                        var grid = new ArrayList<Point>();
                        
                        for (int y3 = max(0, y2 - 1); y3 <= min(size.height() - 1, y2 + 1); y3++) {
                            for (int x3 = max(0, x2 - 1); x3 <= min(size.width() - 1, x2 + 1); x3++) {
                                if (state.board().get(x3, y3) instanceof Cell.MarkedMine) {
                                    flagged += 1;
                                } else if (state.board().get(x3, y3) instanceof Cell.Unknown) {
                                    grid.add(new Point(x3, y3));
                                    empty += 1;
                                }
                            }
                        }
                        if (!(flagged < this_num && empty > 0))
                            continue;
                        
                        final var de = this_num - flagged;
                        
                        
                        for (int y3 = max(0, y2 - 2); y3 <= min(size.height() - 1, y2 + 2); y3++) {
                            for (int x3 = max(0, x2 - 2); x3 <= min(size.width() - 1, x2 + 2); x3++) {
                                if (state.board().get(x3, y3) instanceof Cell.Revealed) {
                                    if (logic.logic(x3, y3, de, grid) instanceof Move move) {
                                        return move;
                                    }
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
                    if (state.board().get(x2, y2) instanceof Cell.Unknown) {
                        
                        return new Move(x2, y2, Move.Click.LEFT);
                    }
                }
            }
        }
        
        
        var empties = new HashSet<Move.Point>();
        var adjacents = new HashSet<Move.Point>();
        for (int y = 0; y < size.height(); y++) {
            for (int x = 0; x < size.width(); x++) {
                if (state.board().get(x, y) instanceof Cell.Unknown) {
                    for (int y2 = max(0, y - 1); y2 <= min(size.height() - 1, y + 1); y2++) {
                        for (int x2 = max(0, x - 1); x2 <= min(size.width() - 1, x + 1); x2++) {
                            if (state.board().get(x2, y2) instanceof Cell.Revealed(var number) && number > 0) {
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
        if (empties.size() < BRUTE_FORCE_LIMIT) {
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
                    for (var point : empties) {
                        if (states.stream()
                                .allMatch((e) ->
                                        e.board().get(point.x(), point.y()) instanceof Cell.Unknown)) {
//                            System.out.println("brute force solution");
                            return new Move(point, Move.Click.LEFT);
                        }
                        if (states.stream()
                                .allMatch((e) ->
                                        e.board().get(point.x(), point.y()) instanceof Cell.MarkedMine)) {
//                            System.out.println("brute force solution");
                            return new Move(point, Move.Click.RIGHT);
                        }
                    }
                }
//                System.out.println("brute force without solution");
//            } finally {
//                System.out.printf("spent %.6f secs\n", (System.nanoTime() - start) / 1_000_000_000.0);
//            }
        } else {
//            System.out.println("skipping brute force: too many combinations");
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
        if (cell instanceof Cell.Unknown) {
            board.set(x, y, Cell.MarkedMine.INSTANCE);
            remaining--;
        } else if (cell instanceof Cell.MarkedMine) {
            board.set(x, y, new Cell.Unknown(0));
            remaining++;
        }
        return new GameState(state.status(), board, remaining);
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
        var cell = ((Cell.Revealed) state.board().get(x, y));
        for (int y3 = max(0, y - 1); y3 <= min(size.height() - 1, y + 1); y3++) {
            for (int x3 = max(0, x - 1); x3 <= min(size.width() - 1, x + 1); x3++) {
                if (state.board().get(x3, y3) instanceof Cell.Unknown) {
                    empties.add(new Move.Point(x3, y3));
                } else if (state.board().get(x3, y3) instanceof Cell.MarkedMine) {
                    flags++;
                }
            }
        }
        
        var mines_to_flag = cell.number() - flags;
        if (mines_to_flag == 0 || empties.isEmpty()) {
            if (index + 1 == points.size())
                return Stream.of(state);
            return bruteForce(points, index + 1, state);
        }
        
        if (mines_to_flag > state.remainingMines())
            return Stream.empty(); // invalid
        
        var stream = Stream.<Stream<GameState>>builder();
        for (var flag_combination : getFlagCombinations(empties, mines_to_flag)) {
            var state_copy = state.clone();
            for (var point : flag_combination) {
                state_copy = simulateRightClick(state_copy, point);
            }
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
}
