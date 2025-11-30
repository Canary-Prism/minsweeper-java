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

package canaryprism.minsweeper.solver;

import java.util.Optional;
import java.util.Set;

/// A Move is a collection of [Click]s to perform on a [canaryprism.minsweeper.Minsweeper] game
///
/// It can also contain an optional [Reason] that explains the reasoning behind the Move
///
/// @param clicks the [Click]s to perform on the game
/// @param reason the optional [Reason] the move can be performed
public record Move(Set<Click> clicks, Optional<Reason> reason) {
    public Move(Set<Click> clicks, Reason reason) {
        this(clicks, Optional.of(reason));
    }
    public Move(Point point, Action action) {
        this(Set.of(new Click(point, action)), Optional.empty());
    }
    public Move(Point point, Action action, Optional<Reason> reason) {
        this(Set.of(new Click(point, action)), reason);
    }
    public Move(Point point, Action action, Reason reason) {
        this(point, action, Optional.of(reason));
    }
    public Move(int x, int y, Action action) {
        this(Set.of(new Click(x, y, action)), Optional.empty());
    }
    public Move(int x, int y, Action action, Reason reason) {
        this(Set.of(new Click(x, y, action)), Optional.of(reason));
    }
    
    /// A single click to be performed on a [canaryprism.minsweeper.Minsweeper] game
    ///
    /// @param point the coordinates to perform the click
    /// @param action the action to use for the click
    public record Click(Point point, Action action) {
        public Click(int x, int y, Action action) {
            this(new Point(x, y), action);
        }
    }
    /// Coordinate for [Click]s
    ///
    /// @param x the x coordinate
    /// @param y the y coordinate
    public record Point(int x, int y) {
    
    }
    /// Action to use for a [Click]
    public enum Action {
        /// [Left click][canaryprism.minsweeper.Minsweeper#leftClick(int, int)]
        LEFT,
        /// [Right click][canaryprism.minsweeper.Minsweeper#rightClick(int, int)]
        RIGHT
    }
}
