# Ativar Conta HubNX (Android e Switch)

## Por que aparece "Configure Firebase para ativar a conta"?

O HubNX não possui um servidor de contas pré-configurado. O app oferece login/sincronização por Firebase, mas você precisa escolher/criar um projeto Firebase e habilitar autenticação e banco de dados. O aplicativo NÃO deve pedir uma senha do GitHub nem uma chave de administrador.

## Passo 1 — Criar projeto Firebase

1. Entre no [Firebase Console](https://console.firebase.google.com/) com sua conta Google.
2. Clique **Adicionar projeto** e escolha um nome, por exemplo "HubNX".
3. Depois que o projeto for criado, abra **Configurações do projeto > Geral**.
4. Em **Seus aplicativos**, clique para adicionar um aplicativo **Web** (ícone </>).
5. Copie os valores `apiKey` (normalmente começa com `AIza`) e `projectId`. **Não copie** `private_key`, `serviceAccount`, tokens de sessão, senhas ou arquivos JSON administrativos.

## Passo 2 — Habilitar login

1. No projeto Firebase abra **Authentication > Sign-in method**.
2. Escolha **E-mail/senha** e ative o provedor.
3. Salve.

## Passo 3 — Habilitar Firestore protegido

1. Abra **Firestore Database > Criar banco de dados**.
2. Escolha uma região adequada. Em ambiente real, procure latência baixa; a região de Firestore costuma ser permanente.
3. Crie o banco, **sem liberar regras de teste públicas em produção**.
4. Na aba **Regras**, substitua pelo conteúdo completo de [firebase/firestore.rules](../firebase/firestore.rules), publicado neste projeto, e clique **Publicar**.
5. Cada usuário somente acessará os favoritos vinculados ao próprio UID. Não publique regras `allow read, write: if true`.

## Passo 4 — Configurar dentro do HubNX 0.2.4

1. Instale o HubNX Android 0.2.4 (ou a versão Switch compilada quando disponível).
2. Toque em **Conta** > **Configurar sincronização**.
3. Cole a **Web API Key** e o **Project ID** do seu projeto.
4. Salve. Essas duas configurações Web são identificadores de frontend, não credenciais secretas de administrador, e ficam no armazenamento local deste aparelho.
5. Toque em **Criar conta** usando e-mail e senha. Se já possuir uma conta Firebase neste projeto, escolha **Entrar**.
6. Repita no outro dispositivo, usando o **mesmo projeto Firebase e a mesma conta**, e toque em **Sincronizar**.

### Observações sobre compartilhar links

- O primeiro login com favoritos locais ainda não sincronizados pode gravá-los no documento da conta; recomendamos cadastrar a lista inicial em somente um dispositivo e confirmar que ela apareceu no outro.
- Quando dois dispositivos fazem alterações simultâneas sem conexão, a última gravação pode prevalecer (ainda não há mesclagem de conflitos).
- Não use a conta de terceiros para compartilhar favoritos privados.
- Faça teste com um link inofensivo antes de guardar um conjunto maior de sites.

## Problemas comuns

- **API_KEY_INVALID:** confira a API Key do aplicativo Web e eventuais restrições da chave.
- **OPERATION_NOT_ALLOWED:** ative **Authentication > E-mail/senha**.
- **PERMISSION_DENIED:** confira o Firestore criado, as regras publicadas e se os dois dispositivos estão autenticados no mesmo projeto.
- **EMAIL_EXISTS:** use a opção **Entrar**.
- **Network failed / erro ao conectar:** confira internet, DNS, data/hora e se a integração REST está acessível pelo navegador do Switch (navegador experimental pode ter limitações de CORS).
- **Sem sincronização:** no HubNX clique **Sincronizar** e leia a mensagem apresentada.

**Estado atual:** a interface de configuração está implementada, mas nenhuma configuração Firebase real foi criada pelo projeto GitHub. É necessário cadastrar o projeto no seu Firebase Console ou conectar o Firebase ao ChatGPT com autorização explícita.
