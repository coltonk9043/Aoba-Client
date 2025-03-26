/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.ArrayList;
import java.util.List;

import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.gui.UIElement;

public class GridComponent extends Component {
	private ArrayList<GridDefinition> columnDefinitions = null;
	private ArrayList<GridDefinition> rowDefinitions = null;

	private Float[] columnWidths;
	private Float[] rowHeights;

	public GridComponent() {
    }

	public void addColumnDefinition(GridDefinition def) {
		if (columnDefinitions == null)
			columnDefinitions = new ArrayList<GridDefinition>();

		columnDefinitions.add(def);
	}

	public void addRowDefinition(GridDefinition def) {
		if (rowDefinitions == null)
			rowDefinitions = new ArrayList<GridDefinition>();
		rowDefinitions.add(def);
	}

	@Override
	public void measure(Size availableSize) {
		List<UIElement> children = getChildren();

		int numChildren = children.size();
		int numColumnDefinitions = 0;
		int numRowDefinitions = 0;
		float sumWidth = 0;
		float sumHeight = 0;

		/**
		 * During this measure pass, we want to do several passes to ensure that we are
		 * properly depleting remaining space according to the size of the column
		 * definitions.
		 */
		if (numChildren > 0) {
			// Force measure all children to get their heights.
			for (UIElement element : children) {
				element.measure(availableSize);
				sumHeight = Math.max(sumHeight, element.getPreferredSize().getHeight());
			}

			// Generate column width array either from the number of column definitions.
			// Otherwise, use only one column.
			if (columnDefinitions != null) {
				numColumnDefinitions = columnDefinitions.size();
				columnWidths = new Float[numColumnDefinitions];
				float availableSpaceX = availableSize.getWidth();
				float totalRelativePartitions = 0;

				// TODO: Somehow simplify this?
				// Measure Absolute values first and get the total number of relative space
				// partitions.
				for (int i = 0; i < columnWidths.length; i++) {

					GridDefinition colDef = columnDefinitions.get(i % numColumnDefinitions);
					if (colDef.unit == RelativeUnit.Absolute) {
						columnWidths[i] = colDef.value;
						availableSpaceX -= colDef.value;
						sumWidth += columnWidths[i];
					} else {
						totalRelativePartitions += colDef.value;
					}
				}

				// Measure relative values using the space partitions
				for (int j = 0; j < columnWidths.length; j++) {
					GridDefinition colDef = columnDefinitions.get(j);
					if (colDef.unit == RelativeUnit.Relative) {
						columnWidths[j] = (colDef.value / totalRelativePartitions) * availableSpaceX;
					}
				}
			} else {
				columnWidths = new Float[1];
				columnWidths[0] = availableSize.getWidth();
				sumWidth = columnWidths[0];
			}

			// Generate row heights array either from the number of row definitions.
			// Otherwise, calculate the number of rows based off of the number of columns.
			// Otherwise, use only one row.
			if (rowDefinitions != null) {
				numRowDefinitions = rowDefinitions.size();
				rowHeights = new Float[numRowDefinitions];
			} else if (columnDefinitions != null) {
				if (numColumnDefinitions == 0) {
					rowHeights = new Float[1];
				} else {
					int rows = (numChildren / numColumnDefinitions);
					rowHeights = new Float[rows];
				}
			} else {
				rowHeights = new Float[1];
			}

			// Measure all of the children.
			int iteration = 0;
			for (UIElement element : children) {
				int row = iteration / columnWidths.length;
				int column = iteration % columnWidths.length;

				if (!element.isVisible())
					continue;

				Float columnWidth = columnWidths[column];
				Float rowHeight = rowHeights[row];

				// TODO: Find Row Heights
				if (rowHeight == null)
					rowHeight = availableSize.getHeight();

				Size newSize = new Size(columnWidth, rowHeight);
				element.measure(newSize);
				iteration++;
			}
		}

		// Add up the size after the fact.
		Size newSize = new Size(sumWidth, sumHeight);
		preferredSize = newSize;
	}

	@Override
	public void arrange(Rectangle finalSize) {
		if (parent != null) {
			setActualSize(finalSize);
		}

		if (columnWidths != null) {
			int iteration = 0;
			float currentX = 0;
			float currentY = finalSize.getY();

			List<UIElement> children = getChildren();
			for (UIElement element : children) {
				int row = iteration / columnWidths.length;
				int column = iteration % columnWidths.length;

				if (column == 0) {
					currentX = finalSize.getX();
				}

				Float columnWidth = columnWidths[column];
				Float rowHeight = rowHeights[row];

				if (rowHeight == null)
					rowHeight = finalSize.getHeight();

				Rectangle newSize = new Rectangle(currentX, currentY, columnWidth, rowHeight);
				element.arrange(newSize);

				iteration++;
				currentX += columnWidth;

				// Increment the currentY if the row is about to change.
				if (column == columnWidths.length - 1) {
					currentY += rowHeight;
				}
			}
		}
	}
}