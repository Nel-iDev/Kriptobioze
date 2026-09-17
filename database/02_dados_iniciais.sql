-- =============================================================================
-- Kriptobioze · Modelagem Estratigráfica de Fósseis e Camadas
-- 02_dados_iniciais.sql  ·  DML — Dados de demonstração
--
-- Os valores espelham o DataSeeder do backend (com.kriptobioze.config.DataSeeder).
-- As idades estão em milhões de anos (Ma) e a profundidade, em metros (m).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- ISÓTOS (usados pelo simulador de decaimento radioativo)
-- -----------------------------------------------------------------------------
INSERT INTO isotopes (name, symbol, half_life_years, description, used_for) VALUES
('Carbono-14',    'C-14',    5730.0,         'Isótopo radioativo do carbono utilizado para datar materiais orgânicos com até ~50.000 anos.', 'Datação de fósseis e materiais orgânicos recentes'),
('Urânio-235',    'U-235',   703800000.0,    'Isótopo radioativo do urânio com meia-vida de ~703,8 milhões de anos.', 'Datação de rochas muito antigas e meteoritos'),
('Urânio-238',    'U-238',   4468000000.0,   'Isótopo mais abundante do urânio natural com meia-vida de ~4,468 bilhões de anos.', 'Datação de rochas ígneas e metamórficas'),
('Potássio-40',   'K-40',    1250000000.0,   'Isótopo radioativo do potássio com meia-vida de ~1,25 bilhão de anos.', 'Datação de rochas minerais e fósseis antigos'),
('Rubídio-87',    'Rb-87',   48800000000.0,  'Isótopo radioativo do rubídio com meia-vida de ~48,8 bilhões de anos.', 'Datação de rochas muito antigas e evolução estelar'),
('Samário-147',   'Sm-147',  106000000000.0, 'Isótopo radioativo do samário com meia-vida de ~106 bilhões de anos.', 'Datação de meteoritos e formação do sistema solar');

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