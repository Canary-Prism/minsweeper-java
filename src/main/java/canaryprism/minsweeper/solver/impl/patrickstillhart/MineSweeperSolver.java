package canaryprism.minsweeper.solver.impl.patrickstillhart;/*
The MIT License (MIT)

Copyright (c) 2015 Patrick Stillhart

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import canaryprism.minsweeper.*;
import canaryprism.minsweeper.solver.Move;
import canaryprism.minsweeper.solver.Move.Point;
import canaryprism.minsweeper.solver.Solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;

import static java.lang.Math.max;
import static java.lang.Math.min;


/// [Minesweeper Solver by Patrick Stillhart ](https://github.com/arcs-/Minesweeper-Solver/blob/master/src/State.java)
///
/// adapted to minsweeper by Mia
public class MineSweeperSolver implements Solver {
    
    
    @Override
    public Move solve(GameState state) {
        var size = state.board().getSize();
        
        for (int y = 0; y < size.height(); y++) {
            for (int x = 0; x < size.width(); x++) {
                if (state.board().get(x, y).type() instanceof CellType.Safe(var number) && number > 0) {
                    if (solveSingle(state, x, y) instanceof Move move)
                        return move;
                }
            }
        }
        
        if (tankSolver(state) instanceof Move move)
            return move;
        
        return null;
    }

    /**
     * Solves a single field the easy way ..
     * if the number of blocks around equals the number on this block, flag them otherwise open them
     *
     * @param x why are you reading this?
     * @param y you seriously should understand it
     */
    private Move solveSingle(GameState state, int x, int y) {
        if (!(state.board().get(x, y).type() instanceof CellType.Safe(int number) && number > 0))
            return null;
        int countClosed = getSurroundingByPredicate(state, x, y, (e) -> e.state() == CellState.UNKNOWN);
        if (countClosed == 0) return null;

        int countAlreadyFlagged = getSurroundingByPredicate(state, x, y, (e) -> e.state() == CellState.FLAGGED);
        
        // First: flag as much as we can
        if (number == countClosed + countAlreadyFlagged) {
            for (int y3 = max(0, y - 1); y3 <= min(state.board().getSize().height() - 1, y + 1); y3++) {
                for (int x3 = max(0, x - 1); x3 <= min(state.board().getSize().width() - 1, x + 1); x3++) {
                    if (state.board().get(x3, y3).state() == CellState.UNKNOWN) {
                        
                        return new Move(x3, y3, Move.Click.RIGHT);
                    }
                }
            }
        }

        // Second: open the ones around
        if (number == countAlreadyFlagged) {
            return new Move(x, y, Move.Click.LEFT);
        }

        return null;
    }

    /**
     * Discovers all the fields around that match the parameter
     *
     * @param x why are you reading this?
     * @param y you seriously should understand it
     * @param predicate predicate to test
     * @return the amount of fields around that match type
     */
    private int getSurroundingByPredicate(GameState state, int x, int y, Predicate<? super Cell> predicate) {
        int hits = 0;
        var size = state.board().getSize();

        if (y > 0) {
            if (x > 0 && predicate.test(state.board().get(x - 1, y - 1))) hits++; // top ■□□
            if (predicate.test(state.board().get(x, y - 1))) hits++;   // top □■□
            if (x < size.width() - 1 && predicate.test(state.board().get(x + 1, y - 1))) hits++; // top □□■
        }

        if (x > 0 && predicate.test(state.board().get(x - 1, y))) hits++; // middle ■□□
        if (x < size.width() - 1 && predicate.test(state.board().get(x + 1, y))) hits++; // middle □□■

        if (y < size.height() - 1) {
            if (x > 0 && predicate.test(state.board().get(x - 1, y + 1))) hits++; // bottom ■□□
            if (predicate.test(state.board().get(x, y + 1))) hits++;   // bottom □■□
            if (x < size.width() - 1 && predicate.test(state.board().get(x + 1, y + 1))) hits++; // bottom □□■
        }

        return hits;

    }

    /**
     * Discovers all boundary blocks around
     * A boundary block is an unopened block with opened blocks next to it.
     *
     * @param x why are you reading this?
     * @param y you seriously should understand it
     * @return true if it is a boundary block
     */
    private boolean isBoundary(GameState state, int x, int y) {
        if (!(state.board().get(x, y).state() == CellState.UNKNOWN)) return false;

        if (y > 0) {
            if (x > 0 && state.board().get(x - 1, y - 1).state() == CellState.REVEALED) return true; // top ■□□
            if (state.board().get(x, y - 1).state() == CellState.REVEALED) return true;   // top □■□
            if (x < state.board().getSize().width() - 1 && state.board().get(x + 1, y - 1).state() == CellState.REVEALED) return true; // top □□■
        }

        if (x > 0 && state.board().get(x - 1, y).state() == CellState.REVEALED) return true; // middle ■□□
        if (x < state.board().getSize().width() - 1 && state.board().get(x + 1, y).state() == CellState.REVEALED) return true; // middle □□■

        if (y < state.board().getSize().height() - 1) {
            if (x > 0 && state.board().get(x - 1, y + 1).state() == CellState.REVEALED) return true; // bottom ■□□
            if (state.board().get(x, y + 1).state() == CellState.REVEALED) return true;   // bottom □■□
            if (x < state.board().getSize().width() - 1 && state.board().get(x + 1, y + 1).state() == CellState.REVEALED) return true; // bottom □□■
        }

        return false;
    }
    

    /**
     * How many flags exist around this block?
     *
     * @param array the array to check in
     * @param x why are you reading this?
     * @param y you seriously should understand it
     * @return amount of flags around
     */
    private int countFlagsAround(GameState state, boolean[][] array, int x, int y) {
        int mines = 0;

        if (y > 0) {
            if (x > 0 && array[x - 1][y - 1]) mines++; // top ■□□
            if (array[x][y - 1]) mines++;   // top □■□
            if (x < array.length - 1 && array[x + 1][y - 1]) mines++; // top □□■
        }

        if (x > 0 && array[x - 1][y]) mines++; // middle ■□□
        if (x < array.length - 1 && array[x + 1][y]) mines++; // middle □□■

        if (y < array[0].length - 1) {
            if (x > 0 && array[x - 1][y + 1]) mines++; // bottom ■□□
            if (array[x][y + 1]) mines++;   // bottom □■□
            if (x < array.length - 1 && array[x + 1][y + 1]) mines++; // bottom □□■
        }

        return mines;
    }

    /**
     * Tank solver
     * By LuckyToilet: https://luckytoilet.wordpress.com/2012/12/23/2125/
     *
     * TANK solver: slow and heavyweight backtrack solver designed to
     * solve any conceivable position!
     */
    private Move tankSolver(GameState state) {
        
        Cell[][] tankBoard = null;
        boolean[][] knownMine = null;
        boolean[][] knownEmpty = null;
        ArrayList<boolean[]> tankSolutions;
        
        // Should be true -- if false, we're brute forcing the endgame
        boolean borderOptimization;

        // Timing

        ArrayList<Point> borderBlocks = new ArrayList<>();
        ArrayList<Point> allEmptyBlocks = new ArrayList<>();

        // Endgame case: if there are few enough tiles, don't bother with border tiles.
        borderOptimization = false;
        for (int x = 0; x < state.board().getSize().width(); x++)
            for (int y = 0; y < state.board().getSize().height(); y++)
                if (state.board().get(x, y).state() == CellState.UNKNOWN && state.board().get(x, y).state() == CellState.FLAGGED) allEmptyBlocks.add(new Point(x, y));

        // Determine all border tiles
        for (int x = 0; x < state.board().getSize().width(); x++)
            for (int y = 0; y < state.board().getSize().height(); y++)
                if (isBoundary(state, x, y) && !(state.board().get(x, y).state() == CellState.FLAGGED)) borderBlocks.add(new Point(x, y));

        // Count how many blocks outside the knowable range
        int countBlocksOutOfRange = allEmptyBlocks.size() - borderBlocks.size();
        if (countBlocksOutOfRange > 8) { // 8 = brute force limit
            borderOptimization = true;
        } else borderBlocks = allEmptyBlocks;


        // Something went wrong
        if (borderBlocks.isEmpty()) return null;


        // Run the segregation routine before recursing one by one
        // Don't bother if it's endgame as doing so might make it miss some cases
        ArrayList<ArrayList<Point>> segregated;
        if (!borderOptimization) {
            segregated = new ArrayList<>();
            segregated.add(borderBlocks);
        } else segregated = tankSegregate(state, borderBlocks);
        
        double propBest = 0; // Store information about the best probability
        int totalMultiCases = 1,
                propBestBlock = -1,
                probBestS = -1;
        for (int currentBlockId = 0; currentBlockId < segregated.size(); currentBlockId++) {

            // Copy everything into temporary constructs
            tankSolutions = new ArrayList<>();
            var field = toArray(state.board());
            tankBoard = field.clone();

            knownMine = new boolean[state.board().getSize().width()][state.board().getSize().height()];
            for (int x = 0; x < state.board().getSize().width(); x++) {
                for (int y = 0; y < state.board().getSize().height(); y++) {
                    knownMine[x][y] = state.board().get(x, y).state() == CellState.FLAGGED;
                    
                }
            }

            knownEmpty = new boolean[state.board().getSize().width()][state.board().getSize().height()];
            for (int x = 0; x < state.board().getSize().width(); x++) {
                for (int y = 0; y < state.board().getSize().height(); y++) {
                    knownEmpty[x][y] = tankBoard[x][y].type() instanceof CellType.Safe(var number) && number == 0;
                }
            }

            // Compute solutions -- here's the time consuming step
            tankRecurse(state, segregated.get(currentBlockId), 0, tankBoard, knownMine, knownEmpty, tankSolutions, borderOptimization);

            // Something screwed up
            if (tankSolutions.isEmpty()) return null;


            // Check for solved squares
            for (int i = 0; i < segregated.get(currentBlockId).size(); i++) {
                boolean allMine = true,
                        allEmpty = true;
                for (boolean[] sln : tankSolutions) {
                    if (!sln[i]) allMine = false;
                    if (sln[i]) allEmpty = false;
                }

                Point block = segregated.get(currentBlockId).get(i);

                if (allMine)
                    return new Move(block, Move.Click.RIGHT);
                else if (allEmpty) {
                    return new Move(block, Move.Click.LEFT);
                }
            }

            totalMultiCases *= tankSolutions.size();

        }
        return null;
    }

    /**
     * Segregation routine: if two regions are independent then consider them as separate regions
     *
     * @param borderBlocks the blocks to check
     * @return the separated regions
     */
    private ArrayList<ArrayList<Point>> tankSegregate(GameState state, ArrayList<Point> borderBlocks) {
        
        var size = state.board().getSize();

        ArrayList<ArrayList<Point>> allRegions = new ArrayList<>();
        ArrayList<Point> covered = new ArrayList<>();

        while (true) {

            LinkedList<Point> queue = new LinkedList<>();
            ArrayList<Point> finishedRegion = new ArrayList<>();

            // Find a suitable starting point
            for (Point firstB : borderBlocks) {
                if (!covered.contains(firstB)) {
                    queue.add(firstB);
                    break;
                }
            }

            if (queue.isEmpty()) break;

            while (!queue.isEmpty()) {

                Point block = queue.poll();
                finishedRegion.add(block);
                covered.add(block);

                // Find all connecting blocks
                for (Point compareBlock : borderBlocks) {

                    boolean isConnected = false;

                    if (finishedRegion.contains(compareBlock)) continue;

                    if (Math.abs(block.x() - compareBlock.x()) > 2 || Math.abs(block.y() - compareBlock.y()) > 2) isConnected = false;
                    else {
                        // Perform a search on all the blocks
                        blockSearch: for (int x = 0; x < size.width(); x++) {
                            for (int y = 0; y < size.height(); y++) {
                                if (state.board().get(x, y).type() instanceof CellType.Safe(var number) && number > 0) {
                                    if (Math.abs(block.x() - x) <= 1 && Math.abs(block.y() - y) <= 1 && Math.abs(compareBlock.x() - x) <= 1 && Math.abs(compareBlock.y() - y) <= 1) {
                                        isConnected = true;
                                        break blockSearch;
                                    }
                                }
                            }
                        }
                    }

                    if (!isConnected) continue;
                    if (!queue.contains(compareBlock)) queue.add(compareBlock);

                }
            }

            allRegions.add(finishedRegion);

        }

        return allRegions;

    }

    /**
     * Recurse from depth (0 is root)
     * Assumes the tank variables are already set; puts solutions in the arraylist.
     * @param borderTiles the region to analyze
     * @param depth which depth lvl we're in
     */
     void tankRecurse(GameState state, ArrayList<Point> borderTiles, int depth, Cell[][] tankBoard, boolean[][] knownMine, boolean[][] knownEmpty, ArrayList<boolean[]> tankSolutions, boolean borderOptimization) {
//         System.out.println("recurse");
        var size = state.board().getSize();
        
        // Return if at this point, it's already inconsistent
        int flagCount = 0;
        for (int x = 0; x < size.width(); x++)
            for (int y = 0; y < size.height(); y++) {

                // Count flags for endgame cases
                if (knownMine[x][y]) flagCount++;
                
                int currentBlockValue = toNumber(tankBoard[x][y]);
                if (currentBlockValue < 0) continue;

                // Scenario 1: too many mines
                if (countFlagsAround(state, knownMine, x, y) > currentBlockValue) return;

                // Total bordering blocks
                int countBorderingBlocks;
                if ((x == 0 && y == 0) || (x == state.board().getSize().width() - 1 && y == state.board().getSize().height() - 1)) countBorderingBlocks = 3;
                else if (x == 0 || y == 0 || x == state.board().getSize().width() - 1 || y == state.board().getSize().height() - 1) countBorderingBlocks = 5;
                else countBorderingBlocks = 8;

                // Scenario 2: too many empty
                if (countBorderingBlocks - countFlagsAround(state, knownEmpty, x, y) < currentBlockValue) return;
            }

        // We have too many flags
        if (flagCount > state.remainingMines()) return;


        // Solution found!
        if (depth == borderTiles.size()) {

            // We don't have the exact mine count, so no
            if (!borderOptimization && flagCount < state.remainingMines()) return;

            boolean[] solution = new boolean[borderTiles.size()];
            for (int i = 0; i < borderTiles.size(); i++) {
                Point block = borderTiles.get(i);
                solution[i] = knownMine[block.x()][block.y()];
            }
            tankSolutions.add(solution);
            return;
        }

        Point block = borderTiles.get(depth);

        // Recurse two positions: mine and no mine
        knownMine[block.x()][block.y()] = true;
        tankRecurse(state, borderTiles, depth + 1, tankBoard, knownMine, knownMine, tankSolutions, borderOptimization);
        knownMine[block.x()][block.y()] = false;

        knownEmpty[block.x()][block.y()] = true;
        tankRecurse(state, borderTiles, depth + 1, tankBoard, knownMine, knownMine, tankSolutions, borderOptimization);
        knownEmpty[block.x()][block.y()] = false;

    }

    static Cell[][] toArray(Board board) {
        var size = board.getSize();
        var cells = new Cell[size.width()][size.height()];
        for (int y = 0; y < size.height(); y++)
            for (int x = 0; x < size.width(); x++)
                cells[x][y] = board.get(x, y);
        
        return cells;
    }

    static int toNumber(Cell cell) {
        return switch (cell.type()) {
            case CellType.Safe(var number) -> number;
            case CellType.Mine ignored -> -3;
            case CellType.Unknown ignored -> switch (cell.state()) {
                case FLAGGED -> -1;
                case UNKNOWN -> -2;
                case REVEALED -> throw new IllegalArgumentException();
            };
        };
//        return switch (cell) {
//            case Cell.ExplodedMine ignored -> -3;
//            case Cell.Unknown ignored -> -2;
//            case Cell.MarkedMine ignored -> -1;
//            case Cell.Revealed(var number) -> number;
//            default -> throw new IllegalArgumentException();
//        };
    }
    
}
