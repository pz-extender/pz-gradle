/*
Copyright (c) 2019-2021 OpenRS2 Authors

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
*/
package info.pzss.zomboid.gradle.tasks.decompile

import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider
import org.jetbrains.java.decompiler.main.extern.IResultSaver
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile
import java.util.jar.Manifest

class DecompilerIo(private val destination: Path) : IBytecodeProvider, IResultSaver, Closeable {
    private val inputJars = mutableMapOf<String, JarFile>()

    override fun getBytecode(externalPath: String, internalPath: String?): ByteArray {
        if (internalPath == null) {
            throw UnsupportedOperationException()
        }

        val jar = inputJars.computeIfAbsent(externalPath) {
            JarFile(it)
        }

        jar.getInputStream(jar.getJarEntry(internalPath)).use {
            return it.readBytes()
        }
    }

    override fun saveFolder(path: String) {
        // ignore
    }

    override fun copyFile(source: String, path: String, entryName: String) {
        throw UnsupportedOperationException()
    }

    override fun saveClassFile(
        path: String,
        qualifiedName: String,
        entryName: String,
        content: String,
        mapping: IntArray
    ) {
        throw UnsupportedOperationException()
    }

    override fun createArchive(path: String, archiveName: String, manifest: Manifest?) {
        // ignore
    }

    override fun saveDirEntry(path: String, archiveName: String, entryName: String) {
        // ignore
    }

    override fun copyEntry(source: String, path: String, archiveName: String, entry: String) {
        // ignore
    }

    override fun saveClassEntry(
        path: String,
        archiveName: String,
        qualifiedName: String,
        entryName: String,
        content: String
    ) {
        val p = destination.resolve(entryName)
        Files.createDirectories(p.parent)
        Files.writeString(p, content)
    }

    override fun closeArchive(path: String, archiveName: String) {
        // ignore
    }

    override fun close() {
        for (jar in inputJars.values) {
            jar.close()
        }
    }
}
