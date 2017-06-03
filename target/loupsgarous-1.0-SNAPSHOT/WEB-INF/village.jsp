<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="fr.ensimag.loupsgarous.modele.Role" %>
<%@ page import="fr.ensimag.loupsgarous.modele.Pouvoir" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>
      ${partie.nomPartie}
    </title>

    <link href="/resources/css/bootstrap.min.css" media="all" rel="stylesheet" type="text/css" />
    <link href="/resources/css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
    <script src="/resources/js/jquery-3.2.0.min.js" type="text/javascript"></script>
    <script src="/resources/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/resources/js/jquery-ui.min.js" type="text/javascript"></script>

    <link href="/resources/css/loupsgarous.css" media="all" rel="stylesheet" type="text/css" />
    <script src="/resources/js/village.js" type="text/javascript"></script>

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
              <h1>${partie.nomPartie}</h1>
            </div>
            <div class="col-lg-3 text-right">
              <c:if test="${(! villageois.vivant) || (partie.vainqueurs != null)}">
              <a class="btn btn-info btn-header" href="/loupsgarous/village?action=archives">Voir les archives</a>
              </c:if>
              <a class="btn btn-primary btn-header" href="/loupsgarous/village">Recharger</a>
            </div>
          </div>
          <c:if test="${not empty alert}">
            <!-- Rajouter le message transmis -->
            ${alert}
          </c:if>
        </div>
        <div class="panel-body">
      
        <div class="row">

          <c:choose>
          <c:when test="${partie.vainqueurs != null}">    
          <div class="col-lg-9">
            <div class="panel panel-default">
              <div class="panel-header">
                <h2>Partie terminée</h2>
                <c:choose>
                  <c:when test="${partie.vainqueurs == villageois.roleVillageois}">
                <h3 class="text-success">
                  </c:when>
                  <c:otherwise>
                <h3 class="text-danger">
                  </c:otherwise>
                </c:choose>
                Les ${partie.vainqueurs.toPluriel()} ont gagné !
                </h3>
              </div>
            </div>
          </div>
          </c:when>

          <c:otherwise>
          <div class="col-lg-9">
            <!-- Chatrooms -->

            <c:forEach items="${chatrooms.values()}" var="chatroom">
            <c:if test="${villageois.canRead(chatroom)}">
              <div class="panel panel-default">
                <div class="panel-header">
                  <h2>${chatroom.typeChatroom.toString()} - Jour ${chatroom.numeroJour}</h2>
                </div>
                <div class="panel-body">
                  <div class="row">
                      <div class="table-chatroom col-md-9">
                          <table class="table table-striped">
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
                      <div class="col-md-3">
                        <table class="table">
                          <thead>
                            <tr>
                              <th><span>Participants</span></th>
                            </tr>
                          </thead>
                          <tbody>
                            <c:forEach items="${allVillageois.values()}" var="v">
                            <c:if test="${v.canWrite(chatroom)}">
                                <tr><td class="villageois">${v.joueurID}</td></tr>
                            </c:if>
                            </c:forEach>
                          </tbody>
                        </table>
                      </div>
                  </div>
                  <c:if test="${villageois.canWrite(chatroom)}">
                  <form action="/loupsgarous/village" method="post">
                      <input type="hidden" name="action" value="envoyerMessage" />
                      <input type="hidden" name="chatroomID" value="${chatroom.id}" />
                      <div class="input-group">
                        <input class="form-control" placeholder="Entrez un message..." type="text" name="nouveauMessage" autofocus />
                        <div class="input-group-btn">
                          <button type="submit" class="btn btn-default">Envoyer</button>
                        </div>
                      </div>
                  </form>
                  </c:if>
                </div>
              </div>
            </c:if>
            </c:forEach>
          </div>
          </c:otherwise>
          </c:choose>

          <div class="col-lg-3">
            <!-- Vignette d'infos sur le joueur -->
            <div class="panel panel-default">
              <div class="panel-body row">
                <div class="col-xs-8">
                  <h2 class="text-center">${villageois.joueurID}</h2>
                  <p>${villageois.printRole()}</p>
                  <c:if test="${! villageois.vivant}">
                    <p>Mort</p>
                  </c:if>
                </div>
                <div class="col-xs-4">
                  <c:choose>
                      <c:when test="${! villageois.vivant}">
                        <img class="img-responsive img-rounded" src="/resources/img/dead.png" />                        
                      </c:when>
                      <c:when test="${villageois.roleVillageois == Role.HUMAIN}">
                        <img class="img-responsive img-rounded" src="/resources/img/villager.jpg" />
                      </c:when>
                      <c:when test="${villageois.roleVillageois == Role.LOUPGAROU}">
                        <img class="img-responsive img-rounded" src="/resources/img/werewolf.jpg" />
                      </c:when>
                  </c:choose>
                </div>
              </div>
            </div>

            <!-- Liste des joueurs & actions -->
            <div class="panel panel-default">
              <div class="panel-header">
                <h2>Liste des joueurs</h2>
              </div>
              <div class="panel-body">
                <table class="table table-striped">
                  <thead>
                    <tr>
                      <th>Nom</th>
                      <th>Votes</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                  <c:forEach items="${allVillageois.values()}" var="v">
                  <tr>
                    <td>
                      <div class="row">
                        <c:choose>
                        <c:when test="${! v.vivant}">
                          <div class="col-xs-2 victory-icon">
                            <img class="img-responsive" src="/resources/img/dead.png" />
                          </div>
                          <div class="col-xs-10">
                              <span class="villageois villageois-mort">${v.joueurID}</span>
                              <c:if test="${partie.vainqueurs != null}">
                              <br />
                              <span class="role-revele">
                                (${v.printRole()})
                              </span>
                              </c:if>
                          </div>
                        </c:when>
                        <c:when test="${partie.vainqueurs != null}">
                          <div class="col-xs-2 victory-icon">
                            <img class="img-responsive" src="/resources/img/trophy.png" />
                          </div>
                          <div class="col-xs-10">
                              <span class="villageois villageois-vainqueur">${v.joueurID}</span>
                              <br />
                              <span class="role-revele">
                                (${v.printRole()})
                              </span>
                          </div>
                        </c:when>
                        <c:otherwise>
                          <div class="col-xs-2 victory-icon"></div>
                          <div class="col-xs-10">
                              <span class="villageois">${v.joueurID}</span>
                              <c:if test="${not empty revelations && revelations.get(v.joueurID) != null}">
                              <br />
                              <span class="role-revele">
                                (${revelations.get(v.joueurID).printRole()})
                              </span>
                              </c:if>
                          </div>
                        </c:otherwise>
                        </c:choose>
                      </div>
                    </td>

                    <td>
                      <c:choose>
                        <c:when test="${votes.get(v.joueurID) != null}">
                          <c:choose>
                          <c:when test="${bulletins.get(v.joueurID) != null}">
                            <span class="a-vote">${votes.get(v.joueurID).nombreVotes}</span>
                          </c:when>
                          <c:otherwise>
                            <span>${votes.get(v.joueurID).nombreVotes}</span>
                          </c:otherwise>
                          </c:choose>
                        </c:when>
                      </c:choose>
                    </td>

                    <td>
                    <c:if test="${partie.vainqueurs == null && villageois.vivant && v.joueurID != villageois.joueurID}">
                      <c:if test="${not empty canVote && v.vivant && bulletins.get(v.joueurID) == null}">
                        <form action="/loupsgarous/village" method="post">
                            <input type="hidden" name="action" value="voter" />
                            <input type="hidden" name="chatroomID" value="${canVote}" />
                            <input type="hidden" name="cibleID" value="${v.joueurID}" />
                            <button type="submit" class="btn btn-primary">Voter</button>
                        </form>
                      </c:if>
                      <c:if test="${not empty canVote && v.vivant && bulletins.get(v.joueurID) != null}">
                      <!-- On n'autorise plus d'annuler le vote
                        <form action="/loupsgarous/village" method="post">
                            <input type="hidden" name="action" value="voter" />
                            <input type="hidden" name="chatroomID" value="${canVote}" />
                            <input type="hidden" name="cibleID" value="${v.joueurID}" />
                            <input type="hidden" name="annuler" value="1" />
                            <button type="submit" class="btn btn-primary">Annuler vote</button>
                        </form>
                      -->
                      </c:if>
                      <c:if test="${not empty canReveal && v.vivant && revelations.get(v.joueurID) == null}">
                        <form action="/loupsgarous/village" method="post">
                            <input type="hidden" name="action" value="reveler" />
                            <input type="hidden" name="chatroomID" value="${canReveal}" />
                            <input type="hidden" name="reveleID" value="${v.joueurID}" />
                            <button type="submit" class="btn btn-default">Révéler le rôle</button>
                        </form>
                      </c:if>
                      <c:if test="${not empty canCallSpirit && ! v.vivant}">
                        <form action="/loupsgarous/village" method="post">
                            <input type="hidden" name="action" value="appelerEsprit" />
                            <input type="hidden" name="chatroomID" value="${canCallSpirit}" />
                            <input type="hidden" name="appeleID" value="${v.joueurID}" />
                            <button type="submit" class="btn btn-default">Appeler l'esprit</button>
                        </form>
                      </c:if>
                      <c:if test="${not empty canContaminate && v.vivant && v.roleVillageois == Role.HUMAIN}">
                        <form action="/loupsgarous/village" method="post">
                            <input type="hidden" name="action" value="contaminer" />
                            <input type="hidden" name="chatroomID" value="${canContaminate}" />
                            <input type="hidden" name="contamineID" value="${v.joueurID}" />
                            <button type="submit" class="btn btn-default">Contaminer</button>
                        </form>
                      </c:if>
                    </c:if>
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
      </div>
    </div>
  </body>
</html>
