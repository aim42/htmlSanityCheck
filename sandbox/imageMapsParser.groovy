import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element

def Elements getHrefsForMap( Elements maps, String mapName ) {
   
}

def Elements getAreasForMap( Element map ) {
    return map.children().select("area")
}

def Elements getImagesForMap( Document doc, String mapName ) {
  return doc?.getElementsByAttributeValue("usemap", "#" + mapName)
}


def Elements getMaps( Document doc ) {
    return  doc?.select("map")
}

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

maps =  getMaps( doc )



println "="*80
Elements yourMap = doc.select("map[name=yourMap]")
println "yourmap = " + yourMap
println "="*80

println "nr of maps: ${maps.size()}"

//println maps

maps.each  { oneMap ->

        String name = oneMap.attr("name")
        // find maps that are NOT referenced by images
        Elements imageElements = getImagesForMap( doc, name );
        
        if(imageElements.size() == 0) {
                print name + " not referenced by any image."
        }
       
       // find areas within map
        Elements areas = getAreasForMap( oneMap )
        
        println areas.size() + " areas found for map $name "
        
        // find hrefs within areas
        ArrayList hrefs = new ArrayList()
        areas.each { area ->
           hrefs += area.attr("href")
        }   
        println "hrefs = " + hrefs
 }
println "*"*50



//println maps.first()
//println maps.first().getClass()


