<%@ include file="/WEB-INF/template/include.jsp"%>


<div id="displayAlerts"><b class="boxHeader"><spring:message
	code="patientalerts.alertDashboardTitle" /></b>
<div class="box">


<table class="patientEncounters" cellspacing="0" cellpadding="2">
	<tbody>
		<tr>
			<th class="tableTitle" colspan="2"></th>
		</tr>
		<tr>
			<th><spring:message code="patientalerts.alertNo" /></th>
			<th><spring:message code="patientalerts.alertMessage" /></th>
			<th></th>
		</tr>
		<c:forEach items="${model.alertsForPatient}" var="alert">
			<tr class="${alert.key%2!=0?'evenRow':'oddRow'}">
				<td>${alert.key}.</td>
				<td>${alert.value}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>


</div>
</div>