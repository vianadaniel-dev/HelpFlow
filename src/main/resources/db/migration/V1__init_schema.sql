CREATE TABLE users (
    id              UUID PRIMARY KEY,
    document        VARCHAR(30)  NOT NULL UNIQUE,
    corporate_email VARCHAR(254) NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    headquarters    VARCHAR(80)  NOT NULL,
    area            VARCHAR(80)  NOT NULL,
    status          VARCHAR(10)  NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE technicians (
    id              UUID PRIMARY KEY,
    document        VARCHAR(30)  NOT NULL UNIQUE,
    corporate_email VARCHAR(254) NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    headquarters    VARCHAR(80)  NOT NULL,
    specialty       VARCHAR(20)  NOT NULL CHECK (specialty IN ('HARDWARE','SOFTWARE','NETWORK','ACCESS','GENERALIST')),
    support_profile VARCHAR(10)  NOT NULL CHECK (support_profile IN ('LEVEL_1','LEVEL_2','LEVEL_3')),
    status          VARCHAR(10)  NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE tickets (
    id                     UUID PRIMARY KEY,
    requester_id           UUID         NOT NULL REFERENCES users (id),
    title                  VARCHAR(200) NOT NULL,
    description            TEXT         NOT NULL,
    category               VARCHAR(10)  NOT NULL CHECK (category IN ('HARDWARE','SOFTWARE','NETWORK','ACCESS','OTHER')),
    priority               VARCHAR(10)  NOT NULL CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    status                 VARCHAR(12)  NOT NULL CHECK (status IN ('OPEN','ASSIGNED','EN_PROGRESS','RESOLVED','CLOSED','CANCELED')),
    headquarters           VARCHAR(80)  NOT NULL,
    created_at             TIMESTAMPTZ  NOT NULL,
    sla_deadline           TIMESTAMPTZ  NOT NULL,
    assigned_technician_id UUID REFERENCES technicians (id),
    escalation_count       INT          NOT NULL DEFAULT 0
);
CREATE INDEX idx_tickets_sla ON tickets (status, sla_deadline);

CREATE TABLE ticket_assets (
    ticket_id UUID         NOT NULL REFERENCES tickets (id),
    asset     VARCHAR(100) NOT NULL,
    PRIMARY KEY (ticket_id, asset)
);

CREATE TABLE assignments (
    id                UUID PRIMARY KEY,
    ticket_id         UUID        NOT NULL REFERENCES tickets (id),
    technician_id     UUID        NOT NULL REFERENCES technicians (id),
    estimated_minutes INT         NOT NULL,
    assigned_at       TIMESTAMPTZ NOT NULL,
    active            BOOLEAN     NOT NULL,
    released_at       TIMESTAMPTZ
);
-- a ticket can have at most one active assignment
CREATE UNIQUE INDEX uq_assignment_active_ticket ON assignments (ticket_id) WHERE active;
CREATE INDEX idx_assignment_active_tech ON assignments (technician_id) WHERE active;

CREATE TABLE ticket_history (
    id           UUID PRIMARY KEY,
    ticket_id    UUID        NOT NULL REFERENCES tickets (id),
    action       VARCHAR(25) NOT NULL,
    from_status  VARCHAR(12),
    to_status    VARCHAR(12),
    observation  TEXT,
    performed_by UUID,                -- NULL = system
    occurred_at  TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_history_ticket ON ticket_history (ticket_id, occurred_at);
