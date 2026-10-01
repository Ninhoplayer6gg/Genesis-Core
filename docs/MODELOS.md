# Modelos editáveis

Abra `art/blockbench/*.bbmodel` no Blockbench 5 ou superior. Cada projeto contém a textura 128×128 incorporada, ossos, cubos e animações. O formato interno Bedrock permite abrir sem plugins; para exportar alterações ao mod, converta com o plugin GeckoLib. Os JSON em `src/main/resources/assets/genesis/geo` e `animations` são os arquivos usados pelo jogo.

- Adaptaris R2: proporções Minecraft, quatro olhos verde-claros, mandíbula dividida, placas ósseas assimétricas e coluna segmentada; órgãos adaptativos e anatomia absoluta controlados pelo renderer.
- Ferronox R2: proporções Minecraft, rosto de fenda âmbar, órgãos dipolares elevados e condutores de cobre.
- Colonyx R2: corpo colonial de seis patas, verde-mar/marfim/violeta; carapaça de lobos móveis, lâmina, membrana e floração.
- Evolyss: conceito Varynth de Krysal-V, grafite, quatro olhos vermelho-laranja e seis fragmentos independentes. Preservado como conceito separado, sem substituir o elenco oficial.

Não renomeie ossos controlados pelo Java (`shield`, `blade`, `thermal`, `absolute` etc.). `tools/export_blockbench.py` recria os projetos editáveis a partir dos assets.

Os sons atuais usam eventos do Minecraft; não há gravações inéditas nesta versão.

Referência do formato nativo: https://www.blockbench.net/wiki/docs/bbmodel/

A forma base abre sem os extras de habilidades. Ative a visibilidade dos grupos correspondentes quando quiser editá-los. As prévias em `art/revision-2/` são calculadas dos arquivos de geometria e textura, fora do jogo.
