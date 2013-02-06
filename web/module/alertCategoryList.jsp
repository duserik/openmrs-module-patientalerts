<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2><spring:message code="patientalerts.alertListTitle" /></h2>
</br>
<p><a
	href="${pageContext.request.contextPath}/module/patientalerts/alertsView.list"><spring:message
	code="patientalerts.alertSeeAllLabel" /></a></p>

<div id="displayAlerts"><b class="boxHeader"><spring:message
	code="patientalerts.alertExistingCategory" /></b>
<div class="box">

<table class="patientEncounters" cellspacing="0" cellpadding="2">
	<tr>
		<th class="tableTitle" colspan="3"></th>
	</tr>
	<tr>
		<th><spring:message code="patientalerts.alertNo" /></th>
		<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message
			code="patientalerts.alertCategory" /></th>
	</tr>
	<c:forEach items="${alertsCategoryList}" var="alert" varStatus="num">
		<tr class="${num.count%2!=0?'evenRow':'oddRow'}">
			<td>${num.count}.</td>
			<td><c:forEach items="${alert.value}" varStatus="status"
				var="loop">
				<c:if test="${status.last}">
					<a
						href="${pageContext.request.contextPath}/module/patientalerts/alertCategoryView.htm?alertCategory=${alert.key}">
					<h5>[${status.count}]&nbsp;&nbsp;&nbsp;${alert.key}</h5>
					</a>
				</c:if>
			</c:forEach></td>
		</tr>

	</c:forEach>
</table>
<br/>
</div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>