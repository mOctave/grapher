package ib.grapher;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * A custom dropdown menu that allows for the selection of a single series from
 * the {@link DataTable}.
 */
public class SeriesSelector extends JComboBox<Series> {
	/**
	 * Sole constructor. Adds all series in the data table to the combo box.
	 */
	public SeriesSelector() {
		super();
		DefaultComboBoxModel<Series> model = new DefaultComboBoxModel<Series>(Main.getDataTable().getData().toArray(new Series[0]));
		this.setModel(model);
		this.setPreferredSize(new Dimension(120, 20));
		this.setSelectedItem(null);
		Main.getSelectors().add(this);
	}

	/**
	 * Refreshes the combo box so that series match those in the data table.
	 * If this is called after the selected series has been removed from the
	 * data table, it will also reset the combo box.
	 */
	public void refresh() {
		Object selected = this.getSelectedItem();

		DefaultComboBoxModel<Series> model = new DefaultComboBoxModel<Series>(Main.getDataTable().getData().toArray(new Series[0]));
		this.setModel(model);

		if (selected == null || model.getIndexOf(selected) < 0)
			this.setSelectedItem(null);
		else
			this.setSelectedItem(selected);
	}
}
