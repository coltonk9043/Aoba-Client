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

import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;
import net.aoba.gui.types.GridDefinition.RelativeUnit;

public class GridComponent extends PanelComponent {
	private ArrayList<GridDefinition> columnDefinitions = null;
	private ArrayList<GridDefinition> rowDefinitions = null;

	private Float[] columnWidths;
	private Float[] rowHeights;

	public static final UIProperty<Float> HorizontalSpacingProperty = new UIProperty<>("HorizontalSpacing", 0f, false, true);
	public static final UIProperty<Float> VerticalSpacingProperty = new UIProperty<>("VerticalSpacing", 0f, false, true);
	
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
	public Size measure(Size availableSize) {
		List<UIElement> children = getChildren();

		float horizontalSpacing = getProperty(GridComponent.HorizontalSpacingProperty);
		float verticalSpacing = getProperty(GridComponent.VerticalSpacingProperty);
		
		int numChildren = children.size();
		int numColumnDefinitions = 0;
		float sumWidth = 0;
		float sumHeight = 0;

		if (numChildren > 0) {
			// Force measure all children with full available size to get preferred sizes.
			for (UIElement element : children) {
				element.measureCore(availableSize);
			}

			if (columnDefinitions != null) {
				numColumnDefinitions = columnDefinitions.size();
				columnWidths = new Float[numColumnDefinitions];
				float availableSpaceX = availableSize.width();
				float totalRelativePartitions = 0;

				// Subtract horizontal spacing between columns from available space.
				if (numColumnDefinitions > 1)
					availableSpaceX -= horizontalSpacing * (numColumnDefinitions - 1);

				// Pass 1: Resolve Auto columns from children's preferred sizes.
				for (int i = 0; i < numColumnDefinitions; i++) {
					GridDefinition colDef = columnDefinitions.get(i);
					if (colDef.unit == RelativeUnit.Auto) {
						float maxChildWidth = 0;
						int iteration = 0;
						for (UIElement element : children) {
							boolean elementIsVisible = element.getProperty(UIElement.IsVisibleProperty);
							if (!elementIsVisible) 
								continue;
							
							int col = iteration % numColumnDefinitions;
							if (col == i) {
								maxChildWidth = Math.max(maxChildWidth, element.getPreferredSize().width());
							}
							iteration++;
						}
						columnWidths[i] = maxChildWidth;
						availableSpaceX -= maxChildWidth;
						sumWidth += maxChildWidth;
					}
				}

				// Pass 2: Resolve Absolute columns.
				for (int i = 0; i < numColumnDefinitions; i++) {
					GridDefinition colDef = columnDefinitions.get(i);
					if (colDef.unit == RelativeUnit.Absolute) {
						columnWidths[i] = colDef.value;
						availableSpaceX -= colDef.value;
						sumWidth += colDef.value;
					} else if (colDef.unit == RelativeUnit.Relative) {
						totalRelativePartitions += colDef.value;
					}
				}

				// Pass 3: Resolve Relative columns with remaining space.
				for (int i = 0; i < numColumnDefinitions; i++) {
					GridDefinition colDef = columnDefinitions.get(i);
					if (colDef.unit == RelativeUnit.Relative) {
						columnWidths[i] = (colDef.value / totalRelativePartitions) * availableSpaceX;
					}
				}

				// Add horizontal spacing to total width.
				if (numColumnDefinitions > 1)
					sumWidth += horizontalSpacing * (numColumnDefinitions - 1);
			} else {
				numColumnDefinitions = 1;
				columnWidths = new Float[1];
				columnWidths[0] = availableSize.width();
				sumWidth = columnWidths[0];
			}

			int numRows;
			if (rowDefinitions != null) {
				numRows = rowDefinitions.size();
			} else if (numColumnDefinitions > 0) {
				numRows = Math.max(1, (int) Math.ceil((double) numChildren / numColumnDefinitions));
			} else {
				numRows = 1;
			}
			rowHeights = new Float[numRows];

			if (rowDefinitions != null) {
				float availableSpaceY = availableSize.height();
				float totalRelativePartitions = 0;

				// Subtract vertical spacing between rows from available space.
				if (numRows > 1)
					availableSpaceY -= verticalSpacing * (numRows - 1);

				// Pass 1: Resolve Auto rows from children's preferred sizes.
				for (int i = 0; i < numRows; i++) {
					GridDefinition rowDef = rowDefinitions.get(i);
					if (rowDef.unit == RelativeUnit.Auto) {
						float maxChildHeight = 0;
						int iteration = 0;
						for (UIElement element : children) {
							boolean elementIsVisible = element.getProperty(UIElement.IsVisibleProperty);
							if (!elementIsVisible) 
								continue;
							
							int row = iteration / numColumnDefinitions;
							if (row == i) {
								maxChildHeight = Math.max(maxChildHeight, element.getPreferredSize().height());
							}
							iteration++;
						}
						rowHeights[i] = maxChildHeight;
						availableSpaceY -= maxChildHeight;
						sumHeight += maxChildHeight;
					}
				}

				// Pass 2: Resolve Absolute rows.
				for (int i = 0; i < numRows; i++) {
					GridDefinition rowDef = rowDefinitions.get(i);
					if (rowDef.unit == RelativeUnit.Absolute) {
						rowHeights[i] = rowDef.value;
						availableSpaceY -= rowDef.value;
						sumHeight += rowDef.value;
					} else if (rowDef.unit == RelativeUnit.Relative) {
						totalRelativePartitions += rowDef.value;
					}
				}

				// Pass 3: Resolve Relative rows with remaining space.
				for (int i = 0; i < numRows; i++) {
					GridDefinition rowDef = rowDefinitions.get(i);
					if (rowDef.unit == RelativeUnit.Relative) {
						rowHeights[i] = (rowDef.value / totalRelativePartitions) * availableSpaceY;
					}
				}

				// Add vertical spacing to total height.
				if (numRows > 1)
					sumHeight += verticalSpacing * (numRows - 1);
			} else {
				// No row definitions — compute row heights from children's preferred sizes.
				for (int i = 0; i < numRows; i++) {
					float maxChildHeight = 0;
					int iteration = 0;
					for (UIElement element : children) {
						boolean elementIsVisible = element.getProperty(UIElement.IsVisibleProperty);
						if (!elementIsVisible) 
							continue;
						
						int row = iteration / numColumnDefinitions;
						if (row == i) {
							maxChildHeight = Math.max(maxChildHeight, element.getPreferredSize().height());
						}
						iteration++;
					}
					rowHeights[i] = maxChildHeight;
					sumHeight += maxChildHeight;
				}

				if (numRows > 1)
					sumHeight += verticalSpacing * (numRows - 1);
			}

			// Re-measure children with their actual column/row sizes.
			int iteration = 0;
			for (UIElement element : children) {
				boolean elementIsVisible = element.getProperty(UIElement.IsVisibleProperty);
				if (!elementIsVisible) 
					continue;
				
				int row = iteration / columnWidths.length;
				int column = iteration % columnWidths.length;

				Float columnWidth = columnWidths[column];
				Float rowHeight = rowHeights[row];
				if (rowHeight == null) rowHeight = availableSize.height();

				element.measureCore(new Size(columnWidth, rowHeight));
				iteration++;
			}
		}

		return new Size(sumWidth, sumHeight);
	}

	@Override
	public void arrange(Rectangle finalSize) {
		setActualSize(finalSize);

		float horizontalSpacing = getProperty(GridComponent.HorizontalSpacingProperty);
		float verticalSpacing = getProperty(GridComponent.VerticalSpacingProperty);
		
		if (columnWidths != null) {
			int iteration = 0;
			float currentX = 0;
			float currentY = finalSize.y();

			List<UIElement> children = getChildren();
			for (UIElement element : children) {
				int row = iteration / columnWidths.length;
				int column = iteration % columnWidths.length;

				if (column == 0) {
					currentX = finalSize.x();
				}

				Float columnWidth = columnWidths[column];
				Float rowHeight = rowHeights[row];

				if (rowHeight == null)
					rowHeight = finalSize.height();

				Rectangle newSize = new Rectangle(currentX, currentY, columnWidth, rowHeight);
				element.arrange(newSize);

				iteration++;
				currentX += columnWidth + horizontalSpacing;

				// Increment the currentY if the row is about to change.
				if (column == columnWidths.length - 1) {
					currentY += rowHeight + verticalSpacing;
				}
			}
		}
	}
}