# HubNX para Nintendo Switch 1

Esta pasta contém a interface HTML do aplicativo **homebrew independente** a ser incorporado ao navegador Chromium NX na etapa de build. Não é um binário executável e não instala Android no Switch.

## Objetivo

- Um ícone HubNX no HOME, sem abrir outro aplicativo na interface.
- Abrir diretamente os sites cadastrados.
- Sincronizar pelo Firebase com o HubNX Android após configurar uma conta.
- Manter dados locais entre reinicializações por perfil persistente no cartão SD.

## Estado

Ainda é **código-fonte**, sem `.nro` compilado ou testado. A inclusão do motor Chromium no próprio homebrew exige ambiente devkitPro e build Chromium NX. A Netflix pode ser aberta como site, mas não garantimos reprodução devido a DRM. O CineVS também requer teste real de vídeo.

O código de terceiros e os termos de distribuição do Chromium NX devem ser respeitados.
