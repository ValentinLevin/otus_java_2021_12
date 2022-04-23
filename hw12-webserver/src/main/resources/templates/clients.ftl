<html xmlns="http://www.w3.org/1999/xhtml" lang="ru">
<head>
    <title>Клиенты</title>
    <script>
        function getClientById() {
            const clientIdTextBox = document.getElementById('clientIdTextBox');
            const clientDataContainer = document.getElementById('clientDataContainer');
            const id = clientIdTextBox.value;
            fetch('api/clients/' + id)
                .then(response => response.json())
                .then(client => clientDataContainer.innerHTML = JSON.stringify(client));
        }
    </script>
</head>
<body>
    <form action="/api/clients" method="post">
        <div>
            <label for="name">Имя клиента</label><input type="text" name="name" id="name">
        </div>
        <div>
            <label for="address">Адрес</label><input type="text" name="address" id="address">
        </div>
        <div>
            <label for="phone">Номер телефона</label><input type="text" name="phone" id="phone">
        </div>
        <input type="submit" value="Добавить клиента">
    </form>

    <h4>Список клиентов</h4>
    <table style="width: 400px" border="1" cellspacing="0">
        <thead>
            <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Address</th>
                <th>Phones</th>
            </tr>
        </thead>
        <tbody>
        <#list clients as client>
            <tr>
                <td>${client.id}</td>
                <td>${client.name}</td>
                <td>${client.address.street}</td>
                <td>
                    <#list client.phones as phone>
                        ${phone.number}<#sep><br/></#sep>
                    </#list>
                </td>
            </tr>
        <#else>
            <tr>
                <td colspan="4">No clients</td>
            </tr>
        </#list>
        </tbody>
    </table>
    <hr/>
    <h4>Получить клиента по id</h4>
    <input type="number" id = "clientIdTextBox" placeholder="Введите id клиента">
    <button onclick="getClientById()">Получить</button>
    <pre id = "clientDataContainer"></pre>

</body>
</html>