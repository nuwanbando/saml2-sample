<%@ page import="org.apache.axiom.util.base64.Base64Utils" %>
<%@ page import="java.util.Arrays" %>
<!DOCTYPE html>
<!--
~ Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
~
~ WSO2 Inc. licenses this file to you under the Apache License,
~ Version 2.0 (the "License"); you may not use this file except
~ in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing,
~ software distributed under the License is distributed on an
~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
~ KIND, either express or implied. See the License for the
~ specific language governing permissions and limitations
~ under the License.
-->

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>WGU Sample REST app</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/localstyles.css" rel="stylesheet">
    <link rel="stylesheet" href="css/redmond/jquery-ui-1.9.2.custom.min.css"/>
    <link type="text/css" href="css/style.css" rel="stylesheet" media="all"/>
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
    String user = (String) request.getParameter("Subject");
    String error = null;
    String tokenType = null;
    String expiresIn = null;
    String refreshToken = null;
    String accessToken = null;
    String apiResponse = null;

    if (request.getParameter("Error") != null) {
        error = request.getParameter("Error");
    } else {
        tokenType = (String) request.getParameter("TokenType");
        expiresIn = (String) request.getParameter("ExpiresIn");
        refreshToken = (String) request.getParameter("RefreshToken");
        accessToken = (String) request.getParameter("AccessToken");
    }
    if (request.getParameter("ApiResponse") != null) {
        apiResponse = request.getParameter("ApiResponse");
    }
    session.setAttribute("user", user);
%>

<div class="middle">
    <div class="header">
        <ul class="header-links">
            <li class="wso2">
                <a href="http://wso2.com" target="_blank"></a>
            </li>
            <li class="contact">
                <a href="http://wso2.com/contact/" target="_blank">Contact</a>
            </li>
            <li class="support">
                <a href="http://wso2.com/support/" target="_blank">Support</a>
            </li>
        </ul>

    </div>
</div>

<div class="title">
    <table class="title_table">
        <tr>
            <td class="title_text">Sample Web Application - WGU</td>
        </tr>
    </table>
    <br/>
    <br/>

    <h3>You are signed as: <%=user%>
    </h3>
</div>

<div class="container">
    <div class="row">
        <div class="span10 offset3 content-section" style="margin-left:250px">

            <form action="sample-consumer" id="loginForm" class="well form-horizontal">
                <div id="loginTable1" class="identity-box">
                    <%
                        if (error != null) {
                    %>
                    <div class="control-group">
                        <label class="control-label">Error:</label>

                        <div class="controls">
                            <input class="input-xlarge" type="text" value="<%=error%>"
                                   size='30' disabled/>
                        </div>
                    </div>
                    <%
                    } else {
                    %>
                    <div class="control-group">
                        <label class="control-label">Token Type:</label>

                        <div class="controls">
                            <input class="input-xlarge stnew" type="text" value="<%=tokenType%>"
                                   size='30'/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">Expires in:</label>

                        <div class="controls">
                            <input class="input-xlarge stnew" type="text" value="<%=expiresIn%>"
                                   size='30'/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">Access Token:</label>

                        <div class="controls">
                            <input class="input-xlarge stnew" type="text" value="<%=accessToken%>"
                                   size='30'/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">Refresh Token:</label>

                        <div class="controls">
                            <input class="input-xlarge stnew" type="text" value="<%=refreshToken%>"
                                   size='30'/>
                        </div>
                    </div>

                    <hr size="3">
                    <div class="control-group">
                        <label class="control-label">REST endpoint</label>
                        <input type="hidden" name="subject" value="<%=user%>"/>
                        <input type="hidden" name="TokenType" value="<%=tokenType%>"/>
                        <input type="hidden" name="ExpiresIn" value="<%=expiresIn%>"/>
                        <input type="hidden" name="RefreshToken" value="<%=refreshToken%>"/>
                        <input type="hidden" name="AccessToken" value="<%=accessToken%>"/>

                        <div class="controls">
                            <input class="input-medium stnew" type="text" id="quote" name="quote"
                                   style="width: 450px;"
                                   value="https://ec2-54-83-28-150.compute-1.amazonaws.com:8243/wgu/2.1.3/person/2/banner/123456789/identifiers"/> <input
                                type="submit" value="Invoke"
                                class="btn btn-primary"
                                style="margin-left: 10px">
                        </div>

                    </div>
                    <%
                        if (apiResponse != null) {
                    %>

                    <div class="control-group">
                        <label class="control-label">Response</label>

                        <div class="controls">
                            <textarea
                                    style="height: 200px; width: 450px; font-family: Menlo, Monaco, Consolas, 'Courier New', monospace"><%=new String((Base64Utils.decode(apiResponse)))%>
                            </textarea>

                        </div>
                    </div>

                    <%
                            }
                        }
                    %>

                </div>

            </form>

            <form action="sample-consumer">
                <div style="margin-top:20px;margin-bottom:20px;text-align:center">
                    <input type="submit" value="Logout" class="btn btn-primary">
                    <input type="hidden" name="logout" value="<%=user%>"/>
                </div>
            </form>


        </div>
    </div>
</div>
</div>
<!-- /container -->

</body>
</html>