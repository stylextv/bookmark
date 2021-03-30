package de.chess.main;

import java.awt.Color;
import java.awt.Font;

public class Constants {
	
	public static final String NAME = "Bookmark";
	public static final int ELO = 2600;
	
	public static final int WINDOW_DEFAULT_WIDTH = 1050;
	public static final int WINDOW_DEFAULT_HEIGHT = 772;
	
	public static final int TILE_SIZE = 80;
	public static final int BOARD_SIZE = TILE_SIZE * 8;
	
	public static final int BRIGHTNESS_BLACK = 0;
	public static final Color COLOR_BLACK = new Color(BRIGHTNESS_BLACK, BRIGHTNESS_BLACK, BRIGHTNESS_BLACK);
	public static final Color COLOR_WHITE = new Color(0xFFFFFF);
	
	public static final Color COLOR_BACKGROUND = new Color(0x2B2B2B);
	public static final Color COLOR_MENU_BACKGROUND = new Color(0x3C3F41);
	
	public static final Color COLOR_TEXT_WHITE_TRANSPARENT = new Color(255, 255, 255, 166);
	
	public static final Color COLOR_BUTTON = new Color(0x43494A);
	public static final Color COLOR_BUTTON_OUTLINE = new Color(0x6B6B6B);
	public static final Color COLOR_BUTTON_SHADOW = new Color(0x36393A);
	
	public static final Color COLOR_BOARD_HIGHLIGHT = new Color(155, 199, 0, 105);
	public static final Color COLOR_BOARD_MARKER = new Color(0, 0, 0, 26);
	
	public static final Color COLOR_PLAYER_WHITE = COLOR_WHITE;
	public static final Color COLOR_PLAYER_BLACK = COLOR_MENU_BACKGROUND;
	
	public static final Font FONT_LIGHT = new Font("Segoe UI Light", 0, 18);
	public static final Font FONT_BOLD = new Font("Segoe UI Bold", 0, 12);
	public static final Font FONT_EXTRA_BOLD = new Font("Montserrat ExtraBold", 0, 18);
	public static final Font FONT_EXTRA_BOLD_LARGE = new Font("Montserrat ExtraBold", 0, 24);
	
	public static final boolean PRINT_FPS = false;
	
}
