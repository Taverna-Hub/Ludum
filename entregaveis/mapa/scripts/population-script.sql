
-- ============================================
-- TAGS (Categorias de jogos)
-- ============================================
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
('dev-2', 'studioabc', 'senha123', 'ATIVA', 'DESENVOLVEDORA');


INSERT INTO
    TAG (id, nome)
VALUES
    ('tag-1-Aventura', 'Aventura'),
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