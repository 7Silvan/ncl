package ua.group42.taskmanager.control.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class ReadAndPrintXMLFile {

    public static void main(String args[]) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();  // Build a document ...  
        Document doc = builder.build(".\\tasks.xml");  // ... from a file
        XMLOutputter output = new XMLOutputter();  // And output the document ... 
        output.output(doc, System.out);  // ... unchanged to System.out
        try {
            loadTasks();
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static Collection loadTasks() throws ParseException {
        //TODO: make it with JDOM
        Collection tasks = new LinkedList();
        try {
            SAXBuilder builder = new SAXBuilder();  // Build a document ...  
            Document doc = builder.build(".\\tasks.xml");  // ... from a file
            Element rootNode = doc.getRootElement();
            List childList = rootNode.getChildren("tasks");

            for (Iterator it = childList.iterator(); it.hasNext();) {
                Element task = (Element) it.next();

//                tasks.add(new Task(
//                        task.getChildText("name"),
//                        task.getChildText("description"),
//                        task.getChildText("contacts"),
//                        //TaskController.getInstance().getDateFormatter().parse(task.getChildText("date"))
//                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(task.getChildText("date"))));
                
                System.out.println(task.getChildText("name") + 
                        task.getChildText("description") +
                        task.getChildText("contacts") +
                        //TaskController.getInstance().getDateFormatter().parse(task.getChildText("date"))
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(task.getChildText("date")));
            }
        } catch (Exception e) {
            e.printStackTrace();
    }
        System.out.println(tasks.toArray().toString());
        return tasks;
    }
}
//      public static void main(String args[])
//      {
//         try {
//            Class c = Class.forName("ua.group42.taskmanager.control.data.TaskDAO");
//            Method m[] = c.getDeclaredMethods();
//            c.
//            for (int i = 0; i < m.length; i++)
//            System.out.println(m[i].toString());
//         }
//         catch (Throwable e) {
//            System.err.println(e);
//         }
//      }
//
//    public static void main(String argv[]) {
//        
//        HashMap hashMap = new HashMap();
//    Map map = Collections.synchronizedMap(hashMap);   
//        
//        try {
//
//            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
//            Document doc = docBuilder.parse(new File("book.xml"));
//
//            // normalize text representation
//            doc.getDocumentElement().normalize();
//            System.out.println("Root element of the doc is "
//                    + doc.getDocumentElement().getNodeName());
//
//
//            NodeList listOfPersons = doc.getElementsByTagName("person");
//            int totalPersons = listOfPersons.getLength();
//            System.out.println("Total no of people : " + totalPersons);
//
//            for (int s = 0; s < listOfPersons.getLength(); s++) {
//
//
//                Node firstPersonNode = listOfPersons.item(s);
//                if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {
//
//
//                    Element firstPersonElement = (Element) firstPersonNode;
//
//                    //-------
//                    NodeList firstNameList = firstPersonElement.getElementsByTagName("first");
//                    Element firstNameElement = (Element) firstNameList.item(0);
//
//                    NodeList textFNList = firstNameElement.getChildNodes();
//                    System.out.println("First Name : "
//                            + ((Node) textFNList.item(0)).getNodeValue().trim());
//
//                    //-------
//                    NodeList lastNameList = firstPersonElement.getElementsByTagName("last");
//                    Element lastNameElement = (Element) lastNameList.item(0);
//
//                    NodeList textLNList = lastNameElement.getChildNodes();
//                    System.out.println("Last Name : "
//                            + ((Node) textLNList.item(0)).getNodeValue().trim());
//
//                    //----
//                    NodeList ageList = firstPersonElement.getElementsByTagName("age");
//                    Element ageElement = (Element) ageList.item(0);
//
//                    NodeList textAgeList = ageElement.getChildNodes();
//                    System.out.println("Age : "
//                            + ((Node) textAgeList.item(0)).getNodeValue().trim());
//
//                    //------
//
//
//                }//end of if clause
//
//
//            }//end of for loop with s var
//
//
//        } catch (SAXParseException err) {
//            System.out.println("** Parsing error" + ", line "
//                    + err.getLineNumber() + ", uri " + err.getSystemId());
//            System.out.println(" " + err.getMessage());
//
//        } catch (SAXException e) {
//            Exception x = e.getException();
//            ((x == null) ? e : x).printStackTrace();
//
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//        //System.exit (0);
//
//    }//end of main