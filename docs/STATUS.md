# Estado verificado do HubNX — 25/10/2026

- Visibilidade: PUBLIC.
- Android: v0.2.6, código da verificação automática e checagens periódicas publicados.
- CI Android: compilação de APK debug passou para a versão 0.2.6. Ainda não testado no telefone real.
- GitHub Release oficial: não publicada. Bloqueio: secrets protegidos de assinatura Android permanente.
- Android: a instalação de APK exige consentimento do sistema; não há auto-instalação silenciosa.
- Switch: script experimental de adaptação, interface com notificação de versões preparada, mas SEM NRO compilado.
- Ambiente local: sem Docker/devkitPro para compilar o homebrew Switch.
- Firebase: sem projeto real configurado; sincronização Android ↔ Switch não validada.
- Downloads oficiais e aviso de atualização real: não testados, pois não há Release assinada.
- O repositório público não inclui credenciais privadas: arquivos Firebase Web permanecem com valores de exemplo.

Links: README.md, docs/RELEASES.md, docs/FIREBASE.md, docs/TESTES.md.

## Compilação NRO em andamento

- Adicionado workflow GitHub Actions com Docker/devkitPro para gerar pacote Switch independente.
- Primeiro build compilou LKL mas falhou por libavcodec-dev ausente no container Linux. Dependências corrigidas no workflow.
- Nova tentativa de build foi iniciada; o NRO ainda não é um artefato validado até execução bem-sucedida.
- Foi introduzida identidade HubNX no NRO e HTML, e pacote de testes do Android v0.2.6 com menu Backup e configuração Firebase in-app.

## Orientação (25/10/2026)
- Android 0.2.6: controles Auto/Retrato/Paisagem implementados na biblioteca e no navegador interno, com persistência local e preservação da WebView durante alterações de orientação, ainda aguardando teste no dispositivo.
- Switch: opção de layout Amplo/Compacto, sem rotação da tela física.
- Última compilação Switch concluída: falhou por headers de libavcodec no compilador ARM64; workflow corrigido para instalar dependências FFmpeg arm64 e testar headers antes de compilar kernel. Nova tentativa iniciada, sem NRO final confirmado.
