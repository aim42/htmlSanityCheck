
// invalid
assert containsInvalidChars( "#Context Analysis" ) == true
//assert containsInvalidChars( " " ) == true
//assert containsInvalidChars( "*Context-Analysis") == true

// valid
//assert containsInvalidChars( "#ContextAnalysis" ) == false
//assert containsInvalidChars( "#Context-Analysis" ) == false
//assert containsInvalidChars( "#Context_Analysis" ) == false


public static boolean containsInvalidChars( String aString ) {
   String illegalCharsRegex = / |!|\$|\*/

   
   def m = (aString =~ illegalCharsRegex)
   // assert m instanceof Matcher
   
   println "matcher = " + m
   println "matcher.find = " + m.find()
   
   print """\"$aString\" """ 
   
   if (!m) {
      println "does NOT contain illegal chars."
      return false
   }
   else { 
      println "contains illegal chars."
      return true
   }   
}