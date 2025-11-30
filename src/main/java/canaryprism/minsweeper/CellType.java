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

/// The type of [Cell]
///
/// This represents the actual value of the cell, either being [Safe] or [Mine]
///
/// Hovever, [Unknown] is also here in order for [Minsweeper] implementations
/// like [AbstractHidingMinsweeper] to be able to hide information from users
public sealed interface CellType {
    
    /// A Safe [Cell]
    ///
    /// Safe cells contain a number that represents the amount of mines surrounding it
    ///
    /// @param number the amount of mines surrounding the cell
    record Safe(int number) implements CellType {
        public static final Safe EMPTY = new Safe(0);
    }
    
    Mine MINE = Mine.INSTANCE;
    Unknown UNKNOWN = Unknown.INSTANCE;
    
    /// A Mine [Cell]
    ///
    /// If a player reveals a [Cell] with type Mine the game is lost
    enum Mine implements CellType {
        INSTANCE
    }
    
    /// An Unknown [Cell]
    ///
    /// Only [GameState]s given to the player may use this CellType,
    /// as its only use is preventing players from knowing the true type of [Cell]s
    /// without revealing them first
    enum Unknown implements CellType {
        INSTANCE
    }
}
