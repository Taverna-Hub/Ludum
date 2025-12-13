-- Adiciona coluna data_seguimento na tabela SEGUIMENTO
ALTER TABLE SEGUIMENTO ADD COLUMN data_seguimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Atualiza registros existentes com a data atual
UPDATE SEGUIMENTO SET data_seguimento = CURRENT_TIMESTAMP WHERE data_seguimento IS NULL;
