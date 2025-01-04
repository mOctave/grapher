package ib.grapher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
	// MARK: Constructor
	/** Sole constructor. */
	public Graph() {
		// Initialize non-GUI attributes
		graphTitle = "New Graph";
		axisTitleX = "";
		axisTitleY = "";
		graphType = SCATTERPLOT;

		// Set up GUI
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(400, 300));

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

		drawingPanel = new GraphPanel(this);
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
		selectorType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph.this.setGraphType((String) selectorType.getSelectedItem());
				Main.updateAllComponents();
			}
		});

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
				Main.updateAllComponents();
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

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				doUpdate();
			}
		});
	}



	// MARK: Constants
	/** Scatterplot graph type. */
	public static final String SCATTERPLOT = "Scatterplot";
	/** Line graph type. */
	public static final String LINE = "Line";
	/** Bar graph type. */
	public static final String BAR = "Bar";



	// MARK: Properties
	/** The title of this graph. */
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


	// GUI 
	/** The actual panel containing the graph. */
	private final JPanel panelGraph;
	/** The panel drawn on when graphing, never directly modified by the user. */
	private final GraphPanel drawingPanel;

	/** The right-hand menu column of the GUI. */
	private final JPanel panelMenu;
	/** A label for the graph type selector. */
	private final JLabel labelType;
	/** A combo box for choosing graph type. */
	private final JComboBox<String> selectorType;
	/** A label for the horizontal gridline selector. */
	private final JLabel labelGridlineX;
	/** Selector for horizontal gridlines. */
	private final SeriesSelector selectorGridlineX;
	/** A label for the vertical gridline selector. */
	private final JLabel labelGridlineY;
	/** Selector for vertical gridlines. */
	private final SeriesSelector selectorGridlineY;
	/** A label describing the dimensions of the graph. */
	private final JLabel labelDimensions;
	/** A button to export the graph data. */
	private final JButton buttonExport;

	/** A text field for the graph title. */
	private final JTextField fieldGraphTitle;

	// Axis titles are edited from the sidebar, mainly because that's way easier
	// in Swing than rotated text fields, but also because it allows empty 
	// titles to be hidden.

	/** A text field for the horizontal axis title. */
	private JTextField fieldGraphHorizontalAxis;
	/** A text field for the verticalAxis title. */
	private JTextField fieldGraphVerticalAxis;



	// MARK: Update
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
	


	// MARK: Methods
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



	/**
	 * Syncs the graphical components on the graph with its assigned values.
	 */
	public void sync() {
		fieldGraphTitle.setText(graphTitle);
		fieldGraphHorizontalAxis.setText(axisTitleX);
		fieldGraphVerticalAxis.setText(axisTitleY);
		selectorType.setSelectedItem(graphType);
		selectorGridlineX.setSelectedItem(stepX);
		selectorGridlineY.setSelectedItem(stepY);
	}

	// Getters and setters

	/**
	 * Getter: Gets the title of this graph.
	 * @return {@link #graphTitle}
	 */
	public String getGraphTitle() {
		return graphTitle;
	}

	/**
	 * Setter: Changes this graph's title.
	 * @param graphTitle The new {@link #graphTitle} for this graph.
	 */
	public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
		Main.saveMetadata();
	}



	/**
	 * Getter: Gets the horizontal axis title of this graph.
	 * @return {@link #axisTitleX}
	 */
	public String getAxisTitleX() {
		return axisTitleX;
	}

	/**
	 * Setter: Changes this graph's horizontal axis title.
	 * @param axisTitleX The new {@link #axisTitleX} for this graph.
	 */
	public void setAxisTitleX(String axisTitleX) {
		this.axisTitleX = axisTitleX;
		Main.saveMetadata();
	}



	/**
	 * Getter: Gets the vertical axis title of this graph.
	 * @return {@link #axisTitleY}
	 */
	public String getAxisTitleY() {
		return axisTitleY;
	}

	/**
	 * Setter: Changes this graph's vertical axis title.
	 * @param axisTitleY The new {@link #axisTitleY} for this graph.
	 */
	public void setAxisTitleY(String axisTitleY) {
		this.axisTitleY = axisTitleY;
		Main.saveMetadata();
	}



	/**
	 * Getter: Gets the type of graph currently being drawn.
	 * @return {@link #graphType}
	 */
	public String getGraphType() {
		return graphType;
	}

	/**
	 * Setter: Changes which type of graph being drawn.
	 * @param graphType The type of graph to draw. Should be one of
	 * {@link #SCATTERPLOT}, {@link #LINE}, or {@link #BAR}. Do not use
	 * "Scatterplot", "Line", or "Bar".
	 */
	public void setGraphType(String graphType) {
		this.graphType = graphType;
		Main.saveMetadata();
	}



	/**
	 * Getter: Gets the series being used for horizontal gridlines.
	 * @return {@link #stepX}
	 */
	public Series getGridlinesX() {
		return stepX;
	}

	/**
	 * Setter: Changes the series used to draw horizontal gridlines.
	 * @param stepX The new {@link #stepX} series for this graph.
	 */
	public void setGridlinesX(Series stepX) {
		this.stepX = stepX;
		Main.saveMetadata();
	}



	/**
	 * Getter: Gets the series being used for vertical gridlines.
	 * @return {@link #stepY}
	 */
	public Series getGridlinesY() {
		return stepY;
	}

	/**
	 * Setter: Changes the series used to draw vertical gridlines.
	 * @param stepY The new {@link #stepY} series for this graph.
	 */
	public void setGridlinesY(Series stepY) {
		this.stepY = stepY;
		Main.saveMetadata();
	}
}




// MARK: RotatedLabel
/** A class storing a JLabel rotated by an arbitrary amount. */
class RotatedLabel extends JLabel {
	// MARK: Constructor
	/** Sole constructor. */
	public RotatedLabel() {}



	// MARK: Properties
	/** How far to rotate this text field. */
	private double rotation;



	// MARK: Methods
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;
		graphics.rotate(
			rotation / 180 * Math.PI,
			getWidth() / 2,
			getHeight() / 2
		);
	}



	@Override
	public void paintChildren(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		graphics.drawString(getText(), 10, 10);
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets the rotation angle of this text field.
	 * @return {@link #rotation}
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Setter: Changes how far this text field is rotated by.
	 * @param rotation The new {@link #rotation} angle for this label.
	 */
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
}
