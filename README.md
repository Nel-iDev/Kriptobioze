# Kriptobioze — Do Tempo Geológico ao Algoritmo

Sistema interdisciplinar de tempo geológico e evolução (Biologia · 3°DS — Kripta).
Este repositório contém o **frontend** (PWA estático), o **backend** (Spring Boot + JPA) e agora a
**modelagem de banco de dados** da proposta *Modelagem Estratigráfica de Fósseis e Camadas (Foco:
Banco de Dados)*.

---

## 1. O que foi feito nesta entrega

Atendendo à proposta, foram entregues os **4 artefatos de modelagem de dados** na pasta [`database/`](./database):

| Arquivo | Conteúdo |
|---------|----------|
| [`database/DER.md`](./database/DER.md) | **DER** — Diagrama Entidade-Relacionamento das entidades `CAMADA` e `FOSSIL` (mermaid + modelo físico), cardinalidade `1:N` e mapeamento coluna ↔ atributo Java |
| [`database/01_schema.sql`](./database/01_schema.sql) | **DDL** — `CREATE TABLE` de `CAMADA` e `FOSSIL` com **PK** e **FK** declaradas |
| [`database/02_dados_iniciais.sql`](./database/02_dados_iniciais.sql) | **DML** — dados de demonstração (eras geológicas + fósseis em cada estrato) |
| [`database/03_consultas.sql`](./database/03_consultas.sql) | **DQL** — `INNER JOIN` entre `FOSSIL` e `CAMADA` com `ORDER BY profundidade_media DESC`, provando a hierarquia cronológica do tempo profundo |

### Entidades modeladas

```
CAMADA (ID, nome_era, profundidade_media, tipo_rocha, idade_estimada_ma, descricao)
FOSSIL (ID, nome_cientifico, taxonomia, idade_estimada, fk_camada -> CAMADA.ID, localidade_descoberta, descricao)
```

A consulta principal (que deve ser apresentada) é:

```sql
SELECT f.nome_cientifico, f.taxonomia, f.idade_estimada,
       c.nome_era, c.profundidade_media, c.tipo_rocha
FROM fossil f
INNER JOIN camada c ON c.id = f.fk_camada
ORDER BY c.profundidade_media DESC;
```

Retorno esperado com os dados de demonstração — quanto **maior a profundidade**, **maior a idade estimada**
(Stromatólito 3,5 Ga no Arqueano (10.000 m) → Mamute no Cenozóico (topo)). Isso comprova a lei de Darwin.

---

## 2. Como o banco foi integrado ao frontend já existente

A integração é de **3 camadas** e está toda funcional:

```
Frontend (PWA estático)  →  Spring Boot REST API  →  PostgreSQL (tabelas camada + fossil)
```

1. **Tabelas** — as entidades JPA `Camada` e `Fossil`
   (`src/main/java/com/kriptobioze/model/`) foram mapeadas com as **mesmas colunas** da proposta
   (`nome_era`, `profundidade_media`, `tipo_rocha`, `nome_cientifico`, `taxonomia`, `idade_estimada`, `fk_camada`).
   O JPA (`ddl-auto=update`) cria/atualiza automaticamente essas tabelas no banco `kriptobioze`.

2. **API** — o endpoint novo `GET /api/fossils/estratigrafia`
   (`FossilController` → `FossilService` → `FossilRepository`) executa na prática um `INNER JOIN`
   espelhando o DQL do `03_consultas.sql`, com `ORDER BY profundidade DESC`, e devolve
   `EstratigrafiaDTO` (mêsma projeção da consulta):
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
     ...
   ]
   ```

3. **Frontend** — a página ganhou a seção **“Estratigrafia de Fósseis e Camadas”** (`#estratigrafia`), renderizada
   por `renderEstratigrafia()` em `frontend-kriptobioze/js/app.js`. Ela consome
   `window.KB.getEstratigrafia()` e, se o backend estiver desligado, usa dados de fallback locais
   (mesmo dataset do DataSeeder), mantendo o padrão "modo local" que o simulador já usava.

> Observação: os nomes anteriores das entidades (`geological_layers`, `fossils`, campos em inglês)
> foram renomeados/mapeados para o schema da proposta. Se já existiam tabelas antigas no banco,
> podem ser removidas com `DROP TABLE IF EXISTS fossils, geological_layers;` (o Hibernate recria a nova estrutura).

---

## 3. Como rodar (passo a passo)

### 3.1 Subir o banco (PostgreSQL)

1. Instale o PostgreSQL e crie o banco: `CREATE DATABASE kriptobioze;`
2. Ajuste usuário/senha em `src/main/resources/application.properties` (padrão `postgres` / `postgres`).
3. Opcional — criar o schema manualmente (equivalente ao que o JPA gera):
   ```bash
   psql -U postgres -d kriptobioze -f database/01_schema.sql
   psql -U postgres -d kriptobioze -f database/02_dados_iniciais.sql
   ```

### 3.2 Subir o backend

```bash
mvn spring-boot:run
```

O `DataSeeder` popula isótopos, camadas e fósseis automaticamente na primeira execução
(tabelas vazias). A API sobe em `http://localhost:8080`.

### 3.3 Servir o frontend

```bash
# como estático (Backend pode servir via src/main/resources/static) ou:
python -m http.server 5500 -d frontend-kriptobioze
```

Para conectar ao backend, defina antes do `app.js`:

```html
<script>window.KB_API_BASE = "http://localhost:8080";</script>
```

O simulador de decaimento radioativo cai no **modo local** quando a API está fora; o mesmo vale
para a tabela de estratigrafia (fallback local com o resultado esperado do DQL).

---

## 4. Endpoints da API

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET`  | `/api/radioactive-decay/isotopes` | Lista os isótopos de datação |
| `POST` | `/api/radioactive-decay/calculate` | Calcula decaimento (`isotopeSymbol`, `timePeriodYears`) |
| `POST` | `/api/darwin/calculate` | Tempo para atingir altitude (`altitudeMeters`, `elevationRatePerCentury`) |
| `GET`  | `/api/fossils` | Lista os fósseis |
| `GET`  | `/api/fossils/estratigrafia` | **DQL** — fósseis por camada, `ORDER BY profundidade DESC` |
| `GET`  | `/api/fossils/layer/{camadaId}` | Fósseis de uma camada específica |
| `GET`  | `/api/layers` | Lista as camadas geológicas |
| `POST/PUT/DELETE` | `/api/fossils`, `/api/layers` | CRUD de fósseis e camadas |

---

## 5. Estrutura do projeto

```
├── database/                  ← artefatos da proposta (modelagem de banco)
│   ├── DER.md                 ← Diagrama Entidade-Relacionamento
│   ├── 01_schema.sql          ← DDL (CREATE TABLE + PK/FK)
│   ├── 02_dados_iniciais.sql  ← DML (seed)
│   └── 03_consultas.sql       ← DQL (INNER JOIN + ORDER BY profundidade DESC)
├── src/main/java/com/kriptobioze/
│   ├── model/                 ← Camada, Fossil, Isotope (JPA)
│   ├── repository/            ← CamadaRepository, FossilRepository (com a query do JOIN)
│   ├── service/               ← FossilService, DarwinCalculationService, RadioactiveDecayService
│   ├── controller/            ← FossilController, DarwinController, RadioactiveDecayController
│   ├── dto/                   ← EstratigrafiaDTO
│   └── config/DataSeeder.java ← dados iniciais (isótopos, camadas, fósseis)
├── frontend-kriptobioze/      ← PWA estático (simulador + tabela de estratigrafia)
└── README.md
```