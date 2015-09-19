package com.gmail.at.kevinburnseit.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.CharacterData;
import org.xml.sax.SAXException;

public class XmlHelper {
	public static Document readFile(String path) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(path));
			doc.normalizeDocument();
			return doc;
		} catch (ParserConfigurationException e) {
			throw new Exception(e);
		} catch (SAXException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		}
	}
	public static void saveToXml(Element attachTo, String name, String value) {
		Element e = attachTo.getOwnerDocument().createElement(name);
		e.setTextContent(value);
		attachTo.appendChild(e);
	}
	
	public static String readCData(Node e) {
		NodeList ch = e.getChildNodes();
		for (int i = 0; i < ch.getLength(); i++) {
			Node c = ch.item(i);
			if (c instanceof CharacterData) {
				return ((CharacterData)c).getData();
			}
		}
		return null;
	}
	
	public static void saveFile(Document doc, String path) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	 
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new Exception(e);
		} catch (TransformerException e) {
			throw new Exception(e);
		}
	}
	
	@Deprecated public static void writeAttributeInt(Element e, String name, int value) {
		e.setAttribute(name, String.valueOf(value));
	}
	
	@Deprecated public static void writeAttributeDouble(Element e, String name, double value) {
		e.setAttribute(name, String.valueOf(value));
	}
	public static Document newDocument(String xmlTag) throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element docElement = doc.createElement(xmlTag);
		doc.appendChild(docElement);
		
		return doc;
	}
}
