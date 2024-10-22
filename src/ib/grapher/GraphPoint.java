package ib.grapher;

/** A class holding one X/Y point and associated error bars. */
public class GraphPoint {

	/** Constructor with both error bars. */
	public GraphPoint (
		double x,
		double y,
		double ex,
		double ey
	) {
		this.x = x;
		this.y = y;
		this.ex = ex;
		this.ey = ey;
	}
	
	/** The x-coordinate of this point. */
	private double x = 0;
	
	/** The y-coordinate of this point. */
	private double y = 0;

	/** The length of this point's horizontal error bars. */
	private double ex = Double.MIN_VALUE;

	/** The length of this point's vertical error bars. */
	private double ey = Double.MIN_VALUE;

	/**
	 * Method to check if this point should have horizontal error bars drawn.
	 * @return true if {@link ex} is greater than {@link Double#MIN_VALUE},
	 * false otherwise.
	 */
	public boolean drawErrorX() {
		return (ex > Double.MIN_VALUE);
	}

	/**
	 * Method to check if this point should have vertical error bars drawn.
	 * @return true if {@link ey} is greater than {@link Double#MIN_VALUE},
	 * false otherwise.
	 */
	public boolean drawErrorY() {
		return (ey > Double.MIN_VALUE);
	}

	// Getters and setters

	/**
	 * @return The x-coordinate of this point relative to the graph as a whole.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The y-coordinate of this point relative to the graph as a whole.
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return How long the horizontal error bars for this point should be
	 * relative to the graph as a whole.
	 */
	public double getErrorX() {
		return ex;
	}

	/**
	 * @return How long the vertical error bars for this point should be
	 * relative to the graph as a whole.
	 */
	public double getErrorY() {
		return ey;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setErrorX(double ex) {
		this.ex = ex;
	}

	public void setErrorY(double ey) {
		this.ey = ey;
	}
}
