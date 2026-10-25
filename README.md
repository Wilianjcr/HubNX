# HubNX — Android + Nintendo Switch 1

**Projeto privado em testes.** HubNX reúne seus sites favoritos com cadastro pelo Android e futura sincronização para um aplicativo homebrew independente no Nintendo Switch 1 desbloqueado.

## Estrutura

| Pasta | Função | Estado |
|---|---|---|
| android/ | Interface web de cadastro, busca, edição, exclusão, conta e sincronização Firebase REST | Fontes publicadas |
| android-native/ | Empacotamento Android para gerar um APK debug de testes | Código publicado |
| switch/media-hub/ | Interface embutida no Chromium NX adaptado | Código publicado, NRO pendente |
| tools/ | Preparação experimental das fontes do Chromium NX | Compilação pendente |
| firebase/ | Regras de acesso privadas por usuário (UID) | Exigem aplicação no console Firebase |
| docs/ | Roteiros de testes e configuração | Publicados |
| version.json | Metadados de atualização | Pendente endpoint acessível em repo privado |

## Teste Android privado

No GitHub, abra **Actions > HubNX Android APK privado > execução mais recente > Artifacts** e baixe o arquivo **HubNX-Android-debug**. O resultado é um APK de desenvolvimento que requer instalação manual, não uma versão final para distribuição. Não é necessário habilitar GitHub Pages. Se o build falhar, consulte os logs antes de instalar.

O Android envia o usuário ao navegador padrão quando seleciona um site; o gerenciador de favoritos continua no aplicativo.

## Sincronização Android ↔ Switch

Configure Firebase Authentication (Email/Password) e Cloud Firestore de acordo com docs/FIREBASE.md. Use as mesmas configurações web (apiKey e projectId) nos dois aplicativos e aplique as regras restritivas de firebase/firestore.rules. A sincronização real ainda não foi validada num Switch físico.

O GitHub contém código e versões. Favoritos privados ficam no Firestore, separados por conta. Nenhum token pessoal ou senha deve entrar nos commits.

## Compilação Nintendo Switch

Baixe as fontes de https://github.com/shodowlo/chromium-nx e execute o script tools/prepare_chromium_nx.py apontando para a pasta clonada. Use o toolchain upstream Docker/devkitPro para compilar. O pacote esperado inclui hubnx.nro, chromium-full.ext4 e profile.ext4.gz em sd:/switch/hubnx/. Via Sphaira crie um forwarder com Address Space 39-bit.

**Nenhum arquivo .nro funcional foi compilado ou testado ainda.** O script mantém provisoriamente o ícone upstream. Respeitar licenças de terceiros.

## Limitações e segurança

- Netflix pode abrir como página, mas reprodução de vídeo protegida por DRM não é garantida.
- CineVS e outros sites precisam de testes reais de player e compatibilidade.
- O repositório é privado; não ligar Pages sem revisar se o site será público.
- O aviso por raw.githubusercontent.com falha em repositórios privados sem credenciais. Para notificar versões, será necessário endpoint autenticado ou metadados públicos separados.
- Não inserir senhas, tokens pessoais, chaves privadas ou arquivos de contas nos commits.

Consulte docs/TESTES.md para o roteiro completo.
