package net.aoba.gui.tabs.components;

import net.aoba.gui.IHudElement;

public class StackPanelComponent extends Component {
	public enum StackType{
		Horizontal, Vertical
	}

	protected StackType stackType = StackType.Vertical;
	
	public StackPanelComponent(IHudElement parent) {
		super(parent);
	}

	@Override
	public void update() {
		int height = 0;
		for(int i = 0; i < children.size(); i++) {
			Component child = children.get(i);
			if (child.isVisible()) {
				height += child.getHeight();
			}	
			if(i + 1 != children.size()) {
				Component childBelow = children.get(i + 1);
				childBelow.setTop(height);
			}	
		}
		setHeight(height);
	}
}
