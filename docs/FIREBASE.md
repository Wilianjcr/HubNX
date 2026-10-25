# Configuração do Firebase

A sincronização dos sites exige uma conta Firebase configurada por você, pois o GitHub hospeda o código e não é um banco privado de favoritos.

1. Em https://console.firebase.google.com, crie um projeto.
2. Ative Authentication > Sign-in method > Email/Password.
3. Crie um banco Cloud Firestore e aplique as regras de `firebase/firestore.rules` (sem liberar acesso público).
4. No console do Firebase, registre um aplicativo web e copie apenas a `apiKey` **web** e o `projectId`.
5. Atualize `android/firebase-config.js` e `switch/media-hub/firebase-config.js` com os mesmos dois valores.
6. Na aba Authentication > Settings > Authorized domains, autorize o domínio HTTPS usado pela interface, quando necessário para o método de login escolhido.
7. Instale a PWA por HTTPS ou use localhost nos testes.
8. Cadastre favoritos no Android, entre na mesma conta no Switch e sincronize.

**Importante:** estas chaves de configuração web não são credenciais administrativas, mas não exponha senhas, tokens, chaves de service account ou arquivos de conta. O token de sessão é mantido localmente e deve ser protegido caso o console seja compartilhado.

### Limitação atual
A sincronização usa a lista inteira como um documento: a última gravação prevalece quando dois dispositivos editam offline ao mesmo tempo. O próximo aprimoramento será sincronização por item com resolução de conflito e exclusão segura.
