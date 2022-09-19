#!/usr/bin/env nextflow

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

nextflow.enable.dsl = 2

/*******************************************************************************
 * Define processes
 ******************************************************************************/

process LEFT {
    input:
    val meta

    output:
    tuple val(meta), stdout, emit: result

    script:
    """
    echo "${meta.id}"
    sleep 0.01
    """
}

process RIGHT {
    input:
    val meta

    output:
    tuple val(meta), path('*.txt'), emit: result

    script:
    """
    echo "${meta.id}" > "${meta.prefix}.txt"
    echo "${meta.id} ${meta.id}" > "${meta.prefix}_${meta.prefix}.txt"
    sleep 0.01
    """
}

process COMPARE {
    input:
    tuple val(meta), val(id), path(files)

    output:
    tuple val(meta), val(id), path(files)

    script:
    """
    cat ${files}
    echo "${id}"
    sleep 0.2
    """
}

/*******************************************************************************
 * Define main workflow
 ******************************************************************************/

workflow {
    def ch_param = Channel.of(1..20)
        .map { [id: it, prefix: "${it}"] }

    LEFT(ch_param)
    RIGHT(ch_param)

    def ch_joined = CustomChannelOperators.joinOnKeys(LEFT.out.result, RIGHT.out.result, 'id', failOnMismatch: true)

    COMPARE(ch_joined)
}
