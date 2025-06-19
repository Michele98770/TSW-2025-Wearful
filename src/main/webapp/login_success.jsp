<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Login Effettuato</title>
    <link rel="icon" type="image/png" href="./img/small_logo.png">
    <link rel="stylesheet" href="./stylesheets/stilefooter.css">
    <link rel="stylesheet" href="./stylesheets/stileheader.css">
    <link rel="stylesheet" href="./stylesheets/common.css">
    <link rel="stylesheet" href="./stylesheets/form.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>
    <style>
        .success-container {
            max-width: 500px;
            margin: 100px auto;
            padding: 40px;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .success-container h2 {
            color: #28a745;
            font-size: 2.2em;
            margin-bottom: 20px;
        }
        .success-container p {
            font-size: 1.1em;
            color: #555;
            margin-bottom: 30px;
        }
        .success-container .back-home-link {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 12px 25px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: 600;
            transition: background-color 0.3s ease;
        }
        .success-container .back-home-link:hover {
            background-color: #0056b3;
        }
        body {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        .main-content-wrapper {
            flex-grow: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        @media (max-width: 768px) {
            .success-container {
                margin: 50px 20px;
                padding: 30px;
            }
            .success-container h2 {
                font-size: 1.8em;
            }
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp" />

<div class="main-content-wrapper">
    <div class="success-container">
        <h2>Successo!</h2>
        <p><%= request.getAttribute("loginStatus") %></p>
        <a href="<%= request.getContextPath() %>/" class="back-home-link">Torna alla Home</a>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script src="./scripts/menu.js"></script>
</body>
</html>