package com.cardrep.adapter.tui.port;

/**
 * Value object representing a key press event.
 * Provides helper methods for common vim-style navigation checks.
 */
public record KeyInput(KeyType type, Character character) {

    /**
     * Types of key inputs that can be detected.
     */
    public enum KeyType {
        ARROW_UP,
        ARROW_DOWN,
        ARROW_LEFT,
        ARROW_RIGHT,
        ENTER,
        ESCAPE,
        BACKSPACE,
        CHARACTER,
        EOF
    }

    /**
     * Check if this is an "up" navigation key (arrow up or 'k').
     */
    public boolean isUp() {
        return type == KeyType.ARROW_UP || (character != null && character == 'k');
    }

    /**
     * Check if this is a "down" navigation key (arrow down or 'j').
     */
    public boolean isDown() {
        return type == KeyType.ARROW_DOWN || (character != null && character == 'j');
    }

    /**
     * Check if this is a "left" navigation key (arrow left or 'h').
     */
    public boolean isLeft() {
        return type == KeyType.ARROW_LEFT || (character != null && character == 'h');
    }

    /**
     * Check if this is a "right" navigation key (arrow right or 'l').
     */
    public boolean isRight() {
        return type == KeyType.ARROW_RIGHT || (character != null && character == 'l');
    }

    /**
     * Check if this is a select/confirm key (Enter).
     */
    public boolean isSelect() {
        return type == KeyType.ENTER;
    }

    /**
     * Check if this is a back/quit key (Escape or 'q').
     */
    public boolean isBack() {
        return type == KeyType.ESCAPE || (character != null && character == 'q');
    }

    /**
     * Check if this is the search key ('/').
     */
    public boolean isSearch() {
        return character != null && character == '/';
    }

    /**
     * Check if this key matches a specific digit (0-9).
     *
     * @param n the digit to check (0-9)
     * @return true if this key is the specified digit
     */
    public boolean isDigit(int n) {
        if (n < 0 || n > 9) {
            return false;
        }
        return character != null && character == (char) ('0' + n);
    }

    /**
     * Check if this is the end-of-file signal (terminal closed).
     */
    public boolean isEof() {
        return type == KeyType.EOF;
    }

    /**
     * Check if this is the "jump to top" key ('g' or 'G' for first item).
     */
    public boolean isTop() {
        return character != null && character == 'g';
    }

    /**
     * Check if this is the "jump to bottom" key ('G' for last item).
     */
    public boolean isBottom() {
        return character != null && character == 'G';
    }
}
