# Nextflow Utility Services

A collection of custom Groovy service classes intended for use in nextflow pipelines.

| Service                                                        | Demo                                        | Description                                                                                              |
| -------------------------------------------------------------- | ------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| `lib/FormattingService.groovy`                                 | `nextflow run formatting.nf -dump-channels` | Format objects as JSON for pretty printing.                                                              |
| `lib/FileTypeQueryService.groovy`                              | `nextflow run file_type_query.nf`           | Query file path objects for compression and common sequencing data types. Can be extended indefinitely.  |
| `lib/CustomChannelOperators.groovy`                            | `nextflow run join_on_keys.nf`              | Extract values via given keys from maps in two different channels and join those channels on the values. |
| `lib/HTTPRequestsService.groovy`<br/>`lib/HTTPResponse.groovy` | `nextflow run http_requests.nf`             | Perform HTTP requests via a simplified API. At the moment only POST is supported.                        |

## Usage

If you intend to use one or more of the classes from this repository. Simply copy them from the [`lib`](lib) directory to your pipeline's `lib` directory. Some classes additionally require assets which you should similarly copy from the [`assets`](assets) directory to your pipeline's `assets` directory.

If you want to use a service inside one of your modules, rather than in the main workflow, you may have to import it by its class name.

```nextflow
import Service
```

## Copyright

-   Copyright © 2022, Moritz E. Beber
-   Free software, distributed under the [MIT license](LICENSE)
