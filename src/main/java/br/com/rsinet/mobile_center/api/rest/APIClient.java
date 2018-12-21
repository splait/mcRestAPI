package br.com.rsinet.mobile_center.api.rest;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import br.com.rsinet.mobile_center.api.exception.DeviceUnvailableException;
import br.com.rsinet.mobile_center.api.exception.InvalidCredentialsException;
import br.com.rsinet.mobile_center.api.model.DeviceCapabilities;
import br.com.rsinet.mobile_center.api.model.DeviceContent;
import br.com.rsinet.mobile_center.api.model.MobileCenterApps;
import br.com.rsinet.mobile_center.api.model.Reservation;
import br.com.rsinet.mobile_center.api.model.ReservationDetails;
import br.com.rsinet.mobile_center.api.model.User;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class APIClient {

	// *************************************
	// Initiate the constants with your data
	// *************************************

	private String BASE_URL;

	// ************************************
	// Mobile Center APIs end-points
	// ************************************

	private static final String ENDPOINT_CLIENT_LOGIN = "client/login";
	private static final String ENDPOINT_CLIENT_LOGOUT = "client/logout";
	private static final String ENDPOINT_CLIENT_DEVICES = "deviceContent";
	private static final String ENDPOINT_CLIENT_APPS = "apps";
	private static final String ENDPOINT_CLIENT_INSTALL_APPS = "apps/install";
	private static final String ENDPOINT_CLIENT_UPLOAD_APPS = "apps/upload?enforceUpload=true";
	private static final String ENDPOINT_CLIENT_USERS = "v2/users";
	private static final String ENDPOINT_CLIENT_RESERVATION = "v2/public/reservation";

	private static final Integer RESERVATION_TIME_SECONDS = 10;
	// ************************************
	// Initiate proxy configuration
	// ************************************

	private static final boolean USE_PROXY = false;
	private static final String PROXY = "<PROXY>";

	// ************************************
	// Path to app (IPA or APK) for upload
	// ************************************

	@SuppressWarnings("unused")
	private static String APP = "/PATH/TO/APP/FILE.ipa|apk";

	private OkHttpClient client;
	private String hp4msecret;
	private String jsessionid;
	private String responseBodyStr;
	private String userName;

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final MediaType APK = MediaType.parse("application/vnd.android.package-archive");

	// ******************************************************
	// APIClient class constructor to store all info and call API methods
	// ******************************************************

	public APIClient(String host, String port, String userName, String password) throws IOException {
		this.BASE_URL = "http://" + host + ":" + port + "/rest/";
		this.userName = userName;
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().readTimeout(240, TimeUnit.SECONDS)
				.writeTimeout(240, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).cookieJar(new CookieJar() {
					private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

					@Override
					public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
						List<Cookie> storedCookies = cookieStore.get(url.host());
						if (storedCookies == null) {
							storedCookies = new ArrayList<>();
							cookieStore.put(url.host(), storedCookies);
						}
						storedCookies.addAll(cookies);
						for (Cookie cookie : cookies) {
							if (cookie.name().equals("hp4msecret"))
								hp4msecret = cookie.value();
							if (cookie.name().equals("JSESSIONID"))
								jsessionid = cookie.value();
						}
					}

					@Override
					public List<Cookie> loadForRequest(HttpUrl url) {
						List<Cookie> cookies = cookieStore.get(url.host());
						return cookies != null ? cookies : new ArrayList<>();
					}
				});

		if (USE_PROXY) {
			int PROXY_PORT = 8080;
			clientBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY, PROXY_PORT)));
		}

		client = clientBuilder.build();
		login(userName, password);
	}

	// ***********************************************************
	// Login to Mobile Center for getting cookies to work with API
	// ***********************************************************

	private void login(String username, String password) throws IOException {
		String strCredentials = "{\"name\":\"" + username + "\",\"password\":\"" + password + "\"}";
		RequestBody body = RequestBody.create(JSON, strCredentials);
		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + ENDPOINT_CLIENT_LOGIN)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		// add CRSF header
		if (hp4msecret != null) {
			builder.addHeader("x-hp4msecret", hp4msecret);
		}
		builder.post(body);
		Request request = builder.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
				}
			} else if (response.code() == 401) {
				throw new InvalidCredentialsException("Invalid credentials. Verify username and password");
			}
			response.close();
		}
	}

	// ************************************
	// List all apps from Mobile Center
	// ************************************

	public MobileCenterApps getAllApps() throws IOException {
		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + ENDPOINT_CLIENT_APPS)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		// add CRSF header
		if (hp4msecret != null) {
			builder.addHeader("x-hp4msecret", hp4msecret);
		}
		builder.get();
		Request request = builder.build();
		Gson gson = new Gson();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
					MobileCenterApps deviceContentCollection = gson.fromJson(responseBodyStr, MobileCenterApps.class);
					return deviceContentCollection;
				}
			} else {
				throw new IOException("Unexpected code " + response);
			}
			response.close();
		}
		throw new RuntimeException("Was not possible to get the applications list");
	}

	// ************************************
	// List all users from Mobile Center
	// ************************************

	public List<User> getAllUsers() throws IOException {
		executeRestAPI(ENDPOINT_CLIENT_USERS);
		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + ENDPOINT_CLIENT_USERS)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		builder.get();
		Request request = builder.build();
		Gson gson = new Gson();
		List<User> usersList = new ArrayList<>();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
					// Converter response para objeto
					usersList = Arrays.asList(gson.fromJson(responseBodyStr, User[].class));
				}
			} else if (response.code() == 401) {
				throw new InvalidCredentialsException("Invalid credentials. Verify username and password");
			}
			response.close();
		}
		return usersList;
	}

	// ************************************
	// List all devices from Mobile Center
	// ************************************

	public List<DeviceContent> getAllDevicesInformation() throws IOException {
		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + ENDPOINT_CLIENT_DEVICES)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		builder.get();
		Request request = builder.build();
		Gson gson = new Gson();
		List<DeviceContent> devices = new ArrayList<>();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
					// Converter response para objeto
					DeviceContent[] deviceContentCollection = gson.fromJson(responseBodyStr, DeviceContent[].class);
					return Arrays.asList(deviceContentCollection);
				}
			} else if (response.code() == 401) {
				throw new InvalidCredentialsException("Invalid credentials. Verify username and password");
			}
			response.close();
		}
		return devices;
	}

	// ************************************
	// Install application
	// ************************************

	private void installApp(String package_name, String version, String device_id, Boolean is_intrumented)
			throws IOException {
		String counter = version;
		String str = "{\n" + "  \"app\": {\n" + "    \"counter\": " + counter + ",\n" + "    \"id\": \"" + package_name
				+ "\",\n" + "    \"instrumented\": " + (is_intrumented ? "true" : "false") + "\n" + "  },\n"
				+ "  \"deviceCapabilities\": {\n" + "    \"udid\": \"" + device_id + "\"\n" + "  }\n" + "}";
		RequestBody body = RequestBody.create(JSON, str);
		executeRestAPI(ENDPOINT_CLIENT_INSTALL_APPS, HttpMethod.POST, body);
	}

	// ************************************
	// Install application by file name, when there are multiple matches for file
	// name in database, will select first application.
	// ************************************

	private void installAppByFileAndDeviceID(String filename, String device_id, Boolean is_intrumented)
			throws IOException {
		getAllApps();
		String[] res = responseBodyStr.split(filename);
		if (res == null || res.length < 2) {
			return;
		} else {
			String counter = parseProperty(res[0], "\"counter\":", ",");
			String package_name = parseProperty(res[1], "\"identifier\":\"", "\",");
			installApp(package_name, counter, device_id, is_intrumented);
		}
	}

	// ************************************
	// Logout from Mobile Center
	// ************************************

	public void logout() throws IOException {
		RequestBody body = RequestBody.create(JSON, "");
		executeRestAPI(ENDPOINT_CLIENT_LOGOUT, HttpMethod.POST, body);
	}

	private void executeRestAPI(String endpoint) throws IOException {
		executeRestAPI(endpoint, HttpMethod.GET);
	}

	private void executeRestAPI(String endpoint, HttpMethod httpMethod) throws IOException {
		executeRestAPI(endpoint, httpMethod, null);
	}

	private void executeRestAPI(String endpoint, HttpMethod httpMethod, RequestBody body) throws IOException {

		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + endpoint)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());

		// add CRSF header
		if (hp4msecret != null) {
			builder.addHeader("x-hp4msecret", hp4msecret);
		}

		// build the http method
		if (HttpMethod.GET.equals(httpMethod)) {
			builder.get();
		} else if (HttpMethod.POST.equals(httpMethod)) {
			builder.post(body);
		}

		Request request = builder.build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				Gson gson = new Gson();
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
				}
			} else {
				throw new IOException("Unexpected code " + response);
			}
			response.close();
		}
	}

	// ************************************
	// Upload Application to Mobile Center
	// ************************************

	@SuppressWarnings("unused")
	private void uploadApp(String filename) throws IOException {
		String[] parts = filename.split("\\\\");
		System.out.println("Uploading and preparing the app... ");
		RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("file", parts[parts.length - 1], RequestBody.create(APK, new File(filename))).build();

		Request request = new Request.Builder().addHeader("content-type", "multipart/form-data")
				.addHeader("x-hp4msecret", hp4msecret).addHeader("JSESSIONID", jsessionid)
				.url(BASE_URL + ENDPOINT_CLIENT_UPLOAD_APPS).post(requestBody).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				System.out.println("Done!");
				System.out.println(response.toString());
				ResponseBody body = response.body();
				if (body != null) {
					System.out.println(body.string());
				}
			} else {
				throw new IOException("Unexpected code " + response);
			}
			response.close();
		}
	}

	// ************************************
	// Get all reservations from Mobile Center
	// ************************************

	public List<Reservation> getAllReservationInformation() throws IOException {
		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + ENDPOINT_CLIENT_RESERVATION)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		builder.get();
		Request request = builder.build();
		Gson gson = new Gson();
		List<Reservation> devices = new ArrayList<>();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
					// Converter response para objeto
					Reservation[] deviceContentCollection = gson.fromJson(responseBodyStr, Reservation[].class);
					return Arrays.asList(deviceContentCollection);
				}
			} else if (response.code() == 401) {
				throw new InvalidCredentialsException("Invalid credentials. Verify username and password");
			}
			response.close();
		}
		return devices;
	}

	// ************************************
	// Get reservations by user name from Mobile Center
	// ************************************

	public List<Reservation> getAllReservationInformation(String userName) throws IOException {
		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + ENDPOINT_CLIENT_RESERVATION)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		builder.get();
		Request request = builder.build();
		Gson gson = new Gson();
		List<Reservation> reservationsByUser = new ArrayList<>();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
					// Converter response para objeto
					Reservation[] reservations = gson.fromJson(responseBodyStr, Reservation[].class);
					for (Reservation reservation : reservations) {
						if (reservation.getReservedForUser().getName().equals(userName))
							reservationsByUser.add(reservation);
					}
					return reservationsByUser;
				}
			} else if (response.code() == 401) {
				throw new InvalidCredentialsException("Invalid credentials. Verify username and password");
			}
			response.close();
		}
		return reservationsByUser;
	}

	// ************************************
	// Create a reservation
	// ************************************

	public List<Reservation> createReservation(String deviceId) throws IOException {
		// build the request URL and headers
		ReservationDetails reservationDetails = new ReservationDetails();
		DeviceCapabilities deviceCapabilities = new DeviceCapabilities();
		deviceCapabilities.setUdid(deviceId);
		String startTime = getCurrentTimeUsingCalendar();
		String endTime = getCurrentTimeUsingCalendar(RESERVATION_TIME_SECONDS);
		reservationDetails.setStartTime(startTime);
		reservationDetails.setEndTime(endTime);
		reservationDetails.setReleaseOnJobCompletion(false);
		reservationDetails.setDeviceCapabilities(deviceCapabilities);
		String jsonRequestBodyNew = new Gson().toJson(reservationDetails);
		RequestBody body = RequestBody.create(JSON, jsonRequestBodyNew);
		// build the request URL and headers
		Request.Builder builder = new Request.Builder().url(BASE_URL + ENDPOINT_CLIENT_RESERVATION)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		// add CRSF header
		if (hp4msecret != null) {
			builder.addHeader("x-hp4msecret", hp4msecret);
		}
		builder.post(body);
		Request request = builder.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				Gson gson = new Gson();
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
				}
			} else if (response.code() == 409) {
				throw new DeviceUnvailableException(String.format("Device '%s' couldn't be reserved", deviceId));
			}
			response.close();
		}
		return null;
	}

	/**
	 * Removes a reservation by its uid
	 * 
	 * @param reservationUID
	 * @return
	 * @throws IOException
	 */
	public List<Reservation> removeReservation(String reservationUID) throws IOException {
		Request.Builder builder = new Request.Builder()
				.url(BASE_URL + ENDPOINT_CLIENT_RESERVATION + "/" + reservationUID)
				.addHeader("Content-type", JSON.toString()).addHeader("Accept", JSON.toString());
		// add CRSF header
		if (hp4msecret != null) {
			builder.addHeader("x-hp4msecret", hp4msecret);
		}
		builder.delete();
		Request request = builder.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				Gson gson = new Gson();
				final ResponseBody responseBody = response.body();
				if (responseBody != null) {
					responseBodyStr = responseBody.string();
				}
			} else if (response.code() == 409) {
				throw new DeviceUnvailableException(
						String.format("Reservation '%s' couldn't be removed", reservationUID));
			}
			response.close();
		}
		return null;
	}

	private enum HttpMethod {
		GET, POST
	}

	private String parseProperty(String source, String prefix, String suffix) {
		try {
			String[] array = source.split(prefix);
			String str = array[array.length - 1];
			return str.split(suffix)[0];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String... args) throws IOException {
		APIClient api = new APIClient("10.1.15.10", "8080", "guilherme.sousa@rsinet.com.br", "Senha1234");
		System.out.println("========= Devices Reservation ========");
		for (DeviceContent device : api.getAllDevicesInformation()) {
			if (device.isFree() && device.getConnected()) {
				System.out.println(
						String.format("Device '%s' wil be reserved by '%s'", device.getUdid(), api.getUserName()));
				api.createReservation(device.getUdid());
				break;
			}
		}
		System.out.println("============ Reservation  ============");
		for (Reservation reservation : api.getAllReservationInformation()) {
			System.out.println("User: " + reservation.getReservedForUser().getName() + "\nReservation ID: "
					+ reservation.getReservationUid() + "\nDevice: "
					+ reservation.getDeviceCapabilities().getDeviceName());
			System.out.println("------------------------------------");
		}
		System.out.println("============ Reservation by user  ============");
		System.out.println("Removing all reservations from user " + api.getUserName());
		for (Reservation reservation : api.getAllReservationInformation("guilherme.sousa@rsinet.com.br")) {
			System.out.println("User: " + reservation.getReservedForUser().getName() + "\nReservation ID: "
					+ reservation.getReservationUid() + "\nDevice: "
					+ reservation.getDeviceCapabilities().getDeviceName());
			System.out.println("------------------------------------");
		}

		System.out.println("====== Remove all reservations from user ========");
		for (Reservation reservation : api.getAllReservationInformation("guilherme.sousa@rsinet.com.br")) {
			System.out.println("User: " + reservation.getReservedForUser().getName() + "\nReservation ID: "
					+ reservation.getReservationUid() + "\nDevice: "
					+ reservation.getDeviceCapabilities().getDeviceName());
			System.out.println("------------------------------------");
		}

	}

	public String getUserName() {
		return this.userName;
	}

	public String getCurrentTimeUsingCalendar() {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		return dateFormat.format(date);
	}

	public String getCurrentTimeUsingCalendar(int reservationTime) {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		cal.setTime(date);
		cal.add(Calendar.MINUTE, reservationTime);
		return dateFormat.format(cal.getTime());
	}
}