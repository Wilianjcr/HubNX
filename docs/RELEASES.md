# HubNX — atualização oficial (Android e Switch)

## Android 0.2.3 — verificação automática

O app confere uma Release pública ao abrir/retornar (intervalo local de 10 minutos para não sobrecarregar a API). Em segundo plano, WorkManager solicita uma checagem periódica a cada hora quando há rede: o sistema pode adiar conforme bateria, Doze e restrições. Quando encontra uma versão mais recente, mostra um diálogo no app ou uma notificação única para aquela versão. No Android 13+, autorize as notificações quando solicitado.

Ao escolher **Atualizar**, o HubNX inicia download do APK oficial. O Android exige que o usuário autorize a instalação; APK fora da loja não se instala silenciosamente. Se fechar o app durante download, abra o arquivo na pasta Downloads.

A consulta usa GitHub Releases públicas; o simples version.json não atualiza binários e uma Release **não foi publicada ainda**.

## Assinatura definitiva obrigatória

Para futuras atualizações sobre a mesma instalação, todos os APKs oficiais precisam da mesma assinatura. Um APK debug baixado do CI não deve ser considerado versão final e pode ter assinatura diferente das Releases.

Para configurar assinatura sem expor credenciais em repositório público, em um computador Windows com **Java JDK 17** e **GitHub CLI (gh)**:

1. Autentique o GitHub CLI via **gh auth login**.
2. Baixe as fontes do repositório ou somente tools/setup-android-signing.ps1.
3. No PowerShell, execute o script local, leia os avisos e configure os quatro GitHub secrets.
4. Faça backup offline e seguro de hubnx-release.p12 e sua senha. **Nunca publique o arquivo ou as senhas no GitHub nem no chat.**
5. Acesse [Actions — HubNX Android Release](https://github.com/Wilianjcr/HubNX/actions/workflows/release-android.yml) e selecione **Run workflow** na branch main para criar a primeira Release oficial assinada. A ação só publica se os quatro secrets de assinatura existirem e a compilação passar.

O script cria/configura estes GitHub Actions secrets:
- HUBNX_SIGNING_KEYSTORE_B64
- HUBNX_SIGNING_STORE_PASSWORD
- HUBNX_SIGNING_KEY_ALIAS
- HUBNX_SIGNING_KEY_PASSWORD

O APK público terá nome HubNX-Android.apk, em Releases do GitHub. A assinatura é validada pelo Android ao instalar. Se já tiver instalado o APK debug, uma instalação sobreposta pode falhar. **Não desinstale a versão de testes com favoritos locais sem backup/sincronização validada.**

## Switch

A interface de Switch consulta version.json automaticamente ao iniciar e a cada hora; mostrará um aviso quando houver versão Switch nova com NRO disponível. Ainda **não existe NRO compilado nem instalador automático nativo**. Para cumprir o objetivo, faltam compilar o Chromium adaptado com devkitPro, testar no console e desenvolver instalador seguro que substitua o executável fora do processo, preservando perfis.

## Privacidade

Não inclua dados privados no version.json nem em Releases públicas. Os favoritos e senhas de usuários não são enviados ao GitHub.
