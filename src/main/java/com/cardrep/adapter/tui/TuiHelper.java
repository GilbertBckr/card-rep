package com.cardrep.adapter.tui;

import com.cardrep.adapter.tui.port.KeyInput;
import com.cardrep.adapter.tui.port.TerminalSize;
import com.cardrep.adapter.tui.port.TerminalUI;

import java.util.List;

/**
 * Shared utility methods for TUI screens.
 * Handles common operations like list navigation and text formatting.
 */
public class TuiHelper {

    private final TerminalUI terminal;

    public TuiHelper(TerminalUI terminal) {
        this.terminal = terminal;
    }

    /**
     * Display a selectable list and handle vim-style navigation.
     * Returns the index of the selected item, or -1 if cancelled.
     *
     * @param items   list of items to display
     * @param startRow row to start drawing the list
     * @param startCol column to start drawing the list
     * @param title   title shown above the list
     * @return selected index (0-based) or -1 if cancelled
     */
    public int selectFromList(List<String> items, int startRow, int startCol, String title) {
        if (items.isEmpty()) {
            return -1;
        }

        int selectedIndex = 0;
        TerminalSize size = terminal.getSize();

        while (true) {
            terminal.clear();

            // Draw title
            if (title != null) {
                terminal.drawText(startCol, startRow, title);
            }

            // Draw list items
            int listStartRow = startRow + (title != null ? 2 : 0);
            for (int i = 0; i < items.size(); i++) {
                String prefix = (i == selectedIndex) ? "> " : "  ";
                String item = prefix + items.get(i);
                terminal.drawText(startCol, listStartRow + i, item, i == selectedIndex);
            }

            // Draw help text at bottom
            int helpRow = size.rows() - 2;
            terminal.drawText(startCol, helpRow, "[j/k] Navigate   [Enter] Select   [q] Back");

            terminal.refresh();

            // Handle input
            KeyInput key = terminal.readKey();

            if (key.isUp()) {
                selectedIndex = Math.max(0, selectedIndex - 1);
            } else if (key.isDown()) {
                selectedIndex = Math.min(items.size() - 1, selectedIndex + 1);
            } else if (key.isTop()) {
                selectedIndex = 0;
            } else if (key.isBottom()) {
                selectedIndex = items.size() - 1;
            } else if (key.isSelect()) {
                return selectedIndex;
            } else if (key.isBack() || key.isEof()) {
                return -1;
            }
        }
    }

    /**
     * Center text within a given width.
     *
     * @param text  text to center
     * @param width total width
     * @return centered text with padding
     */
    public String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - padding - text.length());
    }

    /**
     * Pad text to a fixed width, truncating if necessary.
     *
     * @param text  text to pad
     * @param width target width
     * @return padded/truncated text
     */
    public String padRight(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }

    /**
     * Draw a message box and wait for any key.
     *
     * @param message the message to display
     */
    public void showMessage(String message) {
        TerminalSize size = terminal.getSize();
        int boxWidth = Math.min(message.length() + 6, size.columns() - 4);
        int boxHeight = 5;
        int col = (size.columns() - boxWidth) / 2;
        int row = (size.rows() - boxHeight) / 2;

        terminal.clear();
        terminal.drawBox(col, row, boxWidth, boxHeight, null);
        terminal.drawText(col + 3, row + 2, message);
        terminal.drawText(col + 3, row + 3, "[Press any key]");
        terminal.refresh();

        terminal.readKey();
    }

    /**
     * Prompt user for text input.
     *
     * @param prompt the prompt to display
     * @return the entered text, or null if cancelled (Escape)
     */
    public String readText(String prompt) {
        TerminalSize size = terminal.getSize();
        StringBuilder input = new StringBuilder();

        while (true) {
            terminal.clear();

            // Draw prompt
            terminal.drawText(2, 2, prompt);

            // Draw input field
            String inputDisplay = input.toString() + "_";
            terminal.drawText(2, 4, "> " + inputDisplay);

            // Draw help
            terminal.drawText(2, size.rows() - 2, "[Enter] Confirm   [Esc] Cancel   [Backspace] Delete");

            terminal.refresh();

            KeyInput key = terminal.readKey();

            if (key.isSelect()) {
                // Enter pressed - return input if not empty
                return input.toString();
            } else if (key.type() == KeyInput.KeyType.ESCAPE) {
                return null;
            } else if (key.type() == KeyInput.KeyType.BACKSPACE) {
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                }
            } else if (key.type() == KeyInput.KeyType.CHARACTER && key.character() != null) {
                input.append(key.character());
            } else if (key.isEof()) {
                return null;
            }
        }
    }

    /**
     * Prompt user for text input with a title context.
     *
     * @param title  context title displayed at top
     * @param prompt the prompt to display
     * @return the entered text, or null if cancelled
     */
    public String readText(String title, String prompt) {
        TerminalSize size = terminal.getSize();
        StringBuilder input = new StringBuilder();

        while (true) {
            terminal.clear();

            // Draw title
            if (title != null) {
                terminal.drawText(2, 1, title);
                terminal.drawText(2, 2, "─".repeat(Math.min(title.length() + 4, size.columns() - 4)));
            }

            // Draw prompt
            int promptRow = title != null ? 4 : 2;
            terminal.drawText(2, promptRow, prompt);

            // Draw input field
            String inputDisplay = input.toString() + "_";
            terminal.drawText(2, promptRow + 2, "> " + inputDisplay);

            // Draw help
            terminal.drawText(2, size.rows() - 2, "[Enter] Confirm   [Esc] Cancel   [Backspace] Delete");

            terminal.refresh();

            KeyInput key = terminal.readKey();

            if (key.isSelect()) {
                return input.toString();
            } else if (key.type() == KeyInput.KeyType.ESCAPE) {
                return null;
            } else if (key.type() == KeyInput.KeyType.BACKSPACE) {
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                }
            } else if (key.type() == KeyInput.KeyType.CHARACTER && key.character() != null) {
                input.append(key.character());
            } else if (key.isEof()) {
                return null;
            }
        }
    }

    /**
     * Show a yes/no confirmation dialog.
     *
     * @param message the confirmation message
     * @return true if user confirmed (y), false otherwise
     */
    public boolean confirm(String message) {
        TerminalSize size = terminal.getSize();

        while (true) {
            terminal.clear();

            terminal.drawText(2, 2, message);
            terminal.drawText(2, 4, "[y] Yes   [n] No");

            terminal.refresh();

            KeyInput key = terminal.readKey();

            if (key.type() == KeyInput.KeyType.CHARACTER && key.character() != null) {
                char c = Character.toLowerCase(key.character());
                if (c == 'y') {
                    return true;
                } else if (c == 'n' || key.isBack()) {
                    return false;
                }
            } else if (key.isBack() || key.isEof()) {
                return false;
            }
        }
    }

    /**
     * Show an error message.
     *
     * @param message the error message
     */
    public void showError(String message) {
        showMessage("Error: " + message);
    }

    /**
     * Get the terminal instance.
     */
    public TerminalUI getTerminal() {
        return terminal;
    }
}
