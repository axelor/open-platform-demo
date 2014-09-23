<%--

    Axelor Business Solutions

    Copyright (C) 2012-2014 Axelor (<http://axelor.com>).

    This program is free software: you can redistribute it and/or  modify
    it under the terms of the GNU Affero General Public License, version 3,
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>

<%@ page import="com.axelor.i18n.I18n" %>

<%
String labelAbout = I18n.get("About");
String labelContact = I18n.get("Contact");
String labelAdmin = I18n.get("Administrator");
String labelDemo = I18n.get("Demo User");
String labelLogin = I18n.get("Log in");
%>

<header>
  <div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
      <div class="container-fluid">
        <a class="brand-logo" href="">
          <img src="img/axelor-logo.png">
        </a>
        <ul class="nav">
          <li><a href="http://www.axelor.com"><%= labelAbout %></a></li>
          <li><a href="http://www.axelor.com/contact"><%= labelContact %></a></li>
        </ul>
      </div>
    </div>
  </div>
</header>

<div class="container-fluid">
  <div class="row-fluid defined-users">
    <div class="panel offset3 span3">
      <div class="panel-body">
        <div class="span12 text-center">
          <i class="fa fa-user fa-3x"></i>
          <p><strong><%= labelAdmin %></strong></p>
          <button class="btn btn-default btn-block" data-user="admin"><%= labelLogin %></button>
        </div>
      </div>
    </div>
    <div class="panel span3">
      <div class="panel-body">
        <div class="span12 text-center">
          <i class="fa fa-user fa-3x"></i>
          <p><strong><%= labelDemo %></strong></p>
          <button class="btn btn-default btn-block" data-user="demo"><%= labelLogin %></button>
        </div>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
  $(function () {
    $('.defined-users button.btn').click(function (e) {
      var user = $(e.target).data("user");
      $('#usernameId,#passwordId').val(user);
      $('#login-form').submit();
    });
  });
</script>
