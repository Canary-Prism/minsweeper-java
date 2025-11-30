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

/// The status of the game
///
/// Just like whether it's still ongoing or won or lost or whatever idk
///
/// @see GameState
public enum GameStatus {
    
    /// Minsweeper game is still ongoing
    PLAYING,
    
    /// Minsweeper game has been won
    WON,
    
    /// Minsweeper game has been lost
    LOST,
    
    /// Minsweeper game hasn't been started yet
    NEVER
}
