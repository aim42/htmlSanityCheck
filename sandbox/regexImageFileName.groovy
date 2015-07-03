def HTML_FILE_EXTENSION_PATTERN = ~/(?i).+\.htm(l)?$/
def IMAGE_FILE_EXTENSION_PATTERN = ~/(?i).+\.(jpg|jpeg|png|gif|bmp)?$/

def fileName = "test.JPEG"

def filenames = ["test.jpg", "test.png", "test.txt"]

println fileName ==~ IMAGE_FILE_EXTENSION_PATTERN

filenames.each { fn ->
   println fn + ": " + (fn ==~ IMAGE_FILE_EXTENSION_PATTERN)
}