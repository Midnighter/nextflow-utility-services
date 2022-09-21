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

import groovy.transform.MapConstructor
import groovy.transform.Memoized
import groovy.json.JsonSlurper

/**
 * Define a response data object.
 *
 * @author Moritz E. Beber
 */
@MapConstructor
class HTTPResponse {

    Integer statusCode
    String text
    String contentType
    Map headers

    HTTPResponse(HttpURLConnection request) {
        // Create a copy of the immutable headers map.
        Map responseHeaders = [:]
        request.headerFields.each { key, value ->
            responseHeaders[key] = value
        }
        // Drop the `null` key that contains the response code and message.
        responseHeaders.remove(null)
        this.statusCode = request.responseCode
        this.contentType = request.contentType
        this.headers = responseHeaders
        // Try to parse response message.
        // Could also look at content length before trying that.
        try {
            this.text = request.content.text
        } catch (IOException error) {
            this.text = request.errorStream?.text
        }
    }

    @Memoized
    Map getJson() {
        if (this.text && this.contentType == 'application/json') {
            return new JsonSlurper().parseText(this.text)
        }
    }

}
