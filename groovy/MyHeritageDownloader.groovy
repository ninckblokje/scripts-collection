// Copyright (c) 2014, ninckblokje
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
// 
// * Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import java.util.regex.Matcher
import java.util.UUID

def downloadUrl(sourceUrl, siteId) {
	def subDir = new File(siteId, "files")
	subDir.mkdirs()
	
	def extension = sourceUrl.tokenize(".")[-1]
	def newName = UUID.randomUUID() as String
	
	def targetFile = new File(subDir, "${newName}.${extension}")
	println "Downloading URL [${sourceUrl}] to file [${targetFile}]"
	
	assert !targetFile.exists() && !targetFile.isDirectory(), "File [${targetFile}] already exists!]"
	
	def out = new BufferedOutputStream(new FileOutputStream(targetFile))
    out << new URL(sourceUrl).openStream()
    out.close()
	
	return [siteId: siteId, directory: "files", fileName: targetFile.name]
}

def isLineType(lineData, expectedLineType) {
	return lineData.id == expectedLineType.id && lineData.tag == expectedLineType.tag
}

def getLineData(line) {
	Matcher lineMatcher = line =~ /^(\d{1,}) ([A-Za-z_\-@]{1,}) ?(.*)$/
	
	if (!lineMatcher.matches()) {
		println "Unable to parse line [${line}]"
		return null
	}
	
	def lineData = [id: null, tag: null, value:null]
	lineData.id = lineMatcher[0][1]
	lineData.tag = lineMatcher[0][2]
	lineData.value = lineMatcher[0][3]
	
	return lineData
}

def cli = new CliBuilder(usage: "MyHeritageDownloader.groovy -i [GED_FILE]")
cli.with {
	h(longOpt: "help", "Help", required: false)
	i(longOpt: "input", "Input GED file", required: true, args: 1)
}

def options = cli.parse(args)
if (!options) {
	return
} else if (options.h) {
	cli.usage()
	return
}

def inputFile = new File(options.i)
println "Parsing GED file [${inputFile}] and downloading pictures"

def EXPORTED_FROM_SITE_LINE = [id: "1", tag: "_EXPORTED_FROM_SITE_ID"]
def FILE_TO_DOWNLOAD_LINE = [id: "2", tag: "FILE"]

def siteId = null

def lineNumber = 1
new File("${options.i}.tmp").withWriter { out ->
	inputFile.eachLine { line ->
		println "Parsing line [${lineNumber++}]"
		def lineData = getLineData(line)
		if (lineData != null) {
			if (isLineType(lineData, EXPORTED_FROM_SITE_LINE)) {
				siteId = lineData.value
				new File(siteId).mkdirs()
				out.println line
			} else if (isLineType(lineData, FILE_TO_DOWNLOAD_LINE)) {
				def downloadData = downloadUrl(lineData.value, siteId)
				out.println "${FILE_TO_DOWNLOAD_LINE.id} ${FILE_TO_DOWNLOAD_LINE.tag} ${downloadData.directory}/${downloadData.fileName}"
			} else {
				out.println line
			}
		} else {
			out.println line
		}
	}
}

println "Moving temporary file [${options.i}.tmp] to [${siteId}/${options.i}]"
new AntBuilder().move(file: "${options.i}.tmp", tofile: "${siteId}/${options.i}")
