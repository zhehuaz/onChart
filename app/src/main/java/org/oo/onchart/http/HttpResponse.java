package org.oo.onchart.http;

import java.util.List;
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

/**
 * An entity class for a response.A response is sent back from host
 * for a request.
 */
public class HttpResponse {
    private String requestUrl;
    private String responseContent;
    private Map<String, List<String>> header;

    public HttpResponse(String requestUrl, String responseContent) {
        this.responseContent = responseContent;
        this.requestUrl = requestUrl;
    }

    public void setHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public interface ResponseListener {
        void onResponse(HttpResponse response);
    }

    public interface ErrorListener {
        void onError(HttpError error);
    }


}
