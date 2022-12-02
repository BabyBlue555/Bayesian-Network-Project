import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLParser {


    /*
    *get the document builder
    * get document
    * Normalize the xml structure
    * Get all the elements by the tag name
     */

    public static void main(String[] args)  {
        // get the document builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            // get document
            File xml_file = new File("network_structure.xml");
            xml_file = new File("C:\\Users\\User\\IdeaProjects\\AI_try\\src\\network_structure.xml");
            xml_file = new File("C:\\Users\\User\\IdeaProjects\\AI_try\\big_net.xml");
            Document document = builder.parse(xml_file);
            // normalize the xml structure
            document.getDocumentElement().normalize();
            // Get all the element by the tag name
            NodeList varList = document.getElementsByTagName("VARIABLE");
            System.out.println(varList.getLength());
            for (int i = 0; i < varList.getLength(); i++) {
                Node variable = varList.item(i); // returns the node in the i position in the nodelist
                if (variable.getNodeType() == Node.ELEMENT_NODE) {
                    //  Element varElement = (Element) variable;
                    NodeList names = ((Element) variable).getElementsByTagName("NAME");
                    NodeList outcomes = ((Element) variable).getElementsByTagName("OUTCOME");
                    // print variable names and outcomes
                    for (int t = 0; t < names.getLength(); t++) {
                        System.out.println("NAME:" + names.item(t).getTextContent()); // VARIABLE NAME
                        for (int j = 0; j < outcomes.getLength(); j++)
                            System.out.println("OUTCOME:" + outcomes.item(j).getTextContent()); // OUTCOME NAME
                        //   System.out.println("outcome name:" + outcomes.item(1).getTextContent());
                    }

                    // print info about cpt's
                    NodeList defList = document.getElementsByTagName("DEFINITION");


                    for (int m = 0; m <defList.getLength(); m++) {
                        Node definition = defList.item(m);
                     //   System.out.println(defList.getLength());
                        NodeList nameVars = ((Element) definition).getElementsByTagName("FOR");
                        NodeList parents = ((Element) definition).getElementsByTagName("GIVEN");
                        NodeList table = ((Element) definition).getElementsByTagName("TABLE");

                        // print variable names and outcomes
                        for (int k = 0; k < nameVars.getLength(); k++) {
                            System.out.println("FOR:" + nameVars.item(k).getTextContent()); // VARIABLE NAME
                        for (int l= 0; l < parents.getLength(); l++) {
                            System.out.println("GIVEN:" + parents.item(l).getTextContent()); // parents NAME
                        }
                            //   System.out.println("outcome name:" + outcomes.item(1).getTextContent());
                        for (int j= 0; j < table.getLength(); j++) {
                            System.out.println("TABLE:" + table.item(j).getTextContent()); // table values
                        }
                        }

                        NodeList varDetails = variable.getChildNodes();
                        // get the details of each variable
//                    for(int j=0; j< varDetails.getLength(); j++){
//                        Node detail = varDetails.item(j);
//                        if(detail.getNodeType() == Node.ELEMENT_NODE){
//                            Element detailElement = (Element) detail;
//                            System.out.println("" + detailElement.getTagName()+ ": "+ detailElement.getAttribute("OUTCOME"));
//                        }
//                    }
                    }
                }
            }
        }
        catch (ParserConfigurationException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


}
