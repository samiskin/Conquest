package Menu;

import java.io.IOException;
import java.awt.*;
import java.awt.event.*;

import UIElements.CircleButton;
import UIElements.TextField;

;

/** Lets the user select the name and dimensions of a new map
 * @author Shiranka Miskin
 * @version January 2013 */
public class NewMapMenu extends Menu
{

	private TextField name;
	private TextField width;
	private TextField height;
	private CircleButton goButton;
	
	/** Creates a new menu that can create a new map
	 * @param size the size of the menu
	 * @throws IOException */
	public NewMapMenu(Dimension size) throws IOException
	{
		super(size);
		name = new TextField(new Point(390, 240), new Dimension(500, 40));
		name.setHoverColor(new Color(0, 0, 0, 0));

		width = new TextField(new Point(530, 340), new Dimension(100, 40));
		width.setHoverColor(new Color(0, 0, 0, 0));
		height = new TextField(new Point(650, 340), new Dimension(100, 40));
		height.setHoverColor(new Color(0, 0, 0, 0));

		goButton = new CircleButton(new Point(640, 440), 15);
		goButton.setHoverColor(Color.green);

	}

	/** Gets the currently typed name in the text field
	 * @return the text of the name text field */
	public String getName()
	{
		return name.getText();
	}

	/** Gets the currently entered width
	 * @return the width typed into the width text field */
	public int getWidth()
	{
		return Integer.parseInt(width.getText());
	}

	/** Gets the currently entered height
	 * @return the height that is typed into the height text field */
	public int getHeight()
	{
		return Integer.parseInt(height.getText());
	}

	/** Draws the menu
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		super.draw(g, container);
		name.draw(g, container);
		height.draw(g, container);
		width.draw(g, container);
		goButton.draw(g, container);

		g.setColor(Color.white);
		g.drawString("Map Name", 590, 227);
		g.drawString("Width", 555, 327);
		g.drawString("Height", 674, 327);

	}

	/** Handles mouse movement
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{

		name.getMouseMovement(event);
		height.getMouseMovement(event);
		width.getMouseMovement(event);
		goButton.getMouseMovement(event);

	}

	/** Handles mouse clicks
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		name.getMousePress(event);
		height.getMousePress(event);
		width.getMousePress(event);
		if (goButton.getMousePress(event))
		{
			if (name.getText().isEmpty() || height.getText().isEmpty()
					|| width.getText().isEmpty())
				goButton.setClicked(false);
			else
				completed = true;
		}

	}

	/** Handles keyboard input
	 * @param event the keyboard event */
	public void getKeyInput(KeyEvent event)
	{
		name.getKeyInput(event);

		// Accept only numbers, backspace, enter, and limit it to 2 digits long
		if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE
				|| event.getKeyCode() == KeyEvent.VK_ENTER)
		{
			height.getKeyInput(event);
			width.getKeyInput(event);
		} else if (Character.isDigit(event.getKeyChar()))
		{
			if (height.getText().length() < 2)
				height.getKeyInput(event);
			if (width.getText().length() < 2)
				width.getKeyInput(event);
		}
	}

}
