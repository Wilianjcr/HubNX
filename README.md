# HubNX — Android + Nintendo Switch

O [HubNX](https://github.com/Wilianjcr/HubNX) é um projeto público em desenvolvimento para reunir sites favoritos, navegar dentro do aplicativo Android e futuramente sincronizar a biblioteca com um homebrew próprio no Nintendo Switch 1.

## Situação em 25/10/2026

| Módulo | Situação |
|---|---|
| Android 0.2.3 | Código publicado; APK debug compilado pelo GitHub Actions; navegador integrado |
| Atualizações Android | Verifica automaticamente ao abrir/retornar; WorkManager agenda consulta aproximadamente a cada hora; apresenta diálogo e notificação quando nova Release aparece |
| Instalar atualizações | Baixa APK sob ação do usuário; Android requer confirmação de instalação |
| Release oficial | Ainda não existe, falta assinatura permanente protegida por secrets do GitHub |
| Nintendo Switch | Interface e adaptação Chromium NX, mas ainda sem compilação do arquivo `.nro` |
| Sincronização favoritos | Código Firebase preparado; exige ativação de um projeto e teste de dois dispositivos |

## Instalar Android de testes

Na aba [Actions](https://github.com/Wilianjcr/HubNX/actions/workflows/build-android-private.yml), abra uma execução de sucesso e baixe o artifact **HubNX-Android-debug**. Extraia o APK do ZIP. A assinatura debug pode mudar entre execuções; **não desinstale uma versão com favoritos locais sem backup**.

## Publicar APK oficial e permitir atualizações

O usuário precisa configurar uma chave de assinatura Android permanente, sem publicá-la no GitHub. No Windows, o script `tools/setup-android-signing.ps1` automatiza isso com Java JDK 17 + GitHub CLI autenticado. Veja [RELEASES.md](docs/RELEASES.md).

Após configurar os secrets, abra a ação [HubNX Android Release](https://github.com/Wilianjcr/HubNX/actions/workflows/release-android.yml) e execute **Run workflow** em main. Ela gera e publica `HubNX-Android.apk` com assinatura permanente, sob tag da versão Android.

O aplicativo Android consulta [GitHub Releases](https://github.com/Wilianjcr/HubNX/releases/latest) ao abrir ou voltar ao app. Quando houver versão nova, oferece atualizar na tela. Checagens em segundo plano ocorrem aproximadamente a cada hora, mas podem atrasar por decisões de bateria/rede do sistema. A notificação requer permissão no Android 13+.

## Switch

Não há ainda um `.nro` compilado. O script `tools/prepare_chromium_nx.py` prepara alterações experimentais nas fontes de [Chromium NX](https://github.com/shodowlo/chromium-nx) que requerem toolchain Docker/devkitPro e teste físico. O conteúdo da interface de Switch inclui a verificação de manifesto público ao iniciar, mas **não possui instalador automático de NRO**.

## Firebase

Leia [FIREBASE.md](docs/FIREBASE.md) para cadastrar projeto Authentication Email/Password e Firestore. Os favoritos são privados por conta, não são armazenados no GitHub. Nenhum teste de sincronização real entre Android e Switch foi concluído.

## Segurança e limitações

Não adicione keystores, senhas, tokens privados ou service accounts ao repositório. A Netflix e serviços com DRM podem bloquear o player em WebView ou homebrew experimental. Fique atento a sites desconhecidos.
