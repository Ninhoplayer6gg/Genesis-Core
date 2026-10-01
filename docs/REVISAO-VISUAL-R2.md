# Revisão visual 02 — 1 de outubro de 2026

Correção da semelhança excessiva entre os modelos e das texturas quadriculadas da versão inicial. Esta revisão modifica recursos visuais; as classes Java e as regras do jogo são as mesmas do JAR 0.1.0 validado anteriormente.

## Direção de arte implementada

| Forma | Anatomia e aparência | Cubos |
|---|---|---:|
| Adaptaris | Pele terrosa, quatro olhos verde-claros, mandíbula dividida, placas ósseas claras, grande escudo natural em um ombro, garras e coluna segmentada | 49 |
| Ferronox | Corpo azul-petróleo, abertura sensorial vertical âmbar, estruturas dipolares ocas elevadas, condutores de cobre e dedos bifurcados | 49 |
| Colonyx | Corpo baixo de seis patas, tecido verde-mar, carapaça em lobos independentes, órgãos sensoriais violeta e dois apêndices dianteiros | 62 |

Os humanoides mantêm cabeça cúbica e proporções Minecraft. O Colonyx tem corpo e locomoção próprios. Nenhuma das três formas reutiliza a textura de outra. As pinturas usam contornos, placas, sulcos e faces desenhados em pixels, sem o quadriculado genérico anterior.

## Modelos editáveis

Os arquivos `.bbmodel` contêm a textura 128×128 e 14 animações. Ao abrir, exibem a forma base: `thermal`, `cold`, `electric`, `impact`, `absolute`, `shield`, `blade` e `bloom` ficam ocultos quando presentes. Esses grupos podem ser mostrados pelo painel de ossos; no jogo, o renderer os controla conforme a habilidade.

As animações de espera e movimento diferenciam o peso de Adaptaris, os órgãos de Ferronox e o andar em três apoios do Colonyx. Os nomes usados pelo Java foram preservados. Evolyss permanece como conceito anterior no código-fonte, sem mudança de identidade nem implementação adicional.

## Instalação

Substitua o JAR anterior por `genesis-core-1.20.1-0.1.0-visual-r2.jar`. Mantenha Palladium e GeckoLib. Não deixe os dois JARs Genesis na pasta `mods`. O identificador do mod e a versão interna 0.1.0 continuam iguais; o sufixo do arquivo identifica esta revisão de arte.

## O que foi verificado

- Referências de todos os ossos animados, hierarquia, dimensões dos atlas e limites das UVs.
- Texturas incorporadas e contagem de cubos dos projetos Blockbench iguais aos recursos do mod.
- Abertura na forma base com os extras ocultos no projeto nativo.
- Prévia de frente, três quartos e costas calculada diretamente da geometria e das texturas entregues.
- Integridade ZIP/JAR e identidade byte a byte de todos os arquivos anteriores fora de `assets/genesis/`.

As prévias são renderizações ortográficas dos arquivos, não capturas do Minecraft. Não houve nova execução do cliente nem novo build Java nesta revisão de recursos. Os resultados anteriores de 15 testes unitários e 6 GameTests continuam registrados como resultados da base 0.1.0; não são apresentados como testes desta arte.
