<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Jogos com Desconto!</title>
        <style>
            body {
                font-family: Arial, sans-serif;
            }

            .game-list {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }

            .game-list th, .game-list td {
                border: 1px solid #ccc;
                padding: 10px;
                text-align: left;
            }

            .game-list th {
                background-color: #f2f2f2;
            }

            .discount {
                color: green;
                font-weight: bold;
            }

            .game-image {
                max-width: 100px;
            }
        </style>
    </head>
    <body>
        <h2>🎮 Jogos com Desconto!</h2>
        <p>Confira os jogos em promoção abaixo:</p>

        <table class="game-list">
            <thead>
            <tr>
                <th>Imagem</th>
                <th>Título</th>
                <th>Preço Original</th>
                <th>Preço com Desconto</th>
                <th>Desconto (%)</th>
                <th>Plataforma</th>
            </tr>
            </thead>
            <tbody>
            <#list games as game>
                <tr>
                    <td><img src="${game.image}" alt="${game.title}" class="game-image"/></td>
                    <td><a href="${game.url}" target="_blank">${game.title}</a></td>
                    <td>R$ ${game.initialPrice?string["0.00"]}</td>
                    <td class="discount">R$ ${game.discountPrice?string["0.00"]}</td>
                    <td>${game.discountPercent}%</td>
                    <td>
                        <#if game.platform == "STEAM">
                            Steam
                        <#else>
                            Epic Games Store
                        </#if>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>

        <p style="font-size: 0.9em; color: #888; margin-top: 40px;">
            ⚠️ Nota: Alguns links para jogos da <strong>Epic Games Store</strong> podem não funcionar corretamente.
        </p>
    </body>
</html>
