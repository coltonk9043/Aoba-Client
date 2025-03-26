/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.proxymanager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

public class ProxyManager {
	private ArrayList<Socks5Proxy> proxies;
	private Socks5Proxy activeProxy;

	public ProxyManager() {
		proxies = new ArrayList<>();
		activeProxy = null;
		readProxies();
	}

	public void readProxies() {
		try {
			File proxiesFile = new File("proxies.json");
			if (!proxiesFile.exists()) {
				return; // If file doesn't exist, simply return
			}

			Gson gson = new Gson();
			Type proxiesListType = new TypeToken<List<Socks5Proxy>>() {
			}.getType();
			FileReader reader = new FileReader(proxiesFile);
			proxies = gson.fromJson(reader, proxiesListType);
			reader.close();
		} catch (IOException e) {
			LogUtils.getLogger().error(e.getMessage());
		}
	}

	public void saveProxies() {
		try {
			File proxiesFile = new File("proxies.json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(proxiesFile);
			gson.toJson(proxies, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addProxy(Socks5Proxy proxy) {
		proxies.add(proxy);
		saveProxies();
	}

	public void removeProxy(Socks5Proxy proxy) {
		proxies.remove(proxy);
		if (proxy.equals(activeProxy)) {
			activeProxy = null;
		}
		saveProxies();
	}

	public ArrayList<Socks5Proxy> getProxies() {
		return proxies;
	}

	public Socks5Proxy getProxyByIp(String ip) {
		for (Socks5Proxy proxy : proxies) {
			if (proxy.getIp().equals(ip)) {
				return proxy;
			}
		}
		return null;
	}

	public Socks5Proxy getProxyByPort(int port) {
		for (Socks5Proxy proxy : proxies) {
			if (proxy.getPort() == port) {
				return proxy;
			}
		}
		return null;
	}

	public void setActiveProxy(Socks5Proxy proxy) {
		if (proxy == null || proxies.contains(proxy)) {
			activeProxy = proxy;
		} else {
			throw new IllegalArgumentException("Proxy not found in the manager");
		}
	}

	public Socks5Proxy getActiveProxy() {
		return activeProxy;
	}

	public void clearActiveProxy() {
		activeProxy = null;
	}

	public void clearProxies() {
		proxies.clear();
		activeProxy = null;
		saveProxies();
	}

	public void updateProxy(Socks5Proxy oldProxy, Socks5Proxy newProxy) {
		int index = proxies.indexOf(oldProxy);
		if (index != -1) {
			proxies.set(index, newProxy);
			saveProxies();
		} else {
			throw new IllegalArgumentException("Proxy not found in the manager");
		}
	}

	public List<Socks5Proxy> getInactiveProxies() {
		List<Socks5Proxy> inactiveProxies = new ArrayList<>();
		for (Socks5Proxy proxy : proxies) {
			if (!proxy.equals(activeProxy)) {
				inactiveProxies.add(proxy);
			}
		}
		return inactiveProxies;
	}

	public void updateProxyPort(String ip, int newPort) {
		Socks5Proxy proxy = getProxyByIp(ip);
		if (proxy != null) {
			proxy.setPort(newPort);
			saveProxies();
		} else {
			throw new IllegalArgumentException("Proxy not found in the manager");
		}
	}
}
