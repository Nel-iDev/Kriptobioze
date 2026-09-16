-- =============================================================================
-- Kriptobioze · Modelagem Estratigráfica de Fósseis e Camadas
-- 02_dados_iniciais.sql  ·  DML — Dados de demonstração
--
-- Os valores espelham o DataSeeder do backend (com.kriptobioze.config.DataSeeder).
-- As idades estão em milhões de anos (Ma) e a profundidade, em metros (m).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- CAMADAS (mais profunda ----------------------------------------> mais antiga)
-- -----------------------------------------------------------------------------
INSERT INTO camada (nome_era, profundidade_media, tipo_rocha, idade_estimada_ma, descricao) VALUES
('Cenozóico',    0.0,     'Sedimentar',  66,   'Era mais recente - inclui período quaternário e terciário'),
('Mesozóico',    500.0,   'Sedimentar',  252,  'Era dos dinossauros - triássico, jurássico e cretáceo'),
('Paleozóico',   1500.0,  'Sedimentar',  541,  'Era da vida antiga - cambriano até permiano'),
('Proterozóico', 5000.0,  'Metamórfica', 2500, 'Era da vida primitiva - começo da vida complexa'),
('Arqueano',     10000.0, 'Metamórfica', 4000, 'Era mais antiga - formação da crosta terrestre');

-- -----------------------------------------------------------------------------
-- FÓSSEIS (profundidade da camada cresce => idade_estimada cresce)
-- -----------------------------------------------------------------------------
INSERT INTO fossil (nome_cientifico, taxonomia, idade_estimada, fk_camada, localidade_descoberta, descricao) VALUES
-- Superfície / Cenozóico
('Mammuthus primigenius',        'Mammalia · Proboscidea',         0.04, (SELECT id FROM camada WHERE nome_era = 'Cenozóico'),
    'Sibéria, Norte da Ásia',     'Mamute-lanudo do fim do Quaternário'),
-- Mesozóico
('Tyrannosaurus rex',            'Dinosauria · Theropoda',         66.0, (SELECT id FROM camada WHERE nome_era = 'Mesozóico'),
    'Formação Hell Creek, EUA',   'Cretáceo Superior, era dos dinossauros'),
('Triceratops horridus',         'Dinosauria · Ceratopsidae',      68.0, (SELECT id FROM camada WHERE nome_era = 'Mesozóico'),
    'Formação Lance, EUA',        'Cretáceo Superior, contemporâneo do T. rex'),
-- Paleozóico
('Paradoxides sp.',              'Trilobita · Paradoxididae',      505.0, (SELECT id FROM camada WHERE nome_era = 'Paleozóico'),
    'Folhelhos do Cambriano, Europa', 'Trilobite típico da Explosão Cambriana'),
-- Proterozóico
('Dickinsonia costata',          'Proarticulata · Dickinsoniidae', 560.0, (SELECT id FROM camada WHERE nome_era = 'Proterozóico'),
    'Bioma de Ediacara, Austrália', 'Animal de corpo mole do Ediacarano'),
-- Arqueano (a mais profunda => a mais antiga)
('Stromatólito de cianobactérias','Cyanobacteria (microbialito fóssil)', 3500.0, (SELECT id FROM camada WHERE nome_era = 'Arqueano'),
    'Pilbara Craton, Austrália',  'Vestígios de vida fotossintética do Arqueano');