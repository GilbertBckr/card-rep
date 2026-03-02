package com.cardrep.adapter.tui.port;

/**
 * Value object representing terminal dimensions.
 */
public record TerminalSize(int columns, int rows) {

    /**
     * Check if the terminal is large enough for the TUI.
     *
     * @param minColumns minimum required columns
     * @param minRows    minimum required rows
     * @return true if terminal meets minimum size requirements
     */
    public boolean isAtLeast(int minColumns, int minRows) {
        return columns >= minColumns && rows >= minRows;
    }
}
