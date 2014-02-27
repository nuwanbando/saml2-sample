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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.opensaml.xml.util.Base64;
import org.wso2.identity.sample.webapp.util.OAuthTokenDTO;
import org.wso2.identity.sample.webapp.util.PlatformUtils;

public class OAuthTokenGenerator {
    private String oauthTokenEndpoint;
    private String consumerKey;
    private String consumerSecret;

    private String encodedBasicHeader;

    public OAuthTokenGenerator(String oauthTokenEndpoint, String consumerKey, String consumerSecret) {
        this.oauthTokenEndpoint = oauthTokenEndpoint;
        this.consumerKey =  consumerKey;
        this.consumerSecret = consumerSecret;
        setEncodedBasicHeader();
    }

    private void setEncodedBasicHeader(){
        String str = consumerKey + ":" + consumerSecret;
        encodedBasicHeader = Base64.encodeBytes(str.getBytes(),Base64.DONT_BREAK_LINES);
        encodedBasicHeader = "Basic " + encodedBasicHeader;
    }

    public OAuthTokenDTO getToken(String encodedString) {
        OAuthTokenDTO dto;
        try {
            String response =  sendPost(encodedString,"generate");
            System.out.println(response);
            dto = getOauthTokenDTO(response);
        } catch (Exception e) {
            e.printStackTrace();
            dto = new OAuthTokenDTO("error",e.getMessage());
        }
        return dto;
    }

    public OAuthTokenDTO renewToken(String refreshToken) {
        OAuthTokenDTO dto;
        try {
            String response =  sendPost(refreshToken,"refresh");
            dto = getOauthTokenDTO(response);
        } catch (Exception e) {
            e.printStackTrace();
            dto = new OAuthTokenDTO("error",e.getMessage());
        }
        return dto;
    }

    // HTTP POST request
    private String sendPost(String str, String type) throws Exception{
        PlatformUtils.setKeyStoreProperties();
        PlatformUtils.setKeyStoreParams();
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(oauthTokenEndpoint);

        // add header
        post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        post.setHeader("Authorization", encodedBasicHeader);

        String content = null;

        if(type.equals("generate")){
            content = "grant_type=urn:ietf:params:oauth:grant-type:saml2-bearer&assertion=" + str +"&scope=PRODUCTION";
            System.out.println("#################### SAML ASSERTION ########### ");
            System.out.println(str);
        } else if(type.equals("refresh")){
            content = "grant_type=refresh_token&refresh_token=" + str +"&scope=PRODUCTION";
        }

        HttpEntity entity = new ByteArrayEntity(content.getBytes("UTF-8"));
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        String result = EntityUtils.toString(response.getEntity());

        return result;
    }

    private OAuthTokenDTO getOauthTokenDTO(String tokenString){
        OAuthTokenDTO dto;
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject)parser.parse(tokenString);
        if(!tokenString.contains("error")){
            String tokenType = jsonObject.getAsJsonPrimitive("token_type").getAsString();
            String expiresIn = jsonObject.getAsJsonPrimitive("expires_in").getAsString();
            String refreshToken=null;
            if(jsonObject.getAsJsonPrimitive("refresh_token") != null){
                refreshToken = jsonObject.getAsJsonPrimitive("refresh_token").getAsString();
            }
            String accessToken = jsonObject.getAsJsonPrimitive("access_token").getAsString();
            dto = new OAuthTokenDTO(tokenType,expiresIn,refreshToken,accessToken);
        }else {
            String error = jsonObject.getAsJsonPrimitive("error").getAsString();
            String errorDescription = jsonObject.getAsJsonPrimitive("error_description").getAsString();
            dto = new OAuthTokenDTO(error,errorDescription);
        }
        return dto;
    }
}
