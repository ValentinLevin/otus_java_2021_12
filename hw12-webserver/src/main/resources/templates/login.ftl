<html>
<head>
    <title>Авторизация</title>
</head>
<body>
<form action="/login" method="post">
    <input type="hidden" name="returnUri" value="${returnUri}">
    <div>
        <label for="username">Имя пользователя</label>
        <input type="text" name="username" id="username" value="admin">
    </div>
    <div>
        <label for="password">Пароль</label>
        <input type="password" name="password" id="password" value="12345">
    </div>
    <input type="submit" value="Войти">
</form>
</body>
</html>