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

/// Represents a [Cell]'s state
///
/// A cell's state is the current perceived state it is in by the player.
/// A cell can either be [#UNKNOWN], [#REVEALED] or [#FLAGGED]
public enum CellState {
    
    /// An unknown cell state is a cell that the player hasn't revealed yet and doesn't have flagged
    ///
    /// [Minsweeper] implementations may choose to hide the Cell's [CellType] too by making it [CellType.Unknown]
    UNKNOWN,
    
    /// A revealed cell state is a cell that the player has revealed
    ///
    /// if a [CellType.Mine] cell has been revealed then the game is lost
    REVEALED,
    
    /// A flagged cell state is a cell that the player hasn't revealed yet and has flagged
    ///
    /// [Minsweeper] implementations may choose to hide the Cell's [CellType] too by making it [CellType.Unknown]
    FLAGGED
}
