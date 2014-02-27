<div class="control-group">
    <div class="controls" style="margin-top: 20px; margin-left: 115px">
		<%--<input class="input-large" type="text" id="claimed_id" name="claimed_id"--%>
								  <%--size='30'/>--%>
        <input type="hidden" name="sessionDataKey" value='<%=request.getParameter("sessionDataKey")%>'/>
         <a href="../../commonauth?claimed_id=https://www.google.com/accounts/o8/id"><img src="images/google_logo.png" height="60" width="180"/></a>
         <br/><br/><br/>
         <a href="../../commonauth?claimed_id=https://me.yahoo.com"><img src="images/yahoo_logo.png" height="50" width="180"/></a>
         <br/><br/><br/>
         <a href="../../commonauth?claimed_id=http://myopenid.com"><img src="images/myopenid_logo.png" height="50" width="180"/></a>
         <br/><br/>

    </div>
    <div class="form-actions">
    </div>
</div>


