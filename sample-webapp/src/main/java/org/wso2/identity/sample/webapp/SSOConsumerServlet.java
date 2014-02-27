/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.identity.sample.webapp;

import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.xml.ConfigurationException;
import org.wso2.identity.sample.webapp.util.OAuthTokenDTO;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet implementation class SSOConsumerServlet
 */
public class SSOConsumerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private SamlConsumerManager consumer;
	private static Log log = LogFactory.getLog(SSOConsumerServlet.class);
    private OAuthTokenDTO tokenDTO;
    int expiresIn;
    int remainingTime;
    long generatedTime;
    long currentTime;

    /**
	 * Servlet init
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
	        consumer = new SamlConsumerManager(config);
        } catch (ConfigurationException e) {
        	throw new ServletException("Errow while configuring SAMLConsumerManager", e);
        }
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	                                                                              throws ServletException,
	                                                                              IOException {
		doPost(request, response);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	                                                                               throws ServletException,
	                                                                               IOException {

		String responseMessage = request.getParameter("SAMLResponse");

		if (responseMessage != null) { /* response from the identity provider */

			log.info("SAMLResponse received from IDP");
			
			Map<String, String> result = consumer.processResponseMessage(responseMessage);
            tokenDTO = consumer.generateOAuthToken();
            currentTime = System.currentTimeMillis();
            generatedTime = currentTime;
            String token;
            if(tokenDTO.getError() != null){
               token = "Error=" +tokenDTO.getErrorDescription();
            }else {
                token = "TokenType=" + tokenDTO.getTokenType() + "&";
                token = token + "ExpiresIn=" + tokenDTO.getExpiresIn() + "&";
                expiresIn =  Integer.valueOf(tokenDTO.getExpiresIn());
                token = token + "RefreshToken=" + tokenDTO.getRefreshToken() + "&";
                token = token + "AccessToken=" +tokenDTO.getAccessToken();
            }
            System.out.println("#################### Result #################");
            System.out.println(result);
            if (result == null) {
				// lets logout the user
				response.sendRedirect("index.jsp");
			} else if (result.size() == 1) {
				/*
				 * No user attributes are returned, so just goto the default
				 * home page.
				 */
                String params = "home.jsp?";
                params = params + "Subject=" + result.get("Subject");
                params = params + "&"  + token;
				response.sendRedirect(params);
			} else if (result.size() > 1) {
				/*
				 * We have received attributes, so lets show them in the
				 * attribute home page.
				 */
                String params = "home.jsp?";
				Object[] keys = result.keySet().toArray();
				for (int i = 0; i < result.size(); i++) {
					String key = (String) keys[i];
					String value = (String) result.get(key);
					if (i != result.size()) {
						params = params + key + "=" + value + "&";
					} else {
						params = params + key + "=" + value;
					}
				}
                params = params + "&" + token;
                System.out.println(params);
				response.sendRedirect(params);
			} else {
				// something wrong, re-login
				response.sendRedirect("index.jsp");
			}
		} else { /* time to create the api call, authentication request or logout request */
			try {
                if(request.getParameter("quote") != null){
                    String params = "home.jsp?";
                    params = params + "subject=" + request.getParameter("subject") + "&";
                    currentTime = System.currentTimeMillis();
                    int duration = (int) (currentTime - generatedTime)/1000;
                    remainingTime = expiresIn - duration;
//                    System.out.println(" - ---- Current Time : " + currentTime);
//                    System.out.println(" - ---- Generated Time : " + generatedTime);
//                    System.out.println(" - ---- Duration : " + duration);
//                    System.out.println(" - ---- Expires in : " + expiresIn);
                    if(duration < expiresIn){
                        String apiResponse = consumer.doApiCall(request.getParameter("quote"));
                        System.out.println(apiResponse);
                        params = params + "TokenType=" + request.getParameter( "TokenType") + "&";
                        params = params + "ExpiresIn=" + String.valueOf(remainingTime) + "&";
                        params = params + "RefreshToken=" + request.getParameter("RefreshToken") + "&";
                        params = params + "AccessToken=" + request.getParameter("AccessToken") + "&";
                        params = params + "ApiResponse=" + Base64Utils.encode(apiResponse.getBytes());
                    } else{
                        tokenDTO = consumer.renewToken();
                        currentTime = System.currentTimeMillis();
                        generatedTime = currentTime;
                        String token;
                        if(tokenDTO.getError() != null){
                            token = "Error=" +tokenDTO.getErrorDescription();
                            params = params + token;
                        }else {
                            token = "TokenType=" + tokenDTO.getTokenType() + "&";
                            token = token + "ExpiresIn=" + tokenDTO.getExpiresIn() + "&";
                            expiresIn =  Integer.valueOf(tokenDTO.getExpiresIn());
                            token = token + "RefreshToken=" + tokenDTO.getRefreshToken() + "&";
                            token = token + "AccessToken=" +tokenDTO.getAccessToken() + "&";
                            params = params + token;
                            String apiResponse = consumer.doApiCall(request.getParameter("quote"));
                            params = params + "ApiResponse=" + Base64Utils.encode(apiResponse.getBytes());
                        }
                    }
                    response.sendRedirect(params);
                }
                else {
				    String requestMessage = consumer.buildRequestMessage(request);
                    response.sendRedirect(requestMessage);
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
