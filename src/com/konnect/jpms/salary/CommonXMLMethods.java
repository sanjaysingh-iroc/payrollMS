package com.konnect.jpms.salary;

import static java.lang.System.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/*
 * 
 * @author Konnect
 */
 
public class CommonXMLMethods {
	public Document createDocument() {
		Document document=null;
        try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return document;
    }
	
	public void printToFile(Document document,String strFilePath,String strFileName) {
        File file=new File(strFilePath);
        if(!file.isDirectory()){
        	out.println("Following Path is not exist:\n"+strFilePath);
            return;
        }
        
        try {
            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            XMLSerializer serializer = new XMLSerializer(new FileOutputStream(strFilePath+strFileName), format);
            serializer.serialize(document);
        } catch(IOException ie) {
            ie.printStackTrace();
        }
    }
	
	public Element addRootNode(Document document,String strRootNodeName) {
		Element rootNode=null;
		try {
			rootNode = document.createElement(strRootNodeName);
			document.appendChild(rootNode);
		} catch (DOMException e) {
			e.printStackTrace();
		}
        return rootNode;
    }
    
	public Element addNode(Document document, Element parentElement, String nodeName) {
    	Element childElement=null;
        try {
			childElement = document.createElement(nodeName);
			parentElement.appendChild(childElement);
		} catch (DOMException e) {
			e.printStackTrace();
		}
		
		return childElement;
    }
    
	public static void addText(Document document, Element parentElement, String strText) {
    	if(strText==null)
    		return;
    	
        try {
			Text text = document.createTextNode(strText);
			parentElement.appendChild(text);
			
        } catch (DOMException e) {
			e.printStackTrace();
		}
    }
}
