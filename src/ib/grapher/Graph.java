package ib.grapher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.imageio.ImageIO;

/**
 * The visual graph associated with a project.
 */
public class Graph extends JFrame {
	/** Sole constructor. */
	public Graph() {
		// Initialize non-GUI attributes
		graphTitle = "New Graph";
		axisTitleX = "";
		axisTitleY = "";
		graphType = SCATTERPLOT;

		// Set up GUI
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(400, 200));

		panelGraph = new JPanel();
		panelGraph.setPreferredSize(new Dimension(400, 300));
		panelGraph.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		panelGraph.setBackground(Main.WHITE);
		panelGraph.setLayout(new BorderLayout());
		add(panelGraph, BorderLayout.CENTER);

		// Set up graph
		fieldGraphTitle = new JTextField("Untitled Graph");
		fieldGraphTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
		fieldGraphTitle.setHorizontalAlignment(SwingConstants.CENTER);
		fieldGraphTitle.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		fieldGraphTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setGraphTitle(fieldGraphTitle.getText());
				Main.updateAllComponents();
			}
		});
		fieldGraphTitle.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {};

			public void focusLost(FocusEvent e) {
				Graph.this.setGraphTitle(fieldGraphTitle.getText());
				Main.updateAllComponents();
			};
		});
		panelGraph.add(fieldGraphTitle, BorderLayout.NORTH);

		drawingPanel = new JPanel() {
			private double xLower = 0;
			private double yLower = 0;
			private double xUpper = 0;
			private double yUpper = 0;

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
							double x = activeX.getNumeric();
							double y = activeY.getNumeric();
							if (
								x >= xLower
								&& x <= xUpper
								&& y >= yLower
								&& y <= yUpper
							) {
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
								if (ebX != null) {
									// Add horizontal error bars
									try {
										graphics.drawLine(
											getRelativeX(x - ebX.getNumeric()),
											getRelativeY(y),
											getRelativeX(x + ebX.getNumeric()),
											getRelativeY(y)
										);
										// Draw caps
										graphics.drawLine(
											getRelativeX(x - ebX.getNumeric()),
											getRelativeY(y) - 4,
											getRelativeX(x - ebX.getNumeric()),
											getRelativeY(y) + 4
										);
										graphics.drawLine(
											getRelativeX(x + ebX.getNumeric()),
											getRelativeY(y) - 4,
											getRelativeX(x + ebX.getNumeric()),
											getRelativeY(y) + 4
										);
									} catch (NumberFormatException e) {
										// Non-numeric data. Not actually an error,
										// but no trendline will be drawn
										System.out.println("Non-numeric error bar skipped.");
									}
								}
								if (ebY != null) {
									// Add horizontal error bars
									try {
										graphics.drawLine(
											getRelativeX(x),
											getRelativeY(y - ebY.getNumeric()),
											getRelativeX(x),
											getRelativeY(y + ebY.getNumeric())
										);
										// Draw caps
										graphics.drawLine(
											getRelativeX(x) - 4,
											getRelativeY(y - ebY.getNumeric()),
											getRelativeX(x) + 4,
											getRelativeY(y - ebY.getNumeric())
										);
										graphics.drawLine(
											getRelativeX(x) - 4,
											getRelativeY(y + ebY.getNumeric()),
											getRelativeX(x) + 4,
											getRelativeY(y + ebY.getNumeric())
										);
									} catch (NumberFormatException e) {
										// Non-numeric data. Not actually an error,
										// but no trendline will be drawn
										System.out.println("Non-numeric error bar skipped.");
									}
								}
							}
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

					// Draw trendline
					if (pd.isLinRegActive()) {
						System.out.println("Linear Regression Active");
						pd.doLinearRegression();
						System.out.println(pd.getA());
						System.out.println(pd.getB());
						if (pd.getA() != Double.MIN_VALUE && pd.getB() != Double.MIN_VALUE) {
							int[] points = calculateTrendline(pd.getA(), pd.getB());
							System.out.printf("[(%d, %d), (%d, %d)]%n", points[0], points[1], points[2], points[3]);
							graphics.drawLine(points[0], points[1], points[2], points[3]);
						}
					}
				}

				// Clean up
				graphics.dispose();
			}

			private int yLabelWidth = 30;

			private void getYLabelWidth(FontMetrics metrics) {
				int maxWidth = 30;

				try {
					Cell c = stepY.getFirst().getNext().getNext();
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
				System.out.println(yLabelWidth);
			}

			private int getRelativeX(double x) {
				int labelOffset = axisTitleY.length() == 0 ? 0 : 20;

				labelOffset += (stepX != null && stepX.length() <= 2)
					? 0 : yLabelWidth;
				return (int) ((x - xLower) / (xUpper - xLower) * 
					(getWidth() - (20 + labelOffset))) + (10 + labelOffset);
			}

			private int getRelativeY(double y) {
				int labelOffset = axisTitleX.length() == 0 ? 0 : 20;
				labelOffset += (stepY != null && stepY.length() <= 2) ? 0 : 15;
				return getHeight() - (int) ((y - yLower) / (yUpper - yLower) *
					(getHeight() - (20 + labelOffset))) - (10 + labelOffset);
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
		};
		drawingPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panelGraph.add(drawingPanel, BorderLayout.CENTER);

		panelMenu = new JPanel();
		panelMenu.setLayout(new GridBagLayout());
		add(panelMenu, BorderLayout.EAST);

		// Set up menu panel
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;

		labelType = new JLabel("Graph Type", SwingConstants.CENTER);
		labelType.setPreferredSize(new Dimension(180, 20));
		panelMenu.add(labelType, constraints);
		constraints.gridy++;

		selectorType = new JComboBox<String>(new String[]{SCATTERPLOT, LINE, BAR});
		selectorType.setPreferredSize(new Dimension(180, 20));
		selectorType.setSelectedItem(SCATTERPLOT);
		panelMenu.add(selectorType, constraints);
		constraints.gridy++;

		labelGridlineX = new JLabel("Horizontal Gridlines", SwingConstants.CENTER);
		labelGridlineX.setPreferredSize(new Dimension(180, 20));
		panelMenu.add(labelGridlineX, constraints);
		constraints.gridy++;

		selectorGridlineX = new SeriesSelector();
		selectorGridlineX.setPreferredSize(new Dimension(180, 20));
		selectorGridlineX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setGridlinesX((Series) selectorGridlineX.getSelectedItem());
				Main.updateAllComponents();
			}
		});
		panelMenu.add(selectorGridlineX, constraints);
		constraints.gridy++;

		labelGridlineY = new JLabel("Vertical Gridlines", SwingConstants.CENTER);
		labelGridlineY.setPreferredSize(new Dimension(180, 20));
		panelMenu.add(labelGridlineY, constraints);
		constraints.gridy++;

		selectorGridlineY = new SeriesSelector();
		selectorGridlineY.setPreferredSize(new Dimension(180, 20));
		selectorGridlineY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setGridlinesY((Series) selectorGridlineY.getSelectedItem());
				Main.updateAllComponents();
			}
		});
		panelMenu.add(selectorGridlineY, constraints);
		constraints.gridy++;

		JLabel labelXAxisTitle = new JLabel("X-Axis Title", SwingConstants.CENTER);
		labelXAxisTitle.setPreferredSize(new Dimension(180, 20));
		panelMenu.add(labelXAxisTitle, constraints);
		constraints.gridy++;

		fieldGraphHorizontalAxis = new JTextField();
		fieldGraphHorizontalAxis.setPreferredSize(new Dimension(180, 20));
		fieldGraphHorizontalAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setAxisTitleX(fieldGraphHorizontalAxis.getText());
				Main.updateAllComponents();
			}
		});
		fieldGraphHorizontalAxis.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {};

			public void focusLost(FocusEvent e) {
				Graph.this.setAxisTitleX(fieldGraphHorizontalAxis.getText());
			};
		});
		panelMenu.add(fieldGraphHorizontalAxis, constraints);
		constraints.gridy++;

		JLabel labelYAxisTitle = new JLabel("Y-Axis Title", SwingConstants.CENTER);
		labelYAxisTitle.setPreferredSize(new Dimension(180, 20));
		panelMenu.add(labelYAxisTitle, constraints);
		constraints.gridy++;

		fieldGraphVerticalAxis = new JTextField();
		fieldGraphVerticalAxis.setPreferredSize(new Dimension(180, 20));
		fieldGraphVerticalAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setAxisTitleY(fieldGraphVerticalAxis.getText());
				Main.updateAllComponents();
			}
		});
		fieldGraphVerticalAxis.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {};

			public void focusLost(FocusEvent e) {
				Graph.this.setAxisTitleY(fieldGraphVerticalAxis.getText());
				Main.updateAllComponents();
			};
		});
		panelMenu.add(fieldGraphVerticalAxis, constraints);
		constraints.gridy++;

		JPanel glue = new JPanel();
		constraints.weighty = 1;
		panelMenu.add(glue, constraints);
		constraints.gridy++;
		constraints.weighty = 0;

		labelDimensions = new JLabel("<html><i>###x###</i></html>",
			SwingConstants.CENTER);
		labelDimensions.setPreferredSize(new Dimension(180, 20));
		panelMenu.add(labelDimensions, constraints);
		constraints.gridy++;

		buttonExport = new JButton("Export");
		buttonExport.setPreferredSize(new Dimension(180, 20));
		buttonExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		panelMenu.add(buttonExport, constraints);
	}

	// String constants that are used for choosing .
	public static final String SCATTERPLOT = "Scatterplot";
	public static final String LINE = "Line";
	public static final String BAR = "Bar";

	/** The title of the graph. */
	private String graphTitle;

	/** The label on the horizontal axis. */
	private String axisTitleX;
	/** The label on the vertical axis. */
	private String axisTitleY;

	/** The type of graph this is. */
	private String graphType;

	/** The series holding information about horizontal gridlines. */
	private Series stepX;
	/** The series holding information about vertical gridlines. */
	private Series stepY;

	// GUI components

	/** The actual panel containing the graph. */
	private JPanel panelGraph;
	/** The panel drawn on when graphing, never directly modified by the user. */
	private JPanel drawingPanel;

	/** The right-hand menu column of the GUI. */
	private JPanel panelMenu;
	/** A label for the graph type selector. */
	private JLabel labelType;
	/** A combo box for choosing graph type. */
	private JComboBox<String> selectorType;
	/** A label for the horizontal gridline selector. */
	private JLabel labelGridlineX;
	/** Selector for horizontal gridlines. */
	private SeriesSelector selectorGridlineX;
	/** A label for the vertical gridline selector. */
	private JLabel labelGridlineY;
	/** Selector for vertical gridlines. */
	private SeriesSelector selectorGridlineY;
	/** A label describing the dimensions of the graph. */
	private JLabel labelDimensions;
	/** A button to export the graph data. */
	private JButton buttonExport;

	/** A text field for the graph title. */
	private JTextField fieldGraphTitle;

	// Axis titles are edited from the sidebar, mainly because that's way easier
	// in Swing than rotated text fields, but also because it allows empty 
	// titles to be hidden.

	/** A text field for the horizontal axis title. */
	private JTextField fieldGraphHorizontalAxis;
	/** A text field for the verticalAxis title. */
	private JTextField fieldGraphVerticalAxis;

	class RotatedLabel extends JLabel {

		/** How far to rotate this text field. */
		private double rotation;

		/** Graphics for this component. */
		private Graphics2D graphics;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			graphics = (Graphics2D) g;
			graphics.rotate(
				rotation / 180 * Math.PI,
				getWidth() / 2,
				getHeight() / 2
			);
		}

		@Override
		public void paintChildren(Graphics g) {
			graphics = (Graphics2D) g;
			graphics.drawString(getText(), 10, 10);
		}

		/**
		 * @return The rotation of this text field.
		 */
		public double getRotation() {
			return rotation;
		}

		/**
		 * Changes the rotation of this text field.
		 * @param angle The new angle to rotate the text field to.
		 */
		public void setRotation(double angle) {
			rotation = angle;
		}

	}

	/**
	 * Called whenever this graph updates. Updates the dimension label,
	 * refreshes the graph, and then calls {@link #invalidate()},
	 * {@link #validate()}, and {@link #repaint()}
	 */
	public void doUpdate() {
		labelDimensions.setText(String.format(
			"<html><i>%dx%d</i></html>",
			panelGraph.getWidth(),
			panelGraph.getHeight()
		));
		
		invalidate();
		validate();
		repaint();
	}
	

	/**
	 * Exports this graph to a file.
	 */
	public void export() {
		// Clear text field borders
		fieldGraphTitle.setBorder(null);
	
		// Export
		BufferedImage img = new BufferedImage(
			panelGraph.getWidth(), panelGraph.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		panelGraph.printAll(g);
		g.dispose();
		try {
			ImageIO.write(img, "png", FileDataManager.chooseFile(".png", "PNG Images", true));
		} catch (IOException e) {
			System.err.println("Could not export graph to file.");
			e.printStackTrace();
		}

		// Re-add borders
		fieldGraphTitle.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	}

	// Getters and setters

	/**
	 * @return The title of the graph.
	 */
	public String getGraphTitle() {
		return graphTitle;
	}

	/**
	 * Changes the graph's title.
	 * @param s The new title of the graph.
	 */
	public void setGraphTitle(String s) {
		graphTitle = s;
	}

	/**
	 * @return The horizontal axis title of the graph.
	 */
	public String getAxisTitleX() {
		return axisTitleX;
	}

	/**
	 * Changes the label on the horizontal axis of the graph.
	 * @param s The new X-axis title of the graph.
	 */
	public void setAxisTitleX(String s) {
		axisTitleX = s;
	}

	/**
	 * @return The vertical axis title of the graph.
	 */
	public String getAxisTitleY() {
		return axisTitleY;
	}

	/**
	 * Changes the label on the vertical axis of the graph.
	 * @param s The new Y-axis title of the graph.
	 */
	public void setAxisTitleY(String s) {
		axisTitleY = s;
	}

	/**
	 * @return The type of graph currently being drawn.
	 */
	public String getGraphType() {
		return graphType;
	}

	/**
	 * Changes the type of graph being drawn.
	 * @param type The type of graph to draw. Should be one of
	 * {@link #SCATTERPLOT}, {@link #LINE}, or {@link #BAR}. Do not use
	 * "Scatterplot", "Line", or "Bar".
	 */
	public void setGraphType(String type) {
		graphType = type;
	}

	/**
	 * @return The series currently being used for horizontal gridlines.
	 */
	public Series getGridlinesX() {
		return stepX;
	}

	/**
	 * Changes the series used for horizontal gridlines.
	 * @param r A reference to the new {@link Series} object to use.
	 */
	public void setGridlinesX(Series r) {
		stepX = r;
	}

	/**
	 * @return The series currently being used for vertical gridlines.
	 */
	public Series getGridlinesY() {
		return stepY;
	}

	/**
	 * Changes the series used for vertical gridlines.
	 * @param r A reference to the new {@link Series} object to use.
	 */
	public void setGridlinesY(Series r) {
		stepY = r;
	}
}
