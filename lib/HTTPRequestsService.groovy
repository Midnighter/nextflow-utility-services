/**
 * MIT License
 *
 * Copyright (c) 2022 Moritz E. Beber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Define a service for making HTTP requests.
 *
 * @author Moritz E. Beber
 */
class HTTPRequestsService {

    /**
    * Make an HTTP POST request with optional message body and request headers.
    *
    * @param location The URL to make a request to. Has to use protocol http:// or https://.
    * @param body The raw message body as a string. Should correspond to the content type.
    * @param contentType A shortcut for setting the `Content-Type` header.
    * @param accept A shortcut for setting the `Accept` header.
    * @param headers Pass any number of named arguments which will be set as request headers.
    */
    static HTTPResponse post(
        String location,
        String body = null,
        String contentType = 'application/json',
        String accept = 'application/json',
        Map headers = [:]
    ) {
        HttpURLConnection request = openRequest(location)

        // Prepare request method and content type.
        request.requestMethod = 'POST'
        request.setRequestProperty('Content-Type', contentType)
        request.setRequestProperty('Accept', accept)
        // Headers may overwrite content type and accept.
        setHeaders(headers, request)

        // Create message body.
        if (body) {
            addBody(body, request)
        }

        // Perform actual POST request.
        request.connect()
        return new HTTPResponse(request)
    }

    protected static HttpURLConnection openRequest(String location) {
        URL url = new URL(location)
        assert url.protocol ==~ /https?/, "Only HTTP or HTTPS URLs are supported. Found ${url.scheme}."
        return url.openConnection() as HttpURLConnection
    }

    protected static void setHeaders(Map headers, HttpURLConnection request) {
        headers.each { key, value ->
            request.setRequestProperty(key, value)
        }
    }

    protected static void addBody(String body, HttpURLConnection request) {
        request.doOutput = true
            /* groovylint-disable-next-line UnnecessaryGetter */
        OutputStream stream = request.getOutputStream()
        stream.write(body.getBytes('UTF-8'))
        stream.flush()
        stream.close()
    }

}
