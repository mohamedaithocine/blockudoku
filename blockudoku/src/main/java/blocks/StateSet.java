package blocks;

import blocks.BlockShapes.Cell;

import java.util.HashSet;
import java.util.Set;

public class StateSet {
    // a pure model of the grid or game state,
    // stores only the state of the grid and the score
    Set<Cell> occupiedCells = new HashSet<>();
    int score = 0;
}
