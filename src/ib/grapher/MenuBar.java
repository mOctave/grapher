package ib.grapher;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/** 
 * A menu bar that is attached to all windows and holds commands that allow for
 * easier manipulation of the project.
 */
public class MenuBar extends JMenuBar {
	/** Sole constructor. */
	public MenuBar() {
		// Project menu
		JMenu menuProject = new JMenu("Project");
		add(menuProject);

		JMenuItem projectSaveAs = new JMenuItem("Save As");
		projectSaveAs.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_S,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		projectSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDataManager.chooseFile(".graph", "Grapher Files", true);
				Main.saveAllData();
			}
		});
		menuProject.add(projectSaveAs);

		JMenuItem projectOpen = new JMenuItem("Open");
		projectOpen.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		projectOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDataManager.chooseFile(".graph", "Grapher Files", false);
			}
		});
		menuProject.add(projectOpen);

		// Data menu
		JMenu menuData = new JMenu("Data");
		add(menuData);

		JMenuItem dataImport = new JMenuItem("Import CSV");
		dataImport.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_I,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		dataImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDataManager.chooseFile(".csv", "CSV Files", false);
			}
		});
		menuData.add(dataImport);

		JMenuItem dataSort = new JMenuItem("Sort by Selected Column");
		dataSort.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_R,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		menuData.add(dataSort);

		JMenuItem dataSearch = new JMenuItem("Search");
		dataSearch.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_F,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		menuData.add(dataSearch);

		menuData.addSeparator();

		// Insertion sub menu
		JMenu menuInsert = new JMenu("Insert");
		menuData.add(menuInsert);

		JMenuItem insertRowAbove = new JMenuItem("Insert Row Above");
		insertRowAbove.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_ENTER,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
			| KeyEvent.SHIFT_DOWN_MASK
		));
		insertRowAbove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataTable dt = Main.getDataTable();
				dt.matchActiveToSelected();
				for (Cell c : dt.getActiveCells()) {
					c.insertCellBefore(new Cell());
				}
				dt.update();
			}
		});
		menuInsert.add(insertRowAbove);

		JMenuItem insertRowBelow = new JMenuItem("Insert Row Below");
		insertRowBelow.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_ENTER,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		insertRowBelow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataTable dt = Main.getDataTable();
				dt.matchActiveToSelected();
				for (Cell c : dt.getActiveCells()) {
					c.insertCellAfter(new Cell());
				}
				dt.update();
			}
		});
		menuInsert.add(insertRowBelow);

		JMenuItem insertColumnLeft = new JMenuItem("Insert Column Left");
		insertColumnLeft.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_TAB,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
			| KeyEvent.SHIFT_DOWN_MASK
		));
		insertColumnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataTable dt = Main.getDataTable();
				dt.insertSeries(
					dt.indexOf(dt.getSelectedCell().getSeries()),
					new Series(dt.getData().get(0).length())
				);
				dt.update();
			}
		});
		menuInsert.add(insertColumnLeft);

		JMenuItem insertColumnRight = new JMenuItem("Insert Column Right");
		insertColumnRight.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_TAB,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		insertColumnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataTable dt = Main.getDataTable();
				dt.insertSeries(
					dt.indexOf(dt.getSelectedCell().getSeries()) + 1,
					new Series(dt.getData().get(0).length())
				);
				dt.update();
			}
		});
		menuInsert.add(insertColumnRight);

		JMenuItem dataDeleteRow = new JMenuItem("Delete Row");
		dataDeleteRow.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_BACK_SPACE,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
		));
		menuData.add(dataDeleteRow);

		JMenuItem dataDeleteColumn = new JMenuItem("Delete Column");
		menuData.add(dataDeleteColumn);
	}
}
