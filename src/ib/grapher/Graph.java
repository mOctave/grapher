package ib.grapher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.Box.Filler;

/**
 * The visual graph associated with a project.
 */
public class Graph extends JFrame {
	// Sole constructor. Creates a new graph.
	public Graph() {
		// Initialize non-GUI attributes
		this.graphTitle = "New Graph";
		this.axisTitleX = "";
		this.axisTitleY = "";
		this.graphType = SCATTERPLOT;

		// Set up GUI
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(400, 200));

		this.panelGraph = new JPanel();
		panelGraph.setPreferredSize(new Dimension(400, 300));
		panelGraph.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		panelGraph.setBackground(Main.WHITE);
		panelGraph.setLayout(new BorderLayout());
		this.add(panelGraph, BorderLayout.CENTER);

		// Set up graph
		this.fieldGraphTitle = new JTextField("Untitled Graph");
		fieldGraphTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
		fieldGraphTitle.setHorizontalAlignment(SwingConstants.CENTER);
		fieldGraphTitle.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		fieldGraphTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setGraphTitle(fieldGraphTitle.getText());
			}
		});
		fieldGraphTitle.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {};

			public void focusLost(FocusEvent e) {
				Graph.this.setGraphTitle(fieldGraphTitle.getText());
			};
		});
		panelGraph.add(fieldGraphTitle, BorderLayout.NORTH);

		this.drawingPanel = new JPanel() {
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

				// Draw X-axis title
				graphics.drawString(
					axisTitleX,
					(this.getWidth() - metrics.stringWidth(axisTitleX)) / 2,
					this.getHeight() - 5
				);

				// Draw Y-axis title
				Graphics2D rotatedGraphics = (Graphics2D) graphics.create();
				rotatedGraphics.rotate(
					-0.5 * Math.PI,
					metrics.getHeight() + 5,
					(this.getHeight() + metrics.stringWidth(axisTitleY)) / 2
				);

				rotatedGraphics.drawString(
					axisTitleY,
					metrics.getHeight() + 5,
					(this.getHeight() + metrics.stringWidth(axisTitleY)) / 2
				);

				rotatedGraphics.dispose();

				// Set up borders and draw gridlines
				graphics.setColor(Main.BLACK);

				if (stepX == null) {
					xLower = -10;
					xUpper = 10;
				} else {
					xLower = stepX.getFirst().getNumeric();
					xUpper = stepX.getLast().getNumeric();
				}

				if (stepY == null) {
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
					yLower = stepY.getFirst().getNumeric();
					yUpper = stepY.getLast().getNumeric();
					if (stepY.length() > 2) {
						for (Cell c : stepY) {
							if (c.getIndex() != 0 && c.getIndex() != stepY.length() - 1) {
								graphics.drawLine(
									25,
									getRelativeY(c.getNumeric()),
									this.getWidth(),
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
						}
					}
				}

				if (stepX == null) {
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
				} else {
					if (stepX.length() > 2) {
						for (Cell c : stepX) {
							if (c.getIndex() != 0 && c.getIndex() != stepX.length() - 1) {
								graphics.drawLine(
									getRelativeX(c.getNumeric()),
									0,
									getRelativeX(c.getNumeric()),
									this.getHeight() - 25
								);
								graphics.drawString(
									c.getValue(),
									getRelativeX(c.getNumeric())
										- metrics.stringWidth(c.getValue()) / 2,
									getRelativeY(yLower) + 15
								);
							}
						}
					}
				}

				// Draw plottable data sets
				for (PlottableData pd : Main.getPlottableTable().getDataSets()) {
					graphics.setColor(pd.getColour());

					if (pd.getDataX() == null || pd.getDataY() == null)
						continue;

					Cell activeX = pd.getDataX().getFirst();
					Cell activeY = pd.getDataY().getFirst();

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
					}
				}

				// Clean up
				graphics.dispose();
			}

			private int getRelativeX(double x) {
				int labelOffset = axisTitleY.length() == 0 ? 0 : 20;
				labelOffset += (stepX != null && stepX.length() <= 2) ? 0 : 30;
				return (int) ((x - xLower) / (xUpper - xLower) * 
					(this.getWidth() - (20 + labelOffset))) + (10 + labelOffset);
			}

			private int getRelativeY(double y) {
				int labelOffset = axisTitleX.length() == 0 ? 0 : 20;
				labelOffset += (stepY != null && stepY.length() <= 2) ? 0 : 15;
				return this.getHeight() - (int) ((y - yLower) / (yUpper - yLower) *
					(this.getHeight() - (20 + labelOffset))) - (10 + labelOffset);
			}
		};
		drawingPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panelGraph.add(drawingPanel, BorderLayout.CENTER);

		this.panelMenu = new JPanel();
		panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
		this.add(panelMenu, BorderLayout.EAST);

		// Set up menu panel
		this.labelType = new JLabel("Graph Type", SwingConstants.CENTER);
		labelType.setPreferredSize(new Dimension(120, 20));
		panelMenu.add(labelType);
		this.selectorType = new JComboBox<String>(new String[]{SCATTERPLOT, LINE, BAR});
		selectorType.setPreferredSize(new Dimension(120, 20));
		selectorType.setSelectedItem(SCATTERPLOT);
		panelMenu.add(selectorType);

		this.labelGridlineX = new JLabel("Horizontal Gridlines", SwingConstants.CENTER);
		labelGridlineX.setPreferredSize(new Dimension(120, 20));
		panelMenu.add(labelGridlineX);
		this.selectorGridlineX = new SeriesSelector();
		selectorGridlineX.setPreferredSize(new Dimension(120, 20));
		selectorGridlineX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setGridlinesX((Series) selectorGridlineX.getSelectedItem());
			}
		});
		panelMenu.add(selectorGridlineX);

		this.labelGridlineY = new JLabel("Vertical Gridlines", SwingConstants.CENTER);
		labelGridlineY.setPreferredSize(new Dimension(120, 20));
		panelMenu.add(labelGridlineY);
		this.selectorGridlineY = new SeriesSelector();
		selectorGridlineY.setPreferredSize(new Dimension(120, 20));
		selectorGridlineY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setGridlinesY((Series) selectorGridlineY.getSelectedItem());
			}
		});
		panelMenu.add(selectorGridlineY);

		panelMenu.add(new JLabel("X-Axis Title", SwingConstants.CENTER));
		this.fieldGraphHorizontalAxis = new JTextField();
		fieldGraphHorizontalAxis.setPreferredSize(new Dimension(120, 20));
		fieldGraphHorizontalAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setAxisTitleX(fieldGraphHorizontalAxis.getText());
			}
		});
		fieldGraphHorizontalAxis.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {};

			public void focusLost(FocusEvent e) {
				Graph.this.setAxisTitleX(fieldGraphHorizontalAxis.getText());
			};
		});
		panelMenu.add(fieldGraphHorizontalAxis);


		panelMenu.add(new JLabel("Y-Axis Title", SwingConstants.CENTER));
		this.fieldGraphVerticalAxis = new JTextField();
		fieldGraphVerticalAxis.setPreferredSize(new Dimension(120, 20));
		fieldGraphVerticalAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setAxisTitleY(fieldGraphVerticalAxis.getText());
			}
		});
		fieldGraphVerticalAxis.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {};

			public void focusLost(FocusEvent e) {
				Graph.this.setAxisTitleY(fieldGraphVerticalAxis.getText());
			};
		});
		panelMenu.add(fieldGraphVerticalAxis);

		glue = (Filler) Box.createVerticalGlue();
		glue.changeShape(
			glue.getMinimumSize(),
			new Dimension(0, Integer.MAX_VALUE),
			glue.getMaximumSize()
		);
		panelMenu.add(glue);

		this.labelDimensions = new JLabel("<html><i>###x###</i></html>",
			SwingConstants.CENTER);
		labelDimensions.setPreferredSize(new Dimension(120, 20));
		panelMenu.add(labelDimensions);

		this.buttonExport = new JButton("Export");
		buttonExport.setPreferredSize(new Dimension(120, 20));
		buttonExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		panelMenu.add(buttonExport);
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
	/** Glue to keep everything grouped together. */
	private Filler glue;
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
				this.getWidth() / 2,
				this.getHeight() / 2
			);
		}

		@Override
		public void paintChildren(Graphics g) {
			graphics = (Graphics2D) g;
			graphics.drawString(this.getText(), 10, 10);
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
			this.rotation = angle;
		}

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
