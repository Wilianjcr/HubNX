# HubNX — Android + Nintendo Switch 1

**Projeto de código aberto em desenvolvimento.** HubNX é uma biblioteca de sites favoritos com navegador próprio no Android e uma versão homebrew planejada para Nintendo Switch 1 desbloqueado.

## Estado das plataformas

| Plataforma | Situação |
| --- | --- |
| Android | Versão 0.2.2; APK debug compilado e verificado no GitHub Actions; navegador interno; cadastro de sites; atualizador de Releases implementado em código |
| Nintendo Switch | Interface e script de adaptação do Chromium NX publicados; ainda SEM .nro compilado ou testado |
| Sincronização | Código Firebase disponível; configuração de projeto e teste ponta a ponta ainda pendentes |
| Releases | Não há APK público oficial assinado; precisa de chave de assinatura permanente |

## Organização

- android/: interface de favoritos, com cadastro, edição, remoção e sincronização Firebase opcional.
- android-native/: aplicativo Android com WebView nativo de biblioteca e WebView separado para sites externos.
- switch/media-hub/: interface planejada para executar dentro do Chromium NX adaptado.
- tools/: script experimental de adaptação do Chromium NX.
- firebase/: regras de acesso por usuário para Cloud Firestore.
- docs/: testes, Firebase, atualizações e status de projeto.
- version.json: metadados públicos da versão.

## Instalação Android para testar

Acesse a aba Actions e abra o último workflow HubNX Android APK privado (nome legado do workflow de debug). Baixe o artefato HubNX-Android-debug e extraia o app-debug.apk. Esse arquivo é destinado somente a testes, assinado com uma chave de debug gerada no ambiente de CI. A assinatura pode mudar entre builds: instalar por cima de outro APK debug pode falhar.

No Android, ao selecionar um site, o HubNX abre o navegador integrado e oferece Início, Voltar, Avançar, Atualizar e barra de URL. Reprodução de vídeo depende do site e de DRM.

## Atualizações

O aplicativo Android 0.2.2 verifica a última Release pública do HubNX ao abrir. Caso exista uma versão oficial mais recente com o arquivo HubNX-Android.apk, oferece baixar e instalar com autorização do Android. A primeira Release oficial ainda não foi publicada. Releases futuras precisam de assinatura persistente, configurada por secrets protegidos.

Veja docs/RELEASES.md para configurar assinatura Android e publicar a primeira versão oficial. Nenhuma instalação silenciosa é prometida. O atualizador do Switch ainda não está implementado.

## Sincronizar favoritos

Para usar a mesma lista no Android e no Switch, configure Firebase Authentication (Email/Password) e Firestore seguindo docs/FIREBASE.md. O código dos dois clientes está preparado, mas não houve teste integrado no Switch. Favoritos ficam no Firestore por conta e não são publicados no GitHub.

## Nintendo Switch

O pacote inicial ainda não está pronto. O script tools/prepare_chromium_nx.py prepara fontes para build via devkitPro e ambiente Chromium NX, respeitando a licença do projeto original de https://github.com/shodowlo/chromium-nx. A distribuição esperada inclui hubnx.nro, chromium-full.ext4 e profile.ext4.gz. Nada disso foi compilado ou testado no console pelo HubNX.

## Compatibilidade

Netflix pode abrir sua página, mas vídeo protegido por DRM pode não funcionar em navegador experimental ou WebView. Outros sites e players devem ser testados no dispositivo real.

**Segurança:** não publique senhas, keystores ou service account. Credenciais administrativas não devem ficar em android/firebase-config.js ou switch/media-hub/firebase-config.js.
