-- ============================================
-- LIMPEZA DE DADOS EXISTENTES
-- ============================================
-- (opcional - descomente se necessário)

-- Tabelas dependentes (limpar primeiro)
DELETE FROM REVIEW;

DELETE FROM CURTIDA;

DELETE FROM POST_TAGS;

DELETE FROM POST;

DELETE FROM COMENTARIO;

DELETE FROM ITEM_BIBLIOTECA;

DELETE FROM BIBLIOTECA;

DELETE FROM RECIBO;

DELETE FROM TRANSACAO;

DELETE FROM CARTEIRA;

DELETE FROM MODS_VERSOES;

DELETE FROM MODS;

DELETE FROM JOGO_TAGS;

DELETE FROM JOGO_SCREENSHOTS;

DELETE FROM JOGO_VIDEOS;

DELETE FROM VERSAO_JOGO;

DELETE FROM JOGO;

DELETE FROM SEGUIMENTO;

DELETE FROM BLOQUEIO;

DELETE FROM TAG;

DELETE FROM CONTA;


-- ============================================
-- TAGS (Categorias de jogos)
-- ============================================
INSERT INTO
    TAG (id, nome)
VALUES ('tag-1-Aventura', 'Aventura'),
    ('tag-2-Acao', 'Ação'),
    ('tag-3-RPG', 'RPG'),
    ('tag-4-Puzzle', 'Puzzle'),
    ('tag-5-Estrategia', 'Estratégia'),
    ('tag-6-Simulacao', 'Simulação'),
    ('tag-7-Esportes', 'Esportes'),
    ('tag-8-Corrida', 'Corrida'),
    ('tag-9-Terror', 'Terror'),
    ('tag-10-Sobrevivencia', 'Sobrevivência'),
	('tag-11-Indie', 'Indie'),
	('tag-12-Multiplayer', 'Multiplayer'),
	('tag-13-Singleplayer', 'Singleplayer'),
	('tag-14MundoAberto', 'Mundo Aberto'),
	('tag-15-PixelArt', 'Pixel Art'),
	('tag-16-2D', '2D'),
	('tag-17-3D', '3D'),
	('tag-18-Roguelike', 'Roguelike'),
	('tag-19-Metroidvania', 'Metroidvania'),
	('tag-20-Plataforma', 'Plataforma'),
	('tag-21-Fantasia', 'Fantasia'),
	('tag-22-FiccaoCient9fica', 'Ficção Científica'),
	('tag-23-Medieval', 'Medieval'),
	('tag-24-Cyberpunk', 'Cyberpunk'),
	('tag-25-PosApocaliptico', 'Pós-Apocalíptico'),
	('tag-26-Casual', 'Casual'),
	('tag-27-Competitivo', 'Competitivo'),
	('tag-28-HistoriaRica', 'História Rica'),
	('tag-29-Exploracao', 'Exploração'),
	('tag-30-Crafting', 'Crafting');

-- ============================================
-- 1. CONTAS (Usuários e Desenvolvedoras)
-- ============================================

-- Jogadores
INSERT INTO CONTA (id, nome, senha_hash, status, tipo) VALUES
('jogador-1', 'gael', 'senha123', 'ATIVA', 'JOGADOR'),
('jogador-2', 'maria', 'senha123', 'ATIVA', 'JOGADOR'),
('jogador-3', 'joao', 'senha123', 'ATIVA', 'JOGADOR');

-- Desenvolvedoras
INSERT INTO CONTA (id, nome, senha_hash, status, tipo) VALUES
('dev-1', 'indiedev', 'senha123', 'ATIVA', 'DESENVOLVEDORA'),
('dev-2', 'studioabc', 'senha123', 'ATIVA', 'DESENVOLVEDORA'),
('isaac-dev', 'Edmund McMillen', 'senha123', 'ATIVA', 'DESENVOLVEDORA'),
('team-cherry', 'Team Cherry', 'senha123', 'ATIVA', 'DESENVOLVEDORA'),
('tobyfox', 'Toby Fox', 'senha123', 'ATIVA', 'DESENVOLVEDORA'),
('joeveno', 'Joe Veneziano', 'senha123', 'ATIVA', 'DESENVOLVEDORA'),
('dumativa', 'Dumativa', 'senha123', 'ATIVA', 'DESENVOLVEDORA');

-- Jogos e seus prints e suas tags :) 
INSERT INTO public.jogo
(id, capa_oficial, data_de_lancamento, descricao, desenvolvedora_id, isnsfw, slug, status, titulo)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/389170/header_brazilian.jpg?t=1761053103', '2025-12-13', 'Aventure-se no mundo do Herói em pixel art no qual as ações do jogador são cantadas de forma cômica e dinâmica, nesse jogo de plataforma e muita aventura!
', 'dumativa', false, 'a-lenda-do-heroi-edicao-definitiva', 'PUBLICADO', 'A Lenda do Herói - Edição Definitiva');
INSERT INTO public.jogo
(id, capa_oficial, data_de_lancamento, descricao, desenvolvedora_id, isnsfw, slug, status, titulo)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/header.jpg?t=1764916620', '2025-12-13', 'Forje seu caminho em Hollow Knight! Uma aventura de ação épica em um vasto reino arruinado de insetos e heróis. Explore cavernas serpenteantes, lute contra criaturas malignas e alie-se a insetos bizarros num estilo clássico 2D desenhado à mão.', 'team-cherry', false, 'hollow-knight', 'AGUARDANDO_VALIDACAO', 'Hollow Knight');

INSERT INTO public.jogo_screenshots
(jogo_id, screenshot_url)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/389170/ss_25eae7f21fbc512f43cc817562a43fcb955ff4b5.1920x1080.jpg?t=1761053103');
INSERT INTO public.jogo_screenshots
(jogo_id, screenshot_url)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/389170/ss_01e467b8cf5a167793f19213ca50e40ce1cf3c03.1920x1080.jpg?t=1761053103');
INSERT INTO public.jogo_screenshots
(jogo_id, screenshot_url)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/389170/ss_162468396eb0546c5745344d8de9b69ab016e410.1920x1080.jpg?t=1761053103');
INSERT INTO public.jogo_screenshots
(jogo_id, screenshot_url)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/ss_5384f9f8b96a0b9934b2bc35a4058376211636d2.1920x1080.jpg?t=1764916620');
INSERT INTO public.jogo_screenshots
(jogo_id, screenshot_url)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/ss_a81e4231cc8d55f58b51a4a938898af46503cae5.1920x1080.jpg?t=1764916620');
INSERT INTO public.jogo_screenshots
(jogo_id, screenshot_url)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/ss_92c7e8f34c00bdb455070ecdd5b746f0d2f6d808.1920x1080.jpg?t=1764916620');


INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'tag-20-Plataforma');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'tag-16-2D');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'tag-11-Indie');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'tag-13-Singleplayer');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('1354484b-4054-4b38-804c-b6dced029784', 'tag-1-Aventura');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'tag-10-Sobrevivencia');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'tag-19-Metroidvania');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'tag-18-Roguelike');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'tag-16-2D');
INSERT INTO public.jogo_tags
(jogo_id, tag_id)
VALUES('265e7e84-7025-41d2-a4e8-8cd1a9b4ebf8', 'tag-13-Singleplayer');