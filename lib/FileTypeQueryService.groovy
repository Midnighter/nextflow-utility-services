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

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Define a service class to interrogate a file for its type.
 *
 * For simplicity, we assume that a filename has at most two extensions. It must
 * always have one extension corresponding to its specific type and may
 * optionally have a second extension denoting its compression type.
 *
 * @author Moritz E. Beber
 */
class FileTypeQueryService {

    protected static String cramExtension = 'cram'
    protected static Set<String> compressionExtensions = ['gz', 'bz2', 'zstd', 'lz4', cramExtension] as Set<String>
    protected static Set<String> fastaExtensions = ['fasta', 'fa', 'faa', 'fna', 'ffn', 'frn'] as Set<String>
    protected static Set<String> fastqExtensions = ['fastq', 'fq'] as Set<String>
    protected static Set<String> samExtensions = ['sam', 'bam', cramExtension] as Set<String>

    static boolean isCompressed(Path filename) {
        return compressionExtensions.contains(filename.extension)
    }

    static String getCompressionType(Path filename) {
        if (!isCompressed(filename)) {
            throw new Exception('The given file does not seem to be compressed.')
        }
        return filename.extension
    }

    static boolean isFastA(Path filename) {
        Path result = isCompressed(filename) ? Paths.get(filename.baseName) : filename
        return fastaExtensions.contains(result.extension)
    }

    static boolean isFastQ(Path filename) {
        Path result = isCompressed(filename) ? Paths.get(filename.baseName) : filename
        return fastqExtensions.contains(result.extension)
    }

    static boolean isSAM(Path filename) {
        Path result = filename
        if (isCompressed(filename)) {
            if (filename.extension == cramExtension) {
                return true
            }
            result = Paths.get(filename.baseName)
        }
        return samExtensions.contains(result.extension)
    }

    static String getFileType(Path filename) {
        Path result = filename
        if (isCompressed(filename)) {
            if (filename.extension == cramExtension) {
                return cramExtension
            }
            result = Paths.get(filename.baseName)
        }
        return result.extension
    }

    static String getSimpleName(Path filename) {
        Path result = filename
        if (isCompressed(filename)) {
            if (filename.extension == cramExtension) {
                return filename.baseName
            }
            result = Paths.get(filename.baseName)
        }
        return result.baseName
    }

    static Map getFileInfo(Path filename) {
        Map result = [:].tap {
            is_compressed = isCompressed(filename)
            compression_type = is_compressed ? getCompressionType(filename) : null
            is_fasta = isFastA(filename)
            is_fastq = isFastQ(filename)
            is_sam = isSAM(filename)
            file_type = getFileType(filename)
            simple_name = getSimpleName(filename)
        }
        return result
    }

}
