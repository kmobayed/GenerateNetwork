package generatenetwork;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GenerateRDF {
    String RDFHeader="<rdf:RDF\n"+
                         "\t xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"+
                         "\t xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"+
                         "\t xmlns:scho=\"http://localhost/scho.xml#\" >\n\n";
    String RDFFooter="</rdf:RDF>";

    public void readXGMML(String filename) throws IOException
    {
        String projectID="Project1";
        String webFolder="/Library/WebServer/Documents/";
        XGMMLParser parser =new XGMMLParser(filename);
        ArrayList<String> nodes= parser.parseNodes();
        for(Iterator<String> iter1 =nodes.iterator();iter1.hasNext();)
        {
            String baseURI=iter1.next();
            (new File(webFolder+"S"+baseURI)).mkdir();
            FileWriter outFile = new FileWriter(webFolder+"S"+baseURI+"/scho.xml");
            PrintWriter out = new PrintWriter(outFile);
            out.println(RDFHeader);
            ArrayList<String> edges= parser.parseNodeEdges(baseURI);
            out.println(siteRDF(baseURI,baseURI));
            out.println(projectRDF(projectID, baseURI));
            
            for (Iterator<String> iter2=edges.iterator();iter2.hasNext();)
            {
                String siteID=iter2.next();
                out.println(siteRDF(siteID,baseURI));
                out.println(addSeeAlso(siteID, projectID, baseURI));
                //create pushfeed
                UUID id1 = UUID.randomUUID();
                String pushID="F"+id1.toString();
                out.println(pushFeedRDF(pushID,siteID,baseURI));
                //create pullfeed
                UUID id2 = UUID.randomUUID();
                String pullID="F"+id2.toString();
                out.println(pullFeedRDF(pullID,pushID,siteID,baseURI));
            }
            out.println(RDFFooter);
            out.close();
        }
        
    }

    String siteRDF(String siteID, String baseURI)
    {
        return "\t<rdf:Description rdf:about=\"http://localhost/S"+baseURI+"/scho.xml#S"+siteID+"\">\n"+
               "\t\t<rdf:type rdf:resource=\"http://localhost/scho.xml#Site\"/>\n"+
               "\t</rdf:Description>\n\n";
    }

    String projectRDF(String projectID, String baseURI)
    {
        return "\t<rdf:Description rdf:about=\"http://localhost/S"+baseURI+"/scho.xml#"+projectID+"\">\n"+
               "\t\t <rdf:type rdf:resource=\"http://localhost/scho.xml#Project\"/>\n"+
               "\t</rdf:Description>\n";
    }

    String pushFeedRDF(String pushID, String siteID, String baseURI)
    {
        return "\t<rdf:Description rdf:about=\"http://localhost/S"+baseURI+"/scho.xml#"+pushID+"\">\n"+
               "\t\t<rdf:type rdf:resource=\"http://localhost/scho.xml#PushFeed\"/>\n"+
               "\t\t<scho:onSite rdf:resource=\"http://localhost/S"+baseURI+"/scho.xml#S"+siteID+"\"/>\n"+
               "\t</rdf:Description>\n\n";
    }

    String pullFeedRDF(String pullID, String pushID, String siteID, String baseURI)
    {
        return "\t<rdf:Description rdf:about=\"http://localhost/S"+baseURI+"/scho.xml#"+pullID+"\">\n"+
               "\t\t<rdf:type rdf:resource=\"http://localhost/scho.xml#PullFeed\"/>\n"+
               "\t\t<scho:relatedPush rdf:resource=\"http://localhost/S"+baseURI+"/scho.xml#"+pushID+"\"/>\n"+
               "\t</rdf:Description>\n\n"+
               "\t<rdf:Description rdf:about=\"http://localhost/S"+baseURI+"/scho.xml#S"+baseURI+"\">\n"+
               "\t\t<scho:hasPull rdf:resource=\"http://localhost/S"+baseURI+"/scho.xml#"+pullID+"\"/>\n"+
               "\t</rdf:Description>\n\n";
    }

    String addSeeAlso(String siteID, String projectID, String baseURI)
    {
        return "\t<rdf:Description rdf:about=\"http://localhost/S"+baseURI+"/scho.xml#"+projectID+"\">\n"+
               "\t\t<rdfs:seeAlso rdf:resource=\"http://localhost/S"+siteID+"/scho.xml\"/>\n"+
               "\t</rdf:Description>\n";
    }
}
