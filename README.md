# Meu Personal Style

Aplicativo Android nativo para closet pessoal e sugestões de looks.

## O que funciona nesta versão

- cadastro de foto-base;
- inclusão múltipla de fotos de roupas;
- teste com signo, colorimetria, profissão e estilo;
- armazenamento local das escolhas e fotos;
- geração de combinações a partir do closet;
- preparação conceitual para prova virtual com IA via servidor seguro.

## Compilar o APK

Abra esta pasta no Android Studio, aguarde a sincronização e use **Build > Build APK(s)**.
O APK será criado em `app/build/outputs/apk/debug/app-debug.apk`.

### Pelo GitHub, sem instalar nada

1. Envie o conteúdo desta pasta para um repositório GitHub.
2. Abra a aba **Actions** do repositório.
3. Selecione **Gerar APK Android** e clique em **Run workflow**.
4. Quando concluir, abra a execução e baixe **Meu-Personal-Style-APK** em Artifacts.

O fluxo também é executado automaticamente a cada envio para a branch `main`.

Nunca coloque uma chave de API dentro do aplicativo. A prova virtual deve chamar um servidor próprio autenticado.
