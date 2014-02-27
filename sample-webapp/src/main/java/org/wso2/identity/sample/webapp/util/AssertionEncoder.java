package org.wso2.identity.sample.webapp.util;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.util.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class AssertionEncoder {

    public  String encode(Assertion samlAssertion) {
        String assertionString = null;
        try {
            assertionString = URLEncoder.encode(Base64.encodeBytes(marshall(samlAssertion).getBytes()),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assertionString;
    }

    public String encodeWithDeflate(Assertion samlAssertion){
        String assertionString = null;
        try {
            assertionString = encode(marshall(samlAssertion).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assertionString;
    }


    public static String marshall(XMLObject xmlObject) throws Exception {

        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

        MarshallerFactory marshallerFactory =
                org.opensaml.xml.Configuration.getMarshallerFactory();
        Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
        Element element = marshaller.marshall(xmlObject);

        ByteArrayOutputStream byteArrayOutputStrm = new ByteArrayOutputStream();
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        LSSerializer writer = impl.createLSSerializer();
        LSOutput output = impl.createLSOutput();
        output.setByteStream(byteArrayOutputStrm);
        writer.write(element, output);
        return byteArrayOutputStrm.toString();
    }

    public static String encode(byte[] xmlString) throws Exception {
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream =
                new DeflaterOutputStream(byteArrayOutputStream,
                        deflater);
        deflaterOutputStream.write(xmlString);
        deflaterOutputStream.close();
        // Encoding the compressed message
        String encodedRequestMessage =
                Base64.encodeBytes(byteArrayOutputStream.toByteArray(),
                        Base64.DONT_BREAK_LINES);
        return encodedRequestMessage.trim();
    }

    public String compressString(String data) {
        Deflater deflater = new Deflater();
        byte[] target = new byte[100];
        try {
            deflater.setInput(data.getBytes("UTF-8"));
            deflater.finish();
            int deflateLength = deflater.deflate(target);
            return new String(target);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       return data;
    }



}