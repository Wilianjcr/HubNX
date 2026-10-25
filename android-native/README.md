# HubNX Android — APK de teste

App Android independente com WebView Asset Loader. A interface HTML em `android/` é incorporada ao APK como ativo local, sem depender de GitHub Pages e sem disponibilizar a página publicamente.

## Compilar localmente

Requer Android SDK, JDK 17, Gradle 8.10 e conectividade para baixar dependências. Dentro de `android-native/`, copie os arquivos de `../android/` para `app/src/main/assets/`. Execute `gradle :app:assembleDebug`.

## GitHub Actions

O workflow `.github/workflows/build-android-private.yml` faz a compilação e publica o APK de teste apenas como **artefato privado do GitHub Actions**. No repositório, acesse **Actions > HubNX Android APK privado > execução mais recente > Artifacts** e baixe `HubNX-Android-debug`. É necessário estar autenticado com permissão no repositório.

O APK é assinado com chave padrão de **debug**, não está pronto para distribuição pública. Os usuários deverão permitir a instalação manual de aplicativo desconhecido no Android e verificar a origem do APK.

## Observações

Ao abrir um site da lista no Android, ele é carregado no navegador WebView **interno** ao HubNX, sem iniciar Chrome. A barra possui Início, Voltar, Avançar, Atualizar, endereço e Ir. Vídeo em tela cheia é solicitado ao player do site quando suportado. Alguns sites (especialmente Netflix) podem bloquear autenticação ou reprodução em WebView por requisitos de DRM. Login/sincronização com Firebase só funcionam depois da configuração de `android/firebase-config.js` e `firebase/firestore.rules`.
