package blocks;


import blocks.BlockShapes.Shape;
import blocks.BlockShapes.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class GameView extends JComponent {
    ModelInterface model;
    Palette palette;
    static int margin = 0;
    int shapeRegionHeight;
    static int cellSize = 40;
    static int shrinkSize = 20;
    Piece ghostShape = null;
    List<Shape> poppableRegions = null;

    public GameView(ModelInterface model, Palette palette) {
        this.model = model;
        this.palette = palette;
        this.shapeRegionHeight = cellSize * ModelInterface.height / 2;
    }

    private void paintShapePalette(Graphics g, int cellSize) {
        // paint a background colour
        // then get the list of current shapes from the palette
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(margin, margin + ModelInterface.height * cellSize, ModelInterface.width * cellSize, shapeRegionHeight);
        for (Sprite sprite : palette.getSprites()) {
            if (sprite.state == SpriteState.IN_PALETTE) {
                for (Cell cell : sprite.shape) {
                    int x = sprite.px + cell.x() * shrinkSize;
                    int y = sprite.py + cell.y() * shrinkSize;
                    g.setColor(Color.BLACK); // Add a border to each cell
                    g.drawRect(x, y, shrinkSize, shrinkSize);
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, shrinkSize, shrinkSize);
                }
            }
        }
    }

    private void paintPoppableRegions(Graphics g, int cellSize) {
        if (poppableRegions != null) {
            g.setColor(new Color(255, 0, 0, 128)); // Semi-transparent green
            for (Shape region : poppableRegions) {
                for (Cell cell : region) {
                    int x = margin + cell.x() * cellSize;
                    int y = margin + cell.y() * cellSize;
                    g.fillRect(x, y, cellSize, cellSize);
                }
            }
        }
    }

    private void paintGhostShape(Graphics g, int cellSize) {
        if (ghostShape == null) return;

        for (Cell cell : ghostShape.cells()) {
            // Calculate consistent x and y coordinates for each cell
            int x = margin + cell.x() * cellSize;
            int y = margin + cell.y() * cellSize;

            // Draw the semi-transparent base (ghost shape)
            g.setColor(new Color(117, 251, 253));
            g.fillRect(x, y, cellSize, cellSize);



            // Optional: Add a smaller inner fill for a "shrink" effect
            int shrinkOffset = (cellSize - shrinkSize) / 2; // Center the "shrink"
            int innerX = x + shrinkOffset;
            int innerY = y + shrinkOffset;

            if (shrinkSize > 0 && shrinkSize < cellSize) {
                g.setColor(Color.BLUE);
                g.fillRect(innerX, innerY, shrinkSize, shrinkSize);
            }
        }
    }

    private void paintGrid(Graphics2D g) {
        // for now, we're going to do this based on the cellSize multiple
        int s = ModelInterface.subSize;
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        for (int x = 0; x < ModelInterface.width; x += s) {
            for (int y = 0; y < ModelInterface.height; y += s) {
                g.drawRect(margin + x * cellSize, margin + y * cellSize, s * cellSize, s * cellSize);
            }
        }
    }


    private void paintMiniGrids(Graphics g) {
        int x0 = margin;
        int y0 = margin;
        int width = ModelInterface.width * cellSize;
        int height = ModelInterface.height * cellSize;
        Set<Cell> occupiedCells = model.getOccupiedCells();
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, width, height);
        for (int x = 0; x < ModelInterface.width; x++) {
            for (int y = 0; y < ModelInterface.height; y++) {
                int cellX = x0 + x * cellSize;
                int cellY = y0 + y * cellSize;

                g.setColor(Color.LIGHT_GRAY); // Add grid cell borders
                g.drawRect(cellX, cellY, cellSize, cellSize);

                if (occupiedCells.contains(new Cell(x, y))) {
                    g.setColor(Color.GREEN); // Occupied cells in blue
                } else {
                    g.setColor(Color.WHITE); // Empty cells in white
                }

                g.fill3DRect(cellX, cellY, cellSize, cellSize, true);

            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintMiniGrids(g); // cosmetic
        paintGrid((Graphics2D) g);
        paintGhostShape(g, cellSize);
        paintPoppableRegions(g, cellSize);
        paintShapePalette(g, cellSize);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                ModelInterface.width * cellSize + 2 * margin,
                ModelInterface.height * cellSize + 2 * margin + shapeRegionHeight
        );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clean Blocks");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModelInterface model = new ModelSet();
        Shape shape = new ShapeSet().getShapes().get(0);
        Piece piece = new Piece(shape, new Cell(0, 0));
        Palette palette = new Palette();
        model.place(piece);
        frame.add(new GameView(model, palette));
        frame.pack();
        frame.setVisible(true);
    }
}
