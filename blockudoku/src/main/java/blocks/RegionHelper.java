package blocks;

import blocks.BlockShapes.Cell;
import blocks.BlockShapes.Shape;

import java.util.ArrayList;

public class RegionHelper {
    int width = ModelInterface.width;
    int height = ModelInterface.height;
    int subSize = ModelInterface.subSize;
    int nRows = height;
    int nCols = width;
    int nSubRows = height / subSize;

    Shape rowShape(int row) {
        Shape shape = new Shape();
        for (int x = 0; x < width; x++) {
            shape.add(new Cell(x, row));
        }
        return shape;
    }

    Shape colShape(int col) {
        Shape shape = new Shape();
        for (int y = 0; y < height; y++) {
            shape.add(new Cell(col, y));
        }
        return shape;
    }

    Shape subSquareShape(int x, int y) {
        Shape shape = new Shape();
        for (int dx = 0; dx < subSize; dx++) {
            for (int dy = 0; dy < subSize; dy++) {
                shape.add(new Cell(x * subSize + dx, y * subSize + dy));
            }
        }
        return shape;
    }

    ArrayList<Shape> rowRegions() {
        ArrayList<Shape> regions = new ArrayList<>();
        for (int row = 0; row < nRows; row++) {
            regions.add(rowShape(row));
        }
        return regions;
    }

    ArrayList<Shape> colRegions() {
        ArrayList<Shape> regions = new ArrayList<>();
        for (int col = 0; col < nCols; col++) {
            regions.add(colShape(col));
        }
        return regions;
    }

    ArrayList<Shape> subSquareRegions() {
        ArrayList<Shape> regions = new ArrayList<>();
        for (int x = 0; x < nSubRows; x++) {
            for (int y = 0; y < nSubRows; y++) {
                regions.add(subSquareShape(x, y));
            }
        }
        return regions;
    }

    ArrayList<Shape> allRegions() {
        ArrayList<Shape> regions = new ArrayList<>();
        regions.addAll(rowRegions());
        regions.addAll(colRegions());
        regions.addAll(subSquareRegions());
        return regions;
    }

    public static void main(String[] args) {
        RegionHelper rh = new RegionHelper();
        for (Shape shape : rh.allRegions()) {
            System.out.println(shape);
        }
    }
}
