-- PvP Event: Add event_pvp column to characters table
-- Run this SQL if the column does not already exist
ALTER TABLE characters ADD COLUMN IF NOT EXISTS offline_farm_saved_title VARCHAR(21) DEFAULT NULL;

