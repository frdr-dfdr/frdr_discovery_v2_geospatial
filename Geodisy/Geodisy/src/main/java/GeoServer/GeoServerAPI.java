/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeoServer;

import BaseFiles.FileWriter;
import BaseFiles.GeoLogger;
import BaseFiles.HTTPCaller;
import Crosswalking.GeoBlacklightJson.HTTPCombineCaller;
import Dataverse.*;

import _Strings.GeodisyStrings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

import java.nio.file.Files;
import java.util.concurrent.*;

import static _Strings.GeoBlacklightStrings.GEOSERVER_REST;
import static _Strings.GeodisyStrings.*;
import static _Strings.GeoserverStrings.*;

/**
 *
 * @author pdante
 */
public class GeoServerAPI extends DestinationAPI {
    SourceJavaObject sjo;
    HTTPCallerGeosever caller;
    ProcessBuilder processBuilder;

    public GeoServerAPI(SourceJavaObject sjo) {
        this.sjo = sjo;
        caller = new HTTPCallerGeosever();
        logger =  new GeoLogger(this.getClass());
        processBuilder= new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
    }
    //TODO FIGURE OUT GEOSERVER REST ENDPOINT WITH JOEL
    private boolean generateWorkspace(String workspaceName) {
        try {
            String generateWorkspace = "/usr/bin/curl -u admin:" + GEOSERVER_PASSWORD + "-XPOST -H \"Content-type: text/xml\" -d \"<workspace><name>" + workspaceName + "</name></workspace>\" " + GEOSERVER_REST + "workspaces";
            Process p;
            processBuilder.command("/usr/bin/bash", "-c", generateWorkspace);
            p = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                continue;
            p.waitFor(10, TimeUnit.SECONDS);
            p.destroy();
        } catch (InterruptedException|IOException e) {
            logger.error("Something went wrong trying to create the workspace " + workspaceName + " in geoserver");
            return false;
        }
        return true;
    }

    @Override
    public boolean addVector(String fileName,String geoserverLabel){
        System.out.println("Adding to POSTGIS");
        boolean success = addVectorToPostGIS(fileName,geoserverLabel);
        if(!success) return success;
        System.out.println("Adding to Geoserver");
        return timedAddVector(geoserverLabel,fileName);
        }

    private boolean timedAddVector(String geoserverLabel, String fileName) {
        System.out.println("Adding: "+ fileName);
        VectorCall vectorCall = new VectorCall(geoserverLabel,fileName);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(vectorCall);
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(5, TimeUnit.MINUTES)){
                logger.warn("Timed out trying to add file to geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
                System.out.println("Timed out trying to add file to geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
                return false;
            }
        }catch (InterruptedException e){
            logger.error("Something went wrong trying to add file to geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
            return false;
        }
        boolean success;
        try {
            success = future.get(2,TimeUnit.SECONDS);
        } catch (InterruptedException|ExecutionException|TimeoutException e) {
            return false;
        }
        if(success)
            return timedUpdateTitleInGeosercer(geoserverLabel,fileName);
        return false;
    }

    private boolean timedUpdateTitleInGeosercer(String geoserverLabel,String fileName) {
        VectorCallTitle v = new VectorCallTitle(geoserverLabel,fileName);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(v);
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(30, TimeUnit.SECONDS)){
                logger.warn("Timed out trying to update title in geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
                return false;
            }
        }catch (InterruptedException e){
            logger.error("Something went wrong trying to update title in geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
            return false;
        }
        boolean success;
        try {
            success = future.get(2,TimeUnit.SECONDS);
        } catch (InterruptedException|ExecutionException|TimeoutException e) {
            return false;
        }
        return success;
    }
    private class VectorCall implements Callable<Boolean>{
        String geoserverLabel;
        String fileName;
        public VectorCall(String geoserverLabel, String fileName) {
            this.geoserverLabel = geoserverLabel;
            this.fileName = fileName;
        }
        @Override
        public Boolean call() throws Exception {
            return addPostGISLayerToGeoserver(geoserverLabel, fileName);
        }
    }
    private class VectorCallTitle implements Callable<Boolean>{
        String geoserverLabel;
        String fileName;
        public VectorCallTitle(String geoserverLabel, String fileName) {
            this.geoserverLabel = geoserverLabel;
            this.fileName = fileName;
        }
        @Override
        public Boolean call() throws Exception {
            return updateTitleInGeoserver(geoserverLabel,fileName);
        }
    }

    private boolean addVectorToPostGIS(String fileName, String geoserverlabel) {
            PostGIS postGIS = new PostGIS();
            return postGIS.addFile2PostGIS((DataverseJavaObject) sjo, fileName,geoserverlabel);
        }

        //TODO fix this?
        public boolean addPostGISLayerToGeoserver(String geoserverlabel, String filename){
        String vectorDB = GEOSERVER_VECTOR_STORE;
        String title = filename.substring(0,filename.lastIndexOf('.'));

        try {
            //bring new layer over from POSTGIS
            String call = "curl -u " + GEOSERVER_USERNAME + ":" + GEOSERVER_PASSWORD + " -XPOST -H \"Content-type: text/xml\" -d \"<featureType><name>" + geoserverlabel.toLowerCase() + "</name><title>"+ title +"</title><nativeCRS>EPSG:4326</nativeCRS><srs>EPSG:4326</srs><enabled>true</enabled></featureType>\" " + GEOSERVER_REST + "workspaces/geodisy/datastores/" + vectorDB + "/featuretypes";
            processBuilder.command("/usr/bin/bash", "-c",call);
            Process p = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                continue;
            p.waitFor(30, TimeUnit.SECONDS);
            p.destroy();
        } catch (IOException | InterruptedException e) {
            logger.error("Something went wrong adding vector layer " + geoserverlabel + " from POSTGIS");
            return false;
        }
        return true;
    }

    private boolean updateTitleInGeoserver(String geoserverLabel, String fileName) {
        String vectorDB = GEOSERVER_VECTOR_STORE;
        String title = fileName.substring(0,fileName.lastIndexOf('.'));
        String call = "curl -u " + GEOSERVER_USERNAME + ":" + GEOSERVER_PASSWORD + " -H 'Accept: text/xml' -XGET "+ GEODISY_PATH_ROOT + "geoserver/rest/workspaces/geodisy/datastores/"+ vectorDB + "/featuretypes/" + geoserverLabel+".xml";
        call = GeodisyStrings.replaceSlashes(call);
        try {
            processBuilder.command("/usr/bin/bash", "-c",call);
            Process p = processBuilder.start();
            InputStream iS = p.getInputStream();
            String xml = "";
            for(int i = 0; i<iS.available();i++){
                xml+=iS.read();
            }
            xml.replace("<title>"+geoserverLabel+"</title>","<title>"+title+"</title>");
            call = "curl -u admin:geoserver -H 'Accept: application/xml' -H 'Content-type: application/xml' -XPUT " + GEOSERVER_REST + " workspaces/geodisy/datastores/"+ vectorDB+"/featuretypes/" + geoserverLabel+ ".xml -d '" + xml + "'";
            ProcessBuilder processBuilder2 = new ProcessBuilder();
            processBuilder2.command("/usr/bin/bash", "-c",call);
            processBuilder2.redirectErrorStream(true);
            Process p2 = processBuilder.start();
// wait for 10 seconds and then destroy the process
            Thread.sleep(10000);
            p2.destroy();
            p.destroy();
        } catch (IOException | InterruptedException e) {
            logger.error("Something went wrong updating " + geoserverLabel + " with the title: " + title);
            return false;
        }
        return true;
    }

    @Override
    public boolean addRaster(DataverseGeoRecordFile dgrf) {
        String fileName = dgrf.getTranslatedTitle();
        String geoserverLabel = dgrf.getGeoserverLabel();
        return timedAddRaster(fileName,geoserverLabel);
    }

    private boolean timedAddRaster(String fileName, String geoserverLabel) {
        System.out.println("Adding: "+ fileName);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        RasterCall r = new RasterCall(geoserverLabel,fileName);
        Future<Boolean> successBool = executorService.submit(r);
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(10, TimeUnit.MINUTES)){
                logger.warn("Timed out trying to add file to geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
                System.out.println("Timed out trying to add file to geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
                return false;
            }
        }catch (InterruptedException e){
            logger.error("Something went wrong trying to add file to geoserver: Filename = " + fileName + " doi = " + sjo.getPID());
            return false;
        }
        try {
            return successBool.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }
    private class RasterCall implements Callable<Boolean> {
        String geoserverLabel;
        String fileName;
        public RasterCall(String geoserverLabel, String fileName) {
            this.geoserverLabel=geoserverLabel;
            this.fileName=fileName;
        }

        @Override
        public Boolean call(){
            return addRaster(geoserverLabel, fileName);
        }
    }

    public boolean addRaster(String fileName, String geoserverLabel){
        if (fileName.contains("."))
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        fileName = fileName + ".tif";

        try { deleteOldCoverstore(processBuilder, geoserverLabel);
        }catch (InterruptedException | IOException f) {
            logger.error("Error trying to delete existing raster from geoserver: doi=" + sjo.getPID() + ", geoserver label=" + geoserverLabel + ", file name=" + fileName);
            return false;
        }
        try { normalizeRaster(processBuilder, fileName);
        }catch (InterruptedException | IOException f) {
            logger.error("Error trying to normalize raster from geoserver: doi=" + sjo.getPID() + ", geoserver label=" + geoserverLabel + ", file name=" + fileName);
            return false;
        }
        try { renameRasterToOrig(processBuilder, GeodisyStrings.removeHTTPSAndReplaceAuthority(sjo.getPID()).replace(".","/"),fileName);
        }catch (InterruptedException | IOException f) {
            logger.error("Error trying to rename raster back to correct name from geoserver: doi=" + sjo.getPID() + ", geoserver label=" + geoserverLabel + ", file name=" + fileName);
            return false;
        }
        /*try { addRasterOverviews(processBuilder, fileName);
        }catch (InterruptedException | IOException f) {
            logger.error("Error trying to update raster with overviews from geoserver: doi=" + sjo.getPID() + ", geoserver label=" + geoserverLabel + ", file name=" + fileName);
            return false;
        }*/

        try { createCoverstore(geoserverLabel, processBuilder, fileName);
        }catch (InterruptedException | IOException f) {
            logger.error("Error trying to create a coverstore for raster from geoserver: doi=" + sjo.getPID() + ", geoserver label=" + geoserverLabel + ", file name=" + fileName);
            return false;
        }

        try{ enableCoverageStore(processBuilder, geoserverLabel,fileName);
        }catch (InterruptedException | IOException f){
        logger.error("Error trying to enable coveragestore on geoserver: doi=" + sjo.getPID() + ", geoserver label=" + geoserverLabel + ", file name=" + fileName);
            return false;
        }

        try{ addRasterLayer(processBuilder,geoserverLabel,fileName);
        }catch (InterruptedException | IOException f){
            logger.error("Error trying to add raster to geoserver: doi=" + sjo.getPID() + ", geoserver label=" + geoserverLabel + ", file name=" + fileName);
            return false;
        }
        ExistingRasterRecords existingRasterRecords = ExistingRasterRecords.getExistingRasters();
        existingRasterRecords.addOrReplaceRecord(sjo.getPID(),fileName);
        System.out.println("Added raster with fileName: " + fileName + " and geoserverLabel: " + geoserverLabel);
        return true;
    }

    private void deleteOldCoverstore(ProcessBuilder processBuilder, String geoserverLable) throws InterruptedException, IOException{
        String deleteCoveragestore = "curl -u admin:" + GEOSERVER_PASSWORD + " -XDELETE " + stringed(GEOSERVER_REST + "workspaces/geodisy/coveragestores/" + geoserverLable.toLowerCase() + "?recurse=true");
        System.out.println("Delete old coverage: " + deleteCoveragestore);
        Process p;
        processBuilder.command("/usr/bin/bash", "-c", deleteCoveragestore);
        p = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            continue;
        p.waitFor(10, TimeUnit.SECONDS);
        p.destroy();

    }

    private void normalizeRaster(ProcessBuilder processBuilder, String fileName) throws InterruptedException, IOException {
        String warp = GDALWARP(DATA_DIR_LOC + GeodisyStrings.removeHTTPSAndReplaceAuthority(sjo.getPID()).replace(".","/") + "/", fileName);
        System.out.println("Normalize raster: " + warp);
        Process p;
        processBuilder.command("/usr/bin/bash", "-c", warp);
        p = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            continue;
        p.waitFor(30, TimeUnit.SECONDS);
        p.destroy();
    }

    private void renameRasterToOrig(ProcessBuilder processBuilder, String datasetID, String fileName) throws InterruptedException, IOException{
        String rename = "sudo mv -f " + DATA_DIR_LOC + datasetID + "/1" + fileName + " " + DATA_DIR_LOC + datasetID + "/" + fileName;
        System.out.println("Rename raster to orig: " + rename);
        Process p;
        processBuilder.command("/usr/bin/bash", "-c", rename);
        p = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            continue;
        p.waitFor(5, TimeUnit.SECONDS);
        p.destroy();
    }

    private void addRasterOverviews(ProcessBuilder processBuilder, String fileName) throws InterruptedException, IOException {
        String addo = GDALADDO(DATA_DIR_LOC + GeodisyStrings.removeHTTPSAndReplaceAuthority(sjo.getPID()).replace(".","/") + "/" + fileName);
        Process p;
        processBuilder.command("/usr/bin/bash", "-c", addo);
        p = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            continue;
        p.waitFor(10, TimeUnit.SECONDS);
        p.destroy();
    }

    private void createCoverstore(String geoserverLable, ProcessBuilder processBuilder, String fileName) throws InterruptedException, IOException{
        String createCoveragestore = "/usr/bin/curl -u admin:" + GEOSERVER_PASSWORD + " -XPOST -H " + stringed("Content-type:text/xml") +  " -d '<coverageStore><name>" + geoserverLable.toLowerCase()+ "</name><workspace>geodisy</workspace><enabled>true</enabled><type>GeoTIFF</type><url>file:" + GeodisyStrings.removeHTTPSAndReplaceAuthority(sjo.getPID()).replace(".","/") + "/" + fileName + "</url></coverageStore>' " + stringed(GEOSERVER_REST + "workspaces/geodisy/coveragestores?configure=all");
        System.out.println("Create coverage: " + createCoveragestore);
        Process p;
        processBuilder.command("/usr/bin/bash", "-c", createCoveragestore);
        p = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            continue;
        p.waitFor(5, TimeUnit.SECONDS);
        p.destroy();
    }

    private void enableCoverageStore(ProcessBuilder processBuilder, String geoserverLabel, String translatedTitle) throws InterruptedException, IOException{
    String title = translatedTitle;
    if(title.contains("."))
        title=title.substring(0,title.lastIndexOf("."));
    String enableCoverageStore = "/usr/bin/curl -u admin:" + GEOSERVER_PASSWORD + " -XPOST -H " + stringed("Content-type:application/xml") + " -d '<coverage><name>"+ geoserverLabel.toLowerCase() + "</name><nativeCRS>" + RASTER_CRS + "</nativeCRS><title>" + title + "</title><enabled>True</enabled></coverage>' " + stringed(GEOSERVER_REST + "workspaces/geodisy/coveragestores/"+ geoserverLabel.toLowerCase() + "/coverages");
    System.out.println("Create coverage: " + enableCoverageStore);
    Process p;
    processBuilder.command("/usr/bin/bash", "-c", enableCoverageStore);
    p = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            continue;
    p.waitFor(5, TimeUnit.SECONDS);
    p.destroy();
    }

    private void addRasterLayer(ProcessBuilder processBuilder, String geoserverLabel, String translatedTitle)throws InterruptedException, IOException{
        String fileLocation = DATA_DIR_LOC + GeodisyStrings.removeHTTPSAndReplaceAuthority((sjo.getPID())+"/").replace(".","/")+ GeodisyStrings.replaceSlashes("/") + translatedTitle;
        String addRaster = "/usr/bin/curl -u admin:" + GEOSERVER_PASSWORD + " -XPUT -H \"Content-type: text/plain\" -d 'file://" + fileLocation + "' " + stringed(GEOSERVER_REST + "workspaces/geodisy/coveragestores/"+ geoserverLabel.toLowerCase() + "/external.geotiff?configure=first&coverageName=" + geoserverLabel);
        System.out.println("Add raster layer: " + addRaster);
        Process p;
        processBuilder.command("/usr/bin/bash", "-c", addRaster);
        p = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            continue;
        p.waitFor(2, TimeUnit.MINUTES);
        p.destroy();
    }

    private boolean addPostGISStore() {

        JSONObject jo = new JSONObject("<dataStore>" +
                                                    "<name>geodisyDB</name>" +
                                                    "<connectionParameters>" +
                                                        "<host>localhost</host>" +
                                                        "<port>5432</port>" +
                                                        "<database>geodisy</database>" +
                                                        "<user>geodisy_user</user>" +
                                                        "<passwd>"+ POSTGIS_USER_PASSWORD + "</passwd>" +
                                                        "<dbtype>postgis</dbtype>" +
                                                    "</connectionParameters>" +
                                                "</dataStore>");
        String storePath = PATH_TO_XML_JSON_FILES+"geodisy.xml";
        FileWriter fileWriter = new FileWriter(storePath);
        fileWriter.write(jo.toString());
        HTTPCaller caller = new HTTPCombineCaller();
        caller.callHTTP("/usr/bin/curl -u admin:" + GEOSERVER_PASSWORD + "-XPOST -T "+ storePath +" -H \"Content-type: text/xml\" "+ GEOSERVER_REST + "workspaces/"+ WORKSPACE_NAME + "/datastores");
        return deleteXMLFile(storePath);
    }

    private boolean deleteXMLFile(String path) {
        File file = new File(path);
        try {
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            logger.error("Something went wrong trying to delete file at: " + path);
            return false;
        }
    }

    private String jsonArrayBracketChange(String json) {
        String answer = json.replace("[", "{");
        return answer.replace("]", "}");
    }

    private JSONArray addWorkspaceStore(String workspace, String store) {
        JSONArray array = new JSONArray();
        array.put(storeJSON(store));
        array.put(workspaceJSON(workspace));
        array.put(locationJSON());
        return array;
    }

    private JSONObject locationJSON() {
        JSONObject obj3 = new JSONObject();
        obj3.put(stringed("location"), LOCATION);
        JSONObject obj2 = new JSONObject();
        obj2.put(stringed("type"), stringed("directory"));
        JSONArray array = new JSONArray();
        array.put(obj2);
        array.put(obj3);
        JSONObject obj1 = new JSONObject();
        obj1.put(stringed("data"),array);
        return obj1;
    }

    private JSONObject workspaceJSON(String workspace) {
        JSONObject obj3 = new JSONObject();
        obj3.put(stringed("name"), workspace);
        JSONObject obj2 = new JSONObject();
        obj2.put(stringed("workspace"), obj3);
        JSONObject obj1 = new JSONObject();
        obj1.put(stringed("targetWorkspace"), obj2);
        return obj1;
    }

    private JSONObject storeJSON(String store) {
        JSONObject obj3 = new JSONObject();
        obj3.put(stringed("name"), store);
        JSONObject obj2 = new JSONObject();
        obj2.put(stringed("dataStore"), obj3);
        JSONObject obj1 = new JSONObject();
        obj1.put(stringed("targetStore"), obj2);
        return obj1;
    }
    //TODO generate new workspace

    private boolean deleteWorkspace(String workspaceName){
        String callURL = "curl -u "+ GEOSERVER_USERNAME + ":" + GEOSERVER_PASSWORD + " -X DELETE "+ GEOSERVER_REST + "workspaces/" + workspaceName + "?recurse=true -H \"accept: application/json\" -H \"content-type: application/json\"";
        String response = caller.deleteWorkSpace(callURL);
        return response.contains("HTTP/1.1 200 OK")||response.contains("HTTP/1.1 404 Workspace");
    }

    private String createFileUploadJSON(String workspace,String store, String file) {
        String jsonModified = "";
        if(generateWorkspace(workspace)) {
            JSONObject root = new JSONObject();
            JSONArray array = addWorkspaceStore(workspace,store,file);
            root.put(stringed("import"), array);
            String json = root.toString();
            jsonModified = jsonArrayBracketChange(json);
        }
        return jsonModified;
    }

    private JSONArray addWorkspaceStore(String workspace, String store, String doi) {
        JSONArray array = new JSONArray();
        array.put(workspaceJSON(workspace));
        array.put(storeJSON(store));
        array.put(fileJSON(doi));
        return array;
    }

    private JSONObject fileJSON(String file) {
        JSONObject obj3 = new JSONObject();
        obj3.put(stringed("file"), file);
        JSONObject obj2 = new JSONObject();
        obj2.put(stringed("type"), stringed("file"));
        JSONArray array = new JSONArray();
        array.put(obj2);
        array.put(obj3);
        JSONObject obj1 = new JSONObject();
        obj1.put(stringed("data"),array);
        return obj1;
    }
}
