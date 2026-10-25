# HubNX Android 0.2.6

O HubNX Android incorpora uma interface web local ao APK e abre os sites em um navegador WebView interno separado, com barra de endereço, voltar, avançar, atualizar, voltar à biblioteca e vídeos em tela cheia quando suportados.

## APK de testes via GitHub Actions

O workflow de CI do GitHub compila automaticamente o app-debug.apk. Abra Actions, escolha a compilação mais recente que terminou com Success e baixe o artefato HubNX-Android-debug. Extraia o ZIP e instale o APK em um aparelho Android.

ATENÇÃO: é um APK debug; o certificado gerado em cada executor do GitHub pode mudar, então upgrades entre arquivos debug podem falhar por assinatura diferente. Não desinstale uma versão com favoritos locais sem backup ou sincronização validada.

## Como funciona a verificação de atualização

O aplicativo consulta automaticamente, ao abrir, a última Release do repositório público GitHub. Se existir uma versão mais nova e um arquivo HubNX-Android.apk, ele mostrará uma caixa de diálogo para baixar. O Android requer confirmação do usuário para instalar. O botão Verificar versão permite uma consulta manual.

A primeira Release pública assinada não foi criada. Antes de fazê-la, configure assinatura Android permanente em GitHub Secrets conforme docs/RELEASES.md. Sem isso, downloads futuros podem não instalar por cima da versão anterior.

## Firebase

A sincronização depende de configurar um projeto Firebase e aplicar regras privadas por usuário, seguindo docs/FIREBASE.md. Apenas preparar o código não sincroniza os sites automaticamente.

## Limitações

- Sites com DRM podem bloquear vídeo e login em WebView, especialmente Netflix.
- Sites com pop-ups, downloads especiais ou autenticação externa podem requerer ajustes.
- O projeto Switch ainda não está instalado e necessita compilação.

## Orientação da tela

O controle **⤾** está disponível no navegador integrado e **⤾ Tela** na biblioteca. Alterne entre **Auto**, **Retrato** e **Paisagem**. A orientação escolhida fica em SharedPreferences, e a atividade usa configChanges para adaptar o conteúdo sem apagar a navegação. Funcionalidade compilada no código, sujeita a teste no aparelho real.
