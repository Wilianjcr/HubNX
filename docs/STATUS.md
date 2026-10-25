# Estado confirmado do HubNX — 25/10/2026

## GitHub
- Repositório Wilianjcr/HubNX acessível por integração conectada.
- Visibilidade confirmada: **PRIVATE**.
- Código Android, fonte Switch, documentação e regras Firestore publicados.

## Android
- Interface web e JavaScript publicados em android/index.html.
- Sintaxe JavaScript passou na checagem do GitHub Actions.
- Projeto Android nativo baseado em WebViewAssetLoader publicado em android-native/.
- APK debug compilado com sucesso no workflow privado da execução 40224889894.
- Artefato: HubNX-Android-debug, disponível na página de Actions para quem tem acesso ao repositório.
- Ainda faltam instalação real no celular e testes de favoritos/sincronização.

## Switch
- Interface completa publicada em switch/media-hub/.
- Script tools/prepare_chromium_nx.py publicado para transformar o Chromium NX em um homebrew independente.
- Padrões de patch conferidos contra fontes upstream.
- Ainda não houve compilação do NRO nem teste no hardware.
- Compatibilidade do CineVS e da Netflix ainda não validada.

## Firebase / sincronização
- Código de autenticação e sincronização por API REST integrado aos dois clientes.
- Regras de segurança disponíveis em firebase/firestore.rules.
- O Firebase real ainda precisa ser configurado e as regras implantadas.
- Nenhum teste ponta-a-ponta Android → Firestore → Switch foi executado.

## Atualizações
- version.json publicado no repositório privado.
- Raw Github anônimo não acessa repositório privado; verificação remota ainda exige solução autenticada/endpoint específico.

## Privacidade
- Nenhum GitHub Pages público ativado.
- APK fica em artifact privado, não em release pública.
- Não incluir senhas, tokens nem chaves privadas em commits.
