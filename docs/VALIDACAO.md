# Revisão de texturas R3

A validação específica da pintura está em `TEXTURAS-R3.md`. Os resultados de compilação e GameTest abaixo são históricos da base Java 0.1.0.

# Escopo dos resultados

Os resultados abaixo pertencem à base Java 0.1.0. A revisão visual 02 foi verificada separadamente conforme `REVISAO-VISUAL-R2.md`; não houve nova sessão de cliente.

# Validação — Genesis Core 0.1.0

Execução em 1 de outubro de 2026: Java 17, Gradle 8.8, Forge 1.20.1-47.3.22, Palladium 4.5.4+1.20.1 e GeckoLib 4.4.4.

## Resultados executados

| Verificação | Resultado |
|---|---|
| `compileJava` | **BUILD SUCCESSFUL** |
| `build`, incluindo testes e `reobfJar` | **BUILD SUCCESSFUL** |
| JUnit | **15 testes; 0 falhas; 0 erros** |
| `runGameTestServer -x downloadAssets` | **BUILD SUCCESSFUL; 6 GameTests passaram** |
| Recursos | 44 JSON válidos; quatro power sets carregados pelo Palladium |
| Modelos | 132 cubos em quatro projetos; 14 animações por projeto; UVs dentro de 128×128; hierarquia e referências verificadas |

`docs/validation/` conserva os logs de compilação, build, GameTest e o XML do JUnit. A compilação usa três APIs do Forge marcadas como obsoletas, ainda disponíveis no alvo 1.20.1.

## Cobertura de integração

1. Ativar o item instala o Core. Os dados sobrevivem ao NBT próprio e ao ciclo real de `Player.saveWithoutId` / `load`, incluindo a capability Forge.
2. Os quatro power sets existem no PowerManager real do Palladium.
3. Adaptaris recebe atributos e executa o escudo; cast duplicado é negado; a troca antecipada é negada; Ferronox pode ser selecionado após estabilização; voltar a humano restaura a armadura.
4. O evento Forge de clonagem por morte preserva instalação, XP, desbloqueios, Forma Absoluta, preset e cooldown.
5. Energia zerada encerra a transformação e mantém a instalação e a recuperação.
6. Ferronox cria um projétil real do Palladium, consome exatamente uma pepita e preserva a aparência do item.

Os GameTests utilizam FakePlayers dentro de um servidor Forge real. Eles não substituem uma sessão com dois clientes gráficos. Os testes de domínio também cobrem limite de duas adaptações, teto de resistência, regeneração, XP, desbloqueios, rejeição de espécies não implementadas e preservação de cooldowns.

## Limites e verificações pendentes

A conferência visual do cliente está em andamento. Ainda não há validação manual com duas pessoas conectadas, de todos os casos de desconexão/reconexão, nem de combinação com modpacks arbitrários. O teste NBT e os eventos de login, respawn e dimensão cobrem a base da persistência, mas não equivalem a esse roteiro completo.

O radial aplica foco de câmera, sem desacelerar os ticks do mundo. Só há três formas jogáveis e uma absoluta. O tradutor registra nomes localizados; sons são vanilla. Facção, campanha e sete outras espécies são documentação e arquitetura de expansão.

No ambiente de desenvolvimento, Palladium/PalladiumCore emitem mensagens sobre `minVersion`, addonpacks e `.mcassetsroot`; houve também falhas externas de consulta de atualização/autenticação. Esses avisos não impediram os quatro poderes nem os seis testes. Não foram ocultados dos logs.
