/**
 * JbroFuzz 2.5
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.fuzz.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * <p>The table model for the fuzzers, displayed on the top,
 * right hand side of the "Fuzzing" tab.</p>
 * 
 * @author subere@uncon.org
 * @version 2.2
 * @since 1.2
 */
public class FuzzersTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8133287501650660903L;

	// The names of the columns within the table of generators
	private static final String[] COLUMNNAMES = 
	{ "Fuzzer ID", "Start", "End" };

	// The vector of fuzzer row data
	private final ArrayList<FuzzerRow> dataVector;

	/**
	 * <p>Main Constructor passes the Fuzzing Panel.</p>
	 */
	public FuzzersTableModel() {
		dataVector = new ArrayList<FuzzerRow>();
	}

	/**
	 * <p>Add a fuzzer row to the table</p>
	 */
	public void addRow(String name, String type, String id, int point1,
			int point2) {

		dataVector.add(new FuzzerRow(id, point1, point2));

		dataVector.trimToSize();
		fireTableRowsInserted(dataVector.size(), dataVector.size());
		
	}

	/**
	 * Get a complete column count of the generator table
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return FuzzersTableModel.COLUMNNAMES.length;
	}

	/**
	 * Get a given column name.
	 * 
	 * @param column
	 *            int
	 * @return String
	 */
	@Override
	public String getColumnName(final int column) {
		return FuzzersTableModel.COLUMNNAMES[column];
	}

	/**
	 * <p>
	 * Get a row depending on the corresponding integer given.
	 * </p>
	 * 
	 * @param row
	 *            int
	 * @return String
	 */
	public String getRow(final int row) {

		return row + " - " + getValueAt(row, 0);

	}

	/**
	 * Get a complete row count of the generator table.
	 * 
	 * @return int The number of rows present in the table
	 */
	public int getRowCount() {
		return dataVector.size();
	}

	/**
	 * <p>
	 * Get the value within the generator table at a given location of column
	 * and row.
	 * </p>
	 */
	public Object getValueAt(final int row, final int column) {

		final FuzzerRow record = dataVector.get(row);
		switch (column) {
		case 0:
			return record.getId();
		case 1:
			return Integer.valueOf(record.getStartPoint());
		case 2:
			return Integer.valueOf(record.getEndPoint());
			//		case 6:
			//			return Integer.valueOf(record.getPoint3());
			//		case 7:
			//			return Integer.valueOf(record.getPoint4());
		default:
			return null;
		}
	}

// TODO UCdetector: Remove unused code: 
// 	public int getStart(final int row) {
// 		final FuzzerRow rec_ = dataVector.get(row);
// 		return Integer.valueOf(rec_.getStartPoint());
// 	}

	/**
	 * <p>A cell is always editable within the fuzzers table.</p>
	 */
	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	/**
	 * <p>
	 * Remove a particular row from the table model.
	 * </p>
	 * 
	 * @param row
	 *            The row to remove
	 * 
	 * @see
	 * @author subere@uncon.org
	 * @version 2.2
	 * @since 1.2
	 */
	public void removeRow(final int row) {

		if ((row > -1) && (row < dataVector.size())) {
			dataVector.remove(row);
			fireTableRowsDeleted(0, row);
		}
	}



	/**
	 * <p>
	 * Set a value at the corresponding row and column location.
	 * </p>
	 */
	@Override
	public void setValueAt(final Object value, final int row, final int column) {

		final FuzzerRow record = dataVector.get(row);

		if(column == 0) {
			record.setId((String) value);
		}
		if(column == 1) {
			record.setStartPoint(((Integer) value).intValue());
		}
		if(column == 2) {
			record.setEndPoint(((Integer) value).intValue());
		}

		fireTableCellUpdated(row, column);
	}

}