# Genesis Core 0.1.0 — Texturas R3

Pintura atualizada de Adaptaris, Ferronox e Colonyx sobre os modelos da revisão visual 02. Consulte `docs/TEXTURAS-R3.md` e as prévias em `art/revision-3/`. Use o JAR com sufixo `-texturas-r3`; os controles e o progresso permanecem os mesmos.

Mod Java Forge com três matrizes utilizáveis: **Adaptaris, Ferronox e Colonyx**. O código controla vínculo permanente, save, transformação, energia, progressão, scanner, interface e sincronização. Palladium fornece conjuntos de poderes, atributos, voo, salto e projéteis. GeckoLib renderiza as anatomias e animações.

## Instalar

Use Java 17 e Forge **1.20.1-47.3.22**. Coloque o JAR Genesis Core, **Palladium 4.5.4+1.20.1 Forge** e **GeckoLib 4.4.4 Forge 1.20.1** na pasta `mods` do cliente e do servidor. Palladium distribui suas bibliotecas internas; o projeto as declara separadamente para compilar no ambiente de desenvolvimento. Não use as edições Fabric.

Os números de versão estão fixados para tornar o build reproduzível. Consulte `docs/VALIDACAO.md` para distinguir verificações executadas de testes ainda necessários.

## Primeira sessão

1. Obtenha o Núcleo do Gênesis na aba criativa Genesis Core ou faça o crafting descrito abaixo.
2. Segure o item e use o botão direito. O vínculo é permanente e o item é consumido no Survival.
3. Abra o radial, selecione Adaptaris e teste em terceira pessoa (`F5` do Minecraft).
4. Volte à forma humana pelo botão **Humano** do radial.
5. Escaneie metais e alcance nível 2 para Ferronox. Escaneie slime, magma cube ou bloco de slime e alcance nível 3 para Colonyx.
6. No nível 6, use um Retículo absoluto para liberar a Forma Absoluta do Adaptaris.

Para experimentar o conjunto completo em um mundo com cheats, use **`/genesis testkit`**. O comando exige permissão de operador e libera as três matrizes, Adaptaris Absoluto, energia e munição. `/genesis status` mostra o diagnóstico sem alterar o progresso.

Em mundo de teste com comandos, `/give @s genesis:genesis_core` entrega o dispositivo e `/locate structure genesis:oriel_observatory` localiza a ruína. Os dez conceitos oficiais estão em `docs/ESPECIES.md`; somente os três acima aparecem no seletor.

## Controles padrão

Todos são remapeáveis em **Opções → Controles → Genesis Core**. Se outro mod usar a mesma tecla, altere uma das associações.

| Tecla | Transformado | Humano |
|---|---|---|
| R | Radial, voltar a humano e cores | Radial |
| Z | Habilidade 1 | Scanner |
| X | Habilidade 2 | Radar local |
| C | Habilidade 3 | Escudo de emergência |
| V | Ultimate | — |
| G | Movimento especial | — |
| B | Database / diagnóstico | Database / diagnóstico |

## Formas efetivas

| Forma | Z | X | C | V | G |
|---|---|---|---|---|---|
| Adaptaris | Golpe em cone, 7 de dano | Mitiga dano por 5 s | Cura 6 HP | Impacto radial, 10 de dano | Potencializa salto e impulso por 5 s; salte normalmente |
| Ferronox | Projétil Palladium, 7 de dano; consome 1 pepita de ferro | Atrai metais; agachar inverte o campo | Escudo por 7 s | Área de pressão magnética, dano e lentidão | Libera voo Palladium por 6 s; use o controle de voo do Palladium |
| Colonyx | Lança em cone, alcance 5,5 blocos | Membrana escudo por 5 s | Cura 5 HP | Membros extras, impacto e bônus temporários | Escalada por 10 s; encoste na parede, agache para parar |

Adaptaris analisa no máximo duas categorias de ameaça. Cada exposição elegível, espaçada por ao menos 1,5 s, acrescenta no máximo 14 pontos; as primeiras exposições continuam causando dano. A mitigação tem teto de 50%, ou 65% na Forma Absoluta. Placas específicas aparecem a partir de 25 pontos. A forma absoluta acrescenta estruturas dorsais, alcance de ultimate, esporos e contramedidas conforme a adaptação já desenvolvida.

Ferronox interage com **itens metálicos largados** e seres usando armaduras metálicas. Não arranca blocos de construções nesta versão. O tag `genesis:magnetic_materials` permite acrescentar materiais de outros mods.

Colonyx tem corpo baixo, seis patas, duas estruturas móveis e partes que aparecem durante ataques, escudo e ultimate. Sua habilidade de escalada é implementada em Java; atributos e bônus comuns ficam no Palladium.

## Energia e progressão

Começa com 100 de energia. Uma transformação custa 8, consome 1,2 por segundo e tem estabilização inicial de 9 segundos. A Forma Absoluta consome 4,5 por segundo. Em forma humana, o Tier I regenera 4,5 por segundo. A energia não é reiniciada ao trocar de forma. Cooldowns permanecem durante troca, morte e salvamento.

Habilidades custam, por padrão, **8 / 14 / 22 / 36 / 12** e têm cooldowns de **1,5 / 8 / 15 / 30 / 8 segundos**. Movimento especial ativo cobra também 1,5 de energia por segundo. Sem energia, o Core encerra a transformação.

| Tier | Nome | Nível mínimo | Energia máxima |
|---|---|---:|---:|
| I | Germinal | 1 | 100 |
| II | Convergente | 3 | 150 |
| III | Sináptico | 6 | 200 |
| IV | Harmônico | 10 | 250 |
| V | Ascendente | 15 | 300 |

Cada nova descoberta concede 18 XP; derrotar monstros concede 12; um minuto transformado concede 6. A primeira leitura do arquivo com o Core instalado concede mais 50 XP. Biomas novos também contam como descoberta. Repetir o mesmo scan não gera XP. O custo por nível cresce em passos de 60 XP.

Configuração de servidor: `world/serverconfig/genesis-server.toml`. Ajusta consumo, regeneração, custo, estabilização, alcance do scanner, escudo e PvP das habilidades. Estruturas, minério, receitas, tags e poderes são recursos modificáveis por datapack.

## Materiais, receitas e exploração

O minério de ressonância aparece em deepslate entre Y -56 e 0, em veios pequenos, e exige picareta de ferro. A ruína **Observatório Oriel** é rara em planícies, florestas, taigas, savanas e desertos; seu baú guarda componentes e tem 20% de chance de gerar um Core. Mundo existente precisa de chunks ainda não gerados.

Receitas exatas em `src/main/resources/data/genesis/recipes`. As tabelas abaixo seguem as posições da bancada 3×3; `—` é espaço vazio.

**Liga de ressonância (rende 2):**

| | | |
|---|---|---|
| — | Cobre | — |
| Cobre | Fragmento de ressonância | Cobre |
| — | Cobre | — |

**Fragmento mnemônico:**

| | | |
|---|---|---|
| Ametista | Lápis-lazúli | Ametista |
| Pérola do End | Ressonância | Pérola do End |
| Ametista | Lápis-lazúli | Ametista |

**Cápsula de matriz:**

| | | |
|---|---|---|
| Ametista | Ressonância | Ametista |
| Slime ball | Pérola do End | Slime ball |
| Ametista | Lágrima de ghast | Ametista |

**Núcleo do Gênesis:**

| | | |
|---|---|---|
| Liga | Fragmento mnemônico | Liga |
| Barra de ouro | Cápsula de matriz | Barra de ouro |
| Liga | Bloco de redstone | Liga |

**Retículo absoluto:**

| | | |
|---|---|---|
| Liga | Fragmento mnemônico | Liga |
| Fragmento de netherita | Cápsula de matriz | Fragmento de netherita |
| Liga | Fragmento mnemônico | Liga |

## Universo e arte

Os **Aevorim**, originários do Palimpsesto de Ilyr, criaram o Core para promover compreensão pela experiência biológica. São limitados, divididos e responsáveis por erros. O **Conclave da Margem** tenta controlar a restauração de espécies porque teme apagar ecossistemas que surgiram após extinções antigas. O arquivo Oriel contém pistas sobre matrizes de mundos que aparentemente nunca existiram. A história completa não é revelada.

`docs/UNIVERSO.md` contém a lore inicial. `docs/ESPECIES.json` e `.md` detalham os dez oficiais e suas Formas Absolutas conceituais. Causalis e Mimetrix estão reservados no catálogo de código, desabilitados.

Modelos são voxel, com texturas de 128×128. Humanoides usam cabeça 8×8, tronco 8×12 e membros 4×12, com órgãos e placas 3D extras. Os projetos editáveis com textura e animações incorporadas estão em `art/blockbench/*.bbmodel`; veja `docs/MODELOS.md`. Braços próprios também são exibidos em primeira pessoa. **Evolyss / Varynth / Krysal-V** está incluído como conceito de modelo separado; não altera os dez nomes oficiais nem aparece como uma quarta espécie sem gameplay.

## Arquitetura

- `core`: estado de domínio, capability Forge, NBT versionado e eventos de ciclo de vida.
- `species`: catálogo, anatomia, desbloqueio e identificação dos power sets.
- `transformation`: validação, espaço disponível, troca, retorno e reconciliação.
- `palladium`: única fronteira Java com a API externa; preserva poderes de outros mods.
- `abilities`: interações próprias e regras de combate validadas no servidor.
- `network`: pedidos pequenos, direção explícita e limite por jogador; o cliente não envia dano ou XP.
- `client`: controles, radial, HUD, database, renderizadores GeckoLib e Core no peito.
- `database`, `items`, `registry`, `world`, `config`: scanner, obtenção, arquivo e configuração.

Power IDs ativos: `genesis:adaptaris`, `genesis:ferronox`, `genesis:colonyx`, `genesis:adaptaris_absolute`.

## Compilar e testar

```sh
./gradlew compileJava
./gradlew test
./gradlew runGameTestServer -x downloadAssets
./gradlew build
```

Java 17 e acesso aos repositórios oficiais são necessários no primeiro build. O artefato de instalação é `build/libs/genesis-core-1.20.1-0.1.0.jar`. O arquivo `-sources.jar` não é instalável. Nenhum JAR de mods de terceiros é incluído na distribuição. A opção `-x downloadAssets` evita baixar sons e imagens do cliente para o servidor de testes sem interface gráfica; não omite testes.

## Limites da versão

O radial usa foco visual e redução do campo de visão; **não reduz a velocidade da simulação**. Não altera o tick rate multiplayer. O jogador continua vulnerável com o menu aberto. Há três presets de cores; editor completo de acabamento, sons e efeitos fica para expansão.

O tradutor inicial usa os nomes localizados do Minecraft no database; não é tradução universal de textos de outros mods. Sons são eventos vanilla reutilizados. Não há campanha, facções com NPCs, bosses, planetas ou implementação das sete formas restantes. Veja a validação para testes em cliente e multiplayer.
