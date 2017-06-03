<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="fr.ensimag.loupsgarous.modele.Role" %>
<%@ page import="fr.ensimag.loupsgarous.modele.Pouvoir" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>
      ${partie.nomPartie} - Archives
    </title>

    <link href="/resources/css/bootstrap.min.css" media="all" rel="stylesheet" type="text/css" />
    <link href="/resources/css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
    <script src="/resources/js/jquery-3.2.0.min.js" type="text/javascript"></script>
    <script src="/resources/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/resources/js/jquery-ui.min.js" type="text/javascript"></script>

    <link href="/resources/css/loupsgarous.css" media="all" rel="stylesheet" type="text/css" />
    <script src="/resources/js/archives.js" type="text/javascript"></script>

    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport">
  </head>
  <body class="lobby bg-2">
    <nav class="navbar navbar-default">
      <div class="container-fluid">
        <div class="navbar-header">
          <a class="navbar-brand" href="/loupsgarous" title="Retour au lobby"><img class="img-responsive" src="/resources/img/title.png" /></a>
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbarLobby">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
        </div>
        <div class="collapse navbar-collapse" id="navbarLobby">
          <ul class="nav navbar-nav navbar-right">
            <li><p class="navbar-btn"><a class="btn btn-danger" href="/loupsgarous?action=logout">Déconnexion</a></p></li>
          </ul>
        </div>
      </div>
    </nav>    
    <div class="container-fluid">
      
      <div class="panel panel-default">
        <div class="panel-header">
          <div class="row">
            <div class="col-lg-3 text-left">
              <a class="btn btn-primary btn-header" href="/loupsgarous">Retour au lobby</a>
            </div>
            <div class="col-lg-6">
              <h1>${partie.nomPartie} - Archives</h1>
            </div>
            <div class="col-lg-3 text-right">
              <a class="btn btn-info btn-header" href="/loupsgarous/village">Retour à la partie</a>
              <a class="btn btn-primary btn-header" href="/loupsgarous/village?action=archives">Recharger</a>
            </div>
          </div>
          <c:if test="${not empty alert}">
          <!-- Rajouter le message transmis -->
          ${alert}
          </c:if>

          <!-- Sélecteur -->
          <label for="numeroJour">Choisir le jour à afficher :</label>
          <select name="numeroJour" id="numeroJour">
            <c:forEach items="${numerosJours}" var="n">
              <option value="${n}">Jour ${n}</option>
            </c:forEach>
          </select>
        </div>
        <div class="panel-body">
      
        <!-- Chatrooms -->
        <div class="row">
          <c:forEach items="${chatrooms}" var="chatroom">
          <div class="col-lg-4">
              <div class="panel panel-default chatroom-archive chatroom-jour-${chatroom.numeroJour}">
                <div class="panel-header">
                  <h2>${chatroom.typeChatroom.toString()} - Jour ${chatroom.numeroJour}</h2>
                </div>
                <div class="panel-body">
                  <div class="row">
                      <div class="table-chatroom col-lg-12">
                          <table class="table table-striped table-bordered">
                            <thead>
                              <tr>
                                <th>
                                  <span class="message-date">${chatroom.dateDebut.format(DateTimeFormatter.ofPattern("HH:mm"))}</span>
                                  <span>Début de la discussion</span>
                                </th>
                              </tr>
                            </thead>
                            <tbody>
                              <c:forEach items="${chatroom.messages}" var="message">
                              <tr>
                                <td>
                                  <span class="message-date">${message.dateEnvoi.format(DateTimeFormatter.ofPattern("HH:mm"))}</span>
                                  <span class="message-envoyeur">${message.envoyeurID}</span> :
                                  <span>${message.contenu}</span>
                                </td>
                              </tr>
                              </c:forEach>
                            </tbody>
                          </table>
                      </div>
                  </div>
                </div>
              </div>
          </div>
          </c:forEach>
        </div>
       </div>
      </div>
    </div>
  </body>
</html>
