#!/usr/bin/env nextflow

nextflow.enable.dsl = 2

/*******************************************************************************
 * Define main workflow
 ******************************************************************************/

workflow {
    def ch_param = Channel.of(1..5)

    def multi = ch_param.multiMap {
        a: it
        b: it
    }

    multi.a.view()

    Custom.doMap(ch_param).view()
}
