# Genesis Core — Texturas R3

Melhoria da pintura de Adaptaris, Ferronox e Colonyx sobre a geometria R2. As texturas mantêm 128×128 pixels e as mesmas coordenadas UV.

- **Adaptaris:** pele com variação orgânica, osso com estrias e sombreamento nas placas. Os quatro olhos claros e o Core vermelho foram preservados.
- **Ferronox:** cobre com gradação de brilho, regiões azul-petróleo com sulcos e contraste, oxidação verde discreta nas reentrâncias.
- **Colonyx:** membranas verde-mar, sombras lilases na quitina clara e variação nos órgãos violeta.

Os mapas de brilho acompanham as novas cores sem ampliar as regiões luminosas. Os ícones do radial e as texturas incorporadas nos projetos Blockbench foram atualizados.

## Usar

Substitua o JAR Genesis anterior por `genesis-core-1.20.1-0.1.0-texturas-r3.jar`. Não deixe dois JARs Genesis na pasta `mods`. As dependências continuam Java 17, Forge 1.20.1, Palladium 4.5.4 e GeckoLib 4.4.4.

No Blockbench, abra os novos arquivos `.bbmodel`: a textura está incorporada. Os PNG separados usam exatamente o mesmo mapeamento da revisão R2.

## Verificações

- Geometrias, ossos, UVs e animações idênticos aos da R2.
- PNGs RGBA 128×128 e cobertura de transparência idêntica à anterior, incluindo mapas de brilho.
- Referências de animação, dimensões UV e textura incorporada dos `.bbmodel` verificadas.
- Prévias de frente, três quartos e costas renderizadas a partir dos recursos finais.
- Integridade do JAR e comparação dos arquivos: somente nove PNGs foram substituídos. Java, receitas, configurações, poderes, geometria e animações são idênticos aos da R2.

Esta é uma atualização de recursos. Não houve novo build Java nem teste no cliente Minecraft nesta etapa. As imagens de apresentação são renderizações externas dos arquivos reais.

## Processo e reprodução

A pintura foi criada com a ferramenta integrada de geração/edição de imagem, usando o atlas R2 como alvo e a renderização R2 como referência de materiais. Não foi usada CLI de geração nem chave de API. O prompt completo está em `PROMPT-TEXTURAS-R3.txt`.

O script `tools/assemble_textures_r3.py` usa ImageMagick para reduzir a pintura ao tamanho nativo, recuperar a transparência exata das UVs, limitar a paleta e preservar os minúsculos marcadores emissivos dos rostos e do Core. O arquivo gerado original está em `art/revision-3/generated/atlas-painted.png`.

Para reproduzir os arquivos finais, execute esse script, depois `tools/export_blockbench.py` e `tools/preview_textures_r3.py`. O último usa Pillow e NumPy para renderizar os próprios cubos e UVs; não altera a geometria.
