# Documento de Integração — Kriptobioze

Este documento mapeia **todas as conexões** entre SQL, JPA/Hibernate, Repository, Service, Controller, API REST/JSON, JavaScript/fetch e HTML.

---

## 1. Visão Geral da Arquitetura de Integração

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         CAMADAS DE INTEGRAÇÃO                                  │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  HTML (index.html)                                                              │
│    ↕ DOM manipulation                                                           │
│  JavaScript (app.js)                                                            │
│    ↕ fetch() HTTP                                                               │
│  API REST (Controllers)                                                         │
│    ↕ injeção de dependência                                                     │
│  Service (lógica de negócio)                                                    │
│    ↕ chamada ao repositório                                                     │
│  Repository (JpaRepository)                                                     │
│    ↕ queries JPQL                                                               │
│  JPA/Hibernate (ORM)                                                            │
│    ↕ SQL gerado                                                                 │
│  PostgreSQL (tabelas)                                                           │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Mapeamento SQL ↔ JPA/Hibernate

### 2.1 Tabela `camada` ↔ Entidade `Camada`

| Coluna SQL | Tipo SQL | Atributo Java | Atributo JPA | Tipo Java |
|------------|----------|---------------|--------------|-----------|
| `id` | BIGINT PK | `id` | `@Id @GeneratedValue(IDENTITY)` | `Long` |
| `nome_era` | VARCHAR(100) NOT NULL | `nomeEra` | `@Column(name="nome_era") @NotBlank` | `String` |
| `profundidade_media` | DOUBLE PRECISION NOT NULL | `profundidadeMedia` | `@Column(name="profundidade_media") @NotNull` | `Double` |
| `tipo_rocha` | VARCHAR(100) NOT NULL | `tipoRocha` | `@Column(name="tipo_rocha") @NotBlank` | `String` |
| `idade_estimada_ma` | INTEGER NOT NULL | `idadeEstimadaMa` | `@Column(name="idade_estimada_ma") @NotNull` | `Integer` |
| `descricao` | TEXT | `descricao` | `@Column` (default) | `String` |

**Arquivo SQL:** `database/01_schema.sql:20-27`
**Arquivo Java:** `src/main/java/com/kriptobioze/model/Camada.java:7-60`

### 2.2 Tabela `fossil` ↔ Entidade `Fossil`

| Coluna SQL | Tipo SQL | Atributo Java | Atributo JPA | Tipo Java |
|------------|----------|---------------|--------------|-----------|
| `id` | BIGINT PK | `id` | `@Id @GeneratedValue(IDENTITY)` | `Long` |
| `nome_cientifico` | VARCHAR(150) NOT NULL | `nomeCientifico` | `@Column(name="nome_cientifico") @NotBlank` | `String` |
| `taxonomia` | VARCHAR(100) NOT NULL | `taxonomia` | `@NotBlank` (coluna default) | `String` |
| `idade_estimada` | DOUBLE PRECISION NOT NULL | `idadeEstimada` | `@Column(name="idade_estimada") @NotNull` | `Double` |
| `fk_camada` | BIGINT NOT NULL | `camada` | `@ManyToOne(LAZY) @JoinColumn(name="fk_camada")` | `Camada` |
| `localidade_descoberta` | VARCHAR(200) | `localidadeDescoberta` | `@Column(name="localidade_descoberta")` | `String` |
| `descricao` | TEXT | `descricao` | `@Column` (default) | `String` |

**Arquivo SQL:** `database/01_schema.sql:34-44`
**Arquivo Java:** `src/main/java/com/kriptobioze/model/Fossil.java:7-66`

### 2.3 Tabela `isotopes` ↔ Entidade `Isotope`

| Coluna SQL | Tipo SQL | Atributo Java | Atributo JPA | Tipo Java |
|------------|----------|---------------|--------------|-----------|
| `id` | BIGINT PK | `id` | `@Id @GeneratedValue(IDENTITY)` | `Long` |
| `name` | VARCHAR | `name` | `@NotBlank` (coluna default) | `String` |
| `symbol` | VARCHAR | `symbol` | `@NotBlank` (coluna default) | `String` |
| `half_lifeyears` | DOUBLE PRECISION | `halfLifeYears` | `@NotNull` (coluna default) | `Double` |
| `description` | TEXT | `description` | `@Column` (default) | `String` |
| `used_for` | TEXT | `usedFor` | `@Column` (default) | `String` |

**Nota:** Esta tabela NÃO está documentada em `database/01_schema.sql` nem em `database/DER.md`. Ela é criada automaticamente pelo Hibernate (`ddl-auto=update`).

**Arquivo Java:** `src/main/java/com/kriptobioze/model/Isotope.java:8-55`

### 2.4 Relacionamentos

| Relação | Tipo SQL | Tipo JPA | FK |
|---------|----------|----------|-----|
| CAMADA → FOSSIL | `FOREIGN KEY (fk_camada) REFERENCES camada(id)` | `@ManyToOne` em `Fossil.camada` | `fossil.fk_camada` → `camada.id` |

**Cardinalidade:** 1:N (uma camada → vários fósseis)

---

## 3. Mapeamento Repository ↔ JPQL ↔ SQL

### 3.1 FossilRepository

**Arquivo:** `src/main/java/com/kriptobioze/repository/FossilRepository.java:12-25`

| Método Java | Tipo | JPQL/SQL Gerado |
|-------------|------|-----------------|
| `findAll()` | JpaRepository | `SELECT f FROM Fossil f` |
| `findById(Long id)` | JpaRepository | `SELECT f FROM Fossil f WHERE f.id = ?` |
| `save(Fossil f)` | JpaRepository | INSERT ou UPDATE |
| `deleteById(Long id)` | JpaRepository | `DELETE FROM Fossil f WHERE f.id = ?` |
| `findByCamadaId(Long camadaId)` | Spring Data | `SELECT f FROM Fossil f WHERE f.camada.id = ?` |
| `findByTaxonomia(String taxonomia)` | Spring Data | `SELECT f FROM Fossil f WHERE f.taxonomia = ?` |
| `findByIdadeEstimadaBetween(Double min, Double max)` | Spring Data | `SELECT f FROM Fossil f WHERE f.idadeEstimada BETWEEN ? AND ?` |
| `findAllEstratigrafiaOrderByProfundidadeDesc()` | @Query JPQL | Ver abaixo |

**Query JPQL personalizada (linha 17-24):**
```java
@Query("""
    SELECT new com.kriptobioze.dto.EstratigrafiaDTO(
        f.id, f.nomeCientifico, f.taxonomia, f.idadeEstimada,
        c.nomeEra, c.profundidadeMedia, c.tipoRocha)
    FROM Fossil f
    INNER JOIN f.camada c
    ORDER BY c.profundidadeMedia DESC
""")
```

**SQL equivalente gerado pelo Hibernate:**
```sql
SELECT f.id, f.nome_cientifico, f.taxonomia, f.idade_estimada,
       c.nome_era, c.profundidade_media, c.tipo_rocha
FROM fossil f
INNER JOIN camada c ON c.id = f.fk_camada
ORDER BY c.profundidade_media DESC;
```

**Corresponde a:** `database/03_consultas.sql:17-27`

### 3.2 CamadaRepository

**Arquivo:** `src/main/java/com/kriptobioze/repository/CamadaRepository.java:10-12`

| Método Java | Tipo | SQL Gerado |
|-------------|------|------------|
| `findAll()` | JpaRepository | `SELECT c FROM Camada c` |
| `findById(Long id)` | JpaRepository | `SELECT c FROM Camada c WHERE c.id = ?` |
| `save(Camada c)` | JpaRepository | INSERT ou UPDATE |
| `deleteById(Long id)` | JpaRepository | `DELETE FROM Camada c WHERE c.id = ?` |
| `findByProfundidadeMediaBetween(Double min, Double max)` | Spring Data | `SELECT c FROM Camada c WHERE c.profundidade_media BETWEEN ? AND ?` |
| `findByTipoRocha(String tipoRocha)` | Spring Data | `SELECT c FROM Camada c WHERE c.tipo_rocha = ?` |

### 3.3 IsotopeRepository

**Arquivo:** `src/main/java/com/kriptobioze/repository/IsotopeRepository.java:10-12`

| Método Java | Tipo | SQL Gerado |
|-------------|------|------------|
| `findAll()` | JpaRepository | `SELECT i FROM Isotope i` |
| `findBySymbol(String symbol)` | Spring Data | `SELECT i FROM Isotope i WHERE i.symbol = ?` |
| `findByName(String name)` | Spring Data | `SELECT i FROM Isotope i WHERE i.name = ?` |

---

## 4. Mapeamento Service ↔ Repository

### 4.1 FossilService

**Arquivo:** `src/main/java/com/kriptobioze/service/FossilService.java:13-83`

| Método | Repository Chamado | Responsabilidade |
|--------|-------------------|------------------|
| `getAllFossils()` | `fossilRepository.findAll()` | Lista todos os fósseis |
| `getFossilById(Long id)` | `fossilRepository.findById(id)` | Busca fóssil por ID |
| `getFossilsByLayer(Long camadaId)` | `fossilRepository.findByCamadaId(camadaId)` | Fósseis de uma camada |
| `getEstratigrafia()` | `fossilRepository.findAllEstratigrafiaOrderByProfundidadeDesc()` | INNER JOIN + ORDER BY |
| `createFossil(Fossil fossil)` | `fossilRepository.save(fossil)` | Cria fóssil |
| `updateFossil(Long id, Fossil fossilDetails)` | `fossilRepository.findById(id)` + `save()` | Atualiza fóssil |
| `deleteFossil(Long id)` | `fossilRepository.deleteById(id)` | Remove fóssil |
| `getAllLayers()` | `camadaRepository.findAll()` | Lista todas as camadas |
| `getLayerById(Long id)` | `camadaRepository.findById(id)` | Busca camada por ID |
| `createLayer(Camada layer)` | `camadaRepository.save(layer)` | Cria camada |
| `updateLayer(Long id, Camada layerDetails)` | `camadaRepository.findById(id)` + `save()` | Atualiza camada |
| `deleteLayer(Long id)` | `camadaRepository.deleteById(id)` | Remove camada |

### 4.2 RadioactiveDecayService

**Arquivo:** `src/main/java/com/kriptobioze/service/RadioactiveDecayService.java:12-100`

| Método | Repository Chamado | Responsabilidade |
|--------|-------------------|------------------|
| `getAllIsotopes()` | `isotopeRepository.findAll()` | Lista todos os isótopos |
| `getIsotopeBySymbol(String symbol)` | `isotopeRepository.findBySymbol(symbol)` | Busca isótopo por símbolo |
| `calculateDecay(String isotopeSymbol, Double timePeriodYears)` | `isotopeRepository.findBySymbol(isotopeSymbol)` | Calcula decaimento radioativo |

**Lógica de negócio (linha 18-40):**
- Validação: `isotopeSymbol` não pode ser vazio/null
- Validação: `timePeriodYears` deve ser > 0
- Busca isótopo no banco pelo símbolo
- Calcula: `remainingFraction = Math.pow(0.5, timePeriodYears / halfLife)`
- Calcula: `decayedFraction = 1.0 - remainingFraction`
- Calcula: `halfLivesElapsed = timePeriodYears / halfLife`
- Gera 11 checkpoints (0% a 100% do tempo, em 10 passos)

**Classes internas:**
- `DecayResult`: resultado do cálculo (isotopo, tempo, frações, checkpoints)
- `DecayCheckpoint`: ponto na curva (tempo, fração restante)

### 4.3 DarwinCalculationService

**Arquivo:** `src/main/java/com/kriptobioze/service/DarwinCalculationService.java:5-58`

| Método | Repository Chamado | Responsabilidade |
|--------|-------------------|------------------|
| `calculateTimeToAltitude(Double altitudeMeters, Double elevationRatePerCentury)` | Nenhum (cálculo puro) | Calcula tempo para atingir altitude |

**Lógica de negócio (linha 8-21):**
- Validação: `altitudeMeters` deve ser > 0
- Validação: `elevationRatePerCentury` deve ser > 0
- Calcula: `elevationRatePerYear = elevationRatePerCentury / 100.0`
- Calcula: `yearsRequired = altitudeMeters / elevationRatePerYear`
- Calcula: `millionsOfYears = yearsRequired / 1_000_000.0`
- Classifica a escala temporal (humana, geológica curta/média/longa/profunda)

**Classe interna:** `CalculationResult` com todos os valores + classificação

---

## 5. Mapeamento Controller ↔ Service

### 5.1 FossilController

**Arquivo:** `src/main/java/com/kriptobioze/controller/FossilController.java:13-81`
**Rota base:** `/api`

| Método HTTP | Rota | Parâmetros | Service Chamado | Retorno |
|-------------|------|------------|-----------------|---------|
| `GET` | `/fossils` | - | `fossilService.getAllFossils()` | `List<Fossil>` |
| `GET` | `/fossils/{id}` | `@PathVariable Long id` | `fossilService.getFossilById(id)` | `Fossil` |
| `GET` | `/fossils/layer/{layerId}` | `@PathVariable Long layerId` | `fossilService.getFossilsByLayer(layerId)` | `List<Fossil>` |
| `GET` | `/fossils/estratigrafia` | - | `fossilService.getEstratigrafia()` | `List<EstratigrafiaDTO>` |
| `POST` | `/fossils` | `@RequestBody Fossil fossil` | `fossilService.createFossil(fossil)` | `Fossil` |
| `PUT` | `/fossils/{id}` | `@PathVariable Long id`, `@RequestBody Fossil fossil` | `fossilService.updateFossil(id, fossil)` | `Fossil` |
| `DELETE` | `/fossils/{id}` | `@PathVariable Long id` | `fossilService.deleteFossil(id)` | `204 No Content` |
| `GET` | `/layers` | - | `fossilService.getAllLayers()` | `List<Camada>` |
| `GET` | `/layers/{id}` | `@PathVariable Long id` | `fossilService.getLayerById(id)` | `Camada` |
| `POST` | `/layers` | `@RequestBody Camada layer` | `fossilService.createLayer(layer)` | `Camada` |
| `PUT` | `/layers/{id}` | `@PathVariable Long id`, `@RequestBody Camada layer` | `fossilService.updateLayer(id, layer)` | `Camada` |
| `DELETE` | `/layers/{id}` | `@PathVariable Long id` | `fossilService.deleteLayer(id)` | `204 No Content` |

### 5.2 RadioactiveDecayController

**Arquivo:** `src/main/java/com/kriptobioze/controller/RadioactiveDecayController.java:14-40`
**Rota base:** `/api/radioactive-decay`

| Método HTTP | Rota | Parâmetros | Service Chamado | Retorno |
|-------------|------|------------|-----------------|---------|
| `GET` | `/isotopes` | - | `radioactiveDecayService.getAllIsotopes()` | `List<Isotope>` |
| `GET` | `/isotopes/{symbol}` | `@PathVariable String symbol` | `radioactiveDecayService.getIsotopeBySymbol(symbol)` | `Isotope` |
| `POST` | `/calculate` | `@RequestParam String isotopeSymbol`, `@RequestParam Double timePeriodYears` | `radioactiveDecayService.calculateDecay(isotopeSymbol, timePeriodYears)` | `DecayResult` |

### 5.3 DarwinController

**Arquivo:** `src/main/java/com/kriptobioze/controller/DarwinController.java:11-27`
**Rota base:** `/api/darwin`

| Método HTTP | Rota | Parâmetros | Service Chamado | Retorno |
|-------------|------|------------|-----------------|---------|
| `POST` | `/calculate` | `@RequestParam Double altitudeMeters`, `@RequestParam Double elevationRatePerCentury` | `darwinCalculationService.calculateTimeToAltitude(altitudeMeters, elevationRatePerCentury)` | `CalculationResult` |

---

## 6. Mapeamento JavaScript ↔ API REST

### 6.1 Objeto `window.KB` (app.js:11-71)

| Método JS | Método HTTP | Endpoint | Parâmetros | Retorno Esperado |
|-----------|-------------|----------|------------|------------------|
| `KB.getIsotopes()` | `GET` | `/api/radioactive-decay/isotopes` | - | `Array<Isotope>` |
| `KB.calculateDecay(isotopeSymbol, timePeriodYears)` | `POST` | `/api/radioactive-decay/calculate?isotopeSymbol=...&timePeriodYears=...` | Query params | `DecayResult` |
| `KB.calculateDarwin(altitudeMeters, elevationRatePerCentury)` | `POST` | `/api/darwin/calculate?altitudeMeters=...&elevationRatePerCentury=...` | Query params | `CalculationResult` |
| `KB.getFossils()` | `GET` | `/api/fossils` | - | `Array<Fossil>` |
| `KB.getLayers()` | `GET` | `/api/layers` | - | `Array<Camada>` |
| `KB.getEstratigrafia()` | `GET` | `/api/fossils/estratigrafia` | - | `Array<EstratigrafiaDTO>` |

### 6.2 Configuração da API Base (app.js:11-16)

```javascript
const KB = (window.KB = {
  config: {
    apiBase: window.KB_API_BASE || "",  // vazio = modo local
    endpoints: {
      isotopes: "/api/radioactive-decay/isotopes",
      calculate: "/api/radioactive-decay/calculate",
      darwin: "/api/darwin/calculate",
      fossils: "/api/fossils",
      layers: "/api/layers",
      estratigrafia: "/api/fossils/estratigrafia"
    }
  },
  // ...
});
```

### 6.3 Função `_request` (app.js:24-28)

```javascript
async _request(url, options) {
  const res = await fetch(url, options);
  if (!res.ok) throw new Error("HTTP " + res.status + " - " + url);
  return res.json();
}
```

---

## 7. Fluxos de Integração Detalhados

### Fluxo 1: Estratigrafia (o mais completo)

```
1. Usuário abre http://localhost:5500
   ↓
2. index.html carrega app.js (linha 452)
   ↓
3. app.js executa boot() (linha 469)
   ↓
4. boot() renderiza dados locais primeiro:
   - renderIsotopeGroup() (linha 470)
   - renderEstratigrafia() (linha 471)
   - renderAll() (linha 472)
   - updateStatus() (linha 473)
   ↓
5. boot() tenta carregar do backend (linha 496-505):
   const rows = await KB.getEstratigrafia();
   ↓
6. KB.getEstratigrafia() (app.js:68-70)
   → fetch("http://localhost:8080/api/fossils/estratigrafia")
   ↓
7. FossilController.getEstratigrafia() (FossilController.java:36-39)
   → fossilService.getEstratigrafia()
   ↓
8. FossilService.getEstratigrafia() (FossilService.java:35-37)
   → fossilRepository.findAllEstratigrafiaOrderByProfundidadeDesc()
   ↓
9. FossilRepository.findAllEstratigrafiaOrderByProfundidadeDesc() (FossilRepository.java:17-25)
   → @Query JPQL: SELECT new EstratigrafiaDTO(...) FROM Fossil f INNER JOIN f.camada c ORDER BY c.profundidadeMedia DESC
   ↓
10. Hibernate gera SQL:
    SELECT f.id, f.nome_cientifico, f.taxonomia, f.idade_estimada,
           c.nome_era, c.profundidade_media, c.tipo_rocha
    FROM fossil f
    INNER JOIN camada c ON c.id = f.fk_camada
    ORDER BY c.profundidade_media DESC;
    ↓
11. PostgreSQL retorna 6 linhas (camada + fossil)
    ↓
12. Jackson serializa para JSON:
    [
      { "fossilId": 5, "nomeCientifico": "Stromatólito...", "profundidadeMedia": 10000, ... },
      { "fossilId": 4, "nomeCientifico": "Dickinsonia...", "profundidadeMedia": 5000, ... },
      ...
    ]
    ↓
13. fetch() retorna Promise com array de EstratigrafiaDTO
    ↓
14. boot() atribui: estratigrafia = rows (app.js:499)
    ↓
15. renderEstratigrafia() (app.js:353-378) renderiza a tabela HTML:
    - Percorre cada item do array
    - Cria <tr> com: profundidade, era, tipo_rocha, nome_cientifico, taxonomia, idade_estimada
    - Insere em #estratigrafiaBody (index.html:337)
    ↓
16. updateStatus() (app.js:326-338) atualiza #apiStatus:
    - apiMode = "api" → "Dados conectados ao backend (API)"
    ↓
17. Usuário visualiza tabela com 6 fósseis ordenados por profundidade DESC
```

### Fluxo 2: Isótopos (boot + fallback)

```
1. boot() tenta carregar isótopos (app.js:475-494):
   const list = await KB.getIsotopes();
   ↓
2. KB.getIsotopes() (app.js:31-33)
   → fetch("http://localhost:8080/api/radioactive-decay/isotopes")
   ↓
3. RadioactiveDecayController.getAllIsotopes() (RadioactiveDecayController.java:20-23)
   → radioactiveDecayService.getAllIsotopes()
   ↓
4. RadioactiveDecayService.getAllIsotopes() (RadioactiveDecayService.java:55-57)
   → isotopeRepository.findAll()
   ↓
5. PostgreSQL retorna 6 isótopos da tabela `isotopes`
   ↓
6. Jackson serializa para JSON:
   [
     { "id": 1, "name": "Carbono-14", "symbol": "C-14", "halfLifeYears": 5730, ... },
     ...
   ]
   ↓
7. boot() mapeia para formato do frontend (app.js:477-484):
   { symbol, name, halfLifeYears, range, blurb }
   ↓
8. Se sucesso: isotopes = mapped, apiMode = "api", renderIsotopeGroup()
   Se falha: apiMode = "local", mantém FALLBACK_ISOTOPES
   ↓
9. renderIsotopeGroup() (app.js:199-231) renderiza cards de seleção
```

### Fluxo 3: Simulador de Decaimento (local)

```
1. Usuário seleciona isótopo C-14 e ajusta slider para 50%
   ↓
2. Evento "input" no #rangePct (app.js:453-459):
   state.pct = 50
   state.simYears = calcYearsFromPct(50, 5730) = 5730 anos
   ↓
3. renderAll() (app.js:340-344):
   - renderAtoms(): 45 átomos ativos, 45 decaídos (90 total)
   - buildCurve(): SVG com curva exponencial + marcador em t=5730
   - renderReadouts(): "5.730 anos", "Cenozóico"
   ↓
4. Usuário clica "▶ Simular decaimento" (app.js:461-463)
   ↓
5. startSim() (app.js:381-388): inicia setInterval(tick, 60)
   ↓
6. tick() (app.js:400-410) executa a cada 60ms:
   - state.simYears += halfLife * 0.02 (5730 * 0.02 = 114,6 anos/ciclo)
   - state.pct = calcPctFromYears(simYears, halfLife)
   - renderAll() atualiza visualizações
   ↓
7. Para quando pct < 0.05 ou simYears > 6 × halfLife
   ↓
8. NOTA: Este fluxo NÃO usa o backend — cálculo 100% local
```

### Fluxo 4: Darwin (disponível mas não integrado ao UI)

```
Frontend: KB.calculateDarwin(altitudeMeters, elevationRatePerCentury)
   ↓
POST http://localhost:8080/api/darwin/calculate?altitudeMeters=2800&elevationRatePerCentury=2
   ↓
DarwinController.calculate() (DarwinController.java:17-27)
   → darwinCalculationService.calculateTimeToAltitude(2800, 2)
   ↓
DarwinCalculationService (DarwinCalculationService.java:8-21):
   - elevationRatePerYear = 2 / 100 = 0.02
   - yearsRequired = 2800 / 0.02 = 140.000 anos
   - millionsOfYears = 0,14
   - classification = "Escala geológica curta"
   ↓
Retorna CalculationResult via JSON
   ↓
Frontend: NÃO há UI para exibir este resultado (endpoint disponível mas sem interface)
```

---

## 8. Matriz de Integração

| Funcionalidade | Front-end (função) | Endpoint | Controller | Service | Repository | Banco | Status |
|----------------|---------------------|----------|------------|---------|------------|-------|--------|
| Listar isótopos | `KB.getIsotopes()` → `renderIsotopeGroup()` | `GET /api/radioactive-decay/isotopes` | `RadioactiveDecayController.getAllIsotopes()` | `RadioactiveDecayService.getAllIsotopes()` | `IsotopeRepository.findAll()` | `isotopes` | **Integrado** |
| Calcular decaimento | `KB.calculateDecay()` (disponível, não usado no UI) | `POST /api/radioactive-decay/calculate` | `RadioactiveDecayController.calculateDecay()` | `RadioactiveDecayService.calculateDecay()` | `IsotopeRepository.findBySymbol()` | `isotopes` | **Sem UI** |
| Calcular Darwin | `KB.calculateDarwin()` (disponível, não usado no UI) | `POST /api/darwin/calculate` | `DarwinController.calculate()` | `DarwinCalculationService.calculateTimeToAltitude()` | Nenhum | Nenhum | **Sem UI** |
| Estratigrafia | `KB.getEstratigrafia()` → `renderEstratigrafia()` | `GET /api/fossils/estratigrafia` | `FossilController.getEstratigrafia()` | `FossilService.getEstratigrafia()` | `FossilRepository.findAllEstratigrafiaOrderByProfundidadeDesc()` | `fossil` + `camada` (INNER JOIN) | **Integrado** |
| Listar fósseis | `KB.getFossils()` (disponível, não usado no UI) | `GET /api/fossils` | `FossilController.getAllFossils()` | `FossilService.getAllFossils()` | `FossilRepository.findAll()` | `fossil` | **Sem UI** |
| Listar camadas | `KB.getLayers()` (disponível, não usado no UI) | `GET /api/layers` | `FossilController.getAllLayers()` | `FossilService.getAllLayers()` | `CamadaRepository.findAll()` | `camada` | **Sem UI** |
| Buscar fóssil por ID | Não disponível no front | `GET /api/fossils/{id}` | `FossilController.getFossilById()` | `FossilService.getFossilById()` | `FossilRepository.findById()` | `fossil` | **Sem UI** |
| Buscar fósseis por camada | Não disponível no front | `GET /api/fossils/layer/{layerId}` | `FossilController.getFossilsByLayer()` | `FossilService.getFossilsByLayer()` | `FossilRepository.findByCamadaId()` | `fossil` | **Sem UI** |
| Criar fóssil | Não disponível no front | `POST /api/fossils` | `FossilController.createFossil()` | `FossilService.createFossil()` | `FossilRepository.save()` | `fossil` | **Sem UI** |
| Atualizar fóssil | Não disponível no front | `PUT /api/fossils/{id}` | `FossilController.updateFossil()` | `FossilService.updateFossil()` | `FossilRepository.findById()` + `save()` | `fossil` | **Sem UI** |
| Remover fóssil | Não disponível no front | `DELETE /api/fossils/{id}` | `FossilController.deleteFossil()` | `FossilService.deleteFossil()` | `FossilRepository.deleteById()` | `fossil` | **Sem UI** |
| Criar camada | Não disponível no front | `POST /api/layers` | `FossilController.createLayer()` | `FossilService.createLayer()` | `CamadaRepository.save()` | `camada` | **Sem UI** |
| Atualizar camada | Não disponível no front | `PUT /api/layers/{id}` | `FossilController.updateLayer()` | `FossilService.updateLayer()` | `CamadaRepository.findById()` + `save()` | `camada` | **Sem UI** |
| Remover camada | Não disponível no front | `DELETE /api/layers/{id}` | `FossilController.deleteLayer()` | `FossilService.deleteLayer()` | `CamadaRepository.deleteById()` | `camada` | **Sem UI** |
| Simulador decaimento | `renderAll()` (cálculo local) | Nenhum (offline) | Nenhum | Nenhum | Nenhum | Nenhum | **Somente local** |

---

## 9. Estrutura JSON da API

### GET /api/fossils/estratigrafia

```json
[
  {
    "fossilId": 5,
    "nomeCientifico": "Stromatólito de cianobactérias",
    "taxonomia": "Cyanobacteria (microbialito fóssil)",
    "idadeEstimadaMa": 3500.0,
    "nomeEra": "Arqueano",
    "profundidadeMedia": 10000.0,
    "tipoRocha": "Metamórfica"
  }
]
```

### GET /api/radioactive-decay/isotopes

```json
[
  {
    "id": 1,
    "name": "Carbono-14",
    "symbol": "C-14",
    "halfLifeYears": 5730.0,
    "description": "Isótopo radioativo do carbono...",
    "usedFor": "Datação de fósseis e materiais orgânicos recentes"
  }
]
```

### POST /api/radioactive-decay/calculate

Request:
```
POST /api/radioactive-decay/calculate?isotopeSymbol=C-14&timePeriodYears=5730
```

Response:
```json
{
  "isotope": { "id": 1, "name": "Carbono-14", "symbol": "C-14", "halfLifeYears": 5730.0, ... },
  "timePeriodYears": 5730.0,
  "remainingFraction": 0.5,
  "decayedFraction": 0.5,
  "halfLivesElapsed": 1.0,
  "checkpoints": [
    { "timeYears": 0.0, "remainingFraction": 1.0 },
    { "timeYears": 573.0, "remainingFraction": 0.933... },
    ...
    { "timeYears": 5730.0, "remainingFraction": 0.5 }
  ]
}
```

### POST /api/darwin/calculate

Request:
```
POST /api/darwin/calculate?altitudeMeters=2800&elevationRatePerCentury=2
```

Response:
```json
{
  "altitudeMeters": 2800.0,
  "elevationRatePerCentury": 2.0,
  "yearsRequired": 140000.0,
  "millionsOfYears": 0.14,
  "classification": "Escala geológica curta - milhares a centenas de milhares de anos"
}
```

### GET /api/fossils

```json
[
  {
    "id": 1,
    "nomeCientifico": "Mammuthus primigenius",
    "taxonomia": "Mammalia · Proboscidea",
    "idadeEstimada": 0.04,
    "camada": { "id": 1, "nomeEra": "Cenozóico", ... },
    "localidadeDescoberta": "Sibéria, Norte da Ásia",
    "descricao": "Mamute-lanudo do fim do Quaternário"
  }
]
```

**Problema:** O endpoint GET /api/fossils retorna o objeto `camada` aninhado (por causa do `@ManyToOne`). O frontend fallback usa `fossilId` (número), não o objeto completo. Isso pode causar incompatibilidade se o frontend tentar consumir este endpoint diretamente.

---

## 10. CORS e Configuração de Rede

### CORS

Todos os controllers usam `@CrossOrigin(origins = "*")`:
- `FossilController.java:15`
- `RadioactiveDecayController.java:14`
- `DarwinController.java:11`

**Resultado:** Qualquer origem pode acessar a API. Não há restrição de domínio.

### Portas

| Serviço | Porta | Configuração |
|---------|-------|-------------|
| Backend (Spring Boot) | 8080 | `application.properties:2` |
| Frontend (servidor estático) | Variável (ex: 5500) | Definida pelo servidor estático |

### URL do Backend no Frontend

O frontend detecta a API base via `window.KB_API_BASE`:
- Se definido: usa `http://localhost:8080` (ou a URL configurada)
- Se vazio: opera em "modo local" com dados fallback

**Problema:** O `index.html` (linha 450-451) mostra um comentário sugerindo:
```html
<script>window.KB_API_BASE = "http://localhost:8080"</script>
```
Mas esta linha NÃO está descomentada no HTML. O frontend inicia em modo local por padrão.

---

## 11. DataSeeder — Inicialização de Dados

**Arquivo:** `src/main/java/com/kriptobioze/config/DataSeeder.java:18-139`

O `DataSeeder` implementa `CommandLineRunner` e executa na inicialização do Spring Boot:

```java
public void run(String... args) {
    if (isotopeRepository.count() == 0) seedIsotopes();    // 6 isótopos
    if (camadaRepository.count() == 0) seedCamadas();       // 5 camadas
    if (fossilRepository.count() == 0) seedFossils();       // 6 fósseis
}
```

**Condição:** Só popula se as tabelas estiverem vazias.

**Dados inseridos:**

| Tabela | Qtd | Dados |
|--------|-----|-------|
| `isotopes` | 6 | C-14, U-235, U-238, K-40, Rb-87, Sm-147 |
| `camada` | 5 | Cenozóico, Mesozóico, Paleozóico, Proterozóico, Arqueano |
| `fossil` | 6 | Mammuthus, T. rex, Triceratops, Paradoxides, Dickinsonia, Stromatólito |

**Ordem de inserção:** Primeiro isotopos, depois camadas, depois fósseis (dependência de FK).

**Compatibilidade SQL:** Os dados do `DataSeeder` espelham exatamente o `database/02_dados_iniciais.sql`.

---

## 12. Service Worker e Cache

**Arquivo:** `frontend-kriptobioze/sw.js:1-73`

O Service Worker faz cache dos seguintes arquivos:
```javascript
const PRECACHE = [
  "index.html",
  "css/styles.css",
  "js/app.js",
  "manifest.json",
  "icons/icon-192.png",
  "icons/icon-512.png"
];
```

**Comportamento:**
- Request de navegação: tenta rede, fallback para cache
- Request de assets: tenta cache primeiro, depois rede
- Request de origem externa (ex: API backend): ignorado pelo SW (linha 39: `if (url.origin !== self.location.origin) return`)

**Impacto na integração:** As chamadas fetch() ao backend (porta 8080) NÃO são afetadas pelo Service Worker porque são de origem diferente.

---

## 13. GitHub Actions — Deploy

**Arquivo:** `.github/workflows/pages.yml:1-36`

O workflow faz deploy do diretório `frontend-kriptobioze/` para GitHub Pages.

**Trigger:** Push para branch `main` ou manual.

**Resultado:** O frontend é hospedado em `https://nel-idev.github.io/Kriptobioze/`

**Problema:** O frontend no GitHub Pages NÃO pode acessar o backend (que estaria rodando localmente). A integração só funciona em ambiente local.

