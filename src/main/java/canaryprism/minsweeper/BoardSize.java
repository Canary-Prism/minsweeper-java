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

/// Represents a validated size of board and mine count that u can make a [MinsweeperGame] with
///
/// @param width the width of the board
/// @param height the height of the board
/// @param mines the amount of mines in the board
public record BoardSize(int width, int height, int mines) {
    public BoardSize {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Invalid Size");
        if (mines >= width * height)
            throw new IllegalArgumentException("Too Many Mines");
        if (mines <= 0)
            throw new IllegalArgumentException("Too Few Mines");
    }
}
