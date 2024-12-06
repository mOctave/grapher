package ib.grapher;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * A class intended to be composited under a {@link Graph}, showing only the
 * graphical portion of the graph window.
 */
public class GraphPanel extends JPanel {
	// MARK: Constructor
	/**
	 * Sole constructor. Sets the graph that this panel will be composited into.
	 * @param parentGraph The graph this panel is part of
	 */
	public GraphPanel (
		Graph parentGraph
	) {
		this.parentGraph = parentGraph;
	}



	// MARK: Properties
	/** The graph this panel is part of. */
	public final Graph parentGraph;
	
	/** The leftmost x-coordinate to render. */
	private double xLower = 0;
	/** The lowest y-coordinate to render. */
	private double yLower = 0;
	/** The rightmost x-coordinate to render. */
	private double xUpper = 0;
	/** The highest y-coordinate to render. */
	private double yUpper = 0;
	/** The width of the vertical axis label for this graph. */
	private int yLabelWidth = 30;



	// MARK: Methods
	@Override
	protected void paintComponent(Graphics g) {
		// Set up font and graphics objects 
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		Graphics2D graphics = (Graphics2D) g;
		FontMetrics metrics = graphics.getFontMetrics(font);
		getYLabelWidth(metrics);

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);

		final String axisTitleX = parentGraph.getAxisTitleX();
		final String axisTitleY = parentGraph.getAxisTitleY();
		final Series stepX = parentGraph.getGridlinesX();
		final Series stepY = parentGraph.getGridlinesY();

		// Draw X-axis title
		graphics.drawString(
			axisTitleX,
			(getWidth() - metrics.stringWidth(axisTitleX)) / 2,
			getHeight() - 5
		);

		// Draw Y-axis title
		Graphics2D rotatedGraphics = (Graphics2D) graphics.create();
		rotatedGraphics.rotate(
			-0.5 * Math.PI,
			metrics.getHeight() + 5,
			(getHeight() + metrics.stringWidth(axisTitleY)) / 2
		);

		rotatedGraphics.drawString(
			axisTitleY,
			metrics.getHeight() + 5,
			(getHeight() + metrics.stringWidth(axisTitleY)) / 2
		);

		rotatedGraphics.dispose();

		// Set up borders and draw gridlines
		graphics.setColor(Main.BLACK);

		if (stepX == null || stepX.length() < 2) {
			xLower = -10;
			xUpper = 10;
		} else {
			try {
				xLower = stepX.getFirst().getNumeric();
			} catch (NumberFormatException e) {
				System.err.println("Warning: Undefined Left Bound");
				xLower = -10;
			}
			try {
				xUpper = stepX.getFirst().getNext().getNumeric();
			} catch (NumberFormatException e) {
				System.err.println("Warning: Undefined Right Bound");
				// Using this instead of 10 because it won't accidentally
				// flip the graph around or cause other issues if xLower
				// was properly defined.
				xUpper = xLower + 20;
			}
		}

		if (stepY == null || stepY.length() < 2) {
			yLower = -10;
			yUpper = 10;
			graphics.drawLine(
				getRelativeX(xLower),
				getRelativeY(0),
				getRelativeX(xUpper),
				getRelativeY(0)
			);
			graphics.drawString(
				"0",
				getRelativeX(xLower) - 5 - metrics.stringWidth("0"),
				getRelativeY(0)
					+ metrics.getHeight() / 3
			);
		} else {
			try {
				yLower = stepY.getFirst().getNumeric();
			} catch (NumberFormatException e) {
				System.err.println("Warning: Undefined Lower Bound");
				yLower = -10;
			}
			try {
				yUpper = stepY.getFirst().getNext().getNumeric();
			} catch (NumberFormatException e) {
				System.err.println("Warning: Undefined Upper Bound");
				// Using this instead of 10 because it won't accidentally
				// flip the graph around or cause other issues if yLower
				// was properly defined.
				yUpper = yLower + 20;
			}
			if (stepY.length() > 2) {
				for (Cell c : stepY) {
					try {
						if (c.getIndex() > 1) {
							graphics.drawLine(
								getRelativeX(xLower),
								getRelativeY(c.getNumeric()),
								getRelativeX(xUpper),
								getRelativeY(c.getNumeric())
							);
							graphics.drawString(
								c.getValue(),
								getRelativeX(xLower) - 5
									- metrics.stringWidth(c.getValue()),
								getRelativeY(c.getNumeric())
									+ metrics.getHeight() / 3
							);
						}
					} catch (NumberFormatException e) {
						// Usually caused by an empty cell, so nothing
						// to worry about.
					}
				}
			}
		}

		if (stepX == null || stepX.length() < 2) {
			graphics.drawLine(
				getRelativeX(0),
				getRelativeY(yLower),
				getRelativeX(0),
				getRelativeY(yUpper)
			);
			graphics.drawString(
				"0",
				getRelativeX(0) - metrics.stringWidth("0") / 2,
				getRelativeY(yLower) + 15
			);
		} else if (stepX.length() > 2) {
			for (Cell c : stepX) {
				try {
					if (c.getIndex() > 1) {
						graphics.drawLine(
							getRelativeX(c.getNumeric()),
							getRelativeY(yLower),
							getRelativeX(c.getNumeric()),
							getRelativeY(yUpper)
						);
						graphics.drawString(
							c.getValue(),
							getRelativeX(c.getNumeric())
								- metrics.stringWidth(c.getValue()) / 2,
							getRelativeY(yLower) + 15
						);
					}
				} catch (NumberFormatException e) {
					// Usually caused by an empty cell, so nothing
					// to worry about.
				}
			}
		}

		List<GraphPoint> points = new ArrayList<>();

		// Draw plottable data sets
		for (PlottableData pd : Main.getPlottableTable().getDataSets()) {
			graphics.setColor(pd.getColour());

			if (
				(!pd.isActive())
				|| pd.getDataX() == null
				|| pd.getDataY() == null
			)
				continue;

			Cell activeX = pd.getDataX().getFirst();
			Cell activeY = pd.getDataY().getFirst();
			Cell ebX = null;
			Cell ebY = null;

			if (pd.getErrorBarsX() != null)
				ebX = pd.getErrorBarsX().getFirst();
			if (pd.getErrorBarsY() != null)
				ebY = pd.getErrorBarsY().getFirst();

			while (true) {
				try {
					GraphPoint newPoint = new GraphPoint(
						activeX.getNumeric(),
						activeY.getNumeric(),
						Double.MIN_VALUE,
						Double.MIN_VALUE
					);

					if (ebX != null) {
						try {
							newPoint.setErrorX(ebX.getNumeric());
						} catch (NumberFormatException e) {
							// Non-numeric data. Not actually an error,
							// but no trendline will be drawn
							System.out.println("Non-numeric error bar skipped.");
						}
					}

					if (ebY != null) {
						try {
							newPoint.setErrorY(ebY.getNumeric());
						} catch (NumberFormatException e) {
							// Non-numeric data. Not actually an error,
							// but no trendline will be drawn
							System.out.println("Non-numeric error bar skipped.");
						}
					}

					points.add(newPoint);

				} catch (NumberFormatException e) {
					// Non-numeric data. Not actually an error, but
					// it'll skip the pair of cells
					System.out.println("Non-numeric data pair skipped.");
				}

				if (activeX.getNext() == null || activeY.getNext() == null)
					break;

				activeX = activeX.getNext();
				activeY = activeY.getNext();
				if (ebX != null)
					ebX = ebX.getNext();
				if (ebY != null)
					ebY = ebY.getNext();
			}

			GraphPoint lastPoint = null;
			for (GraphPoint point : points) {
				drawPoint(point, graphics);
				if (parentGraph.getGraphType() == Graph.LINE && lastPoint != null) {
					graphics.drawLine(
						getRelativeX(point.getX()),
						getRelativeY(point.getY()),
						getRelativeX(lastPoint.getX()),
						getRelativeY(lastPoint.getY())
					);
				}
				lastPoint = point;
			}

			// Draw trendline
			if (pd.isLinRegActive()) {
				pd.doLinearRegression();
				if (pd.getA() != Double.MIN_VALUE && pd.getB() != Double.MIN_VALUE) {
					int[] lineCoords = calculateTrendline(pd.getA(), pd.getB());
					System.out.printf("[(%d, %d), (%d, %d)]%n", lineCoords[0], lineCoords[1], lineCoords[2], lineCoords[3]);
					graphics.drawLine(lineCoords[0], lineCoords[1], lineCoords[2], lineCoords[3]);
				}
			}
		}

		// Clean up
		graphics.dispose();
	}



	private void getYLabelWidth(FontMetrics metrics) {
		int maxWidth = 30;

		try {
			Cell c = parentGraph.getGridlinesY().getFirst().getNext().getNext();
			while (c != null) {
				int w = metrics.stringWidth(c.getValue()) + 5;
				if (w > maxWidth)
					maxWidth = w;
				
				c = c.getNext();
			}
		} catch (NullPointerException e) {
			// Presumably uses default Y-coordinates.
		}

		yLabelWidth = maxWidth;
	}



	private int getRelativeX(double x) {
		int labelOffset = parentGraph.getAxisTitleY().length() == 0 ? 0 : 20;
		final Series stepX = parentGraph.getGridlinesX();

		labelOffset += (stepX != null && stepX.length() <= 2)
			? 0 : yLabelWidth;
		return (int) ((x - xLower) / (xUpper - xLower) * 
			(getWidth() - (20 + labelOffset))) + (10 + labelOffset);
	}



	private int getRelativeY(double y) {
		int labelOffset = parentGraph.getAxisTitleX().length() == 0 ? 0 : 20;
		final Series stepY = parentGraph.getGridlinesY();

		labelOffset += (stepY != null && stepY.length() <= 2) ? 0 : 15;
		return getHeight() - (int) ((y - yLower) / (yUpper - yLower) *
			(getHeight() - (20 + labelOffset))) - (10 + labelOffset);
	}



	private void drawPoint(GraphPoint point, Graphics2D graphics) {
		double x = point.getX();
		double y = point.getY();

		// Don't draw points outside the graph's bounds.
		if (
			x < xLower
			|| x > xUpper
			|| y < yLower
			|| y > yUpper
		) return;

		// Draw the point itself.
		graphics.drawOval(
			getRelativeX(x) - 3,
			getRelativeY(y) - 3,
			6,
			6
		);
		graphics.drawOval(
			getRelativeX(x) - 2,
			getRelativeY(y) - 2,
			4,
			4
		);

		if (point.drawErrorX()) {
			double err = point.getErrorX();

			graphics.drawLine(
				getRelativeX(x - err),
				getRelativeY(y),
				getRelativeX(x + err),
				getRelativeY(y)
			);
			// Draw caps
			graphics.drawLine(
				getRelativeX(x - err),
				getRelativeY(y) - 4,
				getRelativeX(x - err),
				getRelativeY(y) + 4
			);
			graphics.drawLine(
				getRelativeX(x + err),
				getRelativeY(y) - 4,
				getRelativeX(x + err),
				getRelativeY(y) + 4
			);
		}

		if (point.drawErrorY()) {
			double err = point.getErrorY();

			graphics.drawLine(
				getRelativeX(x),
				getRelativeY(y - err),
				getRelativeX(x),
				getRelativeY(y + err)
			);
			// Draw caps
			graphics.drawLine(
				getRelativeX(x) - 4,
				getRelativeY(y - err),
				getRelativeX(x) + 4,
				getRelativeY(y - err)
			);
			graphics.drawLine(
				getRelativeX(x) - 4,
				getRelativeY(y + err),
				getRelativeX(x) + 4,
				getRelativeY(y + err)
			);
		}
	}



	private int[] calculateTrendline(double a, double b) {
		// Select first point
		double x1 = xLower;
		double y1 = a * x1 + b;

		if (y1 < yLower) {
			y1 = yLower;
			x1 = (y1 - b) / a;
		} else if(y1 > yUpper) {
			y1 = yUpper;
			x1 = (y1 - b) / a;
		}

		// Select second point
		double x2 = xUpper;
		double y2 = a * x2 + b;

		if (y2 < yLower) {
			y2 = yLower;
			x2 = (y2 - b) / a;
		} else if(y2 > yUpper) {
			y2 = yUpper;
			x2 = (y2 - b) / a;
		}

		return new int[]{
			getRelativeX(x1),
			getRelativeY(y1),
			getRelativeX(x2),
			getRelativeY(y2)
		};
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets the relative coordinate that marks the leftmost
	 * x-coordinate drawn on this graph.
	 * @return {@link #xLower}
	 */
	public double getLeftBound() {
		return xLower;
	}



	/**
	 * Getter: Gets the relative coordinate that marks the lowest
	 * y-coordinate drawn on this graph.
	 * @return {@link #yLower}
	 */
	public double getLowerBound() {
		return yLower;
	}



	/**
	 * Getter: Gets the relative coordinate that marks the rightmost
	 * x-coordinate drawn on this graph.
	 * @return {@link #xUpper}
	 */
	public double getRightBound() {
		return xUpper;
	}



	/**
	 * Getter: Gets the relative coordinate that marks the highest
	 * y-coordinate drawn on this graph.
	 * @return {@link #yUpper}
	 */
	public double getUpperBound() {
		return yUpper;
	}



	/**
	 * Getter: Gets the calculated width of the vertical axis label for
	 * this graph.
	 * y-coordinate drawn on this graph.
	 * @return {@link #yLabelWidth}
	 */
	public double getVerticalLabelWidth() {
		return yLabelWidth;
	}

	// No properties have setters, as they are meant to be calculated internally.
}
