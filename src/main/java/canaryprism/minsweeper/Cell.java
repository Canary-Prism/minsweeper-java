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

public sealed interface Cell {
    
    record Revealed(int number) implements Cell {
        
        @Override
        public String toString() {
            return (number == 0) ? " " : String.valueOf(number);
        }
    }
    record Unknown(int number) implements Cell {
        
        @Override
        public String toString() {
            return (number == 0) ? "O" : String.valueOf(number);
        }
    }
    
    enum MarkedMine implements Cell {
        INSTANCE;
        
        @Override
        public String toString() {
            return "!";
        }
    }
    
    enum Mine implements Cell {
        INSTANCE;
        
        @Override
        public String toString() {
            return "*";
        }
    }
    
    record FalseMine(int value) implements Cell {
        
        @Override
        public String toString() {
            return "Ø";
        }
    }
    
    enum ExplodedMine implements Cell {
        INSTANCE;
        
        @Override
        public String toString() {
            return "X";
        }
    }
}
