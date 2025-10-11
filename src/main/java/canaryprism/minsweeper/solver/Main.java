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

import canaryprism.minsweeper.*;
import canaryprism.minsweeper.solver.impl.MiaSolver;
import canaryprism.minsweeper.solver.impl.patrickstillhart.MineSweeperSolver;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    
    public static void main(String[] args) {
//        mewo();
//        System.exit(0);
        System.out.println("mewo");
        var start = System.nanoTime();
        var solver = new MiaSolver();
        var generator = new MineSweeperSolver();
        final var total = 100;
        var successes = new AtomicInteger();
        var losses = new AtomicInteger();
        var size = ConventionalSize.EXPERT.size;
        
        try (var pool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism())) {
            for (int i = 0; i < total; i++) {
                pool.execute(ForkJoinTask.adapt(() -> {
                    var game = new MinsweeperGame(size);
                    game.start(generator);
                    var state = game.leftClick(size.width() / 2, size.height() / 2);
                    
                    var result = solver.solve(game, state);
                    
                    if (result == Solver.Result.WON)
                        successes.incrementAndGet();
                    else if (result == Solver.Result.LOST)
                        losses.incrementAndGet();
                }));
            }
        }
        
        
        
        System.out.printf("%8d successes\n", successes.get());
        System.out.printf("%8d losses\n", losses.get());
        System.out.printf("%8d total\n", total);
        System.out.printf("%10d time elapsed", System.nanoTime() - start);
    }
    
    private static void mewo() {
        var solver = new MiaSolver();
        var size = new BoardSize(5, 4, 4);
        var board = new Board(size);
        board.clear();
        board.add(parse(' ', '1', 'O', 'O', 'O'));
        board.add(parse('1', '2', 'O', 'O', 'O'));
        board.add(parse('1', '!', '3', '!', '!'));
        board.add(parse('1', '1', '2', '2', '2'));
        var state = new GameState(GameStatus.PLAYING, board, 1);
        board.forEach(System.out::println);
        
        var move = solver.solve(state);
        System.out.println(move);
    }
    private static ArrayList<Cell> parse(char... chars) {
        var list = new ArrayList<Cell>();
        for (var e : chars) {
            if ('1' <= e && e <= '8')
                list.add(new Cell(new CellType.Safe(e - '0'), CellState.REVEALED));
            switch (e) {
                case '!' -> list.add(new Cell(CellType.UNKNOWN, CellState.FLAGGED));
                case ' ' -> list.add(new Cell(CellType.Safe.EMPTY, CellState.REVEALED));
                case 'O' -> list.add(new Cell(CellType.UNKNOWN, CellState.UNKNOWN));
            }
        }
        return list;
    }
}
