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
 * Define a service for posting pipeline status messages to Slack.
 *
 * @author Moritz E. Beber
 */
class SlackMessagingService {

    /**
    * Post a pipeline completion status message to Slack.
    *
    * @param workflow A nextflow workflow object available for introspection upon completion.
    * @param hookURL The hook URL for posting the message to Slack.
    * @param log The nextflow logger object.
    */
    static void postPipelineStatus(
        nextflow.script.WorkflowMetadata workflow,
        String hookURL,
        ch.qos.logback.classic.Logger log
    ) {
        Map pipelineSummary = getPipelineSummary(workflow)
        pipelineSummary.nextflow = getNextflowSummary(workflow)
        pipelineSummary.manifest = getManifestSummary(workflow)
        log.debug(FormattingService.prettyFormat(pipelineSummary))

        String message = renderBlocks(
            pipelineSummary,
            new File("${workflow.projectDir}/assets/pipelineStatusMessage.json")
        )
        log.debug(message)

        // TODO: Implement retrying.
        HTTPResponse response
        try {
            response = HTTPRequestsService.post(hookURL, message)
        } catch (MalformedURLException error) {
            log.error("Your webhook URL '${hookURL}' is malformed. Aborting.")
            return
        }
        log.debug(FormattingService.prettyFormat(response))

        if (response.statusCode != 200) {
            log.error('Posting the pipeline status message to Slack failed.')
        }
    }

    /**
    * Render the Block Kit blocks template with values from summaries.
    *
    * @param binding The overall summary map whose values will be bound to the template.
    * @param blocksTemplate The file containing the specifically designed blocks template.
    * @return The rendered template as a string.
    */
    protected static String renderBlocks(
        Map binding, File blocksTemplate
    ) {
        groovy.text.GStringTemplateEngine engine = new groovy.text.GStringTemplateEngine()
        return engine.createTemplate(blocksTemplate).make(binding).toString()
    }

    /**
    * Collect pipeline summary information from the workflow object.
    *
    * @param workflow A nextflow workflow object available for introspection upon completion.
    * @return A map of all the relevant information collected from the `workflow` object.
    */
    protected static Map getPipelineSummary(
        nextflow.script.WorkflowMetadata workflow,
        List<String> keys = [
            'start', 'complete', 'duration', 'success', 'scriptFile', 'scriptId',
            'repository', 'commitId', 'revision', 'runName', 'commandLine'
        ]
    ) {
        return keys.collectEntries { key -> [key, workflow[key]] }
    }

    /**
    * Collect nextflow summary information from the workflow object.
    *
    * @param workflow A nextflow workflow object available for introspection upon completion.
    * @return A map of all the relevant information collected from the `workflow.nextflow` object.
    */
    protected static Map getNextflowSummary(
        nextflow.script.WorkflowMetadata workflow,
        List<String> keys = [
            'version', 'build', 'timestamp'
        ]
    ) {
        return keys.collectEntries { key -> [key, workflow.nextflow[key]] }
    }

    /**
    * Collect manifest summary information from the workflow object.
    *
    * @param workflow A nextflow workflow object available for introspection upon completion.
    * @return A map of all the relevant information collected from the `workflow.manifest` object.
    */
    protected static Map getManifestSummary(
        nextflow.script.WorkflowMetadata workflow,
        List<String> keys = [
            'name', 'version', 'homePage', 'author', 'doi'
        ]
    ) {
        return keys.collectEntries { key -> [key, workflow.manifest[key]] }
    }

}
