<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Verificação de E-mail</title>
        <style>
            body { font-family: Arial, sans-serif; }
            .container {
                padding: 20px;
                border: 1px solid #eee;
                border-radius: 10px;
                max-width: 600px;
                margin: auto;
                background-color: #fafafa;
            }

            .btn {
                display: inline-block;
                padding: 12px 20px;
                margin-top: 10px;
                font-size: 16px;
                color: #fff;
                text-decoration: none;
                border-radius: 5px;
                border: 2px solid #007bff;
            }

            .btn:hover {
                background-color: #007bff;
                color: white;
                border: 2px solid white;
            }

            .note {
                margin-top: 30px;
                font-size: 0.9em;
                color: #888;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2>🔐 Verificação de E-mail</h2>
            <p>Olá! Verifique seu e-mail clicando no botão abaixo:</p>

            <a class="btn" href="${link}" target="_blank">Verificar E-mail</a>

            <p class="note">
                ⚠️ Se você não solicitou esta verificação, ignore esta mensagem.
            </p>
        </div>
    </body>
</html>
