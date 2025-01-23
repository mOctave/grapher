package ib.grapher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * A menu used as part of the {@link PlottableTable} to configure a single
 * {@link PlottableData} set.
 */
public class PlottableDataMenu extends JPanel {
	// MARK: Constructor
	/**
	 * Sole constructor. Sets up a menu and links it back to its data set and
	 * menu.
	 * @param plottableData The plottable data series to permanently link this
	 * menu to.
	 * @param table The overarching data table that this menu is part of.
	 */
	public PlottableDataMenu(PlottableData plottableData, PlottableTable table) {
		this.plottableData = plottableData;
		this.plottableData.setMenu(this);
		this.table = table;


		// GUI
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		fieldName = new JTextField();
		fieldName.setPreferredSize(new Dimension(120, 20));
		fieldName.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				PlottableDataMenu.this.getData().setName(fieldName.getText());
			}
		});
		fieldName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData().setName(fieldName.getText());
			}
		});
		selectorXAxis = new SeriesSelector();
		selectorXAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setDataX((Series) selectorXAxis.getSelectedItem());
				Main.updateAllComponents();
			}
		});
		selectorYAxis = new SeriesSelector();
		selectorYAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setDataY((Series) selectorYAxis.getSelectedItem());
				Main.updateAllComponents();
			}
		});


		JLabel errorBarLabel = new JLabel("Error Bars:", SwingConstants.RIGHT);
		errorBarLabel.setPreferredSize(new Dimension(120, 20));
		

		selectorXErrorBars = new SeriesSelector();
		selectorXErrorBars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setErrorBarsX((Series) selectorXErrorBars.getSelectedItem());
				Main.updateAllComponents();
			}
		});
		selectorYErrorBars = new SeriesSelector();
		selectorYErrorBars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setErrorBarsY((Series) selectorYErrorBars.getSelectedItem());
				Main.updateAllComponents();
			}
		});

		toggleVisible = new JCheckBox("Visibility", true);
		toggleVisible.setPreferredSize(new Dimension(120, 20));
		toggleVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setActive(toggleVisible.isSelected());
				Main.updateAllComponents();
			}
		});

		toggleTrendline = new JCheckBox("Trendline");
		toggleTrendline.setPreferredSize(new Dimension(120, 20));
		toggleTrendline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setLinRegActive(toggleTrendline.isSelected());
				PlottableDataMenu.this.panelTrendline
					.setVisible(toggleTrendline.isSelected());
				Main.updateAllComponents();
			}
		});

		buttonRemove = new JButton("X");
		buttonRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getTable()
					.removePlottableData(PlottableDataMenu.this);
				Main.updateAllComponents();
			}
		});
		buttonRemove.setPreferredSize(new Dimension(60, 20));

		buttonChooseColour = new JButton(new ImageIcon(Main.BUTTON_PALETTE));
		int colourChoice = getTable().getDataSets().indexOf(getData()) % 7;
		buttonChooseColour.setBackground(Main.WONG_COLORS[colourChoice]);
		buttonChooseColour.setOpaque(true);
		buttonChooseColour.setBorderPainted(false);
		getData().setColour(Main.WONG_COLORS[colourChoice]);
		buttonChooseColour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color newColour = JColorChooser.showDialog(
					PlottableDataMenu.this,
					"Select a colour",
					PlottableDataMenu.this.getData().getColour()
				);
				if (newColour != null) {
					PlottableDataMenu.this.getData().setColour(newColour);
					buttonChooseColour.setBackground(newColour);
				}
				Main.updateAllComponents();
			}
		});
		buttonChooseColour.setPreferredSize(new Dimension(60, 20));

		// Extra trendline options
		labelTrendline = new JLabel();
		labelTrendline.setPreferredSize(new Dimension(200, 20));
		labelTrendline.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));

		toggleXAgainstY = new JCheckBox(
			"Regress X against Y (minimize horizontal distance)"
		);
		toggleXAgainstY.setPreferredSize(new Dimension(300, 20));
		toggleXAgainstY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setXAgainstY(toggleXAgainstY.isSelected());
				Main.updateAllComponents();
			}
		});


		// Set up main panel
		panelMain = new JPanel();
		panelMain.setLayout(new GridBagLayout());
		add(panelMain);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		constraints.weightx = 0.2;
		panelMain.add(fieldName, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		panelMain.add(selectorXAxis, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		panelMain.add(selectorYAxis, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		panelMain.add(toggleVisible, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		panelMain.add(buttonRemove, constraints);

		constraints.gridx = 0;
		constraints.gridy++;

		constraints.weightx = 0.2;
		panelMain.add(errorBarLabel, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		panelMain.add(selectorXErrorBars, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		panelMain.add(selectorYErrorBars, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		panelMain.add(toggleTrendline, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		panelMain.add(buttonChooseColour, constraints);


		// Set up the trendline panel
		panelTrendline = new JPanel(new GridBagLayout());
		panelTrendline.setVisible(false);
		add(panelTrendline);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		constraints.weightx = 0.6;
		panelTrendline.add(labelTrendline, constraints);
		constraints.gridx++;
		constraints.weightx = 0.4;
		panelTrendline.add(toggleXAgainstY, constraints);
	}



	// MARK: Properties
	/** The {@link PlottableData} object that this menu is linked to. */
	private final PlottableData plottableData;
	/** The {@link PlottableTable} object that this menu is part of. */
	private final PlottableTable table;


	// GUI
	/** The main menu panel, always shown. */
	private final JPanel panelMain;

	/** A text field to change the name of the plottable data. */
	private final JTextField fieldName;

	/** A menu to change the series to plot on the X-Axis. */
	private final SeriesSelector selectorXAxis;
	/** A menu to change the series to plot on the Y-Axis. */
	private final SeriesSelector selectorYAxis;
	/** A menu to change the series to use for horizontal error bars. */
	private final SeriesSelector selectorXErrorBars;
	/** A menu to change the series to use for vertical error bars. */
	private final SeriesSelector selectorYErrorBars;

	/** A check box to toggle rendering of this data on the {@link Graph}. */
	private final JCheckBox toggleVisible;
	/** A check box to toggle rendering of a trendline for this data. */
	private final JCheckBox toggleTrendline;

	/**
	 * A JPanel which displays linear regression information.
	 * Only shown when the trendline is switched on ({@link #toggleTrendline}
	 * is true).
	 */
	private final JPanel panelTrendline;

	/** A label containing linear regression stats. */
	private final JLabel labelTrendline;

	/** 
	 * A check box to toggle using X against Y regression (instead of Y
	 * against X).
	 */
	private final JCheckBox toggleXAgainstY;


	/** A button to remove this field of plottable data. */
	private final JButton buttonRemove;
	/** A button to change the colour this data is plotted in. */
	private final JButton buttonChooseColour;



	// MARK: Methods
	/**
	 * Updates the label attached to this data set, according to the template
	 * y = ax + b.
	 */
	public void updateTrendlineLabel() {
		if (plottableData.getB() < 0) {
			labelTrendline.setText(String.format(
				"y = %fy - %f • r = %f",
				plottableData.getA(),
				Math.abs(plottableData.getB()),
				plottableData.getR()
			));
		} else {
			labelTrendline.setText(String.format(
				"y = %fy + %f • r = %f",
				plottableData.getA(),
				plottableData.getB(),
				plottableData.getR()
			));
		}
	}



	/**
	 * Syncs the graphical components on this menu with the values of the
	 * linked {@link PlottableData} set.
	 */
	public void sync() {
		fieldName.setText(plottableData.getName());
		selectorXAxis.setSelectedItem(plottableData.getDataX());
		selectorYAxis.setSelectedItem(plottableData.getDataY());
		selectorXErrorBars.setSelectedItem(plottableData.getErrorBarsX());
		selectorYErrorBars.setSelectedItem(plottableData.getErrorBarsY());
		toggleVisible.setSelected(plottableData.isActive());
		toggleTrendline.setSelected(plottableData.isLinRegActive());
		toggleXAgainstY.setSelected(plottableData.isXAgainstY());
	}



	// Getters / Setters
	/**
	 * Getter: Gets the plottable data set of which this is a composite.
	 * @return {@link #plottableData}
	 */
	public PlottableData getData() {
		return plottableData;
	}



	/**
	 * Getter: Gets the plottable data table this menu is associated with,
	 * usually the global {@link Main#getPlottableTable()}.
	 * @return {@link #table}
	 */
	public PlottableTable getTable() {
		return table;
	}



	// GUI
	/**
	 * Getter: Gets the text field which stores the name of this data menu.
	 * @return {@link #fieldName}
	 */
	public JTextField getNameField() {
		return fieldName;
	}



	/**
	 * Getter: Gets the series selector that sets which series is plotted on
	 * the x-axis for this menu's associated data set.
	 * @return {@link #selectorXAxis}
	 */
	public SeriesSelector getSelectorX() {
		return selectorXAxis;
	}



	/**
	 * Getter: Gets the series selector that sets which series is plotted on
	 * the y-axis for this menu's associated data set.
	 * @return {@link #selectorYAxis}
	 */
	public SeriesSelector getSelectorY() {
		return selectorYAxis;
	}



	/**
	 * Getter: Gets the series selector that sets which series controls the
	 * horizontal error bars for this menu's associated data set.
	 * @return {@link #selectorXErrorBars}
	 */
	public SeriesSelector getSelectorXError() {
		return selectorXErrorBars;
	}



	/**
	 * Getter: Gets the series selector that sets which series controls the
	 * vertical error bars for this menu's associated data set.
	 * @return {@link #selectorYErrorBars}
	 */
	public SeriesSelector getSelectorYError() {
		return selectorYErrorBars;
	}
}
