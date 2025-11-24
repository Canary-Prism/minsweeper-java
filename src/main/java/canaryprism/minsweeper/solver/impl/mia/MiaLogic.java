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

package canaryprism.minsweeper.solver.impl.mia;

import canaryprism.minsweeper.solver.Logic;

public enum MiaLogic implements Logic {
    CHORD("the amount of flags around the cell matches its number"),
    FLAG_CHORD("the amount of flaggable cells around the cell matches its number"),
    REGION_DEDUCTION_REVEAL("the surrounding cells force the cells to be safe"),
    REGION_DEDUCTION_FLAG("the surrounding cells force the cells to be a mine"),
    ZERO_MINES_REMAINING("0 mines remaining, all unknown cells must be safe"),
    BRUTE_FORCE("in every possible mine configuration the cells are safe/mines"),
    BRUTE_FORCE_EXHAUSTION("in every possible mine configuration every mine is determined, all unused cells must be safe"),
    ;
    
    public final String description;
    
    MiaLogic(String description) {
        this.description = description;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
}
