-- Applies the existing approval_history table to the active purchase_requests manager workflow.
ALTER TABLE approval_history
    MODIFY COLUMN approval_id BIGINT NULL,
    ADD COLUMN IF NOT EXISTS purchase_request_id BIGINT NULL,
    ADD COLUMN IF NOT EXISTS approval_level INT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS remarks TEXT NULL,
    MODIFY COLUMN action_taken ENUM('APPROVED', 'REJECTED', 'RETURNED', 'ESCALATED') NOT NULL;

ALTER TABLE approval_history
    ADD CONSTRAINT fk_approval_history_purchase_request
    FOREIGN KEY (purchase_request_id) REFERENCES purchase_requests(id);
