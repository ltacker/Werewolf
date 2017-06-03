<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>
      Loup-garou
    </title>
      
    <link href="/resources/css/bootstrap.min.css" media="all" rel="stylesheet" type="text/css" />
    <link href="/resources/css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
    <script src="/resources/js/jquery-3.2.0.min.js" type="text/javascript"></script>
    <script src="/resources/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/resources/js/jquery-ui.min.js" type="text/javascript"></script>
    
    <link href="/resources/css/loupsgarous.css" media="all" rel="stylesheet" type="text/css" />
    <script src="/resources/js/login.js" type="text/javascript"></script>
    
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport">
  </head>
  <body class="bg-1">
    <div class="container">
        <div class="row">
          <div class="col-md-4 col-md-offset-4">
            <div class="panel panel-default panel-login">
              <div class="panel-heading">
                <div class="row">
                  <div class="col-xs-6">
                    <a href="#" class="active" id="login-form-link">Connexion</a>
                  </div>
                  <div class="col-xs-6">
                    <a href="#" id="signup-form-link">Inscription</a>
                  </div>
                </div>
                <hr />
              </div>
              <div class="panel-body">
                <a href="/loupsgarous"><img class="img-responsive" src="/resources/img/title.png" /></a>
                <c:if test="${not empty alert}">
                <!-- Rajouter le message transmis -->
                ${alert}
                </c:if>
                <div class="login-form" id='login-form'>
                    <form action="/loupsgarous" method="post" accept-charset="UTF-8">
                      <div class="form-group">
                        <input class="form-control" placeholder="Nom d'utilisateur" type="text" name="login" required>
                      </div>
                      <div class="form-group">
                        <input class="form-control" placeholder="Mot de passe" type="password" name="password" required>
                      </div>
                      <input type="hidden" name="action" value="connexion" />
                      <input type="submit" class="btn btn-primary" value="Se connecter" />
                    </form>
                </div>
                <div class="signup-form" id='signup-form'>
                  <form action="/loupsgarous" method="post" accept-charset="UTF-8">
                    <div class="form-group">
                      <input class="form-control" type="text" placeholder="Entrez votre nom d'utilisateur" name="login" required>
                    </div>
                    <div class="form-group">
                      <input class="form-control" type="password" placeholder="Entrez votre mot de passe" name="password" required>
                    </div>
                    <div class="form-group">
                        <label class="checkbox"><input type="checkbox" required> <span>J'accepte les <a href="https://goo.gl/X82LWZ">termes d'utilisation</a>.</span></label>
                    </div>
                    <input type="hidden" name="action" value="inscription" />
                    <input type="submit" class="btn btn-primary" value="S'inscrire" />
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
  </body>
</html>
