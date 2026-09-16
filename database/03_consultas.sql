-- =============================================================================
-- Kriptobioze · Modelagem Estratigráfica de Fósseis e Camadas
-- 03_consultas.sql  ·  DQL — Consultas que comprovam a hierarquia estratigráfica
--
-- Princípio de Darwin (superposição): "os fósseis de camadas mais profundas são,
-- em tese, mais antigos". A consulta principal faz um INNER JOIN entre FOSSIL e
-- CAMADA e ordena pela profundidade de forma DECRESCENTE — da camada mais
-- profunda (mais antiga) para a superfície (mais recente).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1) A CONSULTA PRINCIPAL (obrigatória na proposta)
--    INNER JOIN + ORDER BY profundidade_media DESC
--    Retorno já comprova: quanto maior a profundidade_media, maior a
--    idade_estimada do fóssil (hierarquia cronológica do tempo profundo).
-- -----------------------------------------------------------------------------
SELECT
    f.id                  AS codigo_fossil,
    f.nome_cientifico,
    f.taxonomia,
    f.idade_estimada      AS idade_estimada_ma,
    c.profundidade_media  AS profundidade_media_m,
    c.nome_era,
    c.tipo_rocha
FROM fossil f
INNER JOIN camada c ON c.id = f.fk_camada
ORDER BY c.profundidade_media DESC;

-- -----------------------------------------------------------------------------
-- 2) Resumo: quantidade de fósseis por camada (com LEFT JOIN para não perder
--    as camadas que ainda não tivessem fósseis registrados).
-- -----------------------------------------------------------------------------
SELECT
    c.nome_era,
    c.profundidade_media,
    c.tipo_rocha,
    COUNT(f.id)  AS qtd_fosseis
FROM camada c
LEFT JOIN fossil f ON f.fk_camada = c.id
GROUP BY c.id, c.nome_era, c.profundidade_media, c.tipo_rocha
ORDER BY c.profundidade_media DESC;

-- -----------------------------------------------------------------------------
-- 3) Prova lógica da hierarquia: compara as colocações (rank) obtidas ao
--    ordenar por PROFUNDIDADE e por IDADE. Se a Lei da Superposição vale,
--    as duas posições são idênticas (mais profundo = mais antigo; mais
--    raso = mais recente).
-- -----------------------------------------------------------------------------
SELECT
    c.profundidade_media,
    c.nome_era,
    f.nome_cientifico,
    f.idade_estimada                    AS idade_ma,
    RANK() OVER (ORDER BY c.profundidade_media DESC) AS posicao_por_profundidade,
    RANK() OVER (ORDER BY f.idade_estimada DESC)     AS posicao_por_idade
FROM fossil f
INNER JOIN camada c ON c.id = f.fk_camada
ORDER BY c.profundidade_media DESC;