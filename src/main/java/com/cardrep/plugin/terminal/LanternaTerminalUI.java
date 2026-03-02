package com.cardrep.plugin.terminal;

import com.cardrep.adapter.tui.port.KeyInput;
import com.cardrep.adapter.tui.port.KeyInput.KeyType;
import com.cardrep.adapter.tui.port.TerminalSize;
import com.cardrep.adapter.tui.port.TerminalUI;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * Lanterna-based implementation of TerminalUI.
 * This is the only class in the codebase that depends on the Lanterna library.
 */
public class LanternaTerminalUI implements TerminalUI {

    private Terminal terminal;
    private Screen screen;
    private TextGraphics graphics;

    @Override
    public void start() {
        try {
            terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null); // Hide cursor
            graphics = screen.newTextGraphics();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize terminal", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            // Ignore errors during cleanup
        }
    }

    @Override
    public void clear() {
        screen.clear();
    }

    @Override
    public void refresh() {
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException("Failed to refresh screen", e);
        }
    }

    @Override
    public void drawText(int col, int row, String text) {
        drawText(col, row, text, false);
    }

    @Override
    public void drawText(int col, int row, String text, boolean highlight) {
        if (highlight) {
            graphics.enableModifiers(SGR.REVERSE);
        }
        graphics.putString(col, row, text);
        if (highlight) {
            graphics.disableModifiers(SGR.REVERSE);
        }
    }

    @Override
    public void drawBox(int col, int row, int width, int height, String title) {
        // Box drawing characters
        char topLeft = '┌';
        char topRight = '┐';
        char bottomLeft = '└';
        char bottomRight = '┘';
        char horizontal = '─';
        char vertical = '│';

        // Top border with optional title
        StringBuilder top = new StringBuilder();
        top.append(topLeft);
        if (title != null && !title.isEmpty()) {
            top.append(horizontal);
            top.append(" ").append(title).append(" ");
            int remaining = width - 4 - title.length() - 1;
            top.append(String.valueOf(horizontal).repeat(Math.max(0, remaining)));
        } else {
            top.append(String.valueOf(horizontal).repeat(width - 2));
        }
        top.append(topRight);
        graphics.putString(col, row, top.toString());

        // Side borders
        for (int i = 1; i < height - 1; i++) {
            graphics.setCharacter(col, row + i, TextCharacter.fromCharacter(vertical)[0]);
            graphics.setCharacter(col + width - 1, row + i, TextCharacter.fromCharacter(vertical)[0]);
        }

        // Bottom border
        StringBuilder bottom = new StringBuilder();
        bottom.append(bottomLeft);
        bottom.append(String.valueOf(horizontal).repeat(width - 2));
        bottom.append(bottomRight);
        graphics.putString(col, row + height - 1, bottom.toString());
    }

    @Override
    public KeyInput readKey() {
        try {
            KeyStroke keyStroke = screen.readInput();
            return convertKeyStroke(keyStroke);
        } catch (IOException e) {
            return new KeyInput(KeyType.EOF, null);
        }
    }

    @Override
    public TerminalSize getSize() {
        com.googlecode.lanterna.TerminalSize size = screen.getTerminalSize();
        return new TerminalSize(size.getColumns(), size.getRows());
    }

    /**
     * Convert Lanterna KeyStroke to our KeyInput value object.
     */
    private KeyInput convertKeyStroke(KeyStroke keyStroke) {
        return switch (keyStroke.getKeyType()) {
            case ArrowUp -> new KeyInput(KeyType.ARROW_UP, null);
            case ArrowDown -> new KeyInput(KeyType.ARROW_DOWN, null);
            case ArrowLeft -> new KeyInput(KeyType.ARROW_LEFT, null);
            case ArrowRight -> new KeyInput(KeyType.ARROW_RIGHT, null);
            case Enter -> new KeyInput(KeyType.ENTER, null);
            case Escape -> new KeyInput(KeyType.ESCAPE, null);
            case Backspace -> new KeyInput(KeyType.BACKSPACE, null);
            case EOF -> new KeyInput(KeyType.EOF, null);
            case Character -> new KeyInput(KeyType.CHARACTER, keyStroke.getCharacter());
            default -> new KeyInput(KeyType.CHARACTER, null);
        };
    }
}
