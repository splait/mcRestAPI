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
    private static final String ENDPOINT_CLIENT_INSTALL_APPS = "apps/install";
    private static final String ENDPOINT_CLIENT_UPLOAD_APPS = "apps/upload?enforceUpload=true";
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
    private String responseBodyStr;

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
    // Install application
    // ************************************

    private void installApp(String package_name, String version, String device_id, Boolean is_intrumented) throws IOException {
        String counter = version;
//        package_name = "com.Advantage.aShopping";
        String str = "{\n" +
                "  \"app\": {\n" +
                "    \"counter\": " + counter + ",\n" +
                "    \"id\": \"" + package_name + "\",\n" +
                "    \"instrumented\": " + (is_intrumented ? "true" : "false") + "\n" +
                "  },\n" +
                "  \"deviceCapabilities\": {\n" +
                "    \"udid\": \"" + device_id + "\"\n" +
                "  }\n" +
                "}";
        RequestBody body = RequestBody.create(JSON, str);
        executeRestAPI(ENDPOINT_CLIENT_INSTALL_APPS, HttpMethod.POST, body);
    }

    // ************************************
    // Install application by file name, when there are multiple matches for file name in database, will select first application.
    // ************************************

    private void installAppByFileAndDeviceID(String filename, String device_id, Boolean is_intrumented) throws IOException {
        apps();
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
        System.out.println("\n" + request);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println(response.toString());
                final ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    responseBodyStr = responseBody.string();
                    System.out.println("Body: " + responseBodyStr);
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
                .url(BASE_URL + ENDPOINT_CLIENT_UPLOAD_APPS)
                .post(requestBody)
                .build();

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
    // main
    // ************************************

    public static void main(String[] args) throws IOException {
        try {
            APIClient client = new APIClient(username, password);
            client.deviceContent();
//            client.installAppByFileAndDeviceID("AdvantageShoppingInstrumented.apk", "058530e7", true);
//            client.apps();
//            client.uploadApp(APP);
//            client.users();
//            client.logout();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.toString());
        }
    }

    private enum HttpMethod {
        GET,
        POST
    }

    private String parseProperty(String source, String prefix, String suffix) {
        try {
            String str = source.split(prefix)[1];
            return str.split(suffix)[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}