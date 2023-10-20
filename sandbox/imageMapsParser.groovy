import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

def Elements getAreasForMapName( Document doc, String mapName ) {
}



def Elements getAreasForMap( Element map ) {
    println "getAreasForMap: " + map
    println "areas: " + map.children().select("area")
    return map.children().select("area")
}


def Elements getImagesForMap( Document doc, String mapName ) {
  return doc?.getElementsByAttributeValue("usemap", "#" + mapName)
}

def Elements getImagesWithUsemapDeclaration( Document doc ) {
  return doc?.select("img[usemap]")
}


def Elements getMaps( Document doc ) {
    return  doc?.select("map")
}

def Elements getMapsByName( Document doc, String mapName ) {
    return doc.select("""map[name=${mapName}]""")
}

def ArrayList<String> getHrefsForMap( Element map) {
   ArrayList<String> hrefs = new ArrayList()
   
   getAreasForMap( map )?.each{ area ->
       hrefs += area.attr("href")
   }
   return hrefs
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

<img src="image.jpg" usemap="#oneMap">

"""

String html= HTML_HEAD + imageMap + HTML_END

Document doc = Jsoup.parse(html, "UTF-8" );

maps =  getMaps( doc )

println getImagesWithUsemapDeclaration( doc )

println "="*80

Elements yourMap = getMapsByName( doc, "yourMap" )
println "getMapByName( yourmap ) = " + yourMap
println "="*80

println "nr of maps: ${maps.size()}"

//println maps

maps.each  { oneMap ->

   String name = oneMap.attr("name")
   
   // 1.) find maps that are NOT referenced by images
   Elements imageElements = getImagesForMap( doc, name );
   if(imageElements.size() == 0) {
       print name + " not referenced by any image."
   }
       

   // 2.) are there any areas defined in the map?
   Elements areas = getAreasForMap( oneMap )
   println areas.size() + " areas found for map $name "
 
        
   // 3.) are there hrefs defined within areas?
   ArrayList<String> hrefs = getHrefsForMap( oneMap )
   println "hrefs = " + hrefs
 
 
   // 4.) checker needs to verify these hrefs...
   // TODO
   
 }


println "*"*50
