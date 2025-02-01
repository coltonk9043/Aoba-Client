/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.http;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

public class HttpUtils {

	/**
	 * Creates a URI from a given URL.
	 * 
	 * @param url URL to transform into a URI.
	 * @return URI resulting from URL.
	 */
	public static URI createURI(String url) {
		try {
			URI uri = new URI(url);
			return uri;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static BUILDER builder(String url) {
		return new BUILDER(url);
	}

	public static BUILDER builder(URI uri) {
		return new BUILDER(uri);
	}

	public static class BUILDER {
		private final HttpRequest.Builder builder;
		private static final HttpClient client = HttpClient.newBuilder()
				.version(Version.HTTP_2)
				.followRedirects(Redirect.NORMAL)
				.build();


		private BUILDER(String url) {
			builder = HttpRequest.newBuilder(createURI(url)).header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
		}

		private BUILDER(URI uri) {
			builder = HttpRequest.newBuilder(uri).header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
		}

		public BUILDER header(String key, String value) {
			builder.header(key, value);
			return this;
		}

		public BUILDER bearer(String token) {
			builder.header("Authorization", "Bearer " + token);
			return this;
		}

		public BUILDER plaintext() {
			builder.header("Content-Type", "text/plain");
			return this;
		}

		public BUILDER acceptJson() {
			builder.header("Accept", "application/json");
			return this;
		}

		public BUILDER json() {
			builder.header("Content-Type", "application/json");
			return this;
		}

		public BUILDER form() {
			builder.header("Content-Type", "application/x-www-form-urlencoded");
			return this;
		}

		public Optional<String> get() {
			HttpRequest request = builder.build();
			try {
				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				String responseString = response.body();

				int status = response.statusCode();
				if (status != HttpURLConnection.HTTP_OK) {
					return Optional.empty();
				}
				return Optional.of(responseString);
			} catch (Exception e) {
				return Optional.empty();
			}
		}

		public Optional<String> post(String payload) {
			builder.POST(HttpRequest.BodyPublishers.ofString(payload));
			HttpRequest request = builder.build();

			try {
				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				String responseString = response.body();

				int status = response.statusCode();
				if (status != HttpURLConnection.HTTP_OK) {
					return Optional.empty();
				}
				return Optional.of(responseString);
			} catch (Exception e) {
				return Optional.empty();
			}
		}

		public Optional<String> put(String payload) {
			builder.PUT(HttpRequest.BodyPublishers.ofString(payload));
			HttpRequest request = builder.build();

			try {
				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				String responseString = response.body();

				int status = response.statusCode();
				if (status != HttpURLConnection.HTTP_OK) {
					return Optional.empty();
				}
				return Optional.of(responseString);
			} catch (Exception e) {
				return Optional.empty();
			}
		}

		public Optional<String> delete() {
			builder.DELETE();
			HttpRequest request = builder.build();

			try {
				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				String responseString = response.body();

				int status = response.statusCode();
				if (status != HttpURLConnection.HTTP_OK) {
					return Optional.empty();
				}
				return Optional.of(responseString);
			} catch (Exception e) {
				return Optional.empty();
			}
		}
	}
}
