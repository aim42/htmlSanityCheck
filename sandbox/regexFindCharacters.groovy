String aString = "#Context Analysis"

// invalid
assert containsInvalidChars( "#Context Analysis" ) == true
assert containsInvalidChars( " " ) == true

// valid
assert containsInvalidChars( "#ContextAnalysis" ) == false
assert containsInvalidChars( "#Context-Analysis" ) == false
assert containsInvalidChars( "#Context_Analysis" ) == false


def boolean containsInvalidChars( String aString ) {
   String illegalCharsRegex = / |!/

   if (!(aString =~ illegalCharsRegex)) {
      println "chars not found"
      return false
   }
   else { 
      println "found"
      return true
   }   
}
