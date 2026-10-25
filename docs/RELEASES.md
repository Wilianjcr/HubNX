# Releases públicas e atualizações HubNX

## Status

O repositório é público. A versão Android 0.2.2 inclui um verificador de Releases públicas no GitHub, mas não há ainda uma Release oficial assinada. O APK gerado por CI é debug e serve para testes, não para distribuição definitiva.

## Assinatura Android: necessária para updates

O Android só aceita atualizar um aplicativo por cima da versão instalada quando a assinatura é a mesma. Para criar um APK oficial atualizável, gere no seu computador uma chave permanente, guarde em lugar seguro e configure estes quatro Repository secrets em GitHub > HubNX > Settings > Secrets and variables > Actions:

- HUBNX_SIGNING_KEYSTORE_B64: arquivo JKS convertido para Base64 (inteiro, sem caminho local).
- HUBNX_SIGNING_STORE_PASSWORD: senha do keystore.
- HUBNX_SIGNING_KEY_ALIAS: alias da chave, como hubnx-release.
- HUBNX_SIGNING_KEY_PASSWORD: senha da chave.

Exemplo para gerar chave usando keytool no computador:

    keytool -genkeypair -v -keystore hubnx-release.jks -storetype PKCS12 -keyalg RSA -keysize 3072 -validity 10000 -alias hubnx-release

NUNCA grave o arquivo JKS, suas senhas ou tokens nos arquivos do repositório, em issues ou em mensagens de chat. Faça backup seguro da chave: perdê-la impede updates das instalações assinadas com ela.

## Publicar APK de Release

1. Confirme o versionName e versionCode em android-native/app/build.gradle.
2. Configure todos os quatro secrets.
3. Crie uma tag de versão, por exemplo v0.2.2, no commit aprovado e envie ao GitHub.
4. O workflow .github/workflows/release-android.yml valida a tag, compila APK assinado e cria Release contendo HubNX-Android.apk e SHA256SUMS.txt.
5. A instalação Android passará a verificar essa Release ao abrir; uma versão nova gera a opção Baixar atualização.

O Android exige autorização do usuário para instalar o novo APK. Não há atualização silenciosa.

## Migração de debug para Release

O APK debug pode ter assinatura diferente do APK oficial. Nessa situação o Android não permite instalar um sobre o outro. Desinstalar o debug pode apagar favoritos locais; não faça isso sem backup ou sincronização conferida. Ainda falta uma exportação nativa de favoritos.

Depois de instalar o primeiro APK assinado definitivo, os próximos APKs assinados com a MESMA chave poderão atualizar mantendo os dados, desde que a aplicação não altere de identificador e a instalação seja aceita.

## Detalhes de funcionamento

- Consulta automática: https://api.github.com/repos/Wilianjcr/HubNX/releases/latest ao abrir o app.
- Só aceita tags no formato vMAJOR.MINOR.PATCH e o asset exato HubNX-Android.apk da própria Release.
- Se a versão for mais recente, solicita confirmação para baixar.
- Download via DownloadManager do Android, com notificação.
- Ao concluir, com o HubNX aberto, oferece a instalação por meio do instalador do Android.
- Se o app foi fechado, abrir manualmente o arquivo APK em Downloads.
- A verificação manual também pode ser acionada na biblioteca.

## Nintendo Switch

Não há NRO compilado ou atualizador Switch pronto. Um updater seguro exige gerar a distribuição Chromium integrada, validar checksum/assinatura, substituir arquivos fora do processo e preservar favoritos e perfil. Não anunciar updates automáticos do Switch antes de testes no console.

## version.json

Este arquivo público é metadado de versões. Modificá-lo não cria uma Release nem instala APK. O atualizador nativo Android usa exclusivamente GitHub Releases.
