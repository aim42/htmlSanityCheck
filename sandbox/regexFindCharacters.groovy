String aString1 = "#Context Analysis"

String illegalCharsRegex = / |!/

def m = aString =~ illegalCharsRegex

if (!(aString =~ illegalCharsRegex)) {                                                         
    println "chars not found"
 }
else println "found"