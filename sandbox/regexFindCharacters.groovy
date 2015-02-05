
// invalid

assert containsInvalidChars( "#Context Analysis" ) 
assert containsInvalidChars( " " ) 
assert containsInvalidChars( "*Context-Analysis") 

// valid
//assert containsInvalidChars( "#ContextAnalysis" ) == false
//assert containsInvalidChars( "#Context-Analysis" ) == false
//assert containsInvalidChars( "#Context_Analysis" ) == false


public static boolean containsInvalidChars( String aString ) {
   String illegalCharsRegex = / |!|\$|\*/

   
   def m = (aString =~ illegalCharsRegex)
   
   print """\"$aString\" """ 
   
   if (!m.find()) {
      println "does NOT contain illegal chars."
      return false
   }
   else { 
      println "contains illegal chars."
      return true
   }   
}