package com.cardrep.adapter.tui.port;

/**
 * Port interface for terminal UI operations.
 * Implementations live in the plugin layer, isolating the TUI adapter from specific terminal libraries.
 */
public interface TerminalUI {

    /**
     * Initialize the terminal for TUI mode.
     * Should be called before any drawing operations.
     */
    void start();

    /**
     * Restore the terminal to its original state and release resources.
     * Should be called when the TUI exits.
     */
    void stop();

    /**
     * Clear the screen buffer.
     * Changes are not visible until refresh() is called.
     */
    void clear();

    /**
     * Flush the screen buffer to the terminal, making changes visible.
     */
    void refresh();

    /**
     * Draw text at the specified position.
     *
     * @param col  column (0-indexed, from left)
     * @param row  row (0-indexed, from top)
     * @param text the text to draw
     */
    void drawText(int col, int row, String text);

    /**
     * Draw text at the specified position with highlighting (inverted colors).
     *
     * @param col       column (0-indexed, from left)
     * @param row       row (0-indexed, from top)
     * @param text      the text to draw
     * @param highlight if true, draw with inverted colors
     */
    void drawText(int col, int row, String text, boolean highlight);

    /**
     * Draw a bordered box with an optional title.
     *
     * @param col    column of top-left corner
     * @param row    row of top-left corner
     * @param width  width including borders
     * @param height height including borders
     * @param title  optional title (null for no title)
     */
    void drawBox(int col, int row, int width, int height, String title);

    /**
     * Block until the user presses a key and return it.
     *
     * @return the key input, or EOF if the terminal is closed
     */
    KeyInput readKey();

    /**
     * Get the current terminal dimensions.
     *
     * @return terminal size in columns and rows
     */
    TerminalSize getSize();
}
