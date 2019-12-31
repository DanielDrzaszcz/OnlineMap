
package com.dandrzas.onlinemap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Daniel Drząszcz
 */

@WebService(serviceName = "MapWebService")
public class MapWebService {

    private ImageMenager map;  
    private String mapPath, saveLoc="";
    private byte[] mapByteArr;
    private String mapEncodedString = "";

    public MapWebService() {
        mapPath  = getClass().getClassLoader().getResource("map.png").getPath();        
        map = new ImageMenager(mapPath);
    }

    @WebMethod(operationName = "getMap")
    public String getMap() throws IOException {
        //Enkodowanie do Base64  
        mapByteArr = FileUtils.readFileToByteArray(map.originalFile);
        mapByteArr = FileUtils.readFileToByteArray(map.getScalledFile(0.3, saveLoc + "map_scalled.png"));
        mapEncodedString = Base64.getEncoder().encodeToString(mapByteArr);
        return mapEncodedString;
    }

    @WebMethod(operationName = "getMapByPx")
    public String getMapByPx(@WebParam(name = "p1x") int p1x, @WebParam(name = "p1y") int p1y, @WebParam(name = "p2x") int p2x, @WebParam(name = "p2y") int p2y) throws Exception {
        mapByteArr = FileUtils.readFileToByteArray(map.cut(p1x, p1y, p2x-p1x, p2y-p1y, saveLoc + "map_cut.png"));
        mapEncodedString = Base64.getEncoder().encodeToString(mapByteArr);
        System.out.println("p1x: " + Integer.toString(p1x));
        System.out.println("p1y: " + Integer.toString(p1y));
        System.out.println("p2x: " + Integer.toString(p2x));
        System.out.println("p2y: " + Integer.toString(p2y));
        return mapEncodedString;
    }

    @WebMethod(operationName = "getMapByCoord")
    public String getMapByCoord(@WebParam(name = "p1long") double p1long, @WebParam(name = "p1lat") double p1lat, @WebParam(name = "p2long") double p2long, @WebParam(name = "p2lat") double p2lat) throws Exception {

        System.out.println("p1long: " + Double.toString(p1long));
        System.out.println("p1lat: " + Double.toString(p1lat));
        System.out.println("p2long: " + Double.toString(p2long));
        System.out.println("p2lat: " + Double.toString(p2lat));
        
        int p1x = (int) (((p1long - 18.64141) * 1000) / 0.02137);
        int p1y = (int) (((p1lat - 54.34411) * 1000) / 0.0125);
        int p2x = (int) (((p2long - 18.64141) * 1000) / 0.02137);
        int p2y = (int) (((p2lat - 54.34411) * 1000) / 0.0125);

        System.out.println("p1x: " + Integer.toString(p1x));
        System.out.println("p1y: " + Integer.toString(p1y));
        System.out.println("p2x: " + Integer.toString(p2x));
        System.out.println("p2y: " + Integer.toString(p2y));

        mapByteArr = FileUtils.readFileToByteArray(map.cut(p1x, 1000-p2y, p2x - p1x, p2y-p1y, saveLoc+"map_cut.png"));
        mapEncodedString = Base64.getEncoder().encodeToString(mapByteArr);

        return mapEncodedString;

    }

}
