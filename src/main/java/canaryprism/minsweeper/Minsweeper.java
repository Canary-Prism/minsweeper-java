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

package canaryprism.minsweeper;

public interface Minsweeper {
    
    GameState start();

    GameState getGameState();
    
    GameState reveal(int x, int y);
    
    GameState clearAround(int x, int y);
    
    GameState setFlagged(int x, int y, boolean flagged);
    
    default GameState toggleFlag(int x, int y) {
        return setFlagged(x, y, getGameState().board().get(x, y).state() != CellState.FLAGGED);
    }
    
    default GameState leftClick(int x, int y) {
        if (getGameState().status() != GameStatus.PLAYING) return getGameState();
        if (!(x >= 0 && x < getGameState().board().getSize().width() && y >= 0 && y < getGameState().board().getSize().height()))
            return getGameState();
        
        var cell = getGameState().board().get(x, y);
        
        if (cell.type() instanceof CellType.Safe && cell.state() == CellState.REVEALED)
            return clearAround(x, y);
        if (cell.state() == CellState.UNKNOWN)
            return reveal(x, y);
        return getGameState();
    }
    
    default GameState rightClick(int x, int y) {
        return toggleFlag(x, y);
    }
}
