package ib.grapher;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * A custom dropdown menu that allows for the selection of a single series from
 * the {@link DataTable}.
 */
public class SeriesSelector extends JComboBox<Series> {
	// MARK: Constructor
	/**
	 * Sole constructor. Adds all series in the data table to the combo box.
	 */
	public SeriesSelector() {
		super();
		DefaultComboBoxModel<Series> model = new DefaultComboBoxModel<Series>(
			Main.getDataTable().getData().toArray(new Series[0]));
		setModel(model);
		setPreferredSize(new Dimension(120, 20));
		setSelectedItem(null);
		Main.getSelectors().add(this);
	}



	// MARK: Properties
	/**
	 * A workaround to prevent {@link Main#updateAllComponents()} from firing when the
	 * selector is loaded from a file.
	 */
	private boolean shouldTriggerUpdate = true;




	// MARK: Methods
	/**
	 * Refreshes the combo box so that series match those in the data table.
	 * If this is called after the selected series has been removed from the
	 * data table, it will also reset the combo box.
	 * @param shouldTriggerUpdate Whether this refresh should trigger
	 * {@link Main#updateAllComponents()} or not.
	 */
	public void refresh(boolean shouldTriggerUpdate) {
		Object selected = getSelectedItem();

		DefaultComboBoxModel<Series> model = new DefaultComboBoxModel<Series>(
			Main.getDataTable().getData().toArray(new Series[0]));
		setModel(model);

		this.shouldTriggerUpdate = shouldTriggerUpdate;
		if (selected == null || model.getIndexOf(selected) < 0)
			setSelectedItem(null);
		else
			setSelectedItem(selected);
		
		this.shouldTriggerUpdate = true;
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets whether {@link Main#updateAllComponents()} should fire on
	 * item selection.
	 * @return {@link #shouldTriggerUpdate}
	 */
	public boolean shouldTriggerUpdate() {
		return shouldTriggerUpdate;
	}

	// shouldTriggerUpdate has no setter, since it's set in refresh().
}
