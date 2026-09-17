# Kriptobioze — Do Tempo Geológico ao Algoritmo

Sistema interdisciplinar de tempo geológico e evolução (Biologia · 3°DS — Kripta).
Repositório contendo **frontend** (PWA estático), **backend** (Spring Boot + JPA) e
**modelagem de banco de dados** (PostgreSQL).

---

## 1. Nome e Objetivo do Projeto

### O que é o Kriptobioze?

O Kriptobioze é um **sistema web full-stack** que simula a datação de fósseis por decaimento radioativo e demonstra a Lei da Superposição de Darwin por meio de modelagem estratigráfica interativa.

### Problema que resolve

No século XIX, acreditava-se que a Terra tinha apenas ~6.000 anos. Para Darwin, essa escala era insuficiente para explicar a especiação por seleção natural. O projeto resolve a necessidade de:

- **Visualizar** a relação entre profundidade geológica e idade de fósseis
- **Simular** o decaimento radioativo de isótopos (C-14, U-235, K-40, etc.)
- **Demonstrar** a Lei da Superposição de Darwin de forma interativa e educacional

### Contexto Educacional/Científico

O projeto é produzido pela equipe **Kripta** (Biologia e 3°DS) e aborda:

- **Tempo profundo**: a escala de bilhões de anos da história geológica
- **Darwin no Chile (1835)**: observações que exigiam milhões de anos para se explicar
- **História dos isótopos**: técnicas de datação absoluta que confirmaram a idade real da Terra (~4,5 bilhões de anos)
- **Lei da Superposição**: camadas mais profundas = fósseis mais antigos

**Equipe:** Murylo, Pedro Bernardes, Kelvin, Vinícius, Pedro Manoel

---

## 2. Tecnologias Utilizadas

### Front-end

| Tecnologia | Função |
|------------|--------|
| HTML5 | Estrutura da página (SPA vanilla) |
| CSS3 | Animações custom (keyframes para savana, dinossauros, nuvens, sol) |
| JavaScript ES6+ | Lógica vanilla (IIFE, async/await) |
| Tailwind CSS | UI framework (via CDN Play) |
| PWA | Service Worker + manifest.json para funcionamento offline |
| Google Fonts | Baloo 2 (display) + Nunito (body) |

### Back-end

| Tecnologia | Versão | Função |
|------------|--------|--------|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.1.4 | Framework web/API REST |
| Spring Data JPA | (via starter) | ORM / persistência |
| Hibernate | (via JPA) | Mapeamento objeto-relacional |
| PostgreSQL | (driver runtime) | Banco de dados |
| Spring Boot Validation | (starter) | Validação de entidades |
| Jackson | (via starter) | Serialização JSON |
| Maven | (wrapper) | Gerenciador de build/dependências |

### Banco de Dados

| Tecnologia | Função |
|------------|--------|
| PostgreSQL | SGBD relacional |
| DDL scripts | Criação manual de tabelas (database/*.sql) |
| JPA ddl-auto=update | Schema gerenciado automaticamente pelo Hibernate |

### Ferramentas e Dependências

| Ferramenta | Função |
|------------|--------|
| Git | Controle de versão |
| GitHub Actions | CI/CD: deploy do frontend para GitHub Pages |
| GitHub Pages | Hospedagem estática do frontend |

---

## 3. Arquitetura do Projeto

O Kriptobioze segue uma arquitetura **3-tier (3 camadas)** com front-end e back-end desacoplados:

```
┌─────────────────────────────────────────────────────────────────────────┐
│  FRONTEND (PWA Estático)       BACKEND (Spring Boot)      DATABASE    │
│  ─────────────────────         ──────────────────────      ─────────   │
│  index.html + app.js    ──>    REST API (JSON)        ──>  PostgreSQL  │
│  Tailwind CSS + Vanilla JS     Controllers/Services        3 tabelas   │
│  Service Worker (offline)      JPA/Hibernate               (geradas)   │
│  GitHub Pages (deploy)         Porta 8080                              │
└─────────────────────────────────────────────────────────────────────────┘
```

### Fluxo detalhado por camada

```
Front-end (index.html + app.js)
    ↓ fetch() HTTP
API REST (Controllers - porta 8080)
    ↓ injeção de dependência
Service (lógica de negócio)
    ↓ chamada ao repositório
Repository (JpaRepository - queries JPQL)
    ↓ JPA/Hibernate
Banco de Dados PostgreSQL (tabelas: camada, fossil, isotopes)
    ↓ resultado JSON
Front-end (renderização)
```

### Arquitetura MVC do Back-end

```
Controller → Service → Repository → Model → PostgreSQL
    ↑            ↑           ↑
DTOs        Lógica      Queries JPQL
(ex:         de          customizadas
EstratigrafiaDTO)  negócio
```

---

## 4. Estrutura de Pastas

```
Kriptobioze/
├── database/                              # Artefatos de modelagem de banco
│   ├── DER.md                             # Diagrama Entidade-Relacionamento
│   ├── 01_schema.sql                      # DDL: CREATE TABLE camada, fossil
│   ├── 02_dados_iniciais.sql              # DML: INSERT camadas e fósseis
│   └── 03_consultas.sql                   # DQL: INNER JOIN, GROUP BY, RANK
│
├── frontend-kriptobioze/                  # Frontend PWA estático
│   ├── assets/                            # Imagens (dinossauros, árvores, logos)
│   ├── css/
│   │   └── styles.css                     # CSS custom: animações savana
│   ├── icons/
│   │   ├── icon-192.png                   # Ícone PWA 192x192
│   │   └── icon-512.png                   # Ícone PWA 512x512
│   ├── js/
│   │   └── app.js                         # Lógica principal: simulador, API client
│   ├── index.html                         # SPA única (navbar, hero, dashboard, tabela)
│   ├── manifest.json                      # PWA manifest
│   └── sw.js                              # Service Worker para cache offline
│
├── src/
│   └── main/
│       ├── java/com/kriptobioze/
│       │   ├── KriptobiozeApplication.java    # Classe principal Spring Boot
│       │   ├── config/
│       │   │   └── DataSeeder.java            # CommandLineRunner: popula dados
│       │   ├── controller/
│       │   │   ├── DarwinController.java      # POST /api/darwin/calculate
│       │   │   ├── FossilController.java      # CRUD /api/fossils, /api/layers
│       │   │   └── RadioactiveDecayController.java  # GET/POST /api/radioactive-decay/*
│       │   ├── dto/
│       │   │   └── EstratigrafiaDTO.java      # Record: projeção do INNER JOIN
│       │   ├── model/
│       │   │   ├── Camada.java                # Entidade JPA: tabela "camada"
│       │   │   ├── Fossil.java                # Entidade JPA: tabela "fossil"
│       │   │   └── Isotope.java               # Entidade JPA: tabela "isotopes"
│       │   ├── repository/
│       │   │   ├── CamadaRepository.java      # JpaRepository: findByProfundidadeMediaBetween
│       │   │   ├── FossilRepository.java      # JpaRepository: query JPQL (INNER JOIN)
│       │   │   └── IsotopeRepository.java     # JpaRepository: findBySymbol
│       │   └── service/
│       │       ├── DarwinCalculationService.java      # Cálculo: tempo para altitude
│       │       ├── FossilService.java                 # CRUD + getEstratigrafia
│       │       └── RadioactiveDecayService.java       # Decaimento + DecayResult
│       └── resources/
│           └── application.properties        # Config: porta, PostgreSQL, JPA
│
├── pom.xml                                  # Maven: Spring Boot 3.1.4, Java 17
└── README.md                                # Este arquivo
```

### Descrição das pastas principais

| Pasta/Arquivo | Função |
|---------------|--------|
| `database/` | Scripts SQL e documentação do DER |
| `frontend-kriptobioze/` | PWA estático completo (HTML/CSS/JS) |
| `src/main/java/` | Código-fonte Java do back-end |
| `src/main/resources/` | Configurações do Spring Boot |
| `pom.xml` | Dependências e configurações Maven |

---

## 5. Banco de Dados

### Tabelas Existentes

#### Tabela `camada`
| Coluna | Tipo | Restrição | Descrição |
|--------|------|-----------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `nome_era` | VARCHAR(100) | NOT NULL | Nome da era geológica |
| `profundidade_media` | DOUBLE PRECISION | NOT NULL | Profundidade média em metros |
| `tipo_rocha` | VARCHAR(100) | NOT NULL | Tipo de rocha (Sedimentar/Metamórfica) |
| `idade_estimada_ma` | INTEGER | NOT NULL | Idade estimada em milhões de anos |
| `descricao` | TEXT | - | Descrição da camada |

#### Tabela `fossil`
| Coluna | Tipo | Restrição | Descrição |
|--------|------|-----------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `nome_cientifico` | VARCHAR(150) | NOT NULL | Nome científico do fóssil |
| `taxonomia` | VARCHAR(100) | NOT NULL | Classificação taxonômica |
| `idade_estimada` | DOUBLE PRECISION | NOT NULL | Idade estimada em milhões de anos |
| `fk_camada` | BIGINT | FK → camada.id | Chave estrangeira para camada |
| `localidade_descoberta` | VARCHAR(200) | - | Local da descoberta |
| `descricao` | TEXT | - | Descrição do fóssil |

#### Tabela `isotopes`
| Coluna | Tipo | Restrição | Descrição |
|--------|------|-----------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `name` | VARCHAR | NOT NULL | Nome do isótopo |
| `symbol` | VARCHAR | NOT NULL | Símbolo químico (ex: C-14) |
| `half_lifeyears` | DOUBLE PRECISION | NOT NULL | Meia-vida em anos |
| `description` | TEXT | - | Descrição |
| `used_for` | TEXT | - | Uso principal |

### Relacionamentos

```
CAMADA (1) ──────────── (N) FOSSIL
   │                         │
   ├─ id (PK)                ├─ id (PK)
   ├─ nome_era               ├─ nome_cientifico
   ├─ profundidade_media     ├─ taxonomia
   ├─ tipo_rocha             ├─ idade_estimada
   ├─ idade_estimada_ma      ├─ fk_camada (FK → camada.id)
   └─ descricao              ├─ localidade_descoberta
                             └─ descricao
```

- **Cardinalidade:** 1:N (uma camada contém vários fósseis)
- **Integridade referencial:** `fk_camada` é NOT NULL (todo fóssil pertence a uma camada)
- **Índice:** `idx_fossil_fk_camada` para acelerar INNER JOINs

### Scripts SQL

| Arquivo | Conteúdo |
|---------|----------|
| `database/01_schema.sql` | DDL: CREATE TABLE com PK, FK e índice |
| `database/02_dados_iniciais.sql` | DML: 5 camadas + 6 fósseis de demonstração |
| `database/03_consultas.sql` | DQL: INNER JOIN, LEFT JOIN + GROUP BY, RANK window function |

### Consultas Relevantes

**Consulta Principal (Lei da Superposição):**
```sql
SELECT f.id AS codigo_fossil, f.nome_cientifico, f.taxonomia,
       f.idade_estimada AS idade_estimada_ma, c.profundidade_media,
       c.nome_era, c.tipo_rocha
FROM fossil f
INNER JOIN camada c ON c.id = f.fk_camada
ORDER BY c.profundidade_media DESC;
```

**Dados Iniciais (5 camadas, 6 fósseis):**

| Camada | Profundidade | Idade (Ma) | Fóssil | Idade Fóssil (Ma) |
|--------|-------------|------------|--------|-------------------|
| Cenozóico | 0 m | 66 | Mammuthus primigenius | 0,04 |
| Mesozóico | 500 m | 252 | Tyrannosaurus rex | 66 |
| Mesozóico | 500 m | 252 | Triceratops horridus | 68 |
| Paleozóico | 1500 m | 541 | Paradoxides sp. | 505 |
| Proterozóico | 5000 m | 2500 | Dickinsonia costata | 560 |
| Arqueano | 10000 m | 4000 | Stromatólito de cianobactérias | 3500 |

---

## 6. Back-end

### Models (Entidades JPA)

| Entidade | Tabela | Arquivo | Atributos Principais |
|----------|--------|---------|---------------------|
| `Camada` | `camada` | `model/Camada.java` | id, nomeEra, profundidadeMedia, tipoRocha, idadeEstimadaMa, descricao |
| `Fossil` | `fossil` | `model/Fossil.java` | id, nomeCientifico, taxonomia, idadeEstimada, camada (@ManyToOne), localidadeDescoberta, descricao |
| `Isotope` | `isotopes` | `model/Isotope.java` | id, name, symbol, halfLifeYears, description, usedFor |

**Notas sobre o mapeamento JPA:**
- `Fossil.camada` usa `@ManyToOne(fetch = FetchType.LAZY)` com `@JoinColumn(name = "fk_camada")`
- `Camada` e `Isotope` são entidades independentes
- Validações com `@NotBlank` e `@NotNull`

### Controllers

| Controller | Rota Base | Endpoints |
|------------|-----------|-----------|
| `FossilController` | `/api` | GET/POST/PUT/DELETE `/fossils`, GET/POST/PUT/DELETE `/layers`, GET `/fossils/estratigrafia` |
| `RadioactiveDecayController` | `/api/radioactive-decay` | GET `/isotopes`, GET `/isotopes/{symbol}`, POST `/calculate` |
| `DarwinController` | `/api/darwin` | POST `/calculate` |

**Configuração CORS:** `@CrossOrigin(origins = "*")` em todos os controllers.

### Services

| Service | Função | Método Principal |
|---------|--------|------------------|
| `FossilService` | CRUD de fósseis/camadas + consulta estratigráfica | `getEstratigrafia()` |
| `RadioactiveDecayService` | Cálculo de decaimento radioativo | `calculateDecay(isotopeSymbol, timePeriodYears)` |
| `DarwinCalculationService` | Cálculo de tempo para atingir altitude | `calculateTimeToAltitude(altitudeMeters, elevationRatePerCentury)` |

**Lógica de negócio:**
- **Decaimento radioativo:** `remainingFraction = Math.pow(0.5, timePeriodYears / halfLife)`
- **Darwin:** `yearsRequired = altitudeMeters / (elevationRatePerCentury / 100.0)`
- Ambos os services retornam inner classes (`DecayResult`, `CalculationResult`)

### Repositories

| Repository | Métodos Customizados |
|------------|---------------------|
| `CamadaRepository` | `findByProfundidadeMediaBetween()`, `findByTipoRocha()` |
| `FossilRepository` | `findByCamadaId()`, `findByTaxonomia()`, `findByIdadeEstimadaBetween()`, `findAllEstratigrafiaOrderByProfundidadeDesc()` (query JPQL) |
| `IsotopeRepository` | `findBySymbol()`, `findByName()` |

### DTO

| DTO | Tipo | Uso |
|-----|------|-----|
| `EstratigrafiaDTO` | Record Java | Projeção do INNER JOIN: fossilId, nomeCientifico, taxonomia, idadeEstimadaMa, nomeEra, profundidadeMedia, tipoRocha |

### Configuração do Spring Boot

Arquivo `src/main/resources/application.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/kriptobioze
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jackson.serialization.write-dates-as-timestamps=false
```

### DataSeeder

Classe `config/DataSeeder.java` implementa `CommandLineRunner`:
- Popula a tabela `isotopes` com 6 isótopos (C-14, U-235, U-238, K-40, Rb-87, Sm-147)
- Popula a tabela `camada` com 5 eras geológicas
- Popula a tabela `fossil` com 6 fósseis
- Executa apenas se as tabelas estiverem vazias (`count() == 0`)

---

## 7. Front-end

### Estrutura HTML

Arquivo `frontend-kriptobioze/index.html` (SPA única):

| Seção | ID | Descrição |
|-------|----|-----------|
| Navbar | `nav` | Barra de navegação laranja fixa com logo Kripta |
| Hero | `#hero` | Cena animada de savana pré-histórica (CSS) |
| Dashboard | `#dashboard` | Simulador de decaimento radioativo |
| Estratigrafia | `#estratigrafia` | Tabela de fósseis por camada |
| Footer | `footer` | Créditos e link para GitHub |
| Drawer | `#drawer` | Menu lateral com conteúdo teórico |

### CSS

Arquivo `frontend-kriptobioze/css/styles.css` (231 linhas):
- Animações da savana (céu, nuvens, sol)
- Dinossauros andando (translação horizontal)
- Pterodáctilo voando
- Árvores oscilando
- Grid de átomos (decaimento visual)
- Estilos do drawer lateral

### JavaScript

Arquivo `frontend-kriptobioze/js/app.js` (512 linhas):

**Objeto global `window.KB`:**
```javascript
window.KB = {
  getIsotopes(),           // GET /api/radioactive-decay/isotopes
  calculateDecay(),        // POST /api/radioactive-decay/calculate
  calculateDarwin(),       // POST /api/darwin/calculate
  getFossils(),            // GET /api/fossils
  getLayers(),             // GET /api/layers
  getEstratigrafia()       // GET /api/fossils/estratigrafia
}
```

**Chamadas fetch():**
- Todas as chamadas usam `KB._request()` internamente
- Tratamento de erro com try/catch
- Fallback automático para dados locais se a API falhar

### Modo Local / Fallback

O frontend funciona 100% offline com dados hardcoded:
- `FALLBACK_ISOTOPES`: 6 isótopos (mesmos valores do DataSeeder)
- `FALLBACK_ESTRATIGRAFIA`: 6 fósseis ordenados por profundidade
- `GEOLOGICAL_LAYERS`: 5 eras geológicas para classificação

**Indicador de status:** Elemento `#apiStatus` mostra "Dados conectados ao backend (API)" ou "Modo local (front) — backend desligado".

### Endpoints Consumidos

| Endpoint | Método | Uso no Frontend |
|----------|--------|-----------------|
| `/api/radioactive-decay/isotopes` | GET | Carregar isótopos no seletor |
| `/api/radioactive-decay/calculate` | POST | (disponível via `KB.calculateDecay`) |
| `/api/darwin/calculate` | POST | (disponível via `KB.calculateDarwin`) |
| `/api/fossils` | GET | (disponível via `KB.getFossils`) |
| `/api/layers` | GET | (disponível via `KB.getLayers`) |
| `/api/fossils/estratigrafia` | GET | Alimentar tabela de estratigrafia |

### Renderização

A função `renderEstratigrafia()` renderiza a tabela dinamicamente:
- Cada linha exibe: profundidade, era, tipo de rocha, nome científico, taxonomia, idade estimada
- Dados vêm da API (`KB.getEstratigrafia()`) ou do fallback local
- A função `renderAll()` atualiza átomos, curva SVG e leituras

---

## 8. Integração Atual entre as Camadas

### Conectado

| Componente | Status | Detalhes |
|------------|--------|----------|
| Frontend → Backend (isótopos) | Funcional | `KB.getIsotopes()` consome `/api/radioactive-decay/isotopes` |
| Frontend → Backend (estratigrafia) | Funcional | `KB.getEstratigrafia()` consome `/api/fossils/estratigrafia` |
| Backend → PostgreSQL | Funcional | JPA/Hibernate com `ddl-auto=update` |
| DataSeeder → PostgreSQL | Funcional | Popula dados na primeira execução |

### Disponível mas não utilizado no UI

| Componente | Status | Detalhes |
|------------|--------|----------|
| `KB.calculateDecay()` | Disponível | Endpoint `POST /api/radioactive-decay/calculate` |
| `KB.calculateDarwin()` | Disponível | Endpoint `POST /api/darwin/calculate` |
| `KB.getFossils()` | Disponível | Endpoint `GET /api/fossils` |
| `KB.getLayers()` | Disponível | Endpoint `GET /api/layers` |

### Pontos ainda não integrados

1. **Simulador de decaimento radioativo**: O frontend calcula localmente (fórmula matemática). O endpoint `POST /api/radioactive-decay/calculate` existe mas não é chamado pelo simulador durante a animação.
2. **Cálculo de Darwin**: O endpoint `POST /api/darwin/calculate` existe mas não há interface visual para ele no frontend.
3. **CRUD de fósseis/camadas**: Os endpoints CRUD existem mas não há formulários de administração no frontend.

---

## 9. Como Executar o Projeto

### Pré-requisitos

- **Java 17** ou superior
- **Maven** (ou Maven Wrapper: `./mvnw`)
- **PostgreSQL** instalado e rodando
- **Node.js** ou **Python** (para servir o frontend localmente)

### Criação do Banco

```sql
-- Conectar no PostgreSQL e criar o banco
CREATE DATABASE kriptobioze;
```

### Configuração do PostgreSQL

1. Verifique as credenciais em `src/main/resources/application.properties`:
   - Usuário padrão: `postgres`
   - Senha padrão: `postgres`
   - Porta: `5432`
   - Banco: `kriptobioze`

2. Ajuste conforme necessário.

### Execução do Back-end

```bash
# Opção 1: Maven wrapper
./mvnw spring-boot:run

# Opção 2: Maven instalado
mvn spring-boot:run

# Opção 3: Compilar e executar JAR
mvn clean package
java -jar target/kriptobioze-0.0.1-SNAPSHOT.jar
```

O `DataSeeder` popula automaticamente as tabelas na primeira execução (se estiverem vazias).

**URL do back-end:** `http://localhost:8080`

### Execução do Front-end

```bash
# Opção 1: Python
python -m http.server 5500 -d frontend-kriptobioze

# Opção 2: Node.js (http-server)
npx http-server frontend-kriptobioze -p 5500

# Opção 3: Live Server (VS Code)
# Instalar extensão Live Server e abrir index.html
```

**URL do front-end:** `http://localhost:5500`

### Conexão Frontend ↔ Backend

Para conectar o frontend ao backend, defina `window.KB_API_BASE` **antes** do carregamento do `app.js`:

```html
<script>window.KB_API_BASE = "http://localhost:8080";</script>
<script src="js/app.js"></script>
```

Ou defina no console do navegador antes do page load:
```javascript
window.KB_API_BASE = "http://localhost:8080";
```

### URLs Utilizadas

| Serviço | URL |
|---------|-----|
| Back-end (API) | `http://localhost:8080` |
| Front-end | `http://localhost:5500` |

---

## 10. Endpoints da API

### Isótopos

| Método | Rota | Descrição | Entrada | Saída |
|--------|------|-----------|---------|-------|
| `GET` | `/api/radioactive-decay/isotopes` | Lista todos os isótopos | - | `List<Isotope>` |
| `GET` | `/api/radioactive-decay/isotopes/{symbol}` | Busca isótopo por símbolo | `symbol` (path) | `Isotope` |
| `POST` | `/api/radioactive-decay/calculate` | Calcula decaimento radioativo | `isotopeSymbol`, `timePeriodYears` (query params) | `DecayResult` |

### Darwin

| Método | Rota | Descrição | Entrada | Saída |
|--------|------|-----------|---------|-------|
| `POST` | `/api/darwin/calculate` | Calcula tempo para atingir altitude | `altitudeMeters`, `elevationRatePerCentury` (query params) | `CalculationResult` |

### Fósseis

| Método | Rota | Descrição | Entrada | Saída |
|--------|------|-----------|---------|-------|
| `GET` | `/api/fossils` | Lista todos os fósseis | - | `List<Fossil>` |
| `GET` | `/api/fossils/{id}` | Busca fóssil por ID | `id` (path) | `Fossil` |
| `GET` | `/api/fossils/layer/{layerId}` | Fósseis de uma camada | `layerId` (path) | `List<Fossil>` |
| `GET` | `/api/fossils/estratigrafia` | INNER JOIN: fósseis por profundidade DESC | - | `List<EstratigrafiaDTO>` |
| `POST` | `/api/fossils` | Cria um fóssil | `Fossil` (body JSON) | `Fossil` |
| `PUT` | `/api/fossils/{id}` | Atualiza um fóssil | `id` (path), `Fossil` (body JSON) | `Fossil` |
| `DELETE` | `/api/fossils/{id}` | Remove um fóssil | `id` (path) | - (204 No Content) |

### Camadas

| Método | Rota | Descrição | Entrada | Saída |
|--------|------|-----------|---------|-------|
| `GET` | `/api/layers` | Lista todas as camadas | - | `List<Camada>` |
| `GET` | `/api/layers/{id}` | Busca camada por ID | `id` (path) | `Camada` |
| `POST` | `/api/layers` | Cria uma camada | `Camada` (body JSON) | `Camada` |
| `PUT` | `/api/layers/{id}` | Atualiza uma camada | `id` (path), `Camada` (body JSON) | `Camada` |
| `DELETE` | `/api/layers/{id}` | Remove uma camada | `id` (path) | - (204 No Content) |

### Exemplo de Resposta - Estratigrafia

```json
[
  {
    "fossilId": 5,
    "nomeCientifico": "Stromatólito de cianobactérias",
    "taxonomia": "Cyanobacteria (microbialito fóssil)",
    "idadeEstimadaMa": 3500,
    "nomeEra": "Arqueano",
    "profundidadeMedia": 10000,
    "tipoRocha": "Metamórfica"
  },
  {
    "fossilId": 4,
    "nomeCientifico": "Dickinsonia costata",
    "taxonomia": "Proarticulata · Dickinsoniidae",
    "idadeEstimadaMa": 560,
    "nomeEra": "Proterozóico",
    "profundidadeMedia": 5000,
    "tipoRocha": "Metamórfica"
  }
]
```

---

## 11. Fluxo de Dados

### Exemplo Completo: Tabela de Estratigrafia

```
1. Usuário abre http://localhost:5500
   ↓
2. index.html carrega app.js
   ↓
3. app.js executa boot()
   ↓
4. boot() chama KB.getEstratigrafia()
   ↓
5. fetch() envia GET http://localhost:8080/api/fossils/estratigrafia
   ↓
6. FossilController.getEstratigrafia() recebe a requisição
   ↓
7. FossilService.getEstratigrafia() é chamado
   ↓
8. FossilRepository.findAllEstratigrafiaOrderByProfundidadeDesc() executa query JPQL:
   SELECT new EstratigrafiaDTO(f.id, f.nomeCientifico, ...)
   FROM Fossil f INNER JOIN f.camada c
   ORDER BY c.profundidadeMedia DESC
   ↓
9. Hibernate traduz para SQL e consulta PostgreSQL
   ↓
10. PostgreSQL retorna as 6 linhas (camada + fossil)
    ↓
11. Jackson serializa para JSON
    ↓
12. fetch() retorna Promise com array de EstratigrafiaDTO
    ↓
13. app.js atribui: estratigrafia = rows
    ↓
14. renderEstratigrafia() renderiza a tabela HTML
    ↓
15. Usuário visualiza: Stromatólito (10.000m) → Mamute (0m)
```

### Exemplo Completo: Simulador de Decaimento

```
1. Usuário seleciona isótopo "C-14" e arrasta slider para 50%
   ↓
2. app.js calcula: state.simYears = calcYearsFromPct(50, 5730) = 5.730 anos
   ↓
3. renderAll() atualiza:
   - renderAtoms(): 45 átomos ativos, 45 decaídos
   - buildCurve(): SVG exponencial com marcador em t=5.730
   - renderReadouts(): "5.730 anos", "Cenozóico"
   ↓
4. Usuário clica "▶ Simular decaimento"
   ↓
5. tick() executa a cada 60ms:
   - state.simYears += halfLife * 0.02
   - state.pct = calcPctFromYears(state.simYears, halfLife)
   - renderAll() atualiza visualizações
   ↓
6. Simulação para quando pct < 0.05 ou simYears > 6 × halfLife
```

---

## 12. Base para Integração e Apresentação

### Principais Arquivos Envoldos na Integração

| Arquivo | Função |
|---------|--------|
| `frontend-kriptobioze/js/app.js` | Objeto `window.KB` com todas as funções de integração |
| `frontend-kriptobioze/index.html` | Elementos DOM: `#apiStatus`, `#estratigrafiaBody` |
| `src/main/java/com/kriptobioze/controller/FossilController.java` | Endpoints de fósseis e camadas |
| `src/main/java/com/kriptobioze/controller/RadioactiveDecayController.java` | Endpoints de decaimento |
| `src/main/java/com/kriptobioze/controller/DarwinController.java` | Endpoint de cálculo Darwin |
| `src/main/java/com/kriptobioze/service/FossilService.java` | Lógica de negócio de fósseis |
| `src/main/java/com/kriptobioze/service/RadioactiveDecayService.java` | Lógica de decaimento radioativo |
| `src/main/java/com/kriptobioze/repository/FossilRepository.java` | Query JPQL do INNER JOIN |

### Principais Classes

| Classe | Arquivo | Responsabilidade |
|--------|---------|------------------|
| `Camada` | `model/Camada.java` | Entidade JPA da tabela camada |
| `Fossil` | `model/Fossil.java` | Entidade JPA da tabela fossil |
| `Isotope` | `model/Isotope.java` | Entidade JPA da tabela isotopes |
| `EstratigrafiaDTO` | `dto/EstratigrafiaDTO.java` | Record de projeção do JOIN |
| `FossilService` | `service/FossilService.java` | CRUD + estratigrafia |
| `RadioactiveDecayService` | `service/RadioactiveDecayService.java` | Cálculos de decaimento |
| `DarwinCalculationService` | `service/DarwinCalculationService.java` | Cálculos de Darwin |
| `DataSeeder` | `config/DataSeeder.java` | População inicial de dados |

### Principais Funções

| Função | Arquivo | Descrição |
|--------|---------|-----------|
| `KB.getIsotopes()` | `app.js:31` | GET /api/radioactive-decay/isotopes |
| `KB.getEstratigrafia()` | `app.js:68` | GET /api/fossils/estratigrafia |
| `KB.calculateDecay()` | `app.js:36` | POST /api/radioactive-decay/calculate |
| `KB.calculateDarwin()` | `app.js:45` | POST /api/darwin/calculate |
| `renderEstratigrafia()` | `app.js:353` | Renderiza tabela HTML |
| `FossilRepository.findAllEstratigrafiaOrderByProfundidadeDesc()` | `FossilRepository.java:17` | Query JPQL INNER JOIN |
| `RadioactiveDecayService.calculateDecay()` | `RadioactiveDecayService.java:18` | Lógica de decaimento |

### Endpoints

| Endpoint | Método | Status Frontend |
|----------|--------|-----------------|
| `/api/radioactive-decay/isotopes` | GET | Consumido por `KB.getIsotopes()` |
| `/api/radioactive-decay/calculate` | POST | Disponível mas não usado no UI |
| `/api/darwin/calculate` | POST | Disponível mas não usado no UI |
| `/api/fossils` | GET | Disponível mas não usado no UI |
| `/api/fossils/estratigrafia` | GET | Consumido por `KB.getEstratigrafia()` |
| `/api/layers` | GET | Disponível mas não usado no UI |
| CRUD `/api/fossils` | POST/PUT/DELETE | Disponível mas sem UI |
| CRUD `/api/layers` | POST/PUT/DELETE | Disponível mas sem UI |

### Tabelas

| Tabela | Registros | Status |
|--------|-----------|--------|
| `camada` | 5 eras geológicas | Populada pelo DataSeeder |
| `fossil` | 6 fósseis | Populada pelo DataSeeder |
| `isotopes` | 6 isótopos | Populada pelo DataSeeder |

### Relacionamentos

- `fossil.fk_camada` → `camada.id` (1:N, NOT NULL)
- `isotopes`: tabela independente (sem FK)

### Pontos Ainda Não Integrados

1. **Simulador de decaimento**: Calcula localmente, não consome `/api/radioactive-decay/calculate`
2. **Cálculo Darwin**: Endpoint existe mas não há interface visual
3. **CRUD completo**: Endpoints existem mas não há formulários de administração
4. **Autenticação**: Não existe (API aberta com CORS `*`)

### Possíveis Inconsistências Encontradas

1. **Naming inconsistency**: Tabela SQL usa `isotopes` (plural) enquanto `camada` e `fossil` são singulares
2. **Campo `usedFor`**: Atributo em inglês na entidade `Isotope`, enquanto outras entidades usam português (`nomeEra`, `tipoRocha`)
3. **IDs dos fósseis no fallback**: `FALLBACK_ESTRATIGRAFIA` usa `fossilId: 0` para Mammuthus, mas o auto-increment do banco começa em 1
4. **Fetch lazy loading**: `Fossil.camada` usa `FetchType.LAZY`, mas a query JPQL do `findAllEstratigrafiaOrderByProfundidadeDesc` já faz JOIN, então não há problema de N+1 neste caso

### Pontos que Precisam ser Testados

1. **Integração completa**: Verificar se o frontend consome corretamente todos os endpoints quando o backend está rodando
2. **Fallback local**: Confirmar que todos os dados fallback correspondem aos dados do DataSeeder
3. **CRUD**: Testar criação, atualização e exclusão de fósseis/camadas via API (usando Postman ou curl)
4. **Decaimento radioativo**: Verificar se os cálculos do frontend batem com os do backend
5. **Performance**: Testar com volumes maiores de dados (atualmente são apenas 5 camadas e 6 fósseis)
6. **Service Worker**: Verificar cache offline e atualização do PWA

---

## Observações Finais

- **Não há funcionalidades inventadas nesta documentação**: tudo o que foi descrito existe no código
- **O backend e frontend podem operar independentemente**: o frontend funciona 100% offline com dados fallback
- **O projeto está funcional**: backend sobe, popula dados, e frontend consome a API quando conectado
- **Próximas sprints** devem focar na integração completa dos endpoints disponíveis e na criação de interfaces de administração

