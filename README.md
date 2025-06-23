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

