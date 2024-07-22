package ib.grapher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
	public PlottableDataMenu(PlottableData plottableData, PlottableTable table) {
		this.plottableData = plottableData;
		this.table = table;

		this.fieldName = new JTextField();
		fieldName.setPreferredSize(new Dimension(120, 20));
		this.selectorXAxis = new SeriesSelector();
		selectorXAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setDataX((Series) selectorXAxis.getSelectedItem());
			}
		});
		this.selectorYAxis = new SeriesSelector();
		selectorYAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setDataY((Series) selectorYAxis.getSelectedItem());
			}
		});


		JLabel errorBarLabel = new JLabel("Error Bars:", SwingConstants.RIGHT);
		errorBarLabel.setPreferredSize(new Dimension(120, 20));
		

		this.selectorXErrorBars = new SeriesSelector();
		selectorXErrorBars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setErrorBarsX((Series) selectorXErrorBars.getSelectedItem());
			}
		});
		this.selectorYErrorBars = new SeriesSelector();
		selectorYErrorBars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getData()
					.setErrorBarsY((Series) selectorYErrorBars.getSelectedItem());
			}
		});

		this.toggleVisible = new JCheckBox("Visibility", true);
		toggleVisible.setPreferredSize(new Dimension(120, 20));
		this.toggleTrendline = new JCheckBox("Trendline");
		toggleTrendline.setPreferredSize(new Dimension(120, 20));

		this.buttonRemove = new JButton("X");
		buttonRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableDataMenu.this.getTable()
					.removePlottableData(PlottableDataMenu.this);
			}
		});
		buttonRemove.setPreferredSize(new Dimension(60, 20));

		this.buttonChooseColour = new JButton("<html>&#127912;&#xFE0E;</html>");
		int colourChoice = this.getTable().getDataSets().indexOf(this.getData()) % 7;
		buttonChooseColour.setBackground(Main.WONG_COLORS[colourChoice]);
		buttonChooseColour.setOpaque(true);
		buttonChooseColour.setBorderPainted(false);
		this.getData().setColour(Main.WONG_COLORS[colourChoice]);
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
			}
		});
		buttonChooseColour.setPreferredSize(new Dimension(60, 20));

		this.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		constraints.weightx = 0.2;
		this.add(fieldName, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		this.add(selectorXAxis, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		this.add(selectorYAxis, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		this.add(toggleVisible, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		this.add(buttonRemove, constraints);

		constraints.gridx = 0;
		constraints.gridy++;

		constraints.weightx = 0.2;
		this.add(errorBarLabel, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		this.add(selectorXErrorBars, constraints);
		constraints.gridx++;
		constraints.weightx = 0.3;
		this.add(selectorYErrorBars, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		this.add(toggleTrendline, constraints);
		constraints.gridx++;
		constraints.weightx = 0.1;
		this.add(buttonChooseColour, constraints);
	}

	/** The {@link PlottableData} object that this menu is linked to. */
	private final PlottableData plottableData;
	/** The {@link PlottableTable} object that this menu is part of. */
	private final PlottableTable table;

	/** A text field to change the name of the plottable data. */
	private JTextField fieldName;

	/** A menu to change the series to plot on the X-Axis. */
	private SeriesSelector selectorXAxis;
	/** A menu to change the series to plot on the Y-Axis. */
	private SeriesSelector selectorYAxis;
	/** A menu to change the series to use for horizontal error bars. */
	private SeriesSelector selectorXErrorBars;
	/** A menu to change the series to use for vertical error bars. */
	private SeriesSelector selectorYErrorBars;

	/** A check box to toggle rendering of this data on the {@link Graph}. */
	private JCheckBox toggleVisible;
	/** A check box to toggle rendering of a trendline for this data. */
	private JCheckBox toggleTrendline;

	/** A button to remove this field of plottable data. */
	private JButton buttonRemove;
	/** A button to change the colour this data is plotted in. */
	private JButton buttonChooseColour;

	// Getters and setters
	public PlottableData getData() {
		return plottableData;
	}

	public PlottableTable getTable() {
		return table;
	}
}
