package UIElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import Board.Sprite;

/** A circular button that can be clicked
 * Can display an image if necessary
 * @author Shiranka Miskin
 * @version January 2013
 */
public class CircleButton extends Clickable
{


	private Point center;
	private int radius;
	private Ellipse2D circle;
	private Sprite img;
	private Sprite hoverImg;
	private Color normal = new Color(255, 255, 255);

	/** Constructor for a new Circle Button
	 * 
	 * @param center the point that is the center of the button on screen
	 * @param radius the radius of the circle button
	 * @param image the image on the button
	 * @param hoverImg the image that will appear when the mouse hovers over
	 *            this button */
	public CircleButton(Point center, int radius, Sprite image, Sprite hoverImg)
	{
		// Radius is subtracted from the center since clickable stores the top
		// left corner
		super(new Point(center.x - radius, center.y - radius));
		this.center = center;
		img = image;
		this.radius = radius;
		circle = new Ellipse2D.Float(topLeft.x, topLeft.y, radius * 2,
				radius * 2);
		this.hoverImg = hoverImg;
	}

	/** Constructor for a Circle Button without any images
	 * 
	 * @param center the point that is the center of the button on screen
	 * @param radius the radius of the circle button */
	public CircleButton(Point center, int radius)
	{
		this(center, radius, null, null);
	}

	/** Returns the radius of the button
	 * 
	 * @return the radius of the button */
	public int getRadius()
	{
		return radius;
	}

	/** Returns the center of the button
	 * 
	 * @return the center of the button */
	public Point getCenter()
	{
		return center;
	}

	/** Checks to see if a given point is inside the circle that is drawn on the
	 * screen
	 * 
	 * @param p the point to check for if it is contained or not
	 * @return true if the point is inside, false if the point is outside */
	public boolean contains(Point p)
	{
		return circle.contains(p);
	}

	/** Draw method for a circle button
	 * 
	 * @param g Graphics
	 * @param container the container to draw to */
	public void draw(Graphics g, Container container)
	{
		Graphics2D g2 = (Graphics2D) g;
		if (mouseOver)
			g2.setPaint(hoverColor);
		else
			g2.setPaint(normal);

		// The border size scales with the size of the button,
		// and is always greater than 1 due to the ceil function
		int borderSize = (int) Math.ceil(1.0 * radius / 15);
		int spaceBetween = (int) Math.ceil(1.0 * radius / 15);

		g2.setStroke(new BasicStroke((float) borderSize));

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.draw(circle);
		g2.fillOval(topLeft.x + borderSize + spaceBetween, topLeft.y
				+ borderSize + spaceBetween,
				(radius - borderSize - spaceBetween) * 2 + 1, (radius
						- borderSize - spaceBetween) * 2 + 1);
		// Draw the images if they exist
		if (img != null)
		{
			if (mouseOver)
				hoverImg.draw(g, topLeft, container);
			else
				img.draw(g, topLeft, container);
		}
	}

}
