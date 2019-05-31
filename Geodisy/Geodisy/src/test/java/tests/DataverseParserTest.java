package tests;

import BaseFiles.MyTimerTask;
import Crosswalking.JSONParsing.DataverseParser;
import Dataverse.DataverseJSONFieldClasses.Fields.CitationCompoundFields.Author;
import Dataverse.DataverseJSONFieldClasses.Fields.CitationCompoundFields.CitationFields;
import Dataverse.DataverseJSONFieldClasses.Fields.CitationCompoundFields.DatasetContact;
import Dataverse.DataverseJSONFieldClasses.Fields.CitationCompoundFields.Description;
import Dataverse.DataverseJSONFieldClasses.Fields.CitationSimpleJSONFields.SimpleCitationFields;
import Dataverse.DataverseJavaObject;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;


import java.io.IOException;

import static Dataverse.DVFieldNameStrings.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataverseParserTest {

    @Test
    public void testDataverseParser() {
        String jsonData = "{\"status\":\"OK\",\"data\":{\"id\":900,\"identifier\":\"FK2/OVQBMK\",\"persistentUrl\":\"https://doi.org/10.5072/FK2/OVQBMK\",\"protocol\":\"doi\",\"authority\":\"10.5072\",\"publisher\":\"Demo Dataverse\",\"publicationDate\":\"2015-07-13\",\"storageIdentifier\":\"file://10.5072/FK2/OVQBMK\",\"latestVersion\":{\"id\":302,\"storageIdentifier\":\"file://10.5072/FK2/OVQBMK\",\"versionNumber\":1,\"versionMinorNumber\":0,\"versionState\":\"RELEASED\",\"productionDate\":\"Production Date\",\"lastUpdateTime\":\"2015-07-13T11:02:21Z\",\"releaseTime\":\"2015-07-13T11:02:21Z\",\"createTime\":\"2015-07-13T10:59:33Z\",\"license\":\"CC0\",\"termsOfUse\":\"CC0 Waiver\",\"metadataBlocks\":{\"citation\":{\"displayName\":\"Citation Metadata\",\"fields\":[{\"typeName\":\"title\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Shapefile Dataset\"},{\"typeName\":\"author\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Quigley, Elizabeth\"},\"authorAffiliation\":{\"typeName\":\"authorAffiliation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Harvard University\"}}]},{\"typeName\":\"datasetContact\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"datasetContactName\":{\"typeName\":\"datasetContactName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Quigley, Elizabeth\"},\"datasetContactAffiliation\":{\"typeName\":\"datasetContactAffiliation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Harvard University\"},\"datasetContactEmail\":{\"typeName\":\"datasetContactEmail\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"equigley@iq.harvard.edu\"}}]},{\"typeName\":\"dsDescription\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"dsDescriptionValue\":{\"typeName\":\"dsDescriptionValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Dataset for shapefile\"}}]},{\"typeName\":\"subject\",\"multiple\":true,\"typeClass\":\"controlledVocabulary\",\"value\":[\"Earth and Environmental Sciences\"]},{\"typeName\":\"depositor\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Quigley, Elizabeth\"},{\"typeName\":\"dateOfDeposit\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2015-07-13\"}]}},\"files\":[{\"label\":\"shapefiletest.zip\",\"restricted\":false,\"version\":1,\"datasetVersionId\":302,\"dataFile\":{\"id\":901,\"persistentId\":\"\",\"pidURL\":\"\",\"filename\":\"shapefiletest.zip\",\"contentType\":\"application/zipped-shapefile\",\"filesize\":141004,\"storageIdentifier\":\"14e87ee7213-e642881106e3\",\"rootDataFileId\":-1,\"md5\":\"d1086c3d83d0f3e1d5af91a692493317\",\"checksum\":{\"type\":\"MD5\",\"value\":\"d1086c3d83d0f3e1d5af91a692493317\"}}}]}}}";
        JSONObject jo = new JSONObject(jsonData);
        DataverseJavaObject dataverseJavaObject = new DataverseJavaObject("fakeServerName");
        dataverseJavaObject = setValues(dataverseJavaObject);

        //create ObjectMapper instance
        DataverseParser dataverseParser = new DataverseParser();
        DataverseJavaObject djo = dataverseParser.parse(jo, "another fake server name");
        MyTimerTask my = new MyTimerTask();
        try {
            my.trimErrors();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(djo.getBoundingBox().getLatSouth(), dataverseJavaObject.getBoundingBox().getLatSouth());
        jsonData = "{\"id\":93,\"identifier\":\"FK2/WWRAUI\",\"persistentUrl\":\"https://doi.org/10.5072/FK2/WWRAUI\",\"protocol\":\"doi\",\"authority\":\"10.5072\",\"publisher\":\"Geodisy Sandbox\",\"publicationDate\":\"2019-04-16\",\"storageIdentifier\":\"file://10.5072/FK2/KHTOAP\",\"datasetVersion\":{\"id\":19,\"storageIdentifier\":\"file://10.5072/FK2/KHTOAP\",\"versionNumber\":1,\"versionMinorNumber\":0,\"versionState\":\"RELEASED\",\"distributionDate\":\"2018-11-27\",\"productionDate\":\"Production Date\",\"UNF\":\"UNF:6:HAoR/cYJyZLjKKGC8ZIUVQ==\",\"lastUpdateTime\":\"2019-04-16T23:18:20Z\",\"releaseTime\":\"2019-04-16T23:18:20Z\",\"createTime\":\"2019-03-18T21:14:09Z\",\"license\":\"NONE\",\"termsOfUse\":\"<a rel=\\\"license\\\" href=\\\"http://creativecommons.org/licenses/by/4.0/\\\"><img alt=\\\"Creative Commons Licence\\\" style=\\\"border-width:0\\\" src=\\\"https://i.creativecommons.org/l/by/4.0/88x31.png\\\" /></a><br />This work is licensed under a <a rel=\\\"license\\\" href=\\\"http://creativecommons.org/licenses/by/4.0/\\\">Creative Commons Attribution 4.0 International License</a>.\",\"metadataBlocks\":{\"citation\":{\"displayName\":\"Citation Metadata\",\"fields\":[{\"typeName\":\"title\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Replication data for: Rocky intertidal monitoring: a protocol for assessing human impact in Barkley Sound\"},{\"typeName\":\"author\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Bowes, Natalie\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Carpenter, Julie\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Haggarty, Dana\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Harper, Sarah\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Herman, Tom\"},\"authorAffiliation\":{\"typeName\":\"authorAffiliation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Acadia University\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Klady, Rebecca\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Lombardo, Vincie\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"MacInnes, Chrissy\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Markel, Russ\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Osterman, Stacey\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Tyne, Sarah\"}},{\"authorName\":{\"typeName\":\"authorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Woodford, Elizabeth\"}}]},{\"typeName\":\"datasetContact\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"datasetContactName\":{\"typeName\":\"datasetContactName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Bamfield Marine Sciences Centre\"},\"datasetContactAffiliation\":{\"typeName\":\"datasetContactAffiliation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Western Canadian Universities Marine Sciences Society\"},\"datasetContactEmail\":{\"typeName\":\"datasetContactEmail\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"education@bamfieldmsc.com\"}}]},{\"typeName\":\"dsDescription\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"dsDescriptionValue\":{\"typeName\":\"dsDescriptionValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"In 1997 the Bamfield Marine Sciences Centre established a long-term monitoring project to collect baseline data on the distribution and abundance of macroorganisms at two intertidal sites on Wizard Islet in Barkley Sound. The sites were resampled in 2001, and data is presented here for algal and invertebrate cover (%) and invertebrate density.\\r\\n<p/>\\r\\nWizard Islet (48°51’29.5”N, 125°09’31”W) is located within the Deer Group Islands in Barkley Sound and has an area of 1.73 hectares at low tide. The sheltered site (with less wave exposure) is located on a 50m stretch of fixed rocky shore on the northeast side and is characterized by Fucus and Phyllospadix (seagrass). The exposed site is located on a 50m stretch of fixed rocky shore on the southwest side and is characterized by Egregia (feather-boa kelp), goose-necked barnacles and Alaria (brown alga).\\r\\n<p/>\\r\\nFifteen vertical transects at each site were sampled by randomly selecting 15 of the 50 numbered tags (actual tag numbers unknown). Sampling was done at tidal heights of 0.2 (exposed site only), 0.5, 1, 1.5, 2, 2.5, 3 and 3.5m. Quadrats of 25x25cm were used to determine species percent cover and abundance for floral and faunal macroorganisms. Quadrats of 50x50cm were used for larger organisms such as Astroidea and Pisaster. To maintain consistency, all quadrats were positioned to the left and above each point on the transect (when facing away from the water).\\r\\n<p/>\\r\\nSpreadsheets contain columns labelled EH1Q1 to EH8Q15 and SH2Q1 to SH8Q15. It is assumed that E and S refer to Exposed and Sheltered, H1-8 refers to tidal height and Q1-15 refers to transect. Sheltered columns start at SH2 because 0.2m was not sampled.\\r\\n<p/>\\r\\nData was collected by students in the Coastal Biodiversity and Conservation course taught by Dr. Tom Berman with Teaching Assistant Dana Haggarty and support from Russ Markel July 23-Aug 31, 2001.\"},\"dsDescriptionDate\":{\"typeName\":\"dsDescriptionDate\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2018-11-27\"}}]},{\"typeName\":\"subject\",\"multiple\":true,\"typeClass\":\"controlledVocabulary\",\"value\":[\"Earth and Environmental Sciences\"]},{\"typeName\":\"keyword\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Invertebrates\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Seaweeds\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Long term monitoring\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Barkley Sound\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Wizard Islet\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Rocky intertidal habitat\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Wave exposure\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Tidal height\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Species diversity\"}},{\"keywordValue\":{\"typeName\":\"keywordValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Multidimensional scaling\"}}]},{\"typeName\":\"topicClassification\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"topicClassValue\":{\"typeName\":\"topicClassValue\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Open\"},\"topicClassVocab\":{\"typeName\":\"topicClassVocab\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Open Access Tag\"}}]},{\"typeName\":\"publication\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"publicationCitation\":{\"typeName\":\"publicationCitation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Bowes, Natalie, Julie Carpenter, Sarah Harper, Rebecca Klady, Vincie Lombardo, Chrissy MacInnes, Stacey Osterman, Sarah Tyne and Elizabeth Woodford. 2001. Rocky intertidal monitoring: a protocol for assessing human impact in Barkley Sound. [Chapter 2: Rocky Intertidal Monitoring]. Coastal Biodiversity and Conservation Student Report, no. 231. Bamfield, BC: Bamfield Marine Sciences Centre.\"}}]},{\"typeName\":\"notesText\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"http://hdl.handle.net/11272/10678\"},{\"typeName\":\"language\",\"multiple\":true,\"typeClass\":\"controlledVocabulary\",\"value\":[\"English\"]},{\"typeName\":\"producer\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"producerName\":{\"typeName\":\"producerName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Bamfield Marine Sciences Centre\"},\"producerAffiliation\":{\"typeName\":\"producerAffiliation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Western Canadian Universities Marine Sciences Society\"},\"producerAbbreviation\":{\"typeName\":\"producerAbbreviation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"BMSC\"},\"producerURL\":{\"typeName\":\"producerURL\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"http://www.bamfieldmsc.com/\"}}]},{\"typeName\":\"productionDate\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2001\"},{\"typeName\":\"productionPlace\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Bamfield, British Columbia, Canada\"},{\"typeName\":\"distributor\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"distributorName\":{\"typeName\":\"distributorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"University of British Columbia Library\"},\"distributorURL\":{\"typeName\":\"distributorURL\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"https://www.library.ubc.ca/\"}}]},{\"typeName\":\"distributionDate\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2018-11-27\"},{\"typeName\":\"depositor\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Bamfield Marine Sciences Centre, Sally Taylor\"},{\"typeName\":\"dateOfDeposit\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2018-11-27\"},{\"typeName\":\"dateOfCollection\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"dateOfCollectionStart\":{\"typeName\":\"dateOfCollectionStart\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2001-07-23\"},\"dateOfCollectionEnd\":{\"typeName\":\"dateOfCollectionEnd\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2001-08-31\"}}]},{\"typeName\":\"kindOfData\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"biodiversity data\"]},{\"typeName\":\"series\",\"multiple\":false,\"typeClass\":\"compound\",\"value\":{\"seriesName\":{\"typeName\":\"seriesName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Wizard Islet long-term monitoring\"}}},{\"typeName\":\"relatedMaterial\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"2001 data incorporated into 2009 study: Harrington, Angela, Sabrina Kratchner, Leah Meth, and Josh Silberg. 2009. Long term monitoring of the rocky intertidal on Wizard Island in Barkley Sound. Coastal Biodiversity and Conservation Student Report, no. 330. Bamfield, BC: Bamfield Marine Sciences Centre.\"]},{\"typeName\":\"relatedDatasets\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"<a href=\\\"https://doi.org/10.5683/SP2/PGCBPL\\\">1997 Wizard data</a>;\\r\\n<a href=\\\"https://doi.org/10.5683/SP2/BADMTR\\\">2002 Wizard data</a>;\\r\\n<a href=\\\"https://doi.org/10.5683/SP2/KQBIRH\\\">2003 Wizard data</a>;\\r\\n<a href=\\\"https://doi.org/10.5683/SP2/DIHLL0\\\">2007 Wizard data</a>;\\r\\n<a href=\\\"https://doi.org/10.5683/SP2/VBPBFN\\\">2009 Wizard data</a>;\\r\\n<a href=\\\"https://doi.org/10.5683/SP2/C8G480\\\">2017 Wizard data</a>\"]}]},\"geospatial\":{\"displayName\":\"Geospatial Metadata\",\"fields\":[{\"typeName\":\"geographicCoverage\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"country\":{\"typeName\":\"country\",\"multiple\":false,\"typeClass\":\"controlledVocabulary\",\"value\":\"Canada\"},\"state\":{\"typeName\":\"state\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"British Columbia\"},\"otherGeographicCoverage\":{\"typeName\":\"otherGeographicCoverage\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Wizard Islet\"}}]},{\"typeName\":\"geographicBoundingBox\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"westLongitude\":{\"typeName\":\"westLongitude\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"-125.160843\"},\"eastLongitude\":{\"typeName\":\"eastLongitude\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"-125.158203\"},\"northLongitude\":{\"typeName\":\"northLongitude\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"48.8592\"},\"southLongitude\":{\"typeName\":\"southLongitude\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"48.857391\"}}]}]}},\"files\":[{\"description\":\"\",\"label\":\"BMSC_Wizard_Exposed_AlgalInvertCover_2001.tab\",\"restricted\":false,\"version\":2,\"datasetVersionId\":19,\"dataFile\":{\"id\":843,\"persistentId\":\"doi:10.5072/FK2/WWRAUI/UKQ2JS\",\"pidURL\":\"https://doi.org/10.5072/FK2/WWRAUI/UKQ2JS\",\"filename\":\"BMSC_Wizard_Exposed_AlgalInvertCover_2001.tab\",\"contentType\":\"text/tab-separated-values\",\"filesize\":17293,\"description\":\"\",\"storageIdentifier\":\"16a2871872e-a3d938d38ccf\",\"originalFileFormat\":\"text/csv\",\"originalFormatLabel\":\"Comma Separated Values\",\"originalFileSize\":17935,\"UNF\":\"UNF:6:qCfpI1WMHk4IajO/PvOdCQ==\",\"rootDataFileId\":-1,\"md5\":\"bad2c8a9c2755fe3c297fa0057851cc5\",\"checksum\":{\"type\":\"MD5\",\"value\":\"bad2c8a9c2755fe3c297fa0057851cc5\"}}},{\"description\":\"\",\"label\":\"BMSC_Wizard_Exposed_InvertebrateDensity_2001.tab\",\"restricted\":false,\"version\":2,\"datasetVersionId\":19,\"dataFile\":{\"id\":844,\"persistentId\":\"doi:10.5072/FK2/WWRAUI/OZDTU0\",\"pidURL\":\"https://doi.org/10.5072/FK2/WWRAUI/OZDTU0\",\"filename\":\"BMSC_Wizard_Exposed_InvertebrateDensity_2001.tab\",\"contentType\":\"text/tab-separated-values\",\"filesize\":17248,\"description\":\"\",\"storageIdentifier\":\"16a287187bc-d5ab8b295a84\",\"originalFileFormat\":\"text/csv\",\"originalFormatLabel\":\"Comma Separated Values\",\"originalFileSize\":17884,\"UNF\":\"UNF:6:PanYuHGM1X2SA8GwFYTJsQ==\",\"rootDataFileId\":-1,\"md5\":\"f99898d36dbf46fb42cd93aa9251c98b\",\"checksum\":{\"type\":\"MD5\",\"value\":\"f99898d36dbf46fb42cd93aa9251c98b\"}}},{\"description\":\"\",\"label\":\"BMSC_Wizard_SampleDataSheets_2001.pdf\",\"restricted\":false,\"version\":1,\"datasetVersionId\":19,\"dataFile\":{\"id\":845,\"persistentId\":\"doi:10.5072/FK2/WWRAUI/5GGP2S\",\"pidURL\":\"https://doi.org/10.5072/FK2/WWRAUI/5GGP2S\",\"filename\":\"BMSC_Wizard_SampleDataSheets_2001.pdf\",\"contentType\":\"application/pdf\",\"filesize\":719450,\"description\":\"\",\"storageIdentifier\":\"16a287188ec-fb71806cc2da\",\"rootDataFileId\":-1,\"md5\":\"c68773fe30768e9df309ce4405909243\",\"checksum\":{\"type\":\"MD5\",\"value\":\"c68773fe30768e9df309ce4405909243\"}}},{\"description\":\"\",\"label\":\"BMSC_Wizard_Sheltered_AlgalInvertCover_2001.tab\",\"restricted\":false,\"version\":2,\"datasetVersionId\":19,\"dataFile\":{\"id\":846,\"persistentId\":\"doi:10.5072/FK2/WWRAUI/KOZWUV\",\"pidURL\":\"https://doi.org/10.5072/FK2/WWRAUI/KOZWUV\",\"filename\":\"BMSC_Wizard_Sheltered_AlgalInvertCover_2001.tab\",\"contentType\":\"text/tab-separated-values\",\"filesize\":15269,\"description\":\"\",\"storageIdentifier\":\"16a28718c2d-aa128ff0fa5e\",\"originalFileFormat\":\"text/csv\",\"originalFormatLabel\":\"Comma Separated Values\",\"originalFileSize\":15811,\"UNF\":\"UNF:6:/r0J3TFhw+DtBXlY8fxyMA==\",\"rootDataFileId\":-1,\"md5\":\"9b9c4888d21fc321fdb30a9054e78f04\",\"checksum\":{\"type\":\"MD5\",\"value\":\"9b9c4888d21fc321fdb30a9054e78f04\"}}},{\"description\":\"\",\"label\":\"BMSC_Wizard_Sheltered_InvertebrateDensity_2001.tab\",\"restricted\":false,\"version\":2,\"datasetVersionId\":19,\"dataFile\":{\"id\":847,\"persistentId\":\"doi:10.5072/FK2/WWRAUI/EGVOUG\",\"pidURL\":\"https://doi.org/10.5072/FK2/WWRAUI/EGVOUG\",\"filename\":\"BMSC_Wizard_Sheltered_InvertebrateDensity_2001.tab\",\"contentType\":\"text/tab-separated-values\",\"filesize\":12949,\"description\":\"\",\"storageIdentifier\":\"16a28718cb5-ef926a4b51a2\",\"originalFileFormat\":\"text/csv\",\"originalFormatLabel\":\"Comma Separated Values\",\"originalFileSize\":13507,\"UNF\":\"UNF:6:LRWFCAHOZTlpEiO3vKe+yw==\",\"rootDataFileId\":-1,\"md5\":\"e098867effe4cff8cb498f8e8182803e\",\"checksum\":{\"type\":\"MD5\",\"value\":\"e098867effe4cff8cb498f8e8182803e\"}}}],\"citation\":\"Bowes, Natalie; Carpenter, Julie; Haggarty, Dana; Harper, Sarah; Herman, Tom; Klady, Rebecca; Lombardo, Vincie; MacInnes, Chrissy; Markel, Russ; Osterman, Stacey; Tyne, Sarah; Woodford, Elizabeth, 2019, \\\"Replication data for: Rocky intertidal monitoring: a protocol for assessing human impact in Barkley Sound\\\", https://doi.org/10.5072/FK2/WWRAUI, Geodisy Sandbox, V1, UNF:6:HAoR/cYJyZLjKKGC8ZIUVQ== [fileUNF]\"}}";
        jo = new JSONObject(jsonData);
        dataverseJavaObject = new DataverseJavaObject("fakeServerName");
        dataverseJavaObject = dataverseParser.parse(jo,"another fake server name");
        assertEquals(dataverseJavaObject.getBoundingBox().getLongWest(),-125.160843);
        assertEquals(dataverseJavaObject.getBoundingBox().getLatNorth(),48.8592);
        assertEquals(dataverseJavaObject.getDOI(),"10.5072/FK2/WWRAUI");
        assertEquals(dataverseJavaObject.getSimpleFields().getField(TITLE),"Replication data for: Rocky intertidal monitoring: a protocol for assessing human impact in Barkley Sound");
    }

    private DataverseJavaObject setValues(DataverseJavaObject dataverseJavaObject) {
        SimpleCitationFields sf = new SimpleCitationFields();
        sf.setField(ALT_URL,"https://doi.org/10.5072/FK2/OVQBMK");
        sf.setField(PUBLISHER,"Demo Dataverse");
        sf.setField(PUB_DATE,"2015-07-13");
        sf.setField(PROD_DATE,"Production Date");
        sf.setField(DIST_DATE,"2015-07-13T11:02:21Z");
        sf.setField(DEPOS_DATE,"2015-07-13T10:59:33Z");
        sf.setField(LICENSE,"CC0");
        sf.setField(TITLE,"Shapefile Dataset");
        sf.setField(DEPOSITOR,"Quigley, Elizabeth");
        sf.setField(DEPOS_DATE,"2015-07-13");
        dataverseJavaObject.setSimpleFields(sf);
        CitationFields citationFields = new CitationFields();
        Author a = new Author();
        a.setAuthorName("Quigley, Elizabeth");
        a.setAuthorAffiliation("Harvard University");
        citationFields.addAuthor(a);
        DatasetContact dc = new DatasetContact();
        dc.setDatasetContactName("Quigley, Elizabeth");
        dc.setDatasetContactAffiliation("Harvard University");
        dc.setDatasetContactEmail("equigley@iq.harvard.edu");
        citationFields.addDatasetContact(dc);
        Description d = new Description();
        d.setDsDescriptionValue("Dataset for shapefile");
        citationFields.addDsDescription(d);
        citationFields.addSubject("Earth and Environmental Sciences");
        dataverseJavaObject.setCitationFields(citationFields);
        Logger logger = LogManager.getLogger(DataverseParserTest.class);
        logger.error("Testing an error");
        logger.info("Testing an info");

        return dataverseJavaObject;
    }

}
