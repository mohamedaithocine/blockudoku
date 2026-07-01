package blocks;

import blocks.BlockShapes.Cell;
import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelSet extends StateSet implements ModelInterface {

    Set<Cell> locations = new HashSet<>();
    List<Shape> regions = new RegionHelper().allRegions();

    // we need a constructor to initialise the regions
    public ModelSet() {
        super();
        initialiseLocations();
    }
    // method implementations below ...

    public int getScore() {
        return score;
    }

    private void initialiseLocations() {
        // having all grid locations in a set is in line with the set based approach
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                locations.add(new Cell(i, j));
            }
        }
    }

    @Override
    public boolean canPlace(Piece piece) {
        // can be placed if the cells are not occupied i.e. not in the occupiedCells set
        // though each one must be within the bounds of the grid
        // use a stream to check if all the cells are not occupied

        return piece.cells().stream()
                .allMatch(cell -> locations.contains(cell) && !getOccupiedCells().contains(cell));
    }

    private int consecutiveStreak = 0;
    @Override
    public void place(Piece piece) {
        // add the cells in the Piece to the occupiedCells set
        // then remove all the poppable regions
        // increment the score as function of the regions popped

        // Add the piece's cells to the occupied set
        getOccupiedCells().addAll(piece.cells());

        // Find all completed regions first
        List<Shape> completedRegions = new ArrayList<>();
        for (Shape region : regions) {
            if (isComplete(region)) {
                completedRegions.add(region);
            }
        }

        if (!completedRegions.isEmpty()) {
            int baseScore = 0;
            for (Shape region : completedRegions) {
                baseScore += region.size();
                remove(region);
            }

            int simultaneousBonus = completedRegions.size() > 1
                    ? (completedRegions.size() - 1) * 10 // Bonus for completing 2+ regions
                    : 0;

            score += baseScore + simultaneousBonus;

            // Apply consecutive streak bonus
            int streakBonus = consecutiveStreak * 5; // Bonus increases with each streak
            score += streakBonus;
            consecutiveStreak++;
        } else {
            // Reset streak if no regions are completed
            consecutiveStreak = 0;
        }
    }

    @Override
    public void remove(Shape region) {
        // remove the cells from the occupiedCells set
        region.forEach(getOccupiedCells()::remove);
    }

    @Override
    public boolean isComplete(Shape region) {
        // use a stream to check if all the cells in the region are occupied
        return getOccupiedCells().containsAll(region);
    }

    @Override
    public boolean isGameOver(List<Shape> palettePieces) {
        // if any shape in the palette can be placed, the game is not over
        // use a helper function to check whether an individual shape can be placed anywhere
        // and
        return palettePieces.stream().noneMatch(this::canPlaceAnywhere);
    }

    public boolean canPlaceAnywhere(Shape shape) {
        // check if the shape can be placed anywhere on the grid
        // by checking if it can be placed at any loc
        return locations.stream()
                .anyMatch(loc -> canPlace(new Piece(shape, loc)));
    }

    @Override
    public List<Shape> getPoppableRegions(Piece piece) {
        // return the regions that would be popped if the piece is placed
        // to do this we need to iterate over the regions and check if the piece overlaps enough to complete it
        // i.e. we can make a new set of occupied cells and check if the region is complete
        // if it is complete, we add it to the list of regions to be popped

        // Simulate the placement of the piece
        Set<Cell> simulatedOccupied = new HashSet<>(getOccupiedCells());
        simulatedOccupied.addAll(piece.cells());

        // Find complete regions
        List<Shape> poppableRegions = new ArrayList<>();
        for (Shape region : regions) {
            if (simulatedOccupied.containsAll(region)) {
                poppableRegions.add(region);
            }
        }
        return poppableRegions;

    }

    @Override
    public Set<Cell> getOccupiedCells() {
        return occupiedCells;
    }
}
