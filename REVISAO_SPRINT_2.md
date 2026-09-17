# Revisão Sprint 2

## Visão Geral

Esta sprint consistiu na revisão completa do código-fonte do projeto Kriptobioze, com foco no mapeamento da integração entre as três camadas (banco de dados, back-end, front-end) e na identificação de problemas existentes.

Foram analisados **27 arquivos** do projeto, cobrindo:
- 3 arquivos SQL
- 15 arquivos Java (models, repositories, services, controllers, DTO, config)
- 4 arquivos de frontend (HTML, JS, CSS)
- 2 arquivos de configuração (pom.xml, application.properties)
- 2 arquivos de documentação (DER.md, README.md)
- 1 arquivo de CI/CD (pages.yml)

---

## Banco de Dados

### Tabelas identificadas

| Tabela | Entidade JPA | Arquivo SQL | Arquivo Java |
|--------|-------------|-------------|--------------|
| `camada` | `Camada` | `01_schema.sql` | `model/Camada.java` |
| `fossil` | `Fossil` | `01_schema.sql` | `model/Fossil.java` |
| `isotopes` | `Isotope` | **NÃO documentado** | `model/Isotope.java` |

### Consistência SQL ↔ JPA

**Tabela `camada`:** Consistente. Todos os campos, tipos e nomes de colunas coincidem entre `01_schema.sql:20-27` e `Camada.java:7-60`. O mapeamento JPA usa `@Column(name="...")` para mapear camelCase → snake_case corretamente.

**Tabela `fossil`:** Consistente. Todos os campos, tipos e nomes de colunas coincidem entre `01_schema.sql:34-44` e `Fossil.java:7-66`. A FK `fk_camada` é mapeada via `@ManyToOne @JoinColumn(name="fk_camada")`.

**Tabela `isotopes`:** **INCONSISTENTE.** Esta tabela:
- NÃO está documentada em `database/01_schema.sql`
- NÃO está documentada em `database/DER.md`
- É criada automaticamente pelo Hibernate (`ddl-auto=update`)
- Usa nomes de colunas em inglês (`name`, `symbol`, `halfLifeYears`, `description`, `usedFor`)
- Enquanto as outras tabelas usam português (`nome_era`, `profundidade_media`, etc.)

### Dados iniciais

O `DataSeeder` (`config/DataSeeder.java:31-40`) popula as 3 tabelas na primeira execução (se estiverem vazias). Os dados são idênticos aos do `database/02_dados_iniciais.sql`.

### Consultas SQL

O `database/03_consultas.sql` contém 3 consultas:
1. **INNER JOIN principal** (linha 17-27): Correspondente à query JPQL em `FossilRepository.java:17-24`
2. **LEFT JOIN + GROUP BY** (linha 33-41): Quantidade de fósseis por camada. **Não implementada no backend.**
3. **RANK window function** (linha 49-58): Prova lógica da hierarquia. **Não implementada no backend.**

---

## Back-end

### Models (3 entidades)

| Entidade | Tabela | Atributos | Relacionamento |
|----------|--------|-----------|----------------|
| `Camada` | `camada` | id, nomeEra, profundidadeMedia, tipoRocha, idadeEstimadaMa, descricao | 1:N com Fossil |
| `Fossil` | `fossil` | id, nomeCientifico, taxonomia, idadeEstimada, camada, localidadeDescoberta, descricao | N:1 com Camada |
| `Isotope` | `isotopes` | id, name, symbol, halfLifeYears, description, usedFor | Independente |

**Problema:** `Isotope` usa atributos em inglês enquanto as outras entidades usam português. Não há justificativa documentada para a diferença.

### Repositories (3 interfaces)

| Repository | Métodos automáticos | Consultas personalizadas |
|------------|---------------------|--------------------------|
| `CamadaRepository` | findAll, findById, save, deleteById | findByProfundidadeMediaBetween, findByTipoRocha |
| `FossilRepository` | findAll, findById, save, deleteById | findByCamadaId, findByTaxonomia, findByIdadeEstimadaBetween, findAllEstratigrafiaOrderByProfundidadeDesc (@Query JPQL) |
| `IsotopeRepository` | findAll, findById, save, deleteById | findBySymbol, findByName |

**Observação:** Os métodos `findByProfundidadeMediaBetween`, `findByTipoRocha`, `findByTaxonomia`, `findByIdadeEstimadaBetween` e `findByName` existem nos repositories mas **não são chamados por nenhum service ou controller**. São código morto.

### Services (3 classes)

| Service | Dependências | Responsabilidade |
|---------|-------------|------------------|
| `FossilService` | FossilRepository, CamadaRepository | CRUD de fósseis e camadas + estratigrafia |
| `RadioactiveDecayService` | IsotopeRepository | Listar isótopos + calcular decaimento radioativo |
| `DarwinCalculationService` | Nenhuma | Calcular tempo para atingir altitude |

**Observação:** `DarwinCalculationService` não depende de nenhum repository. É um cálculo matemático puro.

### Controllers (3 classes)

| Controller | Rota base | Qtd endpoints | CORS |
|------------|-----------|---------------|------|
| `FossilController` | `/api` | 12 | `@CrossOrigin(origins = "*")` |
| `RadioactiveDecayController` | `/api/radioactive-decay` | 3 | `@CrossOrigin(origins = "*")` |
| `DarwinController` | `/api/darwin` | 1 | `@CrossOrigin(origins = "*")` |

**Total:** 16 endpoints REST.

### DTO (1 record)

| DTO | Campos | Uso |
|-----|--------|-----|
| `EstratigrafiaDTO` | fossilId, nomeCientifico, taxonomia, idadeEstimadaMa, nomeEra, profundidadeMedia, tipoRocha | Projeção do INNER JOIN para o endpoint `/api/fossils/estratigrafia` |

**Observação:** É o único DTO do projeto. Os outros endpoints retornam entidades JPA diretamente (ex: `List<Fossil>`, `List<Isotope>`). Isso pode expor campos internos do banco no JSON.

### Config

| Arquivo | Função |
|---------|--------|
| `application.properties` | Porta 8080, PostgreSQL localhost:5432/kriptobioze, JPA ddl-auto=update |
| `DataSeeder` | Popula dados iniciais na primeira execução |

**Problema:** As credenciais do banco estão fixas no código (`postgres`/`postgres`). Não há variáveis de ambiente.

---

## Front-end

### Estrutura

| Arquivo | Função | Linhas |
|---------|--------|--------|
| `index.html` | SPA única: navbar, hero, dashboard, tabela, footer, drawer | 463 |
| `js/app.js` | Lógica: simulador, API client, renderização | 512 |
| `css/styles.css` | Animações savana, estilos de marca | 231 |
| `sw.js` | Service Worker para cache offline | 73 |
| `manifest.json` | Configuração PWA | 25 |

### JavaScript — Funções de chamada HTTP

| Função | Método | Endpoint | Uso no boot() |
|--------|--------|----------|---------------|
| `KB.getIsotopes()` | GET | `/api/radioactive-decay/isotopes` | Sim, com fallback |
| `KB.getEstratigrafia()` | GET | `/api/fossils/estratigrafia` | Sim, com fallback |
| `KB.calculateDecay()` | POST | `/api/radioactive-decay/calculate` | Não |
| `KB.calculateDarwin()` | POST | `/api/darwin/calculate` | Não |
| `KB.getFossils()` | GET | `/api/fossils` | Não |
| `KB.getLayers()` | GET | `/api/layers` | Não |

### JavaScript — Funções de renderização

| Função | Elemento DOM | Dados |
|--------|-------------|-------|
| `renderIsotopeGroup()` | `#isotopeGroup` | `isotopes[]` |
| `renderAtoms()` | `#atomGrid` | `state.pct` |
| `buildCurve()` | `#curvePoints`, `#curveMarker`, `#curveGuides` | `state.simYears`, `state.isotopeSymbol` |
| `renderReadouts()` | `#ageOut`, `#eraOut`, `#hlOut`, `#timeOut`, `#pctOut`, `#massBar`, `#isotopeName` | `state.*` |
| `renderEstratigrafia()` | `#estratigrafiaBody`, `#estratigrafiaNote` | `estratigrafia[]` |
| `updateStatus()` | `#apiStatus` | `apiMode` |

### JavaScript — Dados fallback

| Variável | Qtd itens | Fonte |
|----------|-----------|-------|
| `FALLBACK_ISOTOPES` | 6 | Mesmos valores do DataSeeder |
| `FALLBACK_ESTRATIGRAFIA` | 6 | Mesmos valores do DataSeeder / 02_dados_iniciais.sql |
| `GEOLOGICAL_LAYERS` | 5 | Mesmas eras do DataSeeder (em anos, não Ma) |

### Modo de operação

O frontend opera em dois modos:
1. **Modo local** (padrão): Dados hardcoded, cálculos locais, sem backend
2. **Modo API**: Backend ativo, dados do PostgreSQL, indicador verde no `#apiStatus`

A transição acontece automaticamente no `boot()` (app.js:469-509).

---

## Fluxos de Integração Encontrados

### Fluxo 1: Estratigrafia (COMPLETO e FUNCIONAL)

```
boot() → KB.getEstratigrafia() → GET /api/fossils/estratigrafia
→ FossilController → FossilService → FossilRepository (JPQL)
→ PostgreSQL (fossil INNER JOIN camada ORDER BY profundidade DESC)
→ JSON → renderEstratigrafia() → tabela HTML
```

**Status:** Integrado e funcional quando backend está rodando.

### Fluxo 2: Isótopos (COMPLETO e FUNCIONAL)

```
boot() → KB.getIsotopes() → GET /api/radioactive-decay/isotopes
→ RadioactiveDecayController → RadioactiveDecayService → IsotopeRepository
→ PostgreSQL (tabela isotopes)
→ JSON → renderIsotopeGroup() → cards de seleção
```

**Status:** Integrado e funcional quando backend está rodando.

### Fluxo 3: Simulador de Decaimento (LOCAL)

```
Usuário interage com slider → state.pct atualizado
→ calcPctFromYears() / calcYearsFromPct() (cálculo local)
→ renderAll() → renderAtoms() + buildCurve() + renderReadouts()
```

**Status:** Funcional 100% offline. NÃO usa o backend.

### Fluxo 4: Darwin (DISPONÍVEL mas SEM UI)

```
KB.calculateDarwin() → POST /api/darwin/calculate
→ DarwinController → DarwinCalculationService (cálculo puro)
→ JSON → ??? (nenhuma função de renderização)
```

**Status:** Endpoint funcional, mas sem interface visual no frontend.

---

## O que já Funciona

1. **Estratigrafia completa:** Tabela de fósseis por camada com INNER JOIN, ordenada por profundidade DESC
2. **Isótopos:** Cards de seleção carregados do backend (ou fallback)
3. **Simulador de decaimento:** Cálculo local com visualização SVG, grid de átomos, barra de massa
4. **Fallback local:** Frontend funciona 100% offline com dados hardcoded
5. **Service Worker:** Cache offline dos assets estáticos
6. **PWA:** Manifest.json configurado para instalação
7. **CORS:** Habilitado para qualquer origem
8. **DataSeeder:** Popula dados automaticamente na primeira execução
9. **GitHub Actions:** Deploy automático do frontend para GitHub Pages

---

## O que Funciona Parcialmente

1. **Integração frontend↔backend:** Só funciona quando:
   - Backend está rodando (`mvn spring-boot:run`)
   - Frontend está servido em servidor estático
   - `window.KB_API_BASE` está definido antes do load do app.js
   - **Problema:** O `index.html` NÃO define `KB_API_BASE` por padrão (linha 450-451 é um comentário)

2. **Indicador de status:** O `#apiStatus` atualiza corretamente, mas só após tentativa de fetch (que pode falhar silenciosamente)

---

## O que Ainda Não Está Integrado

1. **Simulador de decaimento × backend:** O endpoint `POST /api/radioactive-decay/calculate` existe mas o simulador calcula localmente
2. **Cálculo de Darwin:** Endpoint existe mas não há UI para ele
3. **CRUD de fósseis:** Endpoints POST/PUT/DELETE existem mas não há formulários
4. **CRUD de camadas:** Endpoints POST/PUT/DELETE existem mas não há formulários
5. **Busca por ID:** Endpoint GET `/api/fossils/{id}` existe mas não é usado
6. **Busca por camada:** Endpoint GET `/api/fossils/layer/{layerId}` existe mas não é usado
7. **Consultas DQL 2 e 3:** LEFT JOIN+GROUP BY e RANK não estão implementadas no backend

---

## Problemas Encontrados

### Problemas Críticos

| # | Problema | Arquivo(s) | Impacto |
|---|----------|------------|---------|
| 1 | `KB_API_BASE` não está definido no HTML | `index.html:450-451` | Frontend inicia sempre em modo local, mesmo com backend rodando |
| 2 | Tabela `isotopes` não documentada no SQL | `01_schema.sql`, `DER.md` | Schema incompleto, dificulta reprodução manual |
| 3 | Credenciais do banco fixas | `application.properties:6-7` | Senha `postgres` hardcoded, não usar em produção |

### Problemas Moderados

| # | Problema | Arquivo(s) | Impacto |
|---|----------|------------|---------|
| 4 | Naming inconsistency: `isotopes` (plural) vs `camada`/`fossil` (singular) | `Isotope.java:8`, `01_schema.sql` | Confusão de convenção |
| 5 | Atributos de `Isotope` em inglês, outros em português | `Isotope.java:16-26` | Inconsistência de código |
| 6 | `Fossil` serializado com `camada` aninhada (lazy loading) | `Fossil.java:26-28` | GET /api/fossils retorna objeto camada completo dentro de cada fóssil |
| 7 | `fossilId: 0` no fallback para Mammuthus | `app.js:136` | ID 0 não existe no banco (auto-increment começa em 1) |
| 8 | Código morto nos repositories | `CamadaRepository.java:11-12`, `FossilRepository.java:14-15`, `IsotopeRepository.java:12` | Métodos findBy* não usados |

### Problemas Menores

| # | Problema | Arquivo(s) | Impacto |
|---|----------|------------|---------|
| 9 | `@NotBlank` em `descricao` de `Camada` e `Fossil` | `Camada.java:31`, `Fossil.java:33` | Campo descricao aceita null (sem @NotBlank), correto |
| 10 | Não há tratamento de erros no frontend para falhas de rede | `app.js:492-494, 503-504` | Erros são silenciosamente ignorados (catch vazio) |
| 11 | `GEOLOGICAL_LAYERS` no frontend usa anos, não Ma | `app.js:170-176` | Valores corretos mas unidade diferente do banco |
| 12 | GitHub Pages deploy não inclui backend | `pages.yml` | Frontend hospedado não pode acessar API |

---

## Arquivos que Precisam ser Modificados na Próxima Sprint

### Alta Prioridade

| Arquivo | Modificação Necessária |
|---------|----------------------|
| `index.html` | Descomentar/definir `window.KB_API_BASE = "http://localhost:8080"` |
| `database/01_schema.sql` | Adicionar CREATE TABLE para `isotopes` |
| `database/DER.md` | Adicionar entidade `ISOTOPES` ao DER |
| `application.properties` | Considerar usar variáveis de ambiente para credenciais |

### Média Prioridade

| Arquivo | Modificação Necessária |
|---------|----------------------|
| `Isotope.java` | Padronizar nomes de atributos (português ou inglês) |
| `app.js` | Corrigir `fossilId: 0` para `fossilId: 1` no fallback |
| `app.js` | Implementar tratamento de erros visível no fallback |
| `03_consultas.sql` | Implementar consultas 2 e 3 no backend (opcional) |

### Baixa Prioridade

| Arquivo | Modificação Necessária |
|---------|----------------------|
| `Fossil.java` | Considerar `@JsonIgnore` ou `@JsonManagedReference` na propriedade `camada` para GET /api/fossils |
| Repositories | Remover ou usar os métodos `findBy*` não utilizados |
| `README.md` | Atualizar com descobertas da Sprint 2 |

---

## Ordem Recomendada de Integração

1. **Corrigir `KB_API_BASE` no HTML** — Sem isso, a integração não funciona
2. **Documentar tabela `isotopes` no SQL** — Completar o schema
3. **Testar fluxo de isótopos end-to-end** — Validar que GET /api/radioactive-decay/isotopes retorna dados corretos
4. **Testar fluxo de estratigrafia end-to-end** — Validar que a tabela renderiza dados do backend
5. **Integrar simulador ao backend** — Usar POST /api/radioactive-decay/calculate no tick() ou no input do slider
6. **Criar UI para Darwin** — Interface visual para POST /api/darwin/calculate
7. **Criar formulários CRUD** — Para fósseis e camadas

---

## Testes que Deverão Ser Feitos

### Backend

| Teste | Comando/Verificação |
|-------|-------------------|
| Backend inicia sem erros | `mvn spring-boot:run` |
| Tabelas criadas automaticamente | Verificar no PostgreSQL: `\dt` |
| DataSeeder popula dados | Verificar: `SELECT COUNT(*) FROM fossil;` (deve retornar 6) |
| GET /api/radioactive-decay/isotopes | `curl http://localhost:8080/api/radioactive-decay/isotopes` |
| GET /api/fossils/estratigrafia | `curl http://localhost:8080/api/fossils/estratigrafia` |
| POST /api/radioactive-decay/calculate | `curl -X POST "http://localhost:8080/api/radioactive-decay/calculate?isotopeSymbol=C-14&timePeriodYears=5730"` |
| POST /api/darwin/calculate | `curl -X POST "http://localhost:8080/api/darwin/calculate?altitudeMeters=2800&elevationRatePerCentury=2"` |
| GET /api/fossils | `curl http://localhost:8080/api/fossils` |

### Frontend

| Teste | Verificação |
|-------|-------------|
| Frontend inicia em modo local | Abrir http://localhost:5500, verificar "Modo local" no #apiStatus |
| Fallback de isótopos | 6 cards de isótopos visíveis |
| Fallback de estratigrafia | 6 linhas na tabela |
| Simulador funciona | Arrastar slider, verificar atualização visual |
| Integração com backend | Definir KB_API_BASE, recarregar, verificar "Dados conectados ao backend (API)" |
| Service Worker | Verificar em DevTools → Application → Service Workers |

### Integração

| Teste | Verificação |
|-------|-------------|
| Backend + Frontend rodando | Backend em :8080, Frontend em :5500 |
| Estratigrafia do backend | Tabela mostra dados reais do PostgreSQL |
| Isótopos do backend | Cards mostram dados reais do PostgreSQL |
| Fallback automático | Desligar backend, verificar que dados locais aparecem |
| CORS | Verificar que não há erros de CORS no console do navegador |
