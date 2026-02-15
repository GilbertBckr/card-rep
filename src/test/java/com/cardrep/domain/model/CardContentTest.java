package com.cardrep.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CardContent value object.
 */
class CardContentTest {

    @Test
    void create_withValidText_shouldSucceed() {
        CardContent content = new CardContent("Hello World");

        assertEquals("Hello World", content.getText());
        assertNull(content.getImagePath());
        assertFalse(content.hasImage());
    }

    @Test
    void create_withTextAndImage_shouldSucceed() {
        CardContent content = new CardContent("Hello", "/path/to/image.png");

        assertEquals("Hello", content.getText());
        assertEquals("/path/to/image.png", content.getImagePath());
        assertTrue(content.hasImage());
    }

    @Test
    void create_withNullText_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new CardContent(null));
    }

    @Test
    void create_withBlankText_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new CardContent("   "));
    }

    @Test
    void equals_sameValues_shouldBeEqual() {
        CardContent c1 = new CardContent("Text", "/img.png");
        CardContent c2 = new CardContent("Text", "/img.png");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void equals_differentValues_shouldNotBeEqual() {
        CardContent c1 = new CardContent("Text A");
        CardContent c2 = new CardContent("Text B");

        assertNotEquals(c1, c2);
    }

    @Test
    void toString_withImage_shouldIncludeImageInfo() {
        CardContent content = new CardContent("Text", "/img.png");

        assertTrue(content.toString().contains("[Image:"));
    }

    @Test
    void toString_withoutImage_shouldShowTextOnly() {
        CardContent content = new CardContent("Just text");

        assertEquals("Just text", content.toString());
    }
}
