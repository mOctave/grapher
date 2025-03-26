package ib.grapher;

import java.awt.Color;
import java.util.Arrays;

/**
 * A class which stores a single set of plottable data.
 */
public class PlottableData {
	// MARK: Constructor
	/** Sole constructor. Sets up visibility and placeholders. */
	public PlottableData() {
		name = "Unnamed Data Set";
		active = true;
		linRegActive = false;
	}



	// MARK: Properties
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
	/** Slope of the linear regression trendline. */
	private double a;
	/** Y-intercept of the linear regression trendline. */
	private double b;
	/** Pearson correlation coefficient. */
	private double r;



	// MARK: Methods
	/**
	 * Saves this plottable data set to the output file, overwriting an existing
	 * entry for the set. If this plottable data has not yet been added,
	 * {@link FileDataManager#encodeForInsertion(PlottableData)} should be used
	 * instead.
	 */
	public void save() {
		System.out.println("SAVE: Plottable");
		int index = Main.getPlottableTable().getDataSets().indexOf(this);

		int offset = FileDataManager.getOffset(
			FileDataManager.PLOTTABLE,
			index
		);

		Byte[] ba = new Byte[321];
		System.arraycopy(
			Main.stringToByteArray(name, 64),
			0, ba, 0, 64
		);
		Main.seriesCopy(dataX, ba, 64);
		Main.seriesCopy(dataY, ba, 128);
		Main.seriesCopy(errorBarsX, ba, 192);
		Main.seriesCopy(errorBarsY, ba, 256);

		byte options = 0;
		if (isActive())
			options |= 1;
		if (isLinRegActive())
			options |= 2;
		if (isXAgainstY())
			options |= 4;

		ba[320] = options;

		FileDataManager.writeByteList(Arrays.asList(ba), offset);
	}



	/**
	 * Does linear regression on this data set, storing the results in
	 * {@link #a}, {@link #b}, and {@link #r}.
	 */
	public void doLinearRegression() {
		// This algorithm was adapted from the equations in Stewart, n.d., and tested
		// against the output of a TI-84 graphing calculator.
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
					"Skipping invalid data point (%s,%s) in linear regression.%n",
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



	// MARK: Getters / Setters
	/**
	 * Getter: Gets this data set's linked menu.
	 * @return {@link #menu}
	 */
	public PlottableDataMenu getMenu() {
		return menu;
	}

	/**
	 * Setter: Links this data set to a different menu.
	 * @param menu The new {@link #menu} for this data set
	 */
	public void setMenu(PlottableDataMenu menu) {
		this.menu = menu;
	}



	/**
	 * Getter: Gets this data set's name.
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter: Changes the name of this data set.
	 * @param name The new {@link #name} for this data set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * Getter: Gets the colour to draw this data set in.
	 * @return {@link #colour}
	 */
	public Color getColour() {
		return colour;
	}

	/**
	 * Setter: Changes the colour to draw this data set in.
	 * @param colour The new {@link #colour} for this data set
	 */
	public void setColour(Color colour) {
		this.colour = colour;
	}



	/**
	 * Getter: Gets the series being used to plot X-coordinates.
	 * @return {@link #dataX}
	 */
	public Series getDataX() {
		return dataX;
	}

	/**
	 * Setter: Changes the series used to plot X-coordinates.
	 * @param dataX The new value for {@link #dataX}.
	 */
	public void setDataX(Series dataX) {
		this.dataX = dataX;
	}



	/**
	 * Getter: Gets the series being used to plot Y-coordinates.
	 * @return {@link #dataY}
	 */
	public Series getDataY() {
		return dataY;
	}

	/**
	 * Setter: Changes the series used to plot Y-coordinates.
	 * @param dataY The new value for {@link #dataY}.
	 */
	public void setDataY(Series dataY) {
		this.dataY = dataY;
	}



	/**
	 * Getter: Gets the series being used to plot horizontal error bars.
	 * @return {@link #errorBarsX}
	 */
	public Series getErrorBarsX() {
		return errorBarsX;
	}

	/**
	 * Setter: Changes the series used to plot horizontal error bars.
	 * @param errorBarsX The new value for {@link #errorBarsX}.
	 */
	public void setErrorBarsX(Series errorBarsX) {
		this.errorBarsX = errorBarsX;
	}



	/**
	 * Getter: Gets the series being used to plot vertical error bars.
	 * @return {@link #errorBarsY}
	 */
	public Series getErrorBarsY() {
		return errorBarsY;
	}

	/**
	 * Setter: Changes the series used to plot horizontal error bars.
	 * @param errorBarsY The new value for {@link #errorBarsY}.
	 */
	public void setErrorBarsY(Series errorBarsY) {
		this.errorBarsY = errorBarsY;
	}



	/**
	 * Getter: Checks whether or not this series is currently active.
	 * @return {@link #active}
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Setter: Activates or deactivates this data set.
	 * @param active Whether or not this data set should be active.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}



	/**
	 * Getter: Checks whether or not linear regression should be done for this
	 * data set.
	 * @return {@link #linRegActive}
	 */
	public boolean isLinRegActive() {
		return linRegActive;
	}

	/**
	 * Setter: Activates or deactivates linear regression for this data set.
	 * @param linRegActive Whether or not to do linear regression.
	 */
	public void setLinRegActive(boolean linRegActive) {
		this.linRegActive = linRegActive;
	}



	/**
	 * Getter: Checks which distance to minimize in linear regression.
	 * @return {@link #XAgainstY}
	 */
	public boolean isXAgainstY() {
		return XAgainstY;
	}

	/**
	 * Setter: Activates or deactivates X-against-Y regression.
	 * @param XAgainstY Whether or not to regress X against Y.
	 */
	public void setXAgainstY(boolean XAgainstY) {
		this.XAgainstY = XAgainstY;
	}



	// Linear regression data do not have setters, as they are calculated
	// by an object of this class.



	/**
	 * Getter: Gets the a-value, or slope, of the linear regression line of
	 * best fit for this object, in the form y=ax+b.
	 * @return {@link #a}
	 */
	public double getA() {
		return a;
	}



	/**
	 * Getter: Gets the b-value, or y-intercept, of the linear regression line
	 * of best fit for this object, in the form y=ax+b.
	 * @return {@link #b}
	 */
	public double getB() {
		return b;
	}



	/**
	 * Getter: Gets the Pearson correlation coefficient of the line of best fit.
	 * @return {@link #r}
	 */
	public double getR() {
		return r;
	}
}
