# ToDo List App with Encryption

## Descrição do Projeto

Este é um aplicativo de lista de tarefas (ToDo List) para Android desenvolvido em Kotlin com Compose que oferece:
- Autenticação de usuário (login e registro)
- Armazenamento seguro de credenciais e tarefas usando criptografia AES
- Interface moderna e intuitiva
- Filtros para organizar tarefas (todas, pendentes, completadas)
- Opções para marcar tarefas como concluídas, editar e excluir
- Data picker para seleção de datas

O aplicativo utiliza criptografia de ponta a ponta para proteger os dados do usuário, incluindo:
- Criptografia das senhas no banco de dados
- Criptografia do conteúdo das tarefas
- Armazenamento seguro da chave de criptografia usando EncryptedSharedPreferences

## Instalação e Execução
### Para usuários finais:
1. Baixe o arquivo APK mais recente na seção de releases
2. Habilite a instalação de fontes desconhecidas nas configurações do seu Android
3. Instale o APK
4. Execute o aplicativo

### Para desenvolvedores:
1. Clone o repositório: git clone https://github.com/babi2707/ToDoListAndroidStudio.git
2. Abra o projeto no Android Studio
3. Configure um dispositivo virtual ou conecte um dispositivo físico com depuração USB habilitada
4. Execute o aplicativo

## Requisitos do Sistema
- Android 8.0 (API nível 26) ou superior
- Android Studio (para desenvolvimento)

## Como usar
1. Primeiro acesso:
   - Crie uma conta na tela de registro
   - Faça login com suas credenciais
2. Adicionando tarefas:
   - Toque no campo de data para selecionar uma data
   - Digite a descrição da tarefa
   - Toque em "Adicionar"
3. Gerenciando tarefas:
   - Toque no círculo para marcar/desmarcar como concluída
   - Toque no ícone de lixeira para excluir
   - Use os filtros no topo para visualizar diferentes categorias
4. Gerenciamento em massa:
   - "Completar tudo" - marca todas as tarefas como concluídas
   - "Excluir tudo" - remove todas as tarefas
5. Logout:
   - Toque no botão "Sair" no final da tela para deslogar

## Segurança
O aplicativo implementa várias medidas de segurança:
- Todas as senhas são criptografadas antes de serem armazenadas
- As tarefas são criptografadas no banco de dados
- A chave de criptografia é armazenada de forma segura usando Android KeyStore

## Tecnologias Utilizadas
- Kotlin
- Jetpack Compose
- SQLite (banco de dados local)
- Android Security Crypto (para criptografia)

## Capturas de Tela
<p align="center"> <img src="Screenshots/login_screen.png" width="200" alt="Tela de Login"> <img src="Screenshots/task_screen.png" width="200" alt="Lista de Tarefas"> </p>
