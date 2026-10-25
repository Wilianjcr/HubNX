# Plano de testes privados HubNX v0.2.0

## Android (a fazer)
1. Executar em servidor local HTTPS ou localhost, sem habilitar GitHub Pages público.
2. Abrir HubNX, adicionar um link de teste e recarregar: ele deve permanecer salvo.
3. Editar, excluir e pesquisar links; rejeitar URLs javascript: e URLs com credenciais.
4. Ativar projeto Firebase Authentication (Email/Password) e Firestore com as regras de `firebase/firestore.rules`.
5. Criar conta e sincronizar; reiniciar a sessão e confirmar persistência.
6. Testar que outra conta não acessa a lista da primeira.
7. Testar PWA com Chrome Android e a funcionalidade offline da tela inicial.

## Switch (a fazer)
1. Validar build do projeto Chromium NX adaptado e salvar log.
2. Compilar e instalar o arquivo .nro via Sphaira em modo 39-bit.
3. Abrir a interface diretamente pelo atalho do HubNX.
4. Cadastrar favorito, reiniciar e confirmar persistência do perfil.
5. Entrar com a mesma conta Firebase do Android e sincronizar.
6. Testar navegação, controles, vídeo e estabilidade em CineVS; Netflix está sujeita a DRM.

## Atualizações
Em repositório privado, a URL raw do GitHub retorna 404 sem credenciais. Portanto o verificador poderá exibir indisponibilidade. Antes de publicar atualizações automáticas, implementar um endpoint autenticado ou um manifesto público separado sem informações privadas. Novos commits não equivalem a nova versão lançada.

## Privacidade
Não executar deploy de GitHub Pages com repositório privado sem examinar a visibilidade da página. Nunca publicar credenciais, senhas, tokens ou dados pessoais de usuários.
