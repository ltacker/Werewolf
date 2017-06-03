<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>
      Game
    </title>

    <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
    <link href="css/jquery.dataTables.min.css" media="all" rel="stylesheet" type="text/css" />
    <link href="css/picker.classic.css" media="all" rel="stylesheet" type="text/css" />
    <link href="css/bootstrap.min.css" media="all" rel="stylesheet" type="text/css" />
    <script src="js/jquery-3.2.0.min.js" type="text/javascript"></script>
    <script src="js/jquery-ui.min.js" type="text/javascript"></script>
    <script src='js/jquery.dataTables.min.js' type='text/javascript'></script>
    <script src='js/picker.js' type='text/javascript'></script>
    <script src="js/bootstrap.min.js" type="text/javascript"></script>

    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport">
  </head>
  <body class="page-header-fixed bg-1">
    <div class="modal-shiftfix">
      <div class="container-fluid main-content">
        <div class="row">
          <div class="col-lg-6">
            <div class="widget-container fluid-height clearfix">
              <div class="widget-content padded clearfix">
                <h2>Chatroom</h2>

                <div class="post-message">
                  <input class="form-control" placeholder="Message…" type="text"><input type="submit" value="Send">
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-6">
            <div class="widget-container fluid-height clearfix">
              <div class="widget-content padded clearfix">
                <h2>Joueurs</h2>

              </div>
            </div>

            <div class="widget-container fluid-height clearfix">
              <div class="widget-content padded clearfix">
                <h2>Votes</h2>

              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </body>
</html>
