# HubNX — Android + Nintendo Switch

O [HubNX](https://github.com/Wilianjcr/HubNX) é um projeto público em desenvolvimento para reunir sites favoritos, navegar dentro do aplicativo Android e futuramente sincronizar a biblioteca com um homebrew próprio no Nintendo Switch 1.

## Situação em 25/10/2026

| Módulo | Situação |
|---|---|
| Android 0.2.4 | Código publicado; APK debug compilado pelo GitHub Actions; navegador integrado |
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


## Conta HubNX 0.2.4 — configuração no próprio app

O botão Conta > Configurar sincronização permite informar a Web API Key e o Project ID de um projeto Firebase. Não é necessário recompilar o APK para configurar esses dados locais. O projeto Firestore/Auth ainda precisa existir e ter regras seguras habilitadas. Veja [ATIVAR-CONTA.md](docs/ATIVAR-CONTA.md).

## Ícone exclusivo

O emblema oficial está em [branding/hubnx-icon.svg](branding/hubnx-icon.svg), aplicado à interface e ao ícone do launcher Android. A preparação do Switch renderiza esse símbolo para o ícone JPEG do NRO.

## Compilação experimental Nintendo Switch

O workflow [HubNX Switch — compilar NRO experimental](.github/workflows/build-switch-nro.yml) configura Docker e devkitPro para tentar produzir a distribuição completa. O workflow [validar adaptação](.github/workflows/check-switch.yml) já confere a estrutura do código, mas não gera executável. **Só instalar quando o build gerar os arquivos e forem validados em testes físicos.** O script de atualização do Chromium NX preserva a biblioteca integrada, mas vídeo/DRM ainda dependem do motor experimental.

## Atualizar sem desinstalar

No Android oficial, as versões novas devem ser assinadas com a MESMA chave permanente; o aplicativo consulta Releases e oferece o download, mas o Android requer confirmação do instalador. No Switch, o atalho deve continuar apontando a `sd:/switch/hubnx/hubnx.nro`, e futuras versões podem substituir o executável sem remover o forwarder e sem apagar o perfil. Atualização de arquivo NRO pelo próprio homebrew ainda necessita um updater nativo seguro.
