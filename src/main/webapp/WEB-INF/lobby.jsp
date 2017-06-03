<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>
      Lobby
    </title>

    <link href="/resources/css/bootstrap.min.css" rel="stylesheet" />
    <link href="/resources/css/jquery-ui.min.css" rel="stylesheet" />
    <link href="/resources/css/dataTables.bootstrap.min.css" rel="stylesheet" />
    <link href="/resources/css/picker.classic.css" rel="stylesheet" />
    <script src="/resources/js/jquery-3.2.0.min.js" type="text/javascript"></script>
    <script src="/resources/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/resources/js/jquery-ui.min.js" type="text/javascript"></script>
    <script src="/resources/js/jquery.dataTables.min.js" type="text/javascript"></script>
    <script src="/resources/js/dataTables.bootstrap.min.js" type="text/javascript"></script>
    <script src="/resources/js/picker.js" type='text/javascript'></script>

    <link href="/resources/css/loupsgarous.css" rel="stylesheet" />
    <script src="/resources/js/lobby.js" type="text/javascript"></script>

    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport">
  </head>
  <body class="lobby bg-2">

    <!-- Navigation
    <div class="navbar navbar-fixed-top">
      <div class="container-fluid top-bar">
        <div class="pull-right">
          <button class="btn btn-primary"  style="margin-top:8px" >Deconnection</button>
        </div>
        <a class="logo" href="/loupsgarous"><img src="/resources/img/werewolf.png" width="42" height="42" style="margin-top:3px;"></img></a>
      </div>
    </div>-->


    <nav class="navbar navbar-default">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbarLobby">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/loupsgarous" title="Retour au lobby"><img class="img-responsive" src="/resources/img/title.png" /></a>
        </div>
        <div class="collapse navbar-collapse" id="navbarLobby">
          <ul class="nav navbar-nav navbar-right">
            <li><p class="navbar-btn"><a href="/loupsgarous?action=logout" class="btn btn-danger" role="button">Déconnexion</a></p></li>
          </ul>
        </div>
      </div>
    </nav>

    <div class="container-fluid">
        <div class="panel panel-default">
          <div class="panel-header">
            <h1>Lobby</h1>
          </div>
          <div class="panel-body">
           <div class="row">
            <div class="col-lg-6">
              <!-- Listes des parties -->
              <div class="panel panel-default">
                <div class="panel-header">
                  <h2>Liste des parties</h2>
                  <c:if test="${not empty alertList}">
                      <!-- Rajouter le message transmis -->
                      ${alertList}
                  </c:if>
                </div>
                <div class="panel-body">
                  <c:if test="${partiesCourantes.size() > 0}">
                  <div class="panel panel-default" id='parties-courantes'>
                    <!-- Parties rejointes et en cours de déroulement -->
                    <div class="panel-header">
                        <h3>Parties en cours</h3>
                    </div>
                    <div class="panel-body">
                        <table class="table table-striped table-bordered dataTable">
                          <thead>
                          <th>
                            Nom
                          </th>
                          <th>
                            Joueurs
                          </th>
                          <th>
                            Début
                          </th>
                          </thead>
                          <tbody>
                            <c:forEach items="${partiesCourantes.values()}" var="partie">
                            <tr>
                              <td><a href='/loupsgarous/village?id=${partie.id}' title='Rejoindre la partie en cours'>${partie.nomPartie}</a></td>
                              <td>${partie.nombreParticipants}</td>
                              <td> <%-- ${partie.dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))} --%> </td>
                            </tr>
                            </c:forEach>
                          </tbody>
                        </table>
                    </div>
                  </div>
                  </c:if>

                  <c:if test="${! partiesAttente.isEmpty()}">
                  <div class="panel panel-default" id='parties-attente'>
                    <!-- Parties rejointes mais pas encore démarrées -->
                    <div class="panel-header">
                        <h3>Parties en attente de démarrage</h3>
                    </div>
                    <div class="panel-body">
                        <table class="table table-striped table-bordered dataTable">
                          <thead>
                          <th>
                            Nom
                          </th>
                          <th>
                            Joueurs
                          </th>
                          <th>
                            Début
                          </th>
                          </thead>
                          <tbody>
                            <c:forEach items="${partiesAttente.values()}" var="partie">
                            <tr>
                              <td>${partie.nomPartie}</td>
                              <td>
                                ${partie.nombreParticipants}/${partie.maxParticipants}
                                <c:if test="${partie.nombreParticipants < partie.minParticipants}">(${partie.minParticipants} requis)</c:if>
                                </td>
                                <td> <%-- ${partie.dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))} --%> </td>
                            </tr>
                            </c:forEach>
                          </tbody>
                        </table>
                    </div>
                  </div>
                  </c:if>

                  <c:if test="${! partiesDisponibles.isEmpty()}">
                  <div class="panel panel-default" id='parties-disponibles'>
                    <!-- Parties pas encore démarrées et non rejointes -->
                    <div class="panel-header">
                        <h3>Parties disponibles</h3>
                    </div>
                    <div class="panel-body">
                        <table class="table table-striped table-bordered dataTable">
                          <thead>
                          <th>
                            Nom
                          </th>
                          <th>
                            Joueurs
                          </th>
                          <th>
                            Début
                          </th>
                          </thead>
                          <tbody>
                            <c:forEach items="${partiesDisponibles.values()}" var="partie">
                                <tr>
                                  <td>
                                    <c:if test="${partie.nombreParticipants < partie.maxParticipants}" var="pleine">
                                        <a href="/loupsgarous?action=join&id=${partie.id}" title="Rejoindre la partie">
                                    </c:if>
                                        ${partie.nomPartie}
                                        <c:if test="${pleine}"></a></c:if>
                                    </td>
                                    <td>
                                    ${partie.nombreParticipants}/${partie.maxParticipants}
                                    <c:if test="${partie.nombreParticipants < partie.minParticipants}">(${partie.minParticipants} requis)</c:if>
                                    </td>
                                    <td> <%-- ${partie.dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))} --%> </td>
                                </tr>
                            </c:forEach>
                          </tbody>
                        </table>
                    </div>
                  </div>
                  </c:if>

                  <c:if test="${! partiesTerminees.isEmpty()}">
                  <div class="panel panel-default" id='parties-terminees'>
                    <!-- Parties pas encore démarrées et non rejointes -->
                    <div class="panel-header">
                        <h3>Parties terminées</h3>
                    </div>
                    <div class="panel-body">
                        <table class="table table-striped table-bordered dataTable">
                          <thead>
                          <th>
                            Nom
                          </th>
                          <th>
                            Joueurs
                          </th>
                          <th>
                            Début
                          </th>
                          </thead>
                          <tbody>
                            <c:forEach items="${partiesTerminees.values()}" var="partie">
                            <tr>
                              <td><a href='/loupsgarous/village?id=${partie.id}' title='Revoir la partie'>${partie.nomPartie}</a></td>
                              <td>${partie.nombreParticipants}</td>
                              <td> <%-- ${partie.dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))} --%> </td>
                            </tr>
                            </c:forEach>
                          </tbody>
                        </table>
                    </div>
                  </div>
                  </c:if>

                </div>
              </div>
            </div>

            <div class="col-lg-6">
              <!-- Création d'une partie -->
              <div class="panel panel-default">
                <div class="panel-header">
                  <h2>Créer une partie</h2>
                  <c:if test="${not empty alertCreate}">
                      <!-- Rajouter le message transmis -->
                      ${alertCreate}
                  </c:if>
                </div>
                <div class="panel-body">
                  <form action="/loupsgarous" method="post" class="form-horizontal">
                    <div class="form-group row">
                      <label class="col-sm-2" for="nomPartie">Nom :</label>
                      <div class="col-sm-10">
                        <input class="form-control" placeholder="MaPartie" type="text" name="nomPartie" id="nomPartie" required>
                      </div>
                    </div>

                    <div class="slider-container">
                      <p>
                        <label for="slider-user-amount">Nombre de joueurs</label>
                        <span id="slider-user-amount" class="pull-right"></span>
                      </p>
                      <div id="slider-user"></div>
                      <input type="hidden" name="minParticipants" id="minParticipants" value="5"/>
                      <input type="hidden" name="maxParticipants" id="maxParticipants" value="20"/>
                    </div>

                    <div class="slider-container">
                      <p>
                        <label for="slider-lg-amount">Proportion de loups-garous</label>
                        <span id="slider-lg-amount" class="pull-right"></span>
                      </p>
                      <div id="slider-lg"></div>
                      <input type="hidden" name="proportionLG" id="proportionLG" value="33"/>
                    </div>

                    <div class="slider-container">
                      <p>
                        <label for="slider-contamination-amount">Probabilité contamination</label>
                        <span id="slider-contamination-amount" class="pull-right"></span>
                      </p>
                      <div id="slider-contamination"></div>
                      <input type="hidden" name="probaContamination" id="probaContamination" value="0"/>
                    </div>

                    <div class="slider-container">
                      <p>
                        <label for="slider-insomnie-amount">Probabilité insomnie</label>
                        <span id="slider-insomnie-amount" class="pull-right"></span>
                      </p>
                      <div id="slider-insomnie"></div>
                      <input type="hidden" name="probaInsomnie" id="probaInsomnie" value="0"/>
                    </div>

                    <div class="slider-container">
                      <p>
                        <label for="slider-voyance-amount">Probabilité voyance</label>
                        <span id="slider-voyance-amount" class="pull-right"></span>
                      </p>
                      <div id="slider-voyance"></div>
                      <input type="hidden" name="probaVoyance" id="probaVoyance" value="0"/>
                    </div>

                    <div class="slider-container">
                      <p>
                        <label for="slider-spiritisme-amount">Probabilité spiritisme</label>
                        <span id="slider-spiritisme-amount" class="pull-right"></span>
                      </p>
                      <div id="slider-spiritisme"></div>
                      <input type="hidden" name="probaSpiritisme" id="probaSpiritisme" value="0"/>
                    </div>

                    <div class="form-group row">
                      <label class="control-label col-sm-3" for="dateDebut">Date de début</label>
                      <div class="col-sm-3">
                        <input class="form-control datepicker" type="text" name="dateDebut" id="dateDebut" required>
                      </div>

                      <label class="control-label col-sm-3" for="heureDebut">Heure de début :</label>
                      <div class="col-sm-3">
                        <input class="form-control timepicker" type="text" name="heureDebut" id="heureDebut" required>
                      </div>
                    </div>

                    <div class="form-group row">
                      <label class="control-label col-sm-3" for="dureeJour">Durée d'une journée (en minutes)</label>
                      <div class="col-sm-3">
                        <input class="form-control" type="number" value="840" name="dureeJour">
                      </div>
                      <label class="control-label col-sm-3" for="dureeNuit">Durée d'une nuit (en minutes)</label>
                      <div class="col-sm-3">
                        <input class="form-control" type="number" value="600" name="dureeNuit">
                      </div>
                    </div>

                    <div class="form-group text-center">
                      <input type="hidden" name="action" value="creerServeur">
                      <button class="btn btn-primary" type="submit">Créer la partie</button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
         </div>
        </div>
      </div>
  </body>
</html>
