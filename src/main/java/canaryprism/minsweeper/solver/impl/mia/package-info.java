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

/// ## Mia's various impelemntations of [canaryprism.minsweeper.solver.Solver] :3
///
/// The main solver is [canaryprism.minsweeper.solver.impl.mia.MiaSolver]
/// which i plan to update as i find more improvements to make to the solver
///
/// MiaSolver supports [Reason][canaryprism.minsweeper.solver.Reason]s which
/// explain the reasoning behind each move that it makes
///
/// [Logic][canaryprism.minsweeper.solver.Logic] for the reasoning is implemented by
/// [canaryprism.minsweeper.solver.impl.mia.MiaLogic]
///
/// Various parts of MiaSolver are then isolated and made into the other solvers with distinct skill levels
/// - [BeginnerSolver][canaryprism.minsweeper.solver.impl.mia.BeginnerSolver]
///   is the lowest level solver, only being able to make trivial moves
/// - [IntermediateSolver][canaryprism.minsweeper.solver.impl.mia.IntermediateSolver]
///   is the next level of solver, being able to do logical deductions
/// - [ExpertSolver][canaryprism.minsweeper.solver.impl.mia.ExpertSolver]
///   is the highest level of solver, being able to brute force if necessary.
///   it may or may not be identical to MiaSolver in terms of skill level
///   depending on how i update MiaSolver and the rest
///
/// Additionally, there are solvers that will intentionally
/// [resign][canaryprism.minsweeper.solver.Solver.Result#RESIGNED] games if they are too easy
/// - [IntermediateOnlySolver][canaryprism.minsweeper.solver.impl.mia.IntermediateOnlySolver]
///   has the same skill level as IntermediateSolver but will intentionally resign
///   unless BeginnerSolver couldn't also solve the game
/// - [ExpertOnlySolver][canaryprism.minsweeper.solver.impl.mia.ExpertOnlySolver]
///   has the same skill level as ExpertSolver but will intentionally resign
///   unless IntermediateSolver couldn't also solve the game
package canaryprism.minsweeper.solver.impl.mia;