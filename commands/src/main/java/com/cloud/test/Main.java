package com.cloud.test;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by albo1013 on 12.11.2015.
 */
public class Main {
    public static void main (String s[]) throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        saxParser.parse(Main.class.getResourceAsStream("/commands.xsd"),new Handler());
    }

    static class Handler extends DefaultHandler{
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            System.out.println("Start document");
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            System.out.println("End document");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            System.out.println("Start element "+qName);
            for (int i = 0 ; i < attributes.getLength();i++){
                System.out.println(String.format("name=%s value=%s",attributes.getQName(i),attributes.getValue(i)));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            System.out.println("End element "+qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
        }
    }
}
