package Menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;

import Board.Board;
import Board.Sprite;
import Board.StatSet;
import Board.Tile;
import Board.Unit;
import Game.Game;
import Game.Player;

/** A menu that runs the game and handles appropriate UI elements for the game
 * @author Shiranka Miskin
 * @version January 2013 */
public class GameMenu extends Menu
{

	private Sprite unitToolTip;
	private Sprite tileToolTip;
	private Sprite playerBar;
	public static final String uiDir = "res/UI/";
	private Game game;
	private Board board;
	private Rectangle portraitDisplay;
	private Rectangle tileDisplay;
	private boolean showPlayerBar;
	private boolean showUnitTooltip;
	private boolean showTileTooltip;
	private LinkedList<Player> players;
	private Font playerBarFont;
	private Font tooltipTitleFont;
	private Font tooltipTextFont;

	private Unit cursorUnit;
	private Tile cursorTile;

	/** Creates a menu that runs the game
	 * @param size The size of the menu
	 * @throws IOException */
	public GameMenu(Game game, Dimension size) throws IOException
	{
		super(size);

		unitToolTip = new Sprite(uiDir + "UnitTooltip.png");
		tileToolTip = new Sprite(uiDir + "TileTooltip.png");
		playerBar = new Sprite(uiDir + "PlayerBar.png");
		playerBarFont = Main.getFont("Sword Art Online", 25);
		tooltipTitleFont = Main.getFont("Sword Art Online", 23);
		tooltipTextFont = Main.getFont("Sword Art Online", 18);

		portraitDisplay = new Rectangle(new Point(12, 548), new Dimension(86,
				86));
		tileDisplay = new Rectangle(new Point(1177, 548), new Dimension(86, 86));

		this.game = game;
		board = game.getBoard();
		players = game.getPlayers();
	}

	/** Resets the board */
	public void reset()
	{
		super.reset();
		if (board != null)
			board.reset();
	}

	/** Runs the game and cursor */
	public void run()
	{
		cursorUnit = board.getUnitAt(board.getCursorLoc());
		cursorTile = board.getTile(board.getCursorLoc());
		game.run();
		if (game.isOver())
			completed = true;
	}

	/** Sets a graphics parameter to antialiasing for both text and other
	 * graphics (such as lines or rectangles)
	 * @param g2 the graphics context */
	private void setAntialiasing(Graphics2D g2)
	{
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}

	/** Draws the bar at the top of the screen that displays the player name
	 * @param g The graphics to draw with
	 * @param container`The container to draw on */
	private void drawPlayerBar(Graphics g, Container container)
	{

		Graphics2D g2 = (Graphics2D) g;
		setAntialiasing(g2);
		// Draws the image for the bar
		playerBar.draw(g, container);

		// Measure the size of the text
		FontMetrics fontMetrics = g.getFontMetrics(playerBarFont);

		// Makes a rectangle the size of the text
		Rectangle textBounds = new Rectangle(fontMetrics.stringWidth(players
				.getFirst().getName()), fontMetrics.getAscent()
				- fontMetrics.getLeading() - 3);

		// Centers the text on the board
		int stringX = (size.width - textBounds.width) / 2;
		int stringY = 613;
		g.setColor(Color.black);
		g.setFont(playerBarFont);
		g.drawString(players.getFirst().getName(), stringX, stringY);

		g.setColor(players.getFirst().getColor());
		g2.setStroke(new BasicStroke(2f));
		int underlineLength = 100;
		g2.drawLine((size.width - underlineLength) / 2, stringY + 5,
				(size.width + underlineLength) / 2, stringY + 5);

		int[] xPoints = { 535, 553, 728, 746 };
		int[] yPoints = { 640, 576, 576, 640 };
		g2.drawPolygon(xPoints, yPoints, 4);

	}

	/** Draws the menu at the bottom left that displays the unit stats
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	private void drawUnitTooltip(Graphics g, Container container)
	{
		Graphics2D g2 = (Graphics2D) g;

		setAntialiasing(g2);

		cursorUnit.getPortrait().draw(g, portraitDisplay.x, portraitDisplay.y,
				portraitDisplay.width, portraitDisplay.height, container);
		unitToolTip.draw(g, 0, size.height - unitToolTip.getHeight(container),
				container);

		FontMetrics fontMetrics = g.getFontMetrics(tooltipTitleFont);
		Rectangle textBounds = new Rectangle(fontMetrics.stringWidth(cursorUnit
				.toString()), fontMetrics.getAscent()
				- fontMetrics.getLeading() - 3);
		g.setColor(Color.black);
		g.setFont(tooltipTitleFont);
		g.drawString(cursorUnit.toString(), 120, 570);

		StatSet stats = cursorUnit.getCurrentStats();
		g.setFont(tooltipTextFont);
		g.drawString("Attack:   " + stats.getAttack(), 112, 595);
		g.drawString("Movement: " + stats.getMovement(), 112, 620);
		g.drawString("Health: " + stats.getHealth(), 220, 595);
		g.drawString("Range:  " + stats.getRange(), 220, 620);

		g.setColor(cursorUnit.getColor());
		g2.setStroke(new BasicStroke(2f));
		g.drawLine(105, 559, 115, 559);
		g.drawLine(125 + textBounds.width, 559, 283, 559);

	}

	/** Draws the tile icon and stats
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	private void drawTileTooltip(Graphics g, Container container)
	{
		Graphics2D g2 = (Graphics2D) g;
		setAntialiasing(g2);

		cursorTile.draw(g, tileDisplay.getLocation(), tileDisplay.width,
				container);
		tileToolTip.draw(g, size.width - tileToolTip.getWidth(container),
				size.height - tileToolTip.getHeight(container), container);

		FontMetrics fontMetrics = g.getFontMetrics(tooltipTitleFont);
		Rectangle textBounds = new Rectangle(fontMetrics.stringWidth(cursorTile
				.toString()), fontMetrics.getAscent()
				- fontMetrics.getLeading() - 3);
		g.setColor(Color.black);
		g.setFont(tooltipTitleFont);
		g.drawString(cursorTile.toString().toUpperCase(),
				1148 - textBounds.width, 570);

		g2.setStroke(new BasicStroke(2f));
		g.drawLine(1161, 559, 1171, 559);
		g.drawLine(1143 - textBounds.width, 559, 995, 559);

		g.setFont(tooltipTextFont);
		g.drawString("Cost: " + cursorTile.getCost(), 1010, 595);

	}

	/** Draws the tooltips and the game to the screen
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		game.draw(g, container);

		if (cursorUnit != null && showUnitTooltip)
			drawUnitTooltip(g, container);

		if (cursorTile != null && showTileTooltip)
			drawTileTooltip(g, container);

		if (showPlayerBar)
			drawPlayerBar(g, container);

	}

	// Controller

	/** Handles mouse clicks
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		game.getMousePress(event);
	}

	/** Handles mouse movement
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		game.getMouseMovement(event);

		Rectangle rect = new Rectangle(new Point(0, 540), new Dimension(330,
				100));
		showUnitTooltip = !rect.contains(event.getPoint());
		rect.setLocation(950, 540);
		showTileTooltip = !rect.contains(event.getPoint());
		rect.setLocation(535, 575);
		rect.setSize(212, 65);
		showPlayerBar = !rect.contains(event.getPoint());

	}

	/** Handles keyboard input
	 * @param event the keyboard event */
	public void getKeyInput(KeyEvent event)
	{
	}
}
