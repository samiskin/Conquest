package UIElements;

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
import java.io.IOException;

import Menu.Main;

/** A button that displays text on it
 * @author Shiranka Miskin
 * @version January 2013 */
public class TextButton extends RectangleButton implements
		Comparable<TextButton>
{

	private Color textColor = new Color(29, 27, 48);
	protected String text;
	private Font font;

	/** Constructor for a text button
	 * @param text the text on the text button
	 * @param font the font of the text
	 * @param position the position of the text button
	 * @param size the size of the text button */
	public TextButton(String text, Font font, Point position, Dimension size)
	{
		super(position, size, null, null);
		this.text = text;
		this.font = font;
	}

	/** Constructor for a text button
	 * @param text the text on the text button
	 * @param font the font of the text
	 * @param position the position of the text button
	 * @param size the size of the text button
	 * @param normal the colour to set the button to normally
	 * @param hover the colour to set the button to when hovered over */
	public TextButton(String text, Font font, Point position, Dimension size,
			Color normal, Color hover)
	{
		this(text, font, position, size);
		this.hoverColor = hover;
		this.normalColor = normal;
	}

	/** Constructor for a text button
	 * @param text the text on the text button
	 * @param position the position of the text button
	 * @param size the size of the text button
	 * @throws IOException in case the font cannot be read */
	public TextButton(String text, Point position, Dimension size)
			throws IOException
	{
		this(text, Main.getFont("Kalinga", 18), position, size);
	}

	/** Constructor for a text button
	 * @param text the text on the text button
	 * @param font the font of the text
	 * @param position the position of the text button
	 * @param size the size of the text button
	 * @param hover the colour to set the button to when hovered over */
	public TextButton(String text, Font font, Point position, Dimension size,
			Color hover)
	{
		this(text, font, position, size);
		this.hoverColor = hover;
	}

	/** Constructor for a text button
	 * @param text the text on the text button
	 * @param position the position of the text button
	 * @param size the size of the text button
	 * @param hover the colour to set the button to when hovered over
	 * @throws IOException in case the font cannot be read */
	public TextButton(String text, Point position, Dimension size, Color hover)
			throws IOException
	{
		this(text, Main.getFont("Kalinga", 18), position, size, hover);
	}

	/** Constructor for a text button
	 * @param text the text on the text button
	 * @param font the font of the text
	 * @param topLeft the top left corner of the button
	 * @param bottomRight the bottom right corner of the button */
	public TextButton(String text, Font font, Point topLeft, Point bottomRight)
	{
		this(text, font, topLeft, new Dimension(Math.abs(bottomRight.x
				- topLeft.x), Math.abs(bottomRight.y - topLeft.y)));
	}

	/** Constructor for a text button
	 * @param text the text on the text button
	 * @param topLeft the top left corner of the button
	 * @param bottomRight the bottom right corner of the button
	 * @throws IOException in case the font cannot be read */
	public TextButton(String text, Point topLeft, Point bottomRight)
			throws IOException
	{
		this(text, Main.getFont("Kalinga", 18), topLeft, bottomRight);
	}

	/** Sets the colour of the text to this colour
	 * @param color the colour to set the text colour to */
	public void setTextColor(Color color)
	{
		textColor = color;
	}

	/** Returns the text of this button
	 * @return the text of this button */
	public String getText()
	{
		return text;
	}

	/** Returns the text of this button to override the standard toString() for
	 * debugging
	 * @return the text on this button */
	public String toString()
	{
		return text;
	}

	/** Sets the text of this button to the given string
	 * @param str the string to set the text of this button to */
	public void setText(String str)
	{
		text = str;
	}

	/** Sets the font of this button to the given font
	 * @param font the font to set the font of this button to */
	public void setFont(Font font)
	{
		this.font = font;
	}

	/** Compares to text buttons (required to make them comparable) just compares
	 * the value of their text
	 * @param the text button to compare this one to
	 * @return 1 if this is greater, -1 if this is less, 0 if they are equal */
	public int compareTo(TextButton other)
	{
		return text.compareTo(other.text);
	}

	/** Draw method for Text Buttons
	 * @param g Graphics
	 * @param container the container to draw to */
	public void draw(Graphics g, Container container)
	{
		super.draw(g, container);

		g.setFont(font);
		if (!(mouseOver || clicked))
			g.setColor(textColor);
		else
			g.setColor(Color.white);
		
		// Center the text within the box
		FontMetrics fontMetrics = g.getFontMetrics(font);
		Rectangle textBounds = new Rectangle(fontMetrics.stringWidth(text),
				fontMetrics.getAscent() - fontMetrics.getLeading() - 3);
		int stringX = box.x + (box.width - textBounds.width) / 2;
		int stringY = box.y + (box.height + textBounds.height) / 2;
		Graphics2D g2 = (Graphics2D) g;
		
		// Make sure the text is drawn smoothly
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.drawString(text, stringX, stringY);
	}

}