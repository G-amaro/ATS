# Relatório de Análise e Teste de Software (ATS) - SpotifUM

## 1. Introdução
Este relatório descreve o trabalho realizado no âmbito da Unidade Curricular de Análise e Teste de Software. O objetivo principal é aplicar diversas técnicas de teste e análise sobre duas implementações da aplicação **SpotifUM**, um sistema de gestão e reprodução de música desenvolvido originalmente na disciplina de Programação Orientada aos Objetos (POO).

O projeto incidiu sobre duas variantes:
1.  **SpotifyUM_Gradle**: Uma versão em fase inicial, estruturada com o sistema de build Gradle.
2.  **SpotifyUM_Maven**: Uma versão em fase final de desenvolvimento, estruturada com Maven, apresentando uma arquitetura mais robusta e funcionalidades completas.

---

## 2. Descrição das Aplicações
### 2.1. Arquitetura e Funcionalidades
A aplicação SpotifUM permite a gestão de:
-   **Músicas**: Entidades com metadados (nome, intérprete, letra, etc.), incluindo especializações como `ExplicitSong` e `MultimediaSong`.
-   **Utilizadores**: Categorizados por planos de subscrição (`Free`, `PremiumBase`, `PremiumTop`), cada um com diferentes permissões de acesso e sistemas de pontuação.
-   **Playlists**: Coleções de músicas que podem ser manuais, aleatórias ou geradas automaticamente (favoritos, género).
-   **Álbuns**: Agrupamentos de músicas de um determinado artista.

### 2.2. Diferenças entre Versões
| Funcionalidade | SpotifyUM_Gradle (Inicial) | SpotifyUM_Maven (Final) |
| :--- | :--- | :--- |
| **Sistema de Build** | Gradle | Maven |
| **Complexidade** | Média | Elevada |
| **Persistência** | JSON (GSON) e Serialização | Serialização Java |
| **Interface** | CLI (Menus Funcionais) | CLI (Menus Estruturados/Manager) |

---

## 3. Tarefas Desenvolvidas

### 3.1. Testes Unitários (JUnit 5)
Foram desenvolvidos testes manuais para garantir a correção da lógica de negócio, focando-se principalmente nos modelos e utilitários.

#### SpotifyUM_Gradle
Os testes manuais focaram-se na validação das classes base como `Song`, `Album` e `User`.
-   **Ficheiros principais**: `AlbumTest.java`, `SongTest.java`, `UserTest.java`.

#### SpotifyUM_Maven
Dada a maturidade desta versão, os testes manuais foram mais exaustivos, cobrindo também a camada do `Controller`.
-   **Ficheiros principais**: `ControllerTest.java`, `SpotifyUMTest.java`, `PlaylistTest.java`.

### 3.2. Geração Automática de Testes (EvoSuite)
Utilizou-se o **EvoSuite** para gerar suites de teste com o objetivo de maximizar a cobertura de código e detetar casos de erro não previstos nos testes manuais.

-   **Processo**: Foram gerados testes para todas as classes do modelo e lógica. No Gradle, foi criado um script `generate_tests.sh` e uma task customizada no `build.gradle.kts`. No Maven, utilizou-se o plugin oficial do EvoSuite.
-   **Análise**: Os testes gerados revelaram-se fundamentais para cobrir ramos de execução (branches) complexos, especialmente em métodos de comparação (`equals`) e clonagem.

### 3.3. Análise de Cobertura (JaCoCo)
A cobertura de código foi medida utilizando o **JaCoCo**, integrando tanto os testes manuais como os testes gerados pelo EvoSuite.

#### Resultados de Cobertura (Resumo)
-   **SpotifyUM_Gradle**: A cobertura das classes do modelo (`User`, `Song`, `Album`, `Playlist`) atingiu valores entre **89% e 98%**. As classes de utilitários como `JsonDataParser` e `Stats` apresentam coberturas de **86%** e **95%**, respetivamente. A cobertura global é afetada pelas classes de interface (`UserUI`, `AdminUI`), que apresentam valores mais baixos (aprox. **50-60%**) devido à natureza interativa do input.
-   **SpotifyUM_Maven**: Nesta versão, as classes core do modelo, como `Playlist`, `Album`, `Plan` e as respetivas exceções, atingiram os **100%** de cobertura de linha e branch. A classe principal `SpotifUM.java` apresenta uma cobertura de **89%**, refletindo uma suite de testes extremamente abrangente que cobre quase todos os fluxos lógicos e casos de erro.

### 3.4. Teste de Mutação (PIT)
Para avaliar a qualidade da suite de testes, aplicou-se o **PIT Mutation Testing** no projeto Maven.

-   **Análise**: A suite de testes revelou-se eficaz na deteção de alterações subtis na lógica. Por exemplo, mutações em operadores condicionais nas classes de subscrição (`PlanFree`, `PlanPremiumBase`) foram quase todas eliminadas, validando a eficácia dos testes em garantir que as permissões de cada plano são rigorosamente respeitadas.
-   **Mutation Score**: O projeto Maven apresenta um mutation score elevado, indicando que os testes não só cobrem as linhas de código, mas também validam a sua semântica de forma profunda.

### 3.5. Property-Based Testing (jqwik)
Diferenciando-se dos testes tradicionais, o projeto Maven utiliza o **jqwik** para validar propriedades lógicas que devem manter-se independentemente dos inputs específicos.

-   **Propriedade Validada**: No ficheiro `SpotifUMPropertiesTest.java`, validou-se que a operação de registo de um novo utilizador deve ser invariante: o sistema deve sempre crescer em tamanho e o utilizador deve ser recuperável sem perda de informação, para qualquer combinação de caracteres alfanuméricos válida.
-   **Vantagem**: Esta abordagem permitiu testar milhares de combinações de nomes, emails e passwords, garantindo que carateres especiais ou comprimentos inesperados não corrompem o estado do sistema.

---

## 4. Refactoring e Extras
### 4.1. Automação de Processos
Ambos os projetos demonstram um elevado nível de automação:
-   **Gradle**: Integração de tarefas de geração de testes EvoSuite (`evosuiteGenerateAll`) e agregação de relatórios JaCoCo. O script `generate_tests.sh` permite a execução em batch fora do ambiente Gradle, se necessário.
-   **Maven**: Configuração completa do `pom.xml` para integrar o motor de execução do EvoSuite, JaCoCo para cobertura e PIT para mutação, permitindo que todo o ciclo de QA seja corrido com um simples `mvn test`.

### 4.2. Refactoring para Java Streams
No projeto Maven (versão final), foi realizado um esforço consciente de refabricação de código "legado" (ciclos iterativos) para **Java Streams**.
-   **Exemplo**: A classe `SpotifUM.java` utiliza streams para calcular estatísticas complexas, como o utilizador com mais reproduções ou a música mais tocada, resultando num código mais declarativo e menos propenso a erros de indexação.
-   **Impacto**: Esta mudança não só melhorou a legibilidade como também facilitou a implementação de testes de cobertura, uma vez que as operações de filtragem e mapeamento são agora mais fáceis de validar isoladamente.

### 4.3. Tratamento de Exceções
A versão Maven introduziu um sistema de exceções customizadas (`NotFoundException`, `AlreadyExistsException`, `NoPremissionException`) que melhora significativamente a robustez da aplicação, permitindo que a camada de UI forneça feedback preciso ao utilizador sem comprometer a estabilidade do sistema.

---

## 5. Conclusão
O trabalho realizado permitiu não só validar a funcionalidade da aplicação SpotifUM, mas também elevar o seu nível de qualidade e confiança. 

A comparação entre a versão inicial (Gradle) e a versão final (Maven) demonstra claramente como a aplicação de técnicas rigorosas de teste (unitário, automático e por propriedades) influencia a arquitetura do software. A necessidade de atingir coberturas elevadas motivou refabricações para código mais limpo (Streams) e um tratamento de erros mais sofisticado. 

Em suma, a suite de testes final não serve apenas como uma rede de segurança contra regressões, mas como uma prova documental da robustez e correção da lógica de negócio do **SpotifUM**.

---

## 6. Anexos
*(Espaço reservado para gráficos de cobertura e mutação)*
