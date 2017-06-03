<%@ page contentType="text/html; charset=utf-8"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta charset="UTF-8"/>
        <title>Erreur interne</title>
    </head>
    <body>
        <h1 style="text-align: center">Le serveur a rencontré une erreur :</h1>
        <br />
        <pre>${message}</pre>
        <br />
        <c:if test="${not empty exception}">
        <pre>${pageContext.out.flush()}${exception.printStackTrace(pageContext.response.writer)}</pre>
        <br />
        </c:if>
        <a href="/loupsgarous">Retour à l'accueil</a>
    </body>
</html>
