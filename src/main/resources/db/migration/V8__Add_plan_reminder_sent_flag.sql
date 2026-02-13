ALTER TABLE plan
    ADD COLUMN reminder_sent BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_plan_reminder_lookup
    ON plan (plan_date, start_time, completed, reminder_sent);
