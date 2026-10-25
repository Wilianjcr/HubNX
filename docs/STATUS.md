# HubNX — Estado verificado em 25/10/2026

## Visibilidade
- Repositório Wilianjcr/HubNX: PUBLIC (verificado).
- Arquivos Firebase Web possuem apenas placeholders; não há chave privada configurada nesse arquivo.
- Histórico, commits, Issues e código tornam-se visíveis a terceiros.

## Android
- Versão fonte e APK debug: 0.2.2.
- Navegador interno implementado no app Android: confirmado no código e compilado por GitHub Actions.
- Verificador de Releases instalado no código. Consulta GitHub Releases ao iniciar e oferece download do APK oficial com autorização do Android.
- Compilação CI de sucesso: https://github.com/Wilianjcr/HubNX/actions/runs/40228359672
- A primeira Release assinada ainda não existe. É necessário configurar secrets de assinatura Android e publicar uma tag de versão conforme docs/RELEASES.md.
- Nenhum teste do atualizador com uma Release verdadeira ou instalação no celular foi executado.
- APK debug possui certificado potencialmente diferente em novos builds e não é distribuição oficial.

## Switch
- Interface e script de adaptação Chromium NX publicados.
- NÃO existe hubnx.nro compilado, Release do Switch ou autoatualizador executável.
- Nenhum teste de reprodução no console.

## Sincronização
- Código REST Firebase e regras por usuário publicados.
- Projeto Firebase real, autenticação e teste entre os dois dispositivos ainda pendentes.

## Atualizações
- version.json agora é publicamente legível.
- Não é suficiente mudar version.json para atualizar apps Android e Switch.
- Android busca Release binária assinada, pede download/instalação com permissão do usuário.
- Switch requer módulo nativo de atualização ainda não desenvolvido.

## Segurança
- Não colocar secrets, keystores, tokens, chaves de serviço ou senhas nos commits.
- Quando o primeiro APK final for assinado, reutilizar a mesma chave em todas as versões seguintes.
