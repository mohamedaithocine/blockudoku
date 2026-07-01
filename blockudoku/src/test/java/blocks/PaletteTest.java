package blocks;

import blocks.BlockShapes.PixelLoc;
import blocks.BlockShapes.Shape;
import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.SpriteState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaletteTest {

    private Palette palette;

    @BeforeEach
    void setUp() {
        palette = new Palette();
    }

    @Test
    void testReplenishAddsSprites() {
        palette.replenish();
        List<Sprite> sprites = palette.getSprites();
        assertEquals(3, sprites.size(), "Palette should have exactly 3 sprites after replenishment.");
    }

    @Test
    void testReplenishDoesNotReplaceIfReadyPiecesExist() {
        palette.replenish();
        int initialSize = palette.getSprites().size();
        assertEquals(3, initialSize, "Initially replenished palette should have 3 sprites.");
        palette.getSprites().get(0).state = SpriteState.IN_PLAY; // Simulate one piece in play
        palette.replenish();
        assertEquals(initialSize, palette.getSprites().size(), "Replenish should not replace sprites if ready pieces exist.");
    }

    @Test
    void testGetShapesToPlace() {
        palette.replenish();
        ArrayList<Shape> shapesToPlace = palette.getShapesToPlace();
        assertEquals(3, shapesToPlace.size(), "Shapes to place should match the number of sprites in the palette.");
        assertTrue(shapesToPlace.stream().allMatch(shape -> palette.getShapes().contains(shape)),
                "Shapes to place should be part of the available shapes.");
    }

    @Test
    void testGetSpriteAtLocation() {
        palette.replenish();
        int cellSize = 20;
        palette.doLayout(0, 0, cellSize);

        // Retrieve a sprite using a PixelLoc within the bounds of the first sprite
        Sprite firstSprite = palette.getSprites().get(0);
        PixelLoc pixelLocWithin = new PixelLoc(firstSprite.px + 5, firstSprite.py + 5);

        Sprite foundSprite = palette.getSprite(pixelLocWithin, cellSize);
        assertNotNull(foundSprite, "Should find a sprite at the given pixel location.");
        assertEquals(firstSprite, foundSprite, "Found sprite should match the expected sprite.");
    }

    @Test
    void testGetSpriteAtInvalidLocationReturnsNull() {
        palette.replenish();
        int cellSize = 20;
        palette.doLayout(0, 0, cellSize);

        // Use a PixelLoc outside any sprite's bounds
        PixelLoc invalidPixelLoc = new PixelLoc(1000, 1000);

        Sprite foundSprite = palette.getSprite(invalidPixelLoc, cellSize);
        assertNull(foundSprite, "No sprite should be found at an invalid location.");
    }

    @Test
    void testDoLayoutPositionsSpritesCorrectly() {
        palette.replenish();
        int cellSize = 20;
        palette.doLayout(10, 10, cellSize);

        // Verify that sprites have been positioned based on the layout logic
        Sprite firstSprite = palette.getSprites().get(0);
        assertEquals(10, firstSprite.px, "First sprite x-coordinate should be calculated correctly.");
        assertEquals(10, firstSprite.py, "First sprite y-coordinate should be calculated correctly.");

        if (palette.getSprites().size() > 1) {
            Sprite secondSprite = palette.getSprites().get(1);
            assertTrue(secondSprite.px > firstSprite.px,
                    "Second sprite should be positioned to the right of the first.");
        }
    }

    @Test
    void testInitialShapesAndSprites() {
        ArrayList<Shape> shapes = palette.getShapes();
        assertFalse(shapes.isEmpty(), "Palette should have initial shapes available.");
        assertEquals(3, palette.getSprites().size(), "Palette should initially contain 3 sprites.");
    }
}
