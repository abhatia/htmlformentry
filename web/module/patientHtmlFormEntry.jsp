<%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="OPENMRS_DO_NOT_SHOW_PATIENT_SET" scope="request" value="true"/>
<c:set var="pageFragment" value="${param.pageFragment != null && param.pageFragment}"/>
<c:set var="inPopup" value="${pageFragment || (param.inPopup != null && param.inPopup)}"/>

<c:if test="${not pageFragment}">
	<c:choose>
		<c:when test="${inPopup}">
			<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/template/header.jsp" %>
		</c:otherwise>
	</c:choose>

	<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
	<script type="text/javascript">
		$j = jQuery.noConflict();
	</script>
	<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
	<openmrs:htmlInclude file="/dwr/engine.js" />
	<openmrs:htmlInclude file="/dwr/util.js" />
	<openmrs:htmlInclude file="/dwr/interface/DWRHtmlFormEntryService.js" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/htmlFormEntry.js" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/htmlFormEntry.css" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-ui-1.8.2.custom.css" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-1.4.2.min.js" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-ui-1.8.2.custom.min.js" />
</c:if>

<script type="text/javascript">
	var tryingToSubmit = false;
	
	function submitHtmlForm() {
	    if (!tryingToSubmit) {
	        tryingToSubmit = true;
	        DWRHtmlFormEntryService.checkIfLoggedIn(checkIfLoggedInAndErrorsCallback);
	    }
	}

	function findAndHighlightErrors(){
		/* see if there are error fields */
		var containError = false
		var ary = $j(".autoCompleteHidden");
		$j.each(ary,function(index, value){
			if(value.value == "ERROR"){
				if(!containError){
					alert("<spring:message code='htmlformentry.error.autoCompleteAnswerNotValid'/>");
					var id = value.id;
					id = id.substring(0,id.length-4);
					$j("#"+id).focus(); 					
				}
				containError=true;
			}
		});
		return containError;
	}

	/*
		It seems the logic of  showAuthenticateDialog and 
		findAndHighlightErrors should be in the same callback function.
		i.e. only authenticated user can see the error msg of
	*/
	function checkIfLoggedInAndErrorsCallback(isLoggedIn) {
		if (!isLoggedIn) {
			showAuthenticateDialog();
		}else{
			var anyErrors = findAndHighlightErrors();
        	if (anyErrors) {
            	tryingToSubmit = false;
            	return;
        	}else{
        		doSubmitHtmlForm();
        	}
		}
	}

	function showAuthenticateDialog() {
		showDiv('passwordPopup');
		tryingToSubmit = false;
	}

	function loginThenSubmitHtmlForm() {
		hideDiv('passwordPopup');
		var username = $j('#passwordPopupUsername').val();
		var password = $j('#passwordPopupPassword').val();
		$j('#passwordPopupUsername').val('');
		$j('#passwordPopupPassword').val('');
		DWRHtmlFormEntryService.authenticate(username, password, submitHtmlForm); 
	}

	function doSubmitHtmlForm() {
		var form = document.getElementById('htmlform');
		form.submit();
		tryingToSubmit = false;
	}

	function handleDeleteButton() {
		showDiv('confirmDeleteFormPopup');
	}

	function cancelDeleteForm() {
		hideDiv('confirmDeleteFormPopup');
	}
</script>

<c:if test="${!inPopup}">
	<div id="htmlFormEntryBanner">
		<spring:message var="backMessage" code="htmlformentry.goBack"/>
		<c:if test="${command.context.mode == 'ENTER' || command.context.mode == 'EDIT'}">
			<spring:message var="backMessage" code="htmlformentry.discard"/>
		</c:if>
		<div style="float: left">
			<a href="<c:choose><c:when test="${not empty command.returnUrlWithParameters}">${command.returnUrlWithParameters}</c:when><c:otherwise>javascript:back();</c:otherwise></c:choose>">${backMessage}</a>
			| <a href= "javascript:window.print();"><spring:message code="htmlformentry.print"/></a> &nbsp;<br/>
		</div>
		<b>
			
			<c:choose>
                <c:when test="$(command.patient.personName != null}">
                    ${command.patient.personName} |
                </c:when>   
                <c:otherwise>
                    <spring:message code="htmlformentry.newPatient"/> |
                </c:otherwise>            
            </c:choose>
			<c:choose>
				<c:when test="${not empty command.form}">
					${command.form.name} 
				</c:when>				
			</c:choose> 
			<c:if test="${empty command.patient.patientId}">
				| <spring:message code="htmlformentry.newForm"/>
			</c:if>
		</b>
	</div>
</c:if>

<c:if test="${command.context.mode != 'VIEW'}">
	<spring:hasBindErrors name="command">
		<spring:message code="fix.error"/>
		<div class="error">
			<c:forEach items="${errors.allErrors}" var="error">
				<spring:message code="${error.code}" text="${error.code}"/><br/>
			</c:forEach>
		</div>
		<br />
	</spring:hasBindErrors>
</c:if>

<c:if test="${command.context.mode != 'VIEW'}">
	<form id="htmlform" method="post" onSubmit="submitHtmlForm(); return false;">
		<input type="hidden" name="personId" value="${ command.patient.personId }"/>
		<input type="hidden" name="htmlFormId" value="${ command.htmlFormId }"/>
		<input type="hidden" name="formModifiedTimestamp" value="${ command.formModifiedTimestamp }"/>		
		<input type="hidden" name="closeAfterSubmission" value="${param.closeAfterSubmission}"/>
</c:if>
	
	${command.htmlToDisplay}
	
<c:if test="${command.context.mode != 'VIEW'}">
	<div id="passwordPopup" style="position: absolute; z-axis: 1; bottom: 25px; background-color: #ffff00; border: 2px black solid; display: none; padding: 10px">
		<center>
			<table>
				<tr>
					<td colspan="2"><b><spring:message code="htmlformentry.loginAgainMessage"/></b></td>
				</tr>
				<tr>
					<td align="right"><b>Username:</b></td>
					<td><input type="text" id="passwordPopupUsername"/></td>
				</tr>
				<tr>
					<td align="right"><b>Password:</b></td>
					<td><input type="password" id="passwordPopupPassword"/></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="button" value="Submit" onClick="loginThenSubmitHtmlForm()"/></td>
				</tr>
			</table>
		</center>
	</div>
</form>
</c:if>

<c:if test="${not empty command.setLastSubmissionFieldsJavascript || not empty command.lastSubmissionErrorJavascript}"> 
	<script type="text/javascript">
		$j(document).ready( function() {
			${command.setLastSubmissionFieldsJavascript}
			${command.lastSubmissionErrorJavascript}
		});
	</script>
</c:if>

<c:if test="${!pageFragment}">
	<c:choose>
		<c:when test="${inPopup}">
			<%@ include file="/WEB-INF/template/footerMinimal.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/template/footer.jsp" %>
		</c:otherwise>
	</c:choose>
</c:if>