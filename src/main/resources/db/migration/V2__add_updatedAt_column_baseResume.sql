ALTER TABLE base_resumes ADD column updated_at TIMESTAMP;
UPDATE base_resumes SET updated_at = created_at WHERE updated_at IS NULL;
ALTER TABLE base_resumes ALTER COLUMN updated_at SET NOT NULL;
