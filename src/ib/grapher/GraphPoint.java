package ib.grapher;

/** A class holding one X/Y point and associated error bars. */
public class GraphPoint {
	// MARK: Constructor
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
	


	// MARK: Properties
	/** The x-coordinate of this point. */
	private double x = 0;
	/** The y-coordinate of this point. */
	private double y = 0;
	/** The length of this point's horizontal error bars. */
	private double ex = Double.MIN_VALUE;
	/** The length of this point's vertical error bars. */
	private double ey = Double.MIN_VALUE;



	// MARK: Methods
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



	// Getters / Setters
	/**
	 * Getter: Gets x-coordinate of this point relative to the graph as a whole.
	 * @return {@link #x}
	 */
	public double getX() {
		return x;
	}

	/**
	 * Setter: Changes the x-coordinate for this point relative to the graph.
	 * @param x The new {@link #x}-coordinate for this point
	 */
	public void setX(double x) {
		this.x = x;
	}



	/**
	 * Getter: Gets y-coordinate of this point relative to the graph as a whole.
	 * @return {@link #y}
	 */
	public double getY() {
		return y;
	}

	/**
	 * Setter: Changes the y-coordinate for this point relative to the graph.
	 * @param x The new {@link #y}-coordinate for this point
	 */
	public void setY(double y) {
		this.y = y;
	}



	/**
	 * Getter: Gets horizontal error bar length of this point relative to the
	 * graph as a whole.
	 * @return {@link #ex}
	 */
	public double getErrorX() {
		return ex;
	}

	/**
	 * Setter: Changes the horizontal error bar length for this point relative
	 * to the graph.
	 * @param ex The new {@link #ex}-coordinate for this point
	 */
	public void setErrorX(double ex) {
		this.ex = ex;
	}



	/**
	 * Getter: Gets vertical error bar length of this point relative to the
	 * graph as a whole.
	 * @return {@link #ey}
	 */
	public double getErrorY() {
		return ey;
	}



	/**
	 * Setter: Changes the vertical error bar length for this point relative
	 * to the graph.
	 * @param ey The new {@link #ey}-coordinate for this point
	 */
	public void setErrorY(double ey) {
		this.ey = ey;
	}
}
