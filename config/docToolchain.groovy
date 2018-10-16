outputPath = 'build/docs'

// Path where the docToolchain will search for the input files.
// This path is appended to the docDir property specified in gradle.properties
// or in the command line, and therefore must be relative to it.

inputPath = 'src/docs'


inputFiles = [
        [file: 'adoc-sandbox.adoc',    formats: ['html','pdf']],
        [file: 'DevelopmentDocs.adoc', formats: ['html','pdf']],
        [file: 'hsc_arc42.adoc',       formats: ['html','pdf']],
        [file: 'index.adoc',           formats: ['html','pdf']],
]


taskInputsDirs = ["${inputPath}/images"]

taskInputsFiles = []

//*****************************************************************************************

//Configureation for publishToConfluence

confluence = [:]

// 'input' is an array of files to upload to Confluence with the ability
//          to configure a different parent page for each file.
//
// Attributes
// - 'file': absolute or relative path to the asciidoc generated html file to be exported
// - 'url': absolute URL to an asciidoc generated html file to be exported
// - 'ancestorId' (optional): the id of the parent page in Confluence; leave this empty
// 							  if a new parent shall be created in the space
// - 'preambleTitle' (optional): the title of the page containing the preamble (everything
//                               before the first second level heading). Default is 'arc42'
//
// only 'file' or 'url' is allowed. If both are given, 'url' is ignored
confluence.with {
    input = [
            [ file: "build/docs/html5/hsc_arc42.html" ],
            [ file: "build/docs/html5/index.html" ],
            [ file: "build/docs/html5/adoc-sandbox.html" ],
            [ file: "build/docs/html5/DevelopmentDocs.html" ],
    ]


    // endpoint of the confluenceAPI (REST) to be used
    api = 'https://arc42-template.atlassian.net/wiki/rest/api/'

    // the key of the confluence space to write to
    spaceKey = 'HSC'

    // variable to determine whether ".sect2" sections shall be split from the current page into subpages
    createSubpages = true

    // the pagePrefix will be a prefix for each page title
    // use this if you only have access to one confluence space but need to store several
    // pages with the same title - a different pagePrefix will make them unique
    pagePrefix = ''

    // username:password of an account which has the right permissions to create and edit
    // confluence pages in the given space.
    // if you want to store it securely, fetch it from some external storage.
    // you might even want to prompt the user for the password like in this example

    credentials = "ralf.d.mueller+robot@gmail.com:${System.env['CONFLUENCE_API_KEY']}".bytes.encodeBase64().toString()

    // HTML Content that will be included with every page published
    // directly after the TOC. If left empty no additional content will be
    // added
    // extraPageContent = '<ac:structured-macro ac:name="warning"><ac:parameter ac:name="title" /><ac:rich-text-body>This is a generated page, do not edit!</ac:rich-text-body></ac:structured-macro>
    extraPageContent = ''
}
