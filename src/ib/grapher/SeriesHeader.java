package ib.grapher;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/** A class that allows for editing the name of a series. */
public class SeriesHeader extends JPanel {
	// MARK: Constructor
	/**
	 * Sole constructor. Creates a header object that is tied to a
	 * {@link Series} and can be placed on a {@link DataTable}.
	 * @param series The series this header is linked to.
	 */
	public SeriesHeader(Series series) {
		this.series = series;

		// GUI
		setBackground(Main.SILVER);
		setBorder(new EtchedBorder(EtchedBorder.RAISED));

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		// Set up the text field
		textField = new JTextField(8);
		textField.setVisible(true);
		textField.setBackground(Main.TRANSPARENT);
		textField.setMargin(new Insets(0,0,0,0));

		textField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				SeriesHeader.this.setBackground(Main.YELLOW);
			}

			public void focusLost(FocusEvent e) {
				SeriesHeader.this.setBackground(Main.SILVER);

				// Losing focus also does data entry
				SeriesHeader.this.getSeries().setName(SeriesHeader.this.textField.getText());
				for (SeriesSelector selector : Main.getSelectors()) {
					selector.refresh(true);
				}
				SeriesHeader.this.getSeries().save();
				Main.updateAllComponents();
			}
		});

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SeriesHeader.this.getSeries().setName(SeriesHeader.this.textField.getText());
				for (SeriesSelector selector : Main.getSelectors()) {
					selector.refresh(true);
				}
				SeriesHeader.this.getSeries().save();
				Main.updateAllComponents();
			}
		});

		textField.setText("Untitled Series");
	
		add(textField);
	}



	// MARK: Properties
	/** The series that composites this header. */
	private final Series series;

	/** A text field which allows this series' name to be edited. */
	private final JTextField textField;



	// MARK: Getters / Setters
	/**
	 * Getter: Gets the series that composites this header.
	 * @return {@link #series}
	 */
	public Series getSeries() {
		return series;
	}

	// series is final, and so has no setter



	// GUI
	/**
	 * Getter: This header's text field.
	 * @return {@link #textField}
	 */
	public JTextField getTextField() {
		return textField;
	}

	// textField is final, and so has no setter
}
