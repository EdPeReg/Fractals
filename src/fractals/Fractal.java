package fractals;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

// TODO: After clicking the new coordinates, the origin point don't start depending
// where the cursor clicked, only this happens while ZOOMING.
// TODO: After zooming, when you zoom again, the figure moves, no se mantiene en el mismo lugar.

public class Fractal
{
    private Graphics2D g2;
    private Point point;
    private String ruleAGosper;
    private String ruleBGosper;
    private String ruleKoch;
    private int n;
    private double angle;
    private static final int SIZE = 30;

    public Fractal()
    {
    	g2 = null;
    	point = new Point();
    	point.x = 0;
    	point.y = 0;
    	ruleAGosper = "A-B--B+A++AA+B-";
    	ruleBGosper = "+A-BB--B-A++A+B";
    	ruleKoch = "F+F--F+F";
    	n = 3;
    	angle = 90.0;
    }

    public void forward()
    {
        // Obtain what (x,y) are given an angle.
        double endx = point.getX() - SIZE * Math.cos(Math.toRadians(angle));
        double endy = point.getY() - SIZE * Math.sin(Math.toRadians(angle));
        g2.draw(new Line2D.Double(point.getX(), point.getY(), endx, endy));

        // Update our src x,y to start from the last point.
        point.setLocation(endx, endy);
    }

    public void kochCurve(int n)
    {
        if(n == 0) {
            forward();
            return;
        }

		// Just follow the rule.
		for(int i = 0; i < ruleKoch.length(); ++i)
		{
			if(ruleKoch.charAt(i) == '-')
				angle += 60;
			else if(ruleKoch.charAt(i) == '+')
				angle -= 60;
			else if(ruleKoch.charAt(i) == 'F')
				kochCurve(n-1);
		}
    }

    public void gosperFlawsnake(int n, String rule)
    {
        if(n == 0) {
            forward();
            return;
        }

		for(int i = 0; i < rule.length(); ++i)
		{
			if(rule.charAt(i) == '-')
				angle += 60;
			else if(rule.charAt(i) == '+')
				angle -= 60;
			// Apply the corresponding rules.
			else if(rule.charAt(i) == 'A')
				gosperFlawsnake(n-1, ruleAGosper);
			else if(rule.charAt(i) == 'B')
				gosperFlawsnake(n-1, ruleBGosper);
		}
    }

	public static void main(String[] args)
	{
		// Construct this GUI on the Event Dispatch Thread.
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("FRACTALS");
				// Just instantiate to create the panel.
				Fractal fractal = new Fractal();
				MyPanel myPanel = fractal.new MyPanel();

				frame.setSize(800,800);
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				myPanel.setBackground(Color.black);
				frame.add(myPanel);
				frame.setVisible(true);
			}
		});
	}

	public class MyPanel extends JPanel
	{
		private Point mousePt;
		private double scale = 1.0;

		public MyPanel()
		{
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e)
				{
					// Left mouse button is click.
					if(e.getButton() == MouseEvent.BUTTON1)
					{
						// Update the start point depending where the click was made.
						point.setLocation(e.getPoint().getX(), e.getPoint().getY());
						mousePt = point;
						// This will repaint everything in the corresponding position.
						revalidate();
						repaint();
					}
				}
			});

			addMouseWheelListener(new MouseAdapter() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					// Scale and delta are used for zoom.
					double delta = 0.05 * e.getPreciseWheelRotation();
					scale += delta;

					// Is important to have this here, having this will have the
					// correct start (x,y) when you zoom.
					point.setLocation(e.getPoint().getX(), e.getPoint().getY());

					revalidate();
					repaint();
				}
			});

			addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e)
				{
					// Get the distance (x,y) and set it to our new point.
					int dx = e.getX() - mousePt.x;
					int dy = e.getY() - mousePt.y;
					point.setLocation(point.x + dx, point.y + dy);
					revalidate();
					repaint();
				}
			});
		}

		@Override
		public void paintComponent(Graphics g)
		{
			// Without this it seems that the panel is not applied.
			super.paintComponent(g);
			g2 = (Graphics2D) g;
			g2.setColor(Color.red);

			// Only apply some scale to our graphic, that will cause the zoom effect.
			AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            g2.setTransform(at);

//			gosperFlawsnake(n, ruleAGosper);
			kochCurve(n);
		}
	}
}
