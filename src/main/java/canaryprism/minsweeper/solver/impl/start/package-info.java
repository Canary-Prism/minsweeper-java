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

/// This package includes "implementations" of the [canaryprism.minsweeper.solver.Solver] api
///
/// In reality these implementations simply abuse the way the api works
/// in order to affect the first move of [canaryprism.minsweeper.MinsweeperGame] generated games,
/// they do not create ensure no-guessing games
///
/// All implementations' [canaryprism.minsweeper.solver.Solver#solve(canaryprism.minsweeper.GameState)]
/// methods return `null`
///
/// Implementations:
/// - [SafeStart][canaryprism.minsweeper.solver.impl.start.SafeStart]
///   ensures the first move is [safe][canaryprism.minsweeper.CellType.Safe]
/// - [ZeroStart][canaryprism.minsweeper.solver.impl.start.ZeroStart]
///   ensures the first move is [empty][canaryprism.minsweeper.CellType.Safe#EMPTY]
///
package canaryprism.minsweeper.solver.impl.start;
