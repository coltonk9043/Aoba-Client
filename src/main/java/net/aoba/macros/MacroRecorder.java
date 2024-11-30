package net.aoba.macros;

import java.util.LinkedList;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.KeyUpEvent;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.event.listeners.KeyUpListener;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.macros.actions.KeyClickMacroEvent;
import net.aoba.macros.actions.MacroEvent;
import net.aoba.macros.actions.MouseClickMacroEvent;
import net.aoba.macros.actions.MouseMoveMacroEvent;
import net.aoba.macros.actions.MouseScrollMacroEvent;

public class MacroRecorder
		implements MouseClickListener, MouseMoveListener, MouseScrollListener, KeyDownListener, KeyUpListener {

	private LinkedList<MacroEvent> currentMacro = new LinkedList<MacroEvent>();
	private long startTime = 0;
	private boolean recording = false;

	public void startRecording() {
		if (!recording) {
			currentMacro = new LinkedList<MacroEvent>();
			recording = true;
			startTime = System.nanoTime();

			Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
			Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
			Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
			Aoba.getInstance().eventManager.AddListener(KeyUpListener.class, this);
		}
	}

	public void stopRecording() {
		if (recording) {
			recording = false;
			startTime = 0;

			Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
			Aoba.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
			Aoba.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
			Aoba.getInstance().eventManager.RemoveListener(KeyUpListener.class, this);

			addToMacroManager();
		}
	}

	public void addToMacroManager() {
		if (!recording && currentMacro != null) {
			Macro macro = new Macro(currentMacro);
			Aoba.getInstance().macroManager.addMacro(macro);
			Aoba.getInstance().macroManager.setCurrentlySelected(macro);
			currentMacro = null;
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		// Don't record the Aoba GUI button.
		if (event.GetKey() != Aoba.getInstance().guiManager.clickGuiButton.getValue().getCode()
				&& !Aoba.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new KeyClickMacroEvent(timeStamp, event.GetKey(), event.GetScanCode(), event.GetAction(),
					event.GetModifiers()));
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		// Don't record the Aoba GUI button.
		if (event.GetKey() != Aoba.getInstance().guiManager.clickGuiButton.getValue().getCode()
				&& !Aoba.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new KeyClickMacroEvent(timeStamp, event.GetKey(), event.GetScanCode(), event.GetAction(),
					event.GetModifiers()));
		}
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (!Aoba.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseScrollMacroEvent(timeStamp, event.GetHorizontal(), event.GetVertical()));
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (!Aoba.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseMoveMacroEvent(timeStamp, mouseMoveEvent.getX(), mouseMoveEvent.getY()));
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent mouseClickEvent) {
		if (!Aoba.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseClickMacroEvent(timeStamp, mouseClickEvent.button, mouseClickEvent.action,
					mouseClickEvent.mods));
		}
	}
}
