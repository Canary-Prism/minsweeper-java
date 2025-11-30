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

/// Represents conventional [BoardSize]s common between minesweeper implementations
///
/// These include the [#BEGINNER] [#INTERMEDIATE] and [#EXPERT] sizes.
public enum ConventionalSize {
    
    /// The Beginner size in conventional minesweeper implementations
    BEGINNER(new BoardSize(9, 9, 10)),
    
    /// The Intermediate size in conventional minesweeper implementations
    INTERMEDIATE(new BoardSize(16, 16, 40)),
    
    /// The Expert size in conventional minesweeper implementations
    EXPERT(new BoardSize(30, 16, 99))
    ;
    
    /// The [BoardSize] the ConventionalSize represents
    public final BoardSize size;
    
    ConventionalSize(BoardSize size) {
        this.size = size;
    }
}
