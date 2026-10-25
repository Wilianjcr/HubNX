# HubNX — Nintendo Switch 1

## Objetivo
Aplicativo homebrew independente no menu HOME, usando o motor Chromium NX incorporado ao próprio pacote, com biblioteca de favoritos, navegação dentro do HubNX, Joy-Cons, tela sensível ao toque, som e vídeo quando o site for compatível.

## Identidade
O ícone exclusivo está em `branding/hubnx-icon.svg`. A compilação gera um JPEG para a metadata NRO via `tools/render-nro-icon.py`.

## Compilação (experimental)
Existem duas ações GitHub:

- **HubNX Switch — validar adaptação**: valida os patches, sintaxe e interface; não compila NRO.
- **HubNX Switch - compilar NRO experimental**: tenta construir um runtime Chromium/Linux com Docker + devkitPro e criar a distribuição completa.

Acesse [GitHub Actions](https://github.com/Wilianjcr/HubNX/actions/workflows/build-switch-nro.yml), escolha **Run workflow**, selecione a branch main. Se a execução terminar com sucesso, baixe o artifact `HubNX-Switch-NRO-experimental`.

O pacote esperado contém `hubnx.nro`, `chromium-full.ext4` e `profile.ext4.gz`. Antes de instalar, verifique que os três arquivos estão presentes. Não existe uma Release Switch oficial nem garantia de funcionamento até testes físicos.

## Instalação do pacote compilado
1. Extraia a pasta `hubnx` no cartão SD: `sd:/switch/hubnx/`.
2. Abra o Sphaira/Homebrew Menu e execute `hubnx.nro` com memória completa.
3. Opcionalmente, crie um forwarder para o menu HOME mantendo esse caminho fixo e usando o modo de endereçamento exigido pelo Chromium NX.
4. O Chromium integrado deverá abrir diretamente a biblioteca local `file:///usr/share/gx/hubnx/index.html` na própria janela. Nenhum navegador externo deve ser aberto.

## Atualizações
A interface consulta `version.json` ao abrir e periodicamente, exibindo aviso de versão Switch nova quando existir `switch_version` e `nro_available: true`. Atualmente não existe `.nro` liberado nem instalador autoexecutável do Switch.

Quando uma nova distribuição for publicada, **não desinstale o forwarder nem exclua `sd:/switch/hubnx`**. Será suficiente substituir os binários `hubnx.nro` e `chromium-full.ext4` mantendo o mesmo caminho, e preservar `profile.ext4` se já existir (ele contém dados do navegador). Substitua arquivos só com o programa fechado. O automatismo desse processo ainda precisa ser testado e implementado.

## Chromium NX — o que já aproveitamos
- motor Chromium integrado, acelerado via EGL/GLES, V8 e rede HTTPS quando compatível;
- navegação na própria janela, touchscreen e Joy-Con;
- perfil persistente e baixa atualização manual sem reset;
- interface inicial HubNX personalizada.

Limitações: o upstream é experimental, pode travar; vídeos e streaming podem falhar, e Netflix DRM não é suportado de forma comprovada. Melhorias na compatibilidade do motor serão avaliadas após logs e testes reais, sem supor correção de player.

## Conta e favoritos
O cadastro local existe. A opção **Conta > Configurar sincronização** permite informar o mesmo Firebase Web API Key e Project ID do Android sem recompilar. É necessário configurar Firebase Authentication e Firestore corretamente antes de entrar. A compatibilidade das chamadas REST de origem `file:` no Chromium NX exige teste físico.
