package Menu;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import UIElements.RectangleButton;

import Board.Sprite;
import Board.Tile;

public class Main extends JFrame
{

	// Music:
	// Fire Emblem: Awakening Prologue:
	// http://www.youtube.com/watch?v=mWU5rtvqBMg&list=SP58D398750F1219D8


	private Dimension size;
	private Point topLeft;
	private Rectangle screen;

	// The menu that is currently being used
	private Menu currentMenu;

	// The list of menus
	public static Menu mainMenu;
	private Menu optionsMenu;
	private boolean showOptions;
	private RectangleButton muteButton;
	private RectangleButton pauseButton;
	private Menu gameMenu;
	private Menu instructionsMenu;
	private Menu designMenu;
	public static Menu exitGame;

	public static Map<String, Font> fontLibrary;

	public static Container container;

	private static BufferedImage blankCursorImg = new BufferedImage(16, 16,
			BufferedImage.TYPE_INT_ARGB);
	public static Cursor blankCursor = Toolkit
			.getDefaultToolkit()
			.createCustomCursor(blankCursorImg, new Point(0, 0), "blank cursor");
	private static Image cursorImg = Toolkit.getDefaultToolkit().getImage(
			"res/UI/cursor.gif");
	public static Cursor mainCursor = Toolkit.getDefaultToolkit()
			.createCustomCursor(cursorImg, new Point(16, 16), "main cursor");

	private AudioClip bgMusic = Applet
			.newAudioClip(getCompleteURL("res/Prologue_Fire_Emblem_Awakening.wav"));


	/** Initializes the screen, setting its size and adding the panel.
	 * 
	 * @param screenSize the size of the screen, ratios are always kept at 10:7
	 * @throws IOException
	 * @throws FontFormatException */
	public Main(int screenWidth, int screenHeight) throws FontFormatException,
			IOException
	{
		super("MainScreen");
		size = new Dimension();
		size.width = screenWidth;
		size.height = screenHeight;
		setSize(size.width, size.height);
		topLeft = new Point(0, 0);
		screen = new Rectangle(topLeft, size);
		setLocation(topLeft);
		setUndecorated(true);

		fontLibrary = new HashMap<String, Font>();
		fontLibrary.put(
				"Kalinga",
				Font.createFont(Font.TRUETYPE_FONT,
						new File("res/UI/kalinga.ttf")).deriveFont(21f));
		fontLibrary.put(
				"Kalinga Bold",
				Font.createFont(Font.TRUETYPE_FONT,
						new File("res/UI/kalingab.ttf")).deriveFont(21f));
		fontLibrary.put(
				"Sword Art Online",
				Font.createFont(Font.TRUETYPE_FONT,
						new File("res/UI/SwordArtOnline.ttf")).deriveFont(21f));

		// Set the blank cursor to the JFrame.
		this.getContentPane().setCursor(mainCursor);

		initializeMenus();
		currentMenu = mainMenu;
		System.out.println("Loading");
		container = this;
		getContentPane().add(new DrawingPanel(), BorderLayout.CENTER);
		setVisible(true);

		Sprite pauseIcon = new Sprite("res/UI/Pause.png");
		Sprite soundOn = new Sprite("res/UI/Sound_On.png");
		Sprite soundOff = new Sprite("res/UI/Sound_Off.png");
		pauseButton = new RectangleButton(new Point(0, 0),
				new Dimension(30, 30), pauseIcon);
		muteButton = new RectangleButton(new Point(size.width - 30, 0),
				new Dimension(30, 30), soundOn, soundOff);

		// Rounded rectangles will be drawn in their place
		pauseButton.setTransparent();
		muteButton.setTransparent();

		bgMusic.loop();
	}


	/** Loads the database data and initializes the main screen
	 * 
	 * @param args the string arguments
	 * @throws IOException
	 * @throws FontFormatException */
	public static void main(String[] args) throws IOException,
			FontFormatException
	{
		Tile.loadTiles();
		UnitMenu.loadUnitDisplay();
		Main mainScreen = new Main(1281, 642);
		mainScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/** Gets the URL needed for newAudioClip (Code from Mr. Ridout)
	 * @param fileName The name of the file
	 * @return the URL of that file */
	public static URL getCompleteURL(String fileName)
	{
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		} catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
	}

	/** Gets a font from the font database
	 * 
	 * @param fontName The name of the font
	 * @param size The size of the font to load
	 * @return the requested font */
	public static Font getFont(String fontName, int size)
	{
		return fontLibrary.get(fontName).deriveFont((float) size);
	}

	/** Initializes every menu object, and assigns their links. */
	private void initializeMenus()
	{
		try
		{
			mainMenu = new Menu("TitleScreen.jpg", size);
			instructionsMenu = new InstructionsMenu(size);
			gameMenu = new GameSetupMenu("Map.jpg", size);
			optionsMenu = new Menu("options.png", size);
			designMenu = new DesignSetupMenu(size);
			exitGame = new Menu(size);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// Each button on the menu must be the same size
		int buttonHeight = 349;
		int boxHeight = 35;
		int boxWidth = 250;
		int spaceBetween = 3;

		// Addds the links to the main menu
		mainMenu.addClickable("PLAY GAME", fontLibrary.get("Kalinga"),
				new Point((1280 - boxWidth) / 2, buttonHeight), new Dimension(
						boxWidth, boxHeight), gameMenu);
		mainMenu.addClickable("INSTRUCTIONS", fontLibrary.get("Kalinga"),
				new Point((1280 - boxWidth) / 2, buttonHeight + boxHeight
						+ spaceBetween), new Dimension(boxWidth, boxHeight),
				instructionsMenu);
		mainMenu.addClickable("LEVEL DESIGNER", fontLibrary.get("Kalinga"),
				new Point((1280 - boxWidth) / 2, buttonHeight + 2
						* (boxHeight + spaceBetween)), new Dimension(boxWidth,
						boxHeight), designMenu);
		mainMenu.addClickable("EXIT GAME", fontLibrary.get("Kalinga"),
				new Point((1280 - boxWidth) / 2, buttonHeight + 3
						* (boxHeight + spaceBetween)), new Dimension(boxWidth,
						boxHeight), exitGame);

		// Initialize the options menu that is used for every menu throughout
		// the game
		showOptions = false;
		Dimension buttonSize = new Dimension(200, 35);
		int buttonY = 300;

		optionsMenu.addClickable("Resume", Main.getFont("Kalinga", 21),
				new Point(640 - buttonSize.width / 2, buttonY), buttonSize,
				optionsMenu);
		optionsMenu.addClickable("Main Menu", Main.getFont("Kalinga", 21),
				new Point(640 - buttonSize.width / 2, buttonY
						+ buttonSize.height + 5), buttonSize, Main.mainMenu);
		optionsMenu.addClickable("Exit Game", Main.getFont("Kalinga", 21),
				new Point(640 - buttonSize.width / 2, buttonY + 2
						* (buttonSize.height + 5)), buttonSize, Main.exitGame);
		optionsMenu.setClickableHover(new Color(0, 0, 0, 0));
	}

	/** Resets all the menus */
	public void reset()
	{
		gameMenu.reset();
		designMenu.reset();
		mainMenu.reset();
		optionsMenu.reset();
		instructionsMenu.reset();
	}

	/** Checks if a certain point is within the screen
	 * 
	 * @param p the point to check
	 * @return true if the point is within the screen, false if it is outside */
	public boolean isValidPoint(Point p)
	{
		if (p.x < 0 || p.y < 0 || p.x > size.width || p.y > size.height)
			return false;
		return true;
	}

	/** Draws and runs the menus
	 * 
	 * @author Shiranka Miskin
	 * @version December 2012 */
	private class DrawingPanel extends JPanel implements ActionListener
	{

		MouseHandler mouse;
		Timer timer;

		/** Adds the listeners and starts the timer, which activates the run
		 * method */
		public DrawingPanel()
		{
			this.setFocusable(true);

			mouse = new MouseHandler();
			this.addMouseListener(mouse);
			this.addMouseMotionListener(mouse);
			this.addKeyListener(new KeyHandler());
			setBackground(Color.black);
			setResizable(false);

			// The game updates every time the timer finishes
			timer = new Timer(30, this);
			timer.start();
		}

		/** Draws everything to the panel
		 * 
		 * @param g the graphics context to draw on */
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			// Draws whatever the current menu on the screen wants to draw
			currentMenu.draw(g, this);

			// Draw the rounded rectangles for the buttons
			// since the buttons themselves are transparent
			g.setColor(Color.white);
			g.fillRoundRect(-15, -15, 45, 45, 15, 15);
			g.fillRoundRect(size.width - 30, -15, 45, 45, 15, 15);
			// Draw the outlines
			g.setColor(new Color(235, 235, 235));
			g.drawRoundRect(-15, -15, 45, 45, 15, 15);
			g.drawRoundRect(size.width - 30, -15, 45, 45, 15, 15);
			// Draw the appropriate pictures
			pauseButton.draw(g, this);
			muteButton.draw(g, this);

			if (showOptions)
			{
				optionsMenu.draw(g, 0, 0, this);
			}

		}

		/** Refreshes the game every time the timer activates
		 * 
		 * @param e the event that has occured */
		public void actionPerformed(ActionEvent e)
		{

			// If the options menu is being shown, check if it has
			// been completed and ignore the rest of the program running
			if (showOptions)
			{
				if (optionsMenu.isComplete())
				{
					if (optionsMenu.nextMenu != optionsMenu)
					{
						reset();
						currentMenu = optionsMenu.nextMenu;
					}
					optionsMenu.reset();
					showOptions = false;
				}
			}

			// The exit game menu is not actually a menu but simply
			// an indicator to exit the program completely
			if (currentMenu == exitGame)
				System.exit(DISPOSE_ON_CLOSE);

			// Runs everything the current menu wants to run
			if (!showOptions)
				currentMenu.run();
			// If the menu has completed, and it is time to move on to
			// another,
			// switch the current menu to whatever the next menu is
			if (currentMenu.isComplete())
			{
				currentMenu = currentMenu.nextMenu;
				if (currentMenu == mainMenu)
					reset();
			}
			// Draw everything once all necessary data has been changed
			repaint();
		}
	}

	/** Handles mouse input for the program
	 * 
	 * @author Shiranka Miskin
	 * @version January 2012 */
	private class MouseHandler extends MouseAdapter
	{
		/** Runs whatever is supposed to run for the current menu when a mouse
		 * action is completed
		 * 
		 * @param event the key that has been pressed on the mouse */
		public void mousePressed(MouseEvent event)
		{

			// If the sound is on mute, set it back to play if the button was
			// clicked
			// It is necessary to check if clicked first, then check if it has
			// been clicked
			// because the getMousePress method will set the button to clicked
			// automatically
			if (muteButton.isClicked())
			{
				if (muteButton.getMousePress(event))
				{
					muteButton.setClicked(false);
					bgMusic.loop();
				}
			}
			// Otherwise stop the music if the button is pressed
			else if (muteButton.getMousePress(event))
				bgMusic.stop();

			// Pause the game if the pause button has been pressed
			if (pauseButton.getMousePress(event))
			{
				showOptions = true;
				pauseButton.reset();
			}

			// Only allow either the options menu or the current menu to recieve
			// mouse input so that they do not overlap
			if (!showOptions)
				currentMenu.getMousePress(event);
			else
				optionsMenu.getMousePress(event);
		}

		/** Handles the mouse being released
		 * 
		 * @param event the mouse event */
		public void mouseReleased(MouseEvent event)
		{
			if (!showOptions)
				currentMenu.getMouseRelease(event);
		}

		/** Handles the mouse being moved
		 * 
		 * @param event the mouse event */
		public void mouseMoved(MouseEvent event)
		{
			if (showOptions)
				optionsMenu.getMouseMovement(event);
			else if (screen.contains(event.getPoint()))
				currentMenu.getMouseMovement(event);
		}

		/** Handles the mouse being dragged
		 * 
		 * @param event the mouse event */
		public void mouseDragged(MouseEvent event)
		{
			if (!showOptions)
				currentMenu.getMouseDragged(event);
		}
	}

	/** Handles key input for the program
	 * 
	 * @author Shiranka Miskin
	 * @version December 2012 */
	private class KeyHandler extends KeyAdapter
	{
		/** Activates any necessary keyboard input for the current menu
		 * 
		 * @param event the key that is pressed */
		public void keyPressed(KeyEvent event)
		{
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				showOptions = !showOptions;
			if (!showOptions)
				currentMenu.getKeyInput(event);
		}
	}

}
