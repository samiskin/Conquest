package UIElements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

/** A button which when clicked allows the user to enter in and edit alphanumeric
 * data
 * @author Shiranka Miskin
 * @version January 2013 */
public class TextField extends TextButton
{

	protected String baseText;

	/** Constructor for a Text Field
	 * @param topLeft the top left corner of the text field
	 * @param size the size of the text field
	 * @throws IOException in case an IO error occurs */
	public TextField(Point topLeft, Dimension size) throws IOException
	{
		super("", topLeft, size);
		baseText = "";
	}

	/** Constructor for a Text Field
	 * @param base the base text of the text field
	 * @param topLeft the top left corner of the text field
	 * @param size the size of the text field
	 * @throws IOException in case an IO error occurs */
	public TextField(String base, Point topLeft, Dimension size)
			throws IOException
	{
		super(base, topLeft, size);
		baseText = base;
	}

	/** Constructor for a Text Field
	 * @param base the base text of the text field
	 * @param topLeft the top left corner of the text field
	 * @param size the size of the text field
	 * @param bgColor the background colour of the text field */
	public TextField(Point topLeft, Dimension size, Color bgColor)
			throws FontFormatException, IOException
	{
		super("", topLeft, size);
		setHoverColor(bgColor);
	}

	/** Given text set the text of the text field to include that text
	 * @param str the string to add to that text */
	public void setEditText(String str)
	{
		text = baseText + str;
	}

	/** Given a mouse event, check to see if this text field is clicked or not
	 * @param event a mouse event given by a mouse listener */
	public boolean getMousePress(MouseEvent event)
	{
		if (super.getMousePress(event))
			return true;
		else
		{
			// Reset clicked if the user presses outside
			// the text field
			clicked = false;
			return false;
		}
	}

	/** Handles keyboard input for the menu
	 * 
	 * @param event The KeyEvent to operate with */
	public void getKeyInput(KeyEvent event)
	{
		if (clicked)
		{
			// Type upper or lower case depending if shift is pressed
			if (Character.isLetterOrDigit(event.getKeyChar()))
				if (event.isShiftDown())
					text += KeyEvent.getKeyText(event.getKeyCode());
				else
					text += Character.toLowerCase(KeyEvent.getKeyText(
							event.getKeyCode()).charAt(0));
			else
				switch (event.getKeyCode())
				{
				case KeyEvent.VK_SPACE:
					text += ' ';
					break;
				case KeyEvent.VK_BACK_SPACE:
					// Only backspace if it does not overwrite the base text
					if (text.length() > baseText.length())
						text = text.substring(0, text.length() - 1);
					break;
				case KeyEvent.VK_ENTER:
					clicked = false;
					break;

				}
		}
	}

}
