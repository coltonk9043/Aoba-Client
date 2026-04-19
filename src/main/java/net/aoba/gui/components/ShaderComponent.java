/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;

import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Color;
import net.aoba.rendering.shaders.Shader;
import net.aoba.rendering.shaders.ShaderUniform;
import net.aoba.rendering.shaders.ShaderUniformType;

public class ShaderComponent extends Component {
	public static final UIProperty<Shader> ShaderProperty = new UIProperty<>("Shader", null, false, false, ShaderComponent::onShaderPropertyChanged);

	private static void onShaderPropertyChanged(UIElement sender, Shader oldValue, Shader newValue) {
		if (sender instanceof ShaderComponent sc)
			sc.syncFromProperty();
	}

	private final StackPanelComponent uniformsPanel;
	private final ComboBoxComponent shaderCombo;
	private boolean rebuilding;
	private String lastShaderId;

	public ShaderComponent() {
		StackPanelComponent root = new StackPanelComponent();
		root.setSpacing(8f);

		List<Shader> shaders = AOBA.shaderManager.getAvailableShaders();
		List<String> shaderNames = shaders.stream().map(Shader::name).toList();

		this.shaderCombo = new ComboBoxComponent();
		shaderCombo.setProperty(ComboBoxComponent.ItemsSourceProperty, shaderNames);
		shaderCombo.setOnItemChanged(this::onShaderChanged);
		root.addChild(shaderCombo);

		uniformsPanel = new StackPanelComponent();
		uniformsPanel.setSpacing(8f);
		root.addChild(uniformsPanel);

		setContent(root);
	}

	private void syncFromProperty() {
		if (rebuilding)
			return;
		rebuilding = true;

		Shader shader = getProperty(ShaderProperty);
		String shaderId = shader != null ? shader.id() : "solid";

		if (!shaderId.equals(lastShaderId)) {
			lastShaderId = shaderId;
			String shaderName = shader != null ? shader.name() : "Solid";
			shaderCombo.setProperty(ComboBoxComponent.SelectedItemProperty, shaderName);
		}

		buildParams();
		rebuilding = false;
	}

	private void onShaderChanged(Object name) {
		if (rebuilding)
			return;
		rebuilding = true;

		for (Shader e : AOBA.shaderManager.getAvailableShaders()) {
			if (e.name().equals(name)) {
				lastShaderId = e.id();
				setProperty(ShaderProperty, e.copy());
				break;
			}
		}
		buildParams();

		rebuilding = false;
	}

	private void buildParams() {
		uniformsPanel.clearChildren();

		Shader shader = getProperty(ShaderProperty);

		if (shader == null || shader.uniforms().isEmpty()) {
			return;
		}

		List<ShaderUniform> uniforms = shader.uniforms();
		float[] values = shader.uniformValues();

		for (int i = 0; i < uniforms.size(); i++) {
			ShaderUniform p = uniforms.get(i);
			final int off = shader.uniformOffset(i);

			if (p.type() == ShaderUniformType.FLOAT) {
				float value = off < values.length ? values[off] : p.defaultValue()[0];

				SliderComponent slider = new SliderComponent();
				slider.setProperty(SliderComponent.HeaderProperty, p.name());
				slider.setProperty(SliderComponent.MinimumProperty, p.min());
				slider.setProperty(SliderComponent.MaximumProperty, p.max());
				slider.setProperty(SliderComponent.StepProperty, p.step());
				slider.setProperty(SliderComponent.ValueProperty, value);
				slider.setOnValueChanged(v -> {
					values[off] = v;
				});
				uniformsPanel.addChild(slider);
			} else if (p.type() == ShaderUniformType.COLOR) {
				float r = off < values.length ? values[off] : 1f;
				float g = off + 1 < values.length ? values[off + 1] : 1f;
				float b = off + 2 < values.length ? values[off + 2] : 1f;
				float a = off + 3 < values.length ? values[off + 3] : 1f;

				Color paramColor = new Color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
				uniformsPanel.addChild(new StringComponent(p.name()));

				ColorPickerComponent colorPicker = new ColorPickerComponent();
				colorPicker.setProperty(ColorPickerComponent.ColorProperty, paramColor);
				colorPicker.setOnChanged(c -> {
					values[off] = c.getRed();
					values[off + 1] = c.getGreen();
					values[off + 2] = c.getBlue();
					values[off + 3] = c.getAlpha();
				});
				uniformsPanel.addChild(colorPicker);
			}
		}
	}
}
