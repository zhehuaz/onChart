package me.zchang.onchart.http;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

 /** A http request for response.
 * HttpRequest must be configured when being created to be sent to a specific host.
 * Instances of HttpRequest are not reusable; you must use a different instance for
 * each request to a host.
 */
 @Deprecated
public class HttpRequest {
    // TODO Builder pattern
    private int sequence;
    private HttpResponse.ResponseListener responseListener;
    private HttpResponse.ErrorListener errorListener;
    private URL requestUrl;
    private RequestMethod method;
    private String charset;

    /**
     * Constructs a new HttpRequest instance pointing to the host/program specified
     * by the url in method of {@link #method}.
     * @param url the URL.
     * @param method the method GET or POST.
     * @param responseListener the listener to the asynchronous response.
     * @param errorListener the listener to error information.
     * @param charset charset of response, set as GBK by default if null.
     * @throws MalformedURLException the exception thrown by the constructor of {@link URL}.
     */
    public HttpRequest(String url,
                       RequestMethod method,
                       HttpResponse.ResponseListener responseListener,
                       HttpResponse.ErrorListener errorListener,
                       @Nullable String charset)
            throws MalformedURLException {
        requestUrl = new URL(url);
        this.responseListener = responseListener;
        this.errorListener = errorListener;
        if(method != null)
            this.method = method;
        else
            this.method = RequestMethod.GET;
        if(charset != null)
            this.charset = charset;
        else
            this.charset = "GBK";
    }

    /**
     * Constructs a new HttpRequest instance pointing to the host/program specified
     * by the url in method of GET as default.
     * @param url the URL.
     * @param responseListener the listener to the asynchronous response.
     * @param errorListener the listener to error information.
     * @throws MalformedURLException the exception thrown by the constructor of {@link URL}.
     */
    public HttpRequest(String url,
                       HttpResponse.ResponseListener responseListener,
                       HttpResponse.ErrorListener errorListener)
            throws MalformedURLException {
        this(url, RequestMethod.GET, responseListener, errorListener, null);
    }

     /**
      * Constructs a new HttpRequest instance pointing to the host/program specified
      * by the url.Besides, this constructor is for synchronous
      * connection.
      * @param url the URL
      * @param method the method GET or POST.
      * @throws MalformedURLException the exception thrown by the constructor of {@link URL}.
      */
    public HttpRequest(String url,
                       RequestMethod method)
            throws MalformedURLException {
        this(url, method, null, null, null);
    }

     /**
      * Constructs a new HttpRequest instance pointing to the host/program specified
      * by the url in method of GET as default.Besides, this constructor is for synchronous
      * connection.
      * @param url the URL.
      * @throws MalformedURLException
      */
    public HttpRequest(String url)
            throws MalformedURLException {
        this(url, null);
    }

    /**
     * Send a request asynchronously.
     * The response will be passed to the {@link #responseListener}, and the error
     * message passed to the {@link #errorListener} as the unexpected occurs.
     */
    public void asyncSend() {
        new AsyncTask<URL, String, HttpResponse>() {

            @Override
            protected HttpResponse doInBackground(URL... params) {
                try {
                    return send();
                } catch (IOException e) {
                    e.printStackTrace();
                    if(errorListener != null)
                        errorListener.onError(new HttpError(HttpError.ERROR_CODE_IO));
                }
                return null;
            }
            @Override
            protected void onPostExecute(HttpResponse response) {
                super.onPostExecute(response);
                if(responseListener != null)
                    responseListener.onResponse(response);
            }
        }.execute(requestUrl);
    }

    /**
    * Send a request synchronously.
    * The response is passed back as return.
    * @return the response.
    * @throws IOException thrown by input stream or output stream.
    */
    public HttpResponse send() throws IOException {
        // setup connection
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        // set necessary params
        connection.setDoOutput(true);
        connection.setRequestMethod(method2String(method));
        // set headers
        Map<String, String> propParams = getParams();
        for (Map.Entry<String, String> m : propParams.entrySet()) {
            connection.setRequestProperty(m.getKey(), m.getValue());
        }
        // post data
        if(method == RequestMethod.POST) {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(getSentData().getBytes());
        }
        // get response
        String buffer = null;
        String content = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        while((buffer = reader.readLine()) != null) {
            content += buffer;
        }
        HttpResponse response = new HttpResponse(connection.getURL(), content);// be careful
        // get response header
        response.setHeader(connection.getHeaderFields());
        connection.disconnect();
        return response;
    }

    private String method2String(RequestMethod method) {
        if(method == RequestMethod.GET)
            return "GET";
        else if(method == RequestMethod.POST)
            return "POST";
        else
            return null;
    }

    public int getSequence() {
     return sequence;
    }

    public void setSequence(int sequence) {
     this.sequence = sequence;
    }

    /**
     * Override getParams() to customize param in the header of request.
     * The params are stored as key-value pairs.
     * @return the params to be sent in the header of request.
     */
    protected Map<String, String> getParams() {
        return new HashMap<>();
    }

    /**
     * Override getSentData() to customize data to be POSTed to the host.
     * The params are stored as String, so you have to organize you data in
     * "xx=xx&xx=xx" formation.
     * @return the data to be sent in POST request.
     */
    protected String getSentData() {
        return "";
    }
}
