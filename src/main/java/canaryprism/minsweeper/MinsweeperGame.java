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

import canaryprism.minsweeper.solver.Solver;

/// Main class of minsweeper game
public final class MinsweeperGame extends AbstractRandomMinsweeper {
    
    private Solver solver;
    
    public MinsweeperGame(BoardSize sizes, Runnable on_win, Runnable on_lose) {
        super(sizes, on_win, on_lose);
    }
    
    public MinsweeperGame(BoardSize sizes) {
        this(sizes, () -> {}, () -> {});
    }
    
    public MinsweeperGame(ConventionalSize size, Runnable on_win, Runnable on_lose) {
        this(size.size, on_win, on_lose);
    }
    
    public MinsweeperGame(ConventionalSize size) {
        this(size.size);
    }
    
    @Override
    public GameState start() {
        return start(null);
    }
    public GameState start(Solver solver) {
        this.solver = solver;
        
        this.gamestate = new GameState(GameStatus.PLAYING, new Board(sizes), sizes.mines());
        
        this.first = true;
        
        return gamestate.hideMines();
    }
    
    private volatile boolean first;
    
    @Override
    public GameState reveal(int x, int y) {
        if (gamestate.status() != GameStatus.PLAYING) return gamestate;
        if (!(x >= 0 && x < sizes.width() && y >= 0 && y < sizes.height())) return gamestate.hideMines();
        if (this.solver != null && this.first) {
            this.first = false;
//            this.gamestate = generateGame(x, y);
            var solver = this.solver;
//            var future = new CompletableFuture<GameState>();
            final var thread_batch = 100;
            final var loop_batch = 1;
//            try (var pool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism())) {
//                solver_loop:
//                while (true) {
//
//                    var futures = new ArrayList<Future<GameState>>(thread_batch);
//                    try {
//
//                        for (int i = 0; i < thread_batch; i++) {
////                    throw new RuntimeException();
//                            futures.add(pool.submit(ForkJoinTask.adapt(() -> {
//                                for (int j = 0; j < loop_batch; j++) {
//                                    var original_state = generateGame();
//                                    var game = new SetMinsweeperGame(original_state.clone());
//                                    var state = game.reveal(x, y);
//                                    var result = solver.solve(game, state);
//
//                                    if (result == Solver.Result.WON) {
////                                        System.out.println("solution");
//                                        return original_state;
////                                        this.gamestate = original_state;
////                                    break solver_loop;
//                                    }
//                                }
//                                return null;
//                            })));
//                        }
//
//                        for (var future : futures) {
//                            try {
//                                var result = future.get();
//                                if (result != null) {
//                                    System.out.println("solution");
//                                    this.gamestate = result;
//                                    break solver_loop;
//                                }
//                            } catch (InterruptedException | ExecutionException | IllegalStateException ignored) {}
//                        }
//                    } finally {
//                        futures.forEach(future -> future.cancel(true));
//                    }
//
//                }
//            }
            
            while (true) {
                var original_state = generateGame();
                var game = new SetMinsweeperGame(original_state.clone());
                var state = game.reveal(x, y);
                var result = solver.solve(game, state);
                
                if (result == Solver.Result.WON) {
                    this.gamestate = original_state;
//                                        this.gamestate = original_state;
                    break;
                }
            }
        }
        
        return super.reveal(x, y);
    }
    
    @Override
    public GameState setFlagged(int x, int y, boolean flagged) {
        if (first && solver != null)
            return getGameState();
        return super.setFlagged(x, y, flagged);
    }
}
