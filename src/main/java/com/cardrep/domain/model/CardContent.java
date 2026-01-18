package com.cardrep.domain.model;

import java.util.Objects;

/**
 * The content of a card side. Can consist of text and an optional image path.
 * Value Object - immutable, compared by value.
 */
public class CardContent {

    private final String text;
    private final String imagePath;

    public CardContent(String text) {
        this(text, null);
    }

    public CardContent(String text, String imagePath) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Card content text must not be empty");
        }
        this.text = text;
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean hasImage() {
        return imagePath != null && !imagePath.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardContent that = (CardContent) o;
        return Objects.equals(text, that.text) && Objects.equals(imagePath, that.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, imagePath);
    }

    @Override
    public String toString() {
        if (hasImage()) {
            return text + " [Image: " + imagePath + "]";
        }
        return text;
    }
}
