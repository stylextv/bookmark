package de.chess.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.chess.ai.Evaluator;
import de.chess.game.PieceCode;
import de.chess.main.Constants;
import de.chess.util.ColorUtil;
import de.chess.util.ImageUtil;
import de.chess.util.MathUtil;

public class WidgetUI {
	
	private static final int RANGE = 350;
	
	private static int prediction;
	
	private static float lerpedPrediction;
	
	private static int sideSelection;
	
	private static float[] hoveringStates = new float[3];
	
	public static void drawWidgets(Graphics2D graphics) {
		drawPredictionBar(graphics, 16, 66);
		
		drawMenu(graphics, 66 + Constants.BOARD_SIZE + 30, 16);
	}
	
	private static void drawPredictionBar(Graphics2D graphics, int x, int y) {
		int target = prediction;
		
		if(target > RANGE) target = RANGE;
		else if(target < -RANGE) target = -RANGE;
		
		lerpedPrediction = MathUtil.lerp(lerpedPrediction, target, 0.05f);
		
		int w = 40;
		
		Color c1 = Constants.COLOR_PLAYER_WHITE;
		Color c2 = Constants.COLOR_PLAYER_BLACK;
		
		if(BoardUI.getHumanSide() == PieceCode.WHITE) {
			c1 = Constants.COLOR_PLAYER_BLACK;
			c2 = Constants.COLOR_PLAYER_WHITE;
		}
		
		graphics.setColor(c2);
		
		graphics.fillRect(x, y, w, Constants.BOARD_SIZE);
		
		float h = (lerpedPrediction + RANGE) / RANGE / 2 * Constants.BOARD_SIZE;
		
		float f = 0.05f;
		
		if(h < f) h = 0;
		else if(h > Constants.BOARD_SIZE - f) h = Constants.BOARD_SIZE;
		
		graphics.setColor(c1);
		
		graphics.fillRect(x, y, w, (int) h);
		
		if(prediction != 0) {
			String text = getPredictionText(prediction, (BoardUI.getHumanSide() + 1) % 2);
			
			int textY;
			
			Color c;
			
			graphics.setFont(Constants.FONT_BOLD);
			
			if(lerpedPrediction > 0) {
				
				textY = y + 8 + graphics.getFontMetrics().getHeight() - 5;
				c = c2;
			} else {
				
				textY = y + Constants.BOARD_SIZE - 10;
				c = c1;
			}
			
			graphics.setColor(c);
			
			graphics.drawString(text, x + (w - graphics.getFontMetrics().stringWidth(text)) / 2, textY);
		}
		
		graphics.drawImage(ImageUtil.PREDICTION_BAR_CORNERS, x, y, null);
	}
	
	private static String getPredictionText(int i, int side) {
		String s;
		
		int a = Math.abs(i);
		
		if(a > 10000) {
			int moves;
			
			if(i > 0) {
				moves = (99999 - i) / 2 + 1;
			} else {
				moves = (i + 99998) / 2 + 1;
			}
			
			s = "M" + moves;
			
		} else {
			float pawns = (float) a / Evaluator.getPieceValue(PieceCode.PAWN);
			
			s = MathUtil.DISPLAY_DECIMAL_FORMAT.format(pawns);
		}
		
		boolean b = i > 0;
		
		if(side == PieceCode.BLACK) b = !b;
		
		if(b) return "+" + s;
		else return "-" + s;
	}
	
	private static void drawMenu(Graphics2D graphics, int x, int y) {
		int w = UIManager.getWidth() - x - 16;
		int h = UIManager.getHeight() - y - 16;
		
		graphics.setColor(Constants.COLOR_MENU_BACKGROUND);
		
		graphics.fillRoundRect(x, y, w, h, 6, 6);
		
		drawMenuProfile(graphics, x + w / 2, y + 80);
		drawSideSelection(graphics, x + w / 2, y + 80 + 129 + 70);
	}
	
	private static void drawMenuProfile(Graphics2D graphics, int x, int y) {
		graphics.drawImage(ImageUtil.PROFILE_ICON, x - ImageUtil.PROFILE_ICON.getWidth() / 2, y, null);
		
		graphics.setFont(Constants.FONT_EXTRA_BOLD);
		
		int nameWidth = graphics.getFontMetrics().stringWidth(Constants.NAME);
		
		graphics.setFont(Constants.FONT_LIGHT);
		
		String elo = "(" + Constants.ELO + ")";
		
		int eloWidth = graphics.getFontMetrics().stringWidth(elo);
		
		int textX = x - (nameWidth + 7 + eloWidth) / 2;
		int textY = y + ImageUtil.PROFILE_ICON.getHeight() + 35;
		
		graphics.setColor(Constants.COLOR_TEXT_WHITE_TRANSPARENT);
		
		graphics.drawString(elo, textX + nameWidth + 7, textY);
		
		graphics.setFont(Constants.FONT_EXTRA_BOLD);
		
		graphics.setColor(Constants.COLOR_WHITE);
		
		graphics.drawString(Constants.NAME, textX, textY);
	}
	
	private static void drawSideSelection(Graphics2D graphics, int x, int y) {
		int i = isHoveringSelectionButton(UIManager.getMouseX(), UIManager.getMouseY());
		
		drawSideSelectionButton(graphics, 0, ImageUtil.SIDE_SELECTION_RANDOM, x, y, i);
		drawSideSelectionButton(graphics, 1, ImageUtil.SIDE_SELECTION_WHITE, x - 66, y, i);
		drawSideSelectionButton(graphics, 2, ImageUtil.SIDE_SELECTION_BLACK, x + 66, y, i);
	}
	
	private static void drawSideSelectionButton(Graphics2D graphics, int id, BufferedImage image, int x, int y, int hovering) {
		int target = hovering == id ? 1 : 0;
		
		hoveringStates[id] = MathUtil.lerp(hoveringStates[id], target, 0.4f);
		
		int size = 48;
		
		int roundness = 10;
		
		int cornerX = x - size / 2;
		int cornerY = y;
		
		graphics.setColor(Constants.COLOR_BUTTON_SHADOW);
		graphics.fillRoundRect(cornerX, cornerY + 4, size, size, roundness, roundness);
		
		graphics.setColor(Constants.COLOR_BUTTON_OUTLINE);
		graphics.fillRoundRect(cornerX, cornerY, size, size, roundness, roundness);
		
		graphics.setColor(ColorUtil.mixColors(Constants.COLOR_MENU_BACKGROUND, Constants.COLOR_BUTTON, hoveringStates[id]));
		
		graphics.fillRoundRect(cornerX + 2, cornerY + 2, size - 4, size - 4, roundness - 2, roundness - 2);
		
		graphics.drawImage(image, x - image.getWidth() / 2, y + (size - image.getHeight()) / 2, null);
	}
	
	public static int isHoveringSelectionButton(int mx, int my) {
		int x = 66 + Constants.BOARD_SIZE + 30;
		
		int w = UIManager.getWidth() - x - 16;
		
		x += w / 2;
		
		int y = 16 + 80 + 129 + 70;
		
		mx -= x;
		my -= y;
		
		int size = 48;
		
		if(my < 0 || my > size) return -1;
		
		mx += 90;
		
		if(mx >= 0 && mx < size) return 1;
		if(mx >= size + 18 && mx < size * 2 + 18) return 0;
		if(mx >= size * 2 + 36 && mx < size * 3 + 36) return 2;
		
		return -1;
	}
	
	public static void setPrediction(int i) {
		prediction = i;
	}
	
	public static int getSideSelection() {
		return sideSelection;
	}
	
	public static void setSideSelection(int i) {
		sideSelection = i;
	}
	
}
