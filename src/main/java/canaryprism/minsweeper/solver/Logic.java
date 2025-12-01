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

/// The specific logic used in the [Reason]
///
/// Not really sure what to do with it but u can get the [description][#getDescription()] of the Logic
/// for hints and stuff
public interface Logic {
    /// Gets the description of the Logic
    ///
    /// @return the description
    /// @implSpec Implementations should return a human readable description for why a [Move] can be made.
    ///           There's no real rule for how specific the description has to be, but it doesn't have to
    ///           be specific to each specific [Move] and can just be tied to say a specific part of the
    ///           [Solver]'s code that creates the Move
    String getDescription();
}
