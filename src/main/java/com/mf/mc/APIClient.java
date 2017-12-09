package com.mf.mc;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class APIClient {

    // *************************************
    // Initiate the constants with your data
    // *************************************

    private static String BASE_URL = "http://<SERVER>:8080/rest/";
    private static String username = "<USER>";
    private static String password = "<PASSWORD>";

    // ************************************
    // Mobile Center APIs end-points
    // ************************************

    private static final String ENDPOINT_CLIENT_LOGIN = "client/login";
    private static final String ENDPOINT_CLIENT_LOGOUT = "client/logout";
    private static final String ENDPOINT_CLIENT_DEVICES = "deviceContent";
    private static final String ENDPOINT_CLIENT_APPS = "apps";
    private static final String ENDPOINT_CLIENT_USERS = "v2/users";

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

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType APK = MediaType.parse("application/vnd.android.package-archive");

    // ******************************************************
    // APIClient class constructor to store all info and call API methods
    // ******************************************************

    private APIClient(String username, String password) throws IOException {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .readTimeout(240, TimeUnit.SECONDS)
                .writeTimeout(240, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        List<Cookie> storedCookies = cookieStore.get(url.host());
                        if (storedCookies == null) {
                            storedCookies = new ArrayList<>();
                            cookieStore.put(url.host(), storedCookies);
                        }
                        storedCookies.addAll(cookies);
                        for (Cookie cookie: cookies) {
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
        login(username, password);
    }

    // ***********************************************************
    // Login to Mobile Center for getting cookies to work with API
    // ***********************************************************

    private void login(String username, String password) throws IOException {

        String strCredentials = "{\"name\":\"" + username + "\",\"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(JSON, strCredentials);
        executeRestAPI(ENDPOINT_CLIENT_LOGIN, HttpMethod.POST, body);

    }

    // ************************************
    // List all apps from Mobile Center
    // ************************************

    private void apps() throws IOException {
        executeRestAPI(ENDPOINT_CLIENT_APPS);
    }


    // ************************************
    // List all users from Mobile Center
    // ************************************

    private void users() throws IOException {
        executeRestAPI(ENDPOINT_CLIENT_USERS);

    }

    // ************************************
    // List all devices from Mobile Center
    // ************************************

    private void deviceContent() throws IOException {
        executeRestAPI(ENDPOINT_CLIENT_DEVICES);
    }


    // ************************************
    // Logout from Mobile Center
    // ************************************

    private void logout() throws IOException {
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
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .addHeader("Content-type", JSON.toString())
                .addHeader("Accept", JSON.toString());

        // build the http method
        if (HttpMethod.GET.equals(httpMethod)) {
            builder.get();
        } else
        if (HttpMethod.POST.equals(httpMethod)) {
            builder.post(body);
        }

        Request request = builder.build();
        System.out.println("\n" + request);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println(response.toString());
                final ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    System.out.println("Body: " + responseBody.string());
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
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", parts[parts.length - 1],
                        RequestBody.create(APK, new File(filename)))
                .build();

        Request request = new Request.Builder()
                .addHeader("content-type", "multipart/form-data")
                .addHeader("x-hp4msecret", hp4msecret)
                .addHeader("JSESSIONID", jsessionid)
                .url(BASE_URL + ENDPOINT_CLIENT_APPS)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Done!");
                System.out.println(response.toString());
            } else {
                throw new IOException("Unexpected code " + response);
            }
            response.close();

        }

    }

    // ************************************
    // main
    // ************************************

    public static void main(String[] args) throws IOException {
        try{
            APIClient client = new APIClient(username, password);
            client.deviceContent();
            client.apps();
            //client.uploadApp(APP);
            client.users();
            client.logout();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.toString());
        }
    }

    private enum HttpMethod {
        GET,
        POST
    }

}
