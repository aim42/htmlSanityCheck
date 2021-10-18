def url = "https://www.google.com"
def content

try {
    def response = url.toURL().openConnection().getResponseCode()

    println "class = " + url.toURL().openConnection().getClass()
    println "class http://unknownxxx.hostyyy.toURL().openConnection() = " + "http://unknownxxx.hostyyy".toURL().openConnection().getClass()

    println "response = $response"
}
catch (UnknownHostException uhe ) {
    println "UnknownHostException " + uhe.message
}


//try {
//    content = url.toURL().openConnection().with { conn ->
//        readTimeout = 10000
//        if( responseCode != 200 ) {
//            throw new Exception( 'Not Ok' )
//        }
//        conn.content.withReader { r ->
//            r.text
//        }
//    }
//}
//catch( e ) {
//    content="site $url is not available."
//}