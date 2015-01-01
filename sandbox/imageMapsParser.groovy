import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

String HTML_HEAD = "<!DOCTYPE HTML> <html><head></head><body>"
String HTML_END = "</body></html>"
String imageMap = """
<img src="image.gif" usemap="#mymap">
<map name="mymap">
    <area shape="rect" coords="0,0,82,126" href="#test1">
    <area shape="circle" coords="90,58,3" href="#test2" >
</map> 
<img src="image.gif" usemap="#yourmap">
<map name="yourmap">
    <area shape="rect" coords="0,0,82,126" href="#test1">
</map> 
<a href="map#">wtf</a>
<map name="map42">
    <area shape="rect" coords="0,0,82,126" href="#test1">
</map> 
"""

String html= HTML_HEAD + imageMap + HTML_END

Document doc = Jsoup.parse(html, "UTF-8" );

Elements maps = doc.select("map"); 

println "nr of elements: ${maps.size()}"

//println maps

maps.each  { element ->

        String name = element.attr("name")
        // find maps that are NOT referenced by images
        Elements imageElements = doc.getElementsByAttributeValue("usemap", "#" + element.attr("name"));
        if(imageElements.size() == 0) {
                print element.attr("name")
                println " has no image"
        }
       
       // find areas within map
        Elements areas = element.children().select("area")
        println areas.size() + " areas found for map $name "
        ArrayList hrefs = new ArrayList()
        areas.each { area ->
           hrefs += area.attr("href")
        }   
        println "hrefs = " + hrefs
 }
println "*"*50


//println maps.first()
//println maps.first().getClass()

