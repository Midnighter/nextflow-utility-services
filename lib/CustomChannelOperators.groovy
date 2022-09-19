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
 * Provide a collection of custom channel operators that go beyond the nextflow default.
 *
 * @author Moritz E. Beber <https://github.com/Midnighter>
 * @author Mahesh Binzer-Panchal <https://github.com/mahesh-panchal>
 */
class CustomChannelOperators {

    /**
     * Join two channels by one or more keys from a map contained in each channel.
     *
     * The channel elements are assumed to be tuples whose size is at least two.
     * Typically, the maps to join by are in the first position of the tuples.
     * Please read https://www.nextflow.io/docs/latest/operator.html#join carefully.
     *
     * @param args A map of keyword arguments that is passed on to the nextflow join call.
     * @param left The left-hand side channel in the join.
     * @param right The right-hand side channel in the join.
     * @param key A string or list of strings providing the map keys to compare.
     * @param leftBy The position of the map in the left channel.
     * @param rightBy The position of the map in the right channel.
     * @return The joined channels with the map in the original position of the left channel,
     *      followed by all elements of the right channel except for the map.
     */
    static Object joinOnKeys(
            Map joinArgs = [:],
            groovyx.gpars.dataflow.DataflowBroadcast left,
            groovyx.gpars.dataflow.DataflowBroadcast right,
            key,
            int leftBy = 0,
            int rightBy = 0
    ) {
        List keys = key instanceof List ? key : [ key ]

        // Extract desired keys from the left map, located at `leftBy`, and prepend them.
        groovyx.gpars.dataflow.DataflowBroadcast newLeft = left.map { tuple ->
            tuple[leftBy].subMap(keys).values() + tuple
        }

        // Extract desired keys from the right map, located at `rightBy`, and prepend them.
        // Also drop the map itself from the right.
        groovyx.gpars.dataflow.DataflowBroadcast newRight = right.map { tuple ->
            tuple[rightBy].subMap(keys).values() +
                tuple[0..<rightBy] +
                tuple[(rightBy + 1)..<tuple.size()]
        }

        // Set the positions to join on explicitly.
        joinArgs.by = 0..<keys.size()

        // Apply the join channel operator to the channels and finally drop the keys used for joining tuples.
        return newLeft.join(joinArgs, newRight).map { tuple ->
            tuple[keys.size()..<tuple.size()]
        }
    }

}
