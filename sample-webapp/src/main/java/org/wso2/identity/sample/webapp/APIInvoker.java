/*
 * Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.identity.sample.webapp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.wso2.identity.sample.webapp.util.PlatformUtils;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;


public class APIInvoker {
    private String apiEndPoint;
    private String oauthToken;
    private String encodedBasicHeader;

    public APIInvoker(String apiEndPoint, String oauthToken) {
        this.apiEndPoint = apiEndPoint;
        this.oauthToken = oauthToken;
    }

/*    public String getQuote(String company) {
        try {
            //String response =  sendPost(company);
            String response = callRESTep(null);
            if(response.contains("Authentication Failure")){
                return "Authentication Failure";
            }
            //return getCompany(response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public String getResponse(String ep) {
        try {
            String response = callRESTep(ep);
            if (response.contains("Authentication Failure")) {
                return "Authentication Failure";
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCompany(String response) {
        String tempStr = response.substring(response.indexOf("name>"));
        String companyName = tempStr.substring(tempStr.indexOf(">") + 1, tempStr.indexOf("<"));
        tempStr = response.substring(response.indexOf("lastTradeTimestamp>"));
        String lastTradeTime = tempStr.substring(tempStr.indexOf(">") + 1, tempStr.indexOf("<"));
        tempStr = response.substring(response.indexOf("volume>"));
        String volume = tempStr.substring(tempStr.indexOf(">") + 1, tempStr.indexOf("<"));
        return companyName + "," + lastTradeTime + "," + volume;
    }

//    public static void main(String[] args) {
//        APIInvoker apiInvoker = new APIInvoker("","");
//        String ss =apiInvoker.getCompany("<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns:getSimpleQuoteResponse xmlns:ns=\"http://services.samples\"><ns:return xmlns:ax21=\"http://services.samples/xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ax21:GetQuoteResponse\"><ax21:change>3.7912610227823342</ax21:change><ax21:earnings>-8.56436836184496</ax21:earnings><ax21:high>-186.64456456569036</ax21:high><ax21:last>188.6646865408684</ax21:last><ax21:lastTradeTimestamp>Tue Jan 14 14:18:56 MST 2014</ax21:lastTradeTimestamp><ax21:low>-186.53421451576136</ax21:low><ax21:marketCap>3.6144480765752986E7</ax21:marketCap><ax21:name>IBM Company</ax21:name><ax21:open>195.751209927887</ax21:open><ax21:peRatio>25.20011284468643</ax21:peRatio><ax21:percentageChange>1.8204468412987724</ax21:percentageChange><ax21:prevClose>208.25991381750603</ax21:prevClose><ax21:symbol>IBM</ax21:symbol><ax21:volume>8052</ax21:volume></ns:return></ns:getSimpleQuoteResponse></soapenv:Body></soapenv:Envelope>");
//        System.out.println(ss);
//    }

    // HTTP POST request
/*    private String sendPost(String company) throws Exception{

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(apiEndPoint);

        // add header
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Authorization", "Bearer " +oauthToken);
        post.setHeader("SOAPAction", "urn:getSimpleQuote");

        String content = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:getSimpleQuote>\n" +
                "         <!--Optional:-->\n" +
                "         <ser:symbol>" + company +"</ser:symbol>\n" +
                "      </ser:getSimpleQuote>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HttpEntity entity = new ByteArrayEntity(content.getBytes("UTF-8"));
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        String result = EntityUtils.toString(response.getEntity());

        return result;
    }*/

    private String callRESTep(String ep) throws Exception {

        PlatformUtils.setKeyStoreProperties();
        PlatformUtils.setKeyStoreParams();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            SSLSocketFactory sf = null;
            SSLContext sslContext = null;
            StringWriter writer;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
            } catch (NoSuchAlgorithmException e) {
                //<YourErrorHandling>
            } catch (KeyManagementException e) {
                //<YourErrorHandling>
            }

            try {
                sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            } catch (Exception e) {
                //<YourErrorHandling>
            }
            Scheme scheme = new Scheme("https", 8243, sf);
            httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
            HttpGet get = new HttpGet(ep);

            // add header
            get.setHeader("Content-Type", "text/xml;charset=UTF-8");
            get.setHeader("Authorization", "Bearer " + oauthToken);
            get.setHeader("x-saml-assertion", SamlConsumerManager.getEncodedAssertion());

            CloseableHttpResponse response = httpClient.execute(get);
            try {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println("API RESULT" + result);
                return result;
            } finally {
                response.close();
            }

        } finally {
            httpClient.close();
        }
    }

}
