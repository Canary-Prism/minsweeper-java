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

public record Move(Point point, Click action, Optional<Reason> reason) {
    public Move(Point point, Click action) {
        this(point, action, Optional.empty());
    }
    public Move(int x, int y, Click action) {
        this(new Point(x, y), action, Optional.empty());
    }
    public Move(int x, int y, Click action, Reason reason) {
        this(new Point(x, y), action, Optional.of(reason));
    }
    public record Point(int x, int y) {
    
    }
    public enum Click {
        LEFT, RIGHT
    }
}
