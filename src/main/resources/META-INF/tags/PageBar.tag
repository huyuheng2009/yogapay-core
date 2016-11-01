<%@tag description="put the tag description here" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="p" type="com.yogapay.sql.Paginated" required="true"%>
<!-- PageBar  start -->
<script type="text/javascript">
	$(function () {
		$("#page ._page").click(function (evt) {
			$("#page input[name='page']").val($(this).data("p"));
			$("#page form").submit();
		});
		$("#page ._go").click(function (evt) {
			var v = $("#page ._input").val();
			if(v.length===0) {
				evt.preventDefault();
				return;
			}
			$("#page input[name='page']").val(v);
		});
		$("#page ._input").bind("input", function (evt) {
			var disabled = !/^\d*$/.test($(this).val());
			if (!disabled) {
				var page = parseInt($(this).val());
				disabled = page>${p.totalPage} || page<1;
			}
			$("#page ._go").prop("disabled", disabled);
		});
	});
</script>
<div id="page">
	<form action="">
		<% request.setAttribute("params", request.getParameterMap());%>
		<c:forEach var="e"  items="${params}">
			<c:if test="${e.key ne 'page'}">
				<c:forEach var="v"  items="${e.value}">
					<input type="hidden" name="${e.key}" value="${v}" />
				</c:forEach>
			</c:if>
		</c:forEach>
		<input type="hidden" name="page" value="" />
		<div class="pagebar">
			<span class="pages">第${p['empty'] ? 0:p.page}页/共${p.totalPage}页 ${p.count}条记录</span>
			<c:if test="${! p['empty']}">
				<input type="button" class="page _page"  data-p="1" value="第一页" />
				<input type="button" class="page _page"  data-p="${p.page-1}" value="上一页" ${p.hasPrevious ? '':'disabled'}/>
				<c:forEach var="page" items="${p.pageOptions}">
					<c:choose>
						<c:when test="${page eq p.page}"><input type="button" class="page current" value="${page}" disabled="" /></c:when>
						<c:when test="${page ne null}"><input type="button" class="page _page"  data-p="${page}" value="${page}" /></c:when>
						<c:otherwise><span class="extend">...</span></c:otherwise>
					</c:choose>
				</c:forEach>
				<input type="button" class="page _page"  data-p="${p.page+1}" value="下一页" ${p.hasNext ? '':'disabled'}/>
				<input type="button" class="page _page"  data-p="${p.totalPage}" value="最后一页" />
				<input type="text" class="page _input" style="width: 50px;" value="" maxlength="4" />
				<input type="submit" class="page _go"  value="跳转" />
			</c:if>
		</div>
	</form>
</div>
<!-- PageBar  end -->