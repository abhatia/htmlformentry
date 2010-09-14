<%@ include file="/WEB-INF/template/include.jsp"%>

<c:if test="${model.authenticatedUser != null}">

	<openmrs:require privilege="Add Patients" otherwise="/login.htm"
		redirect="/index.htm" />

	<form method="get" name="createPatientForm" id="createPatientForm"
		action="${ pageContext.request.contextPath }/module/htmlformentry/patientHtmlFormEntry.form"
		onSubmit="return validateCreatePatientForm()">
		<div id="createPatient">
			<b class="boxHeader"> <spring:message
					code="htmlformentry.create.patient.portlet.title" /> </b>
			<div class="box">
				<spring:message
					code="htmlformentry.create.patient.portlet.instructions" />
				<br />
				<table>
					<tr>
						<td>
							<spring:message code="htmlformentry.create.patient.portlet.form" />
							<select name="htmlFormId" id="htmlFormId" onChange="clearError('formName')">
								<option value="">
									<spring:message code="htmlformentry.create.patient.portlet.chooseAForm" />
								</option>
								<c:forEach var="hf" items="${ model.htmlForms }">
									<option value="${ hf.id }">${ hf.name }</option>
								</c:forEach>
							</select>
							<span class="error" id="formNameError"><spring:message
									code="htmlformentry.error.htmlform.required" />
							</span>
							<input type="hidden" name="mode" id="mode" value="Enter" />
						</td>
						<td>
							<input type="submit" value='<spring:message code="Patient.create"/>' />
						</td>
					</tr>
				</table>
			</div>
	</form>
	<script type="text/javascript">
		clearError("formName");
		function validateCreatePatientForm() {
			var htmlFormId = document.getElementById("htmlFormId").value;
			if (htmlFormId == null || htmlFormId.length == 0) {
				document.getElementById("formNameError").style.display = "";
				return false;
			}
			return true;
		}
    </script>
    </div>
</c:if>
