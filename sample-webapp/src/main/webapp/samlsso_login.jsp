<!DOCTYPE html>
<!--
 ~ Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->

<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>WGU Sample REST App</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <!-- Le styles -->
        <link href="assets/css/bootstrap.min.css" rel="stylesheet">
        <link href="css/localstyles.css" rel="stylesheet">
        <!--[if lt IE 8]>
        <link href="css/localstyles-ie7.css" rel="stylesheet">
        <![endif]-->

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
        <script src="assets/js/html5.js"></script>
        <![endif]-->
        <script src="assets/js/jquery-1.7.1.min.js"></script>
        <script src="js/scripts.js"></script>

    </head>

    <body>
    <%
        String user = (String) request.getParameter("subject");
        String error = null;
        String tokenType = null;
        String expiresIn = null;
        String refreshToken = null;
        String accessToken = null;
        String apiResponse = null;

        if(request.getParameter("Error") != null){
            error = request.getParameter("Error");
        } else {
            tokenType = (String) request.getParameter("TokenType");
            expiresIn = (String) request.getParameter("ExpiresIn");
            refreshToken = (String) request.getParameter("RefreshToken");
            accessToken = (String) request.getParameter("AccessToken");
        }

        if(request.getParameter("ApiResponse") != null){
            apiResponse = request.getParameter("ApiResponse");
        }

        session.setAttribute("user", user);
    %>
    <div class="header-strip">&nbsp;</div>
    <div class="header-back">
        <div class="container">
            <div class="row">
                <div class="span4 offset3">
                    <a class="logo">&nbsp</a>
                </div>
            </div>
        </div>
    </div>

    <div class="header-text">
            WGU Sample REST App
        <br>
        <h2>You are logged in as <%=user%></h2>
    </div>

    <div class="container">
        <div class="row">
            <div class="span6 offset3 content-section" style="margin-left:230px">
                <div id="loginTable1" class="identity-box">
                    <table>
                        <%
                            if (error != null){
                        %>
                        <tr>
                            <td>Error : </td>
                            <td><%=error%></td>
                        </tr>
                        <%
                        } else{
                        %>
                        <tr>
                            <td>Token Type : </td>
                            <td><%=tokenType%></td>
                        </tr>
                        <tr>
                            <td>Expires In : </td>
                            <td><%=expiresIn%></td>
                        </tr>
                        <tr>
                            <td>Refresh Token : </td>
                            <td><%=refreshToken%></td>
                        </tr>
                        <tr>
                            <td>Access Token : </td>
                            <td><%=accessToken%></td>
                        </tr>
                        <%
                            }
                        %>

                    </table>
                    <%
                        if(apiResponse != null){
                    %>
                    <div>
                        <p>XML Response</p>
                        <p><pre><%=apiResponse%></pre></p>
                    </div>
                    <%
                        }
                    %>

                    <%
                        if (error == null){
                    %>
                    <form action="sample-consumer">
                        <input type="text" id="quote" name="quote"/>
                        <input type="submit" value="Quote">

                        <input type="hidden" name="subject" value="<%=user%>"/>
                        <input type="hidden" name="TokenType" value="<%=tokenType%>"/>
                        <input type="hidden" name="ExpiresIn" value="<%=expiresIn%>"/>
                        <input type="hidden" name="RefreshToken" value="<%=refreshToken%>"/>
                        <input type="hidden" name="AccessToken" value="<%=accessToken%>"/>

                    </form>
                    <%
                        }
                    %>
                </div>

                <div style="margin-top:20px;margin-bottom:20px;text-align:center">
                <div class="form-actions">

                    <br/><br/>
                    <form action="sample-consumer">
                        <input type="submit" value="Logout" class="btn btn-primary">
                        <input type="hidden" name="logout" value="<%=user%>"/>
                    </form>

                </div>
                    </div>
            </div>
        </div>
    </div>
    <!-- /container -->
    
    </body>
    </html>

