package ib.grapher;

import java.awt.Color;

/**
 * A class which stores a single set of plottable data.
 */
public class PlottableData {
	/** Sole constructor. Sets up visibility and placeholders. */
	public PlottableData() {
		this.name = "Unnamed Data Set";
		this.active = true;
		this.linRegActive = false;
	}

	/** The menu this data set is linked to. */
	private PlottableDataMenu menu;

	/** The name of the data set. */
	private String name;
	/** The colour to plot this data in. */
	private Color colour;

	/** The series to use for the X-axis. */
	private Series dataX;
	/** The series to use for the Y-axis. */
	private Series dataY;
	/** The series to use for horizontal error bars. */
	private Series errorBarsX;
	/** The series to use for vertical error bars. */
	private Series errorBarsY;
	/** Whether or not the data set is currently shown on the graph. */
	private boolean active;

	/** Whether or not a linear trendline should be calculated for this data. */
	private boolean linRegActive;
	/** 
	 * When true, linear regression minimizes horizontal distance, rather
	 * than vertical distance.
	 */
	private boolean XAgainstY;
	// Linear regression data, for the form y=ax+b
	private double a;
	private double b;
	/** Pearson correlation coefficient. */
	private double r;

	/**
	 * Does linear regression on this data set, storing the results in
	 * {@link #a}, {@link #b}, and {@link #r}.
	 */
	public void doLinearRegression() {
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumXSquared = 0;
		double sumYSquared = 0;
		int n = 0;
		if (dataX == null || dataY == null) {
			System.err.println("Can't do linear regression without both"
				+ "horizontal and vertical data.");
			return;
		}

		Cell xCell = dataX.getFirst();
		Cell yCell = dataY.getFirst();
		while (xCell != null && yCell != null) {
			try {
				double x = xCell.getNumeric();
				double y = yCell.getNumeric();
				if (XAgainstY) {
					// Flip the inputs in this case because it's the easiest way
					// to calculate it.
					sumX += y;
					sumY += x;
					sumXSquared += y * y;
					sumYSquared += x * x;
				} else {
					// For normal, y-against-x regression
					sumX += x;
					sumY += y;
					sumXSquared += x * x;
					sumYSquared += y * y;
				}
				sumXY += x * y;
				n++;
			} catch (NumberFormatException e) {
				System.out.printf(
					"Skipping invalid data point (%s,%s)%n",
					xCell.getValue(),
					yCell.getValue()
				);
			}
			xCell = xCell.getNext();
			yCell = yCell.getNext();
		}

		try {
			a = (sumXY - (sumX * sumY / n)) / (sumXSquared - (sumX * sumX / n));
			b = (sumY - a * sumX) / n;
			r = (n * sumXY - sumX * sumY) / Math.sqrt(
				(n * sumXSquared - sumX * sumX)
				* (n * sumYSquared - sumY * sumY)
			);

			if (XAgainstY) {
				// Converting back to the form y = ax + b
				b = -(b / a);
				a = 1 / a;
				// r is the same for both types of regression, and doesn't need
				// any conversion.
			}
		} catch (ArithmeticException e) {
			// If there are 0 or 1 values being plotted, no trendline can be
			// calculated.
			a = Double.MIN_VALUE;
			b = Double.MIN_VALUE;
			r = 0;
		}
		menu.updateTrendlineLabel();
	}

	// Getters and setters
	/**
	 * @return The menu this data set is linked to.
	 */
	public PlottableDataMenu getMenu() {
		return menu;
	}

	/**
	 * Links this data set to a menu.
	 * @param pdm The menu to link to.
	 */
	public void setMenu(PlottableDataMenu pdm) {
		menu = pdm;
	}

	/**
	 * Gets the name of the plottable data set.
	 * @return {@link #name}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Changes the name of the plottable data set.
	 * @param s The new name for the set.
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * @return The colour to plot this data set in.
	 */
	public Color getColour() {
		return colour;
	}

	/**
	 * Changes the colour of the data set.
	 * @param col The new colour to use when graphing.
	 */
	public void setColour(Color col) {
		colour = col;
	}

	/**
	 * Gets the series to use for X-coordinates.
	 * @return {@link #dataX}.
	 */
	public Series getDataX() {
		return dataX;
	}

	/**
	 * Changes the series to use for X-coordinates.
	 * @param r The new {@link Series} to use.
	 */
	public void setDataX(Series r) {
		dataX = r;
	}

	/**
	 * Gets the series to use for Y-coordinates.
	 * @return {@link #dataY}.
	 */
	public Series getDataY() {
		return dataY;
	}

	/**
	 * Changes the series to use for Y-coordinates.
	 * @param r The new {@link Series} to use.
	 */
	public void setDataY(Series r) {
		dataY = r;
	}

	/**
	 * Gets the series to use for horizontal error bars.
	 * @return {@link #errorBarsX}.
	 */
	public Series getErrorBarsX() {
		return errorBarsX;
	}

	/**
	 * Changes the series to use for horizontal error bars.
	 * @param r The new {@link Series} to use.
	 */
	public void setErrorBarsX(Series r) {
		errorBarsX = r;
	}

	/**
	 * Gets the series to use for vertical error bars.
	 * @return {@link #errorBarsY}.
	 */
	public Series getErrorBarsY() {
		return errorBarsY;
	}

	/**
	 * Changes the series to use for vertical error bars.
	 * @param r The new {@link Series} to use.
	 */
	public void setErrorBarsY(Series r) {
		errorBarsY = r;
	}

	/**
	 * Checks whether this data set should be plotted.
	 * @return {@link #active}.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Activates or deactivates this data set.
	 * @param active Whether or not the data set should be active.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Checks whether this data set have a trendline drawn.
	 * @return {@link #linRegActive}.
	 */
	public boolean isLinRegActive() {
		return linRegActive;
	}

	/**
	 * Activates or deactivates the trendline for this data set.
	 * @param active Whether or not the data set should be active.
	 */
	public void setLinReg(boolean active) {
		linRegActive = active;
	}

	/**
	 * Checks what type of distance to minimize in linear regression.
	 * @return {@link #XAgainstY}.
	 */
	public boolean ixXAgainstY() {
		return XAgainstY;
	}

	/**
	 * Activates or deactivates X-against-Y regression.
	 * @param active Whether or not to regress X against Y.
	 */
	public void setXAgainstY(boolean active) {
		XAgainstY = active;
	}

	// Linear regression data do not have setters, as they are calculated
	// by an object of this class.

	/**
	 * Gets the a-value, or slope, of the linear regression line of best fit
	 * for this object, in the form y=ax+b.
	 * @return {@link #a}
	 */
	public double getA() {
		return a;
	}

	/**
	 * Gets the b-value, or y-intercept, of the linear regression line of best
	 * fit for this object, in the form y=ax+b.
	 * @return {@link #b}
	 */
	public double getB() {
		return b;
	}

	/**
	 * Gets the Pearson correlation coefficient of the line of best fit.
	 * @return {@link #r}
	 */
	public double getR() {
		return r;
	}
}
