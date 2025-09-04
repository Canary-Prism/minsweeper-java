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

public class Board extends ArrayList<ArrayList<Cell>> {
    
    public Board() {
        super();
    }
    
    public Board(int width, int height) {
        super();
        
        var row = Stream.generate(() -> new Cell.Unknown(0))
                .limit(width)
                .collect(Collectors.toCollection(ArrayList<Cell>::new));
        
        Stream.generate(() -> new ArrayList<>(row))
                .limit(height)
                .forEach(this::add);
    }
    
    /// Gets a cell from the board in a way that is easier to read
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    /// @return the cell at the given coordinates
    public Cell get(int x, int y) {
        return this.get(y).get(x);
    }
    
    void set(int x, int y, Cell cell) {
        this.get(y).set(x, cell);
    }
    
    Board hideMines() {
        return this.stream()
                .map((row) -> row.stream()
                        .map((cell) -> switch (cell) {
                            case Cell.Mine ignored -> new Cell.Unknown(0);
                            case Cell.Unknown ignored -> new Cell.Unknown(0);
                            case Cell.FalseMine ignored -> Cell.MarkedMine.INSTANCE;
                            default -> cell;
                        })
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(Board::new));
    }
    
    boolean hasWon() {
        return this.stream()
                .flatMap(ArrayList::stream)
                .filter((cell) -> cell instanceof Cell.Unknown || cell instanceof Cell.FalseMine)
                .findAny()
                .isEmpty();
    }
    
    @SuppressWarnings({ "unchecked", "MethodDoesntCallSuperMethod" })
    @Override
    public Board clone() {
        return this.stream()
                .map((e) -> ((ArrayList<Cell>) e.clone()))
                .collect(Collectors.toCollection(() -> new Board(size, null)));
    }
}
