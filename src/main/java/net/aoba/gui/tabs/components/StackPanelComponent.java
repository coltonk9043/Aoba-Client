package net.aoba.gui.tabs.components;

import net.aoba.gui.IHudElement;

public class StackPanelComponent extends Component {
	public enum StackType {
		Horizontal, Vertical
	}

	protected StackType stackType = StackType.Vertical;

	public StackPanelComponent(IHudElement parent) {
		super(parent);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void OnChildAdded(IHudElement child) {
		this.RecalculateHeight();
	}

	@Override
	public void OnChildChanged(IHudElement child) {
		this.RecalculateHeight();
	}

	@Override
	public void OnVisibilityChanged() {
		this.RecalculateHeight();
	}

	public void RecalculateHeight() {
		int height = 0;
		for (int i = 0; i < children.size(); i++) {
			Component iChild = children.get(i);

			// If the child is visible, increase the height of the StackPanel.
			if (iChild.isVisible()) {
				height += iChild.getHeight();
			}

			// Move the Top of the child below to the top + height of the previous element.
			if (i + 1 != children.size()) {
				Component childBelow = children.get(i + 1);
				childBelow.setTop(height);
			}
		}
		setHeight(height);
	}
}
