<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/moduleResources/patientalerts/jquery.js" />
<openmrs:htmlInclude file="/moduleResources/patientalerts/jquery.dataTables.js" />
<openmrs:htmlInclude file="/moduleResources/patientalerts/demo_page.css" />
<openmrs:htmlInclude file="/moduleResources/patientalerts/demo_table.css" />

<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				$('#example').dataTable( {
					"sPaginationType": "full_numbers"
				} );
			} );
</script>


<h2><spring:message code="patientalerts.alertListTitle" /></h2>
</br>
<p><a
	href="${pageContext.request.contextPath}/module/patientalerts/alertsView.list"><spring:message
	code="patientalerts.alertSeeAllLabel" /></a> | <a
	href="${pageContext.request.contextPath}/module/patientalerts/alertCategory.list"><spring:message
	code="patientalerts.alertSeeAllExistingCategory" /></a></p>




<div id="container">
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="example">
	 <thead>
	<tr>
		<th><spring:message code="patientalerts.alertNo" /></th>
		<th><spring:message code="patientalerts.alertMessage" /></th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${alertsByCategory}" var="alert" varStatus="status">
		<tr class="${num.count%2!=0?'evenRow':'oddRow'}">
			<td>${status.count}.</td>
			<td>${alert.value}</td>
			<td><a
				href="../../patientDashboard.form?patientId=${alert.key}"><spring:message
				code="patientalerts.alertHandle" /></a></td>
			<td><a
				href="../../patientDashboard.form?patientId=${alert.key}"><spring:message
				code="patientalerts.alertSendMail" /></a></td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<br/>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>