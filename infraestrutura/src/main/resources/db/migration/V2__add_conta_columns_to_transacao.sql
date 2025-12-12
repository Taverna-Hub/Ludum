-- Add conta_origem_id and conta_destino_id columns to transacao table
ALTER TABLE transacao ADD COLUMN IF NOT EXISTS conta_origem_id VARCHAR(255);
ALTER TABLE transacao ADD COLUMN IF NOT EXISTS conta_destino_id VARCHAR(255);
