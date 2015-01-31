String aString = "#Context Analysis"

// invalid
assert containsInvalidChars( "#Context Analysis" ) == true
assert containsInvalidChars( " " ) == true

// valid
assert containsInvalidChars( "#ContextAnalysis" ) == false
assert containsInvalidChars( "#Context-Analysis" ) == false
assert containsInvalidChars( "#Context_Analysis" ) == false


public static boolean containsInvalidChars( String aString ) {
   String illegalCharsRegex = / |!/

   print """\"$aString\" """ 
   
   def m = (aString =~ illegalCharsRegex)
   // assert m instanceof Matcher
   
   if (!(m.find())) {
      println "does NOT contain illegal chars."
      return false
   }
   else { 
      println "contains illegal chars."
      return true
   }   
}
