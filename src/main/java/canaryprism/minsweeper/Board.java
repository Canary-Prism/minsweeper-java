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

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Represents the board of a [Minsweeper] game
///
/// A board holds [Cell]s and can be [clone][#clone()]ed
public class Board extends ArrayList<ArrayList<Cell>> {
    
    private final BoardSize size;
    
    private Board(BoardSize size, Void ignored) {
        super();
        this.size = size;
    }
    
    /// Constructs a Board with a given size and cell to fill with
    ///
    /// @param size the size of the board
    /// @param fill the [Cell] to fill the board with
    public Board(BoardSize size, Cell fill) {
        this(size, ((Void) null));
        
        var width = size.width();
        var height = size.height();
        
        var row = Stream.generate(() -> fill)
                .limit(width)
                .collect(Collectors.toCollection(ArrayList<Cell>::new));
        
        Stream.generate(() -> new ArrayList<>(row))
                .limit(height)
                .forEach(this::add);
    }
    /// Constructs a Board with a given size
    ///
    /// The board is filled with [Cell]s with type [CellType.Safe(0)][CellType.Safe#EMPTY]
    /// and state [CellState#UNKNOWN]
    ///
    /// @param size the size of the board
    public Board(BoardSize size) {
        this(size, new Cell(CellType.Safe.EMPTY, CellState.UNKNOWN));
    }
    
    /// Gets the [BoardSize] of the Board
    ///
    /// @return the board's size
    public BoardSize getSize() {
        return size;
    }
    
    /// Gets a cell from the board in a way that is easier to read
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the cell at the given coordinates
    public Cell get(int x, int y) {
        return this.get(y).get(x);
    }
    
    public void set(int x, int y, Cell cell) {
        this.get(y).set(x, cell);
    }
    
    Board hideMines() {
        return this.stream()
                .map((row) -> row.stream()
                        .map((cell) -> (cell.state() != CellState.REVEALED) ? new Cell(CellType.UNKNOWN, cell.state()) : cell)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(() -> new Board(size, ((Void) null))));
    }
    
    boolean hasWon() {
        return this.stream()
                .flatMap(ArrayList::stream)
                .noneMatch((cell) -> (cell.type() == CellType.MINE && cell.state() == CellState.REVEALED)
                        || (cell.type() instanceof CellType.Safe && cell.state() != CellState.REVEALED));
    }
    
    @SuppressWarnings({ "unchecked", "MethodDoesntCallSuperMethod" })
    @Override
    public Board clone() {
        return this.stream()
                .map((e) -> ((ArrayList<Cell>) e.clone()))
                .collect(Collectors.toCollection(() -> new Board(size, ((Void) null))));
    }
}
