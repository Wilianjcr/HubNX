# Estado verificado do HubNX — 25/10/2026

- Visibilidade: PUBLIC.
- Android: v0.2.3, código da verificação automática e checagens periódicas publicados.
- CI Android: compilação de APK debug passou para a versão 0.2.3. Ainda não testado no telefone real.
- GitHub Release oficial: não publicada. Bloqueio: secrets protegidos de assinatura Android permanente.
- Android: a instalação de APK exige consentimento do sistema; não há auto-instalação silenciosa.
- Switch: script experimental de adaptação, interface com notificação de versões preparada, mas SEM NRO compilado.
- Ambiente local: sem Docker/devkitPro para compilar o homebrew Switch.
- Firebase: sem projeto real configurado; sincronização Android ↔ Switch não validada.
- Downloads oficiais e aviso de atualização real: não testados, pois não há Release assinada.
- O repositório público não inclui credenciais privadas: arquivos Firebase Web permanecem com valores de exemplo.

Links: README.md, docs/RELEASES.md, docs/FIREBASE.md, docs/TESTES.md.
