create schema if not exists public;


drop type if exists smesharik_role cascade;
drop type if exists friend_status cascade;
drop type if exists general_status cascade;
drop type if exists violation_type cascade;

DROP TABLE IF EXISTS smesharik CASCADE;
DROP TABLE IF EXISTS application_for_treatment CASCADE;
DROP TABLE IF EXISTS application_for_treatment_propensity CASCADE;
DROP TABLE IF EXISTS ban CASCADE;
DROP TABLE IF EXISTS carrot CASCADE;
DROP TABLE IF EXISTS comment CASCADE;
DROP TABLE IF EXISTS complaint CASCADE;
DROP TABLE IF EXISTS friend CASCADE;
DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS post_trigger_word CASCADE;
DROP TABLE IF EXISTS comment_trigger_word CASCADE;
DROP TABLE IF EXISTS propensity CASCADE;
DROP TABLE IF EXISTS trigger_word CASCADE;

CREATE TYPE smesharik_role AS ENUM ('ADMIN', 'USER', 'DOCTOR');
CREATE type friend_status AS ENUM ('NEW', 'FRIENDS');
CREATE type general_status AS ENUM ('NEW', 'IN_PROGRESS', 'DONE', 'CANCELED');
CREATE TYPE violation_type AS ENUM ('SPAM', 'EROTIC_CONTENT', 'VIOLENCE', 'HONEY', 'FRAUD_OR_MISLEADING');

CREATE CAST (varchar AS smesharik_role) WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS general_status) WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS violation_type) WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS friend_status) WITH INOUT AS IMPLICIT;

CREATE table if not exists smesharik
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(64)    NOT NULL,
    login       VARCHAR(64)    NOT NULL UNIQUE,
    password    VARCHAR(256)   NOT NULL,
    email       VARCHAR(128)   NOT NULL UNIQUE,
    role        smesharik_role NOT null default 'USER',
    is_online   BOOLEAN        NOT null default false,
    last_active TIMESTAMP      NOT null default CURRENT_TIMESTAMP,
    color       VARCHAR(16)    NOT null default '#91b3f2',
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CHECK (color ~* '\#[A-Fa-f0-9]{6}'),
    CHECK (name <> ''),
    CHECK (login <> '')

);

CREATE TABLE if not exists friend
(
    id          BIGSERIAL PRIMARY KEY,
    followee_id BIGINT        NOT NULL REFERENCES smesharik (id) ON DELETE CASCADE,
    follower_id BIGINT        NOT NULL REFERENCES smesharik (id) ON DELETE CASCADE,
    status      friend_status NOT null default 'NEW',
    UNIQUE (followee_id, follower_id),
    check (follower_id <> followee_id)
);

CREATE table if not exists post
(
    id               BIGSERIAL PRIMARY KEY,
    author_id        BIGINT    NOT NULL REFERENCES smesharik (id) ON DELETE CASCADE,
    is_draft         BOOLEAN   NOT null default true,
    text             VARCHAR(4096),
    private          BOOLEAN   NOT null default true,
    path_to_image    VARCHAR(256),
    publication_date TIMESTAMP          default current_timestamp,
    creation_date    TIMESTAMP NOT null default current_timestamp,
    CHECK (text IS NOT NULL OR path_to_image IS NOT NULL),
    CHECK (text <> ''),
    CHECK (path_to_image <> '')
);

CREATE table if not exists comment
(
    id            BIGSERIAL PRIMARY KEY,
    smesharik_id  BIGINT       NOT NULL REFERENCES smesharik (id) ON DELETE CASCADE,
    post_id       BIGINT REFERENCES post (id) ON DELETE CASCADE,
    comment_id    BIGINT REFERENCES comment (id) ON DELETE CASCADE,
    text          VARCHAR(512) not null,
    creation_date TIMESTAMP    NOT null default current_timestamp,
    CHECK (post_id IS NOT NULL OR comment_id IS NOT NULL),
    CHECK (post_id IS NULL OR comment_id IS NULL),
    CHECK (text <> '')
);

CREATE table if not exists ban
(
    id            BIGSERIAL PRIMARY KEY,
    reason        VARCHAR(512) not null,
    smesharik_id  BIGINT REFERENCES smesharik (id) ON DELETE cascade,
    post_id       BIGINT REFERENCES post (id) ON DELETE cascade,
    comment_id    BIGINT REFERENCES comment (id) ON DELETE cascade,
    creation_date TIMESTAMP    NOT null default current_timestamp,
    end_date      TIMESTAMP    NOT null default current_timestamp + interval '1 hour',
    CHECK (smesharik_id IS NOT NULL OR post_id IS NOT NULL OR comment_id IS NOT NULL),
    CHECK ((smesharik_id IS NOT NULL)::int + (post_id IS NOT NULL)::int + (comment_id IS NOT NULL)::int = 1),
    check (reason <> '')
);

CREATE table if not exists notification
(
    id                 BIGSERIAL PRIMARY KEY,
    notification_date  TIMESTAMP NOT null default current_timestamp,
    smesharik_id       BIGINT    NOT NULL REFERENCES smesharik (id) ON DELETE CASCADE,
    notification_count INT       NOT null default 1
);

CREATE table if not exists carrot
(
    id            BIGSERIAL PRIMARY KEY,
    smesharik_id  BIGINT    NOT NULL REFERENCES smesharik (id) ON DELETE CASCADE,
    post_id       BIGINT REFERENCES post (id) ON DELETE CASCADE,
    comment_id    BIGINT REFERENCES comment (id) ON DELETE CASCADE,
    creation_date TIMESTAMP NOT null default current_timestamp,
    CHECK (post_id IS NOT NULL OR comment_id IS NOT NULL),
    CHECK (post_id IS NULL OR comment_id IS NULL),
    CONSTRAINT unique_carrot_post UNIQUE (smesharik_id, post_id),
    CONSTRAINT unique_carrot_comment UNIQUE (smesharik_id, comment_id)
);

CREATE TABLE if not exists application_for_treatment
(
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT UNIQUE REFERENCES post (id) ON DELETE CASCADE,
    comment_id BIGINT UNIQUE REFERENCES comment (id) ON DELETE CASCADE,
    doctor_id  BIGINT REFERENCES smesharik (id) ON DELETE CASCADE,
    status     general_status NOT null default 'NEW',
    CHECK (post_id IS NOT NULL OR comment_id IS NOT NULL),
    CHECK (post_id IS NULL OR comment_id IS NULL)
);

CREATE table if not exists propensity
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    description VARCHAR(1024),
    check (name <> '')
);

CREATE TABLE if not exists trigger_word
(
    id            BIGSERIAL PRIMARY KEY,
    word          VARCHAR(64) NOT NULL,
    propensity_id BIGINT      NOT NULL REFERENCES propensity (id) ON DELETE cascade,
    check (word <> '')

);

CREATE table if not exists post_trigger_word
(
    id              BIGSERIAL PRIMARY KEY,
    post_id         BIGINT NOT NULL REFERENCES post (id) ON DELETE CASCADE,
    trigger_word_id BIGINT NOT NULL REFERENCES trigger_word (id) ON DELETE CASCADE
);

CREATE table if not exists comment_trigger_word
(
    id              BIGSERIAL PRIMARY KEY,
    comment_id      BIGINT NOT NULL REFERENCES comment (id) ON DELETE CASCADE,
    trigger_word_id BIGINT NOT NULL REFERENCES trigger_word (id) ON DELETE CASCADE
);

CREATE table if not exists application_for_treatment_propensity
(
    id                           BIGSERIAL PRIMARY KEY,
    application_for_treatment_id BIGINT NOT NULL REFERENCES application_for_treatment (id) ON DELETE CASCADE,
    propensity_id                BIGINT NOT NULL REFERENCES propensity (id) ON DELETE CASCADE
);

CREATE table if not exists complaint
(
    id             BIGSERIAL PRIMARY KEY,
    violation_type violation_type NOT null default 'SPAM',
    description    VARCHAR(1024),
    admin_id       BIGINT         REFERENCES smesharik (id) ON DELETE SET NULL,
    post_id        BIGINT REFERENCES post (id) ON DELETE CASCADE,
    comment_id     BIGINT REFERENCES comment (id) ON DELETE CASCADE,
    status         general_status NOT null default 'NEW',
    creation_date  TIMESTAMP      NOT null default current_timestamp,
    closing_date   TIMESTAMP,
    CHECK (post_id IS NOT NULL OR comment_id IS NOT NULL),
    CHECK (post_id IS NULL OR comment_id IS NULL)
);

CREATE INDEX comment_creation_date_idx ON comment (creation_date);
CREATE INDEX comment_post_idx ON comment(post_id);
CREATE INDEX comment_parent_comment_idx ON comment(comment_id);

CREATE INDEX carrot_comment_idx ON carrot(comment_id);
CREATE INDEX carrot_post_idx ON carrot(post_id);

CREATE INDEX post_publication_date_idx on post(publication_date);
CREATE INDEX post_author_idx ON post (author_id);

CREATE INDEX friend_follower_idx ON friend (follower_id);
CREATE INDEX friend_followee_idx ON friend (followee_id);

-- bans
create materialized view if not exists smesharik_ban as
select s.id, b.end_date from smesharik s
left join ban as b on s.id = b.smesharik_id
where b.end_date > now();

create materialized view if not exists post_ban as
select p.id, b.end_date from post p
left join ban as b on p.id = b.post_id
where b.end_date > now();

create materialized view if not exists comment_ban as
select c.id, b.end_date from "comment" c
left join ban as b on c.id = b.comment_id
where b.end_date > now();

-- CREATE OR REPLACE FUNCTION update_last_active()
--  RETURNS TRIGGER AS $$
--  BEGIN
--      NEW.last_active := NOW();
--      RETURN NEW;
--  END;
--  $$ LANGUAGE plpgsql;
--
--
--  CREATE TRIGGER trigger_update_last_active
--  BEFORE UPDATE on smesharik
--  FOR EACH ROW
--  WHEN ( NEW.is_online = false and OLD.is_online IS DISTINCT FROM NEW.is_online )
--  EXECUTE FUNCTION update_last_active();
--
--  CREATE OR REPLACE FUNCTION update_publication_time()
--  RETURNS TRIGGER AS $$
--  BEGIN
--      NEW.publication_date := NOW();
--      RETURN NEW;
--  END;
--  $$ LANGUAGE plpgsql;
--
--
--  CREATE TRIGGER trigger_update_publication_time
--  BEFORE UPDATE on post
--  FOR EACH ROW
--  WHEN (NEW.is_draft = false and OLD.is_draft IS DISTINCT FROM NEW.is_draft )
--  EXECUTE FUNCTION update_publication_time();
--
--
--  CREATE OR REPLACE FUNCTION cancel_application_on_delete()
--  RETURNS TRIGGER AS $$
--  BEGIN
--      UPDATE application_for_treatment
--      SET status = 'canceled'
--      WHERE post_id = OLD.id OR comment_id = OLD.id;
--      RETURN OLD;
--  END;
--  $$ LANGUAGE plpgsql;
--
--  CREATE TRIGGER trigger_cancel_application_on_delete_post
--  AFTER DELETE ON post
--  FOR EACH ROW
--  EXECUTE FUNCTION cancel_application_on_delete();
--
--  CREATE TRIGGER trigger_cancel_application_on_delete_comment
--  AFTER DELETE ON comment
--  FOR EACH ROW
--  EXECUTE FUNCTION cancel_application_on_delete();
--
--
--  CREATE OR REPLACE FUNCTION create_application_for_threatment_post()
--  RETURNS TRIGGER AS $$
--  DECLARE
--  	application_id int8;
--  	prop_id int8;
--  	application_propensity_id int8;
--  BEGIN
--      SELECT apt.id INTO application_id
--      FROM application_for_treatment AS apt
--      WHERE apt.post_id = NEW.post_id;
--
--      IF application_id IS NULL THEN
--          INSERT INTO application_for_treatment (post_id)
--          VALUES (NEW.post_id)
--          RETURNING id INTO application_id;
--      END IF;
--
--      SELECT tgw.propensity_id INTO prop_id
--      FROM trigger_word AS tgw
--      WHERE tgw.id = NEW.trigger_word_id;
--
--
--      SELECT aftp.id INTO application_propensity_id
--      FROM application_for_treatment_propensity AS aftp
--      WHERE aftp.application_for_treatment_id = application_id
--        AND aftp.propensity_id = prop_id;
--
--      IF application_propensity_id IS NULL THEN
--          INSERT INTO application_for_treatment_propensity (application_for_treatment_id, propensity_id)
--          VALUES (application_id, prop_id);
--      END IF;
--
--      RETURN NEW;
--  END;
--  $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_after_insert_post_trigger_word
-- AFTER INSERT ON post_trigger_word
-- FOR EACH ROW
-- EXECUTE FUNCTION create_application_for_threatment_post();
--
-- CREATE OR REPLACE FUNCTION create_application_for_threatment_comment()
--  RETURNS TRIGGER AS $$
--  DECLARE
--  	application_id int8;
--  	prop_id int8;
--  	application_propensity_id int8;
--  BEGIN
--      SELECT apt.id INTO application_id
--      FROM application_for_treatment AS apt
--      WHERE apt.comment_id = NEW.comment_id;
--
--      IF application_id IS NULL THEN
--          INSERT INTO application_for_treatment (comment_id)
--          VALUES (NEW.comment_id)
--          RETURNING id INTO application_id;
--      END IF;
--
--      SELECT tgw.propensity_id INTO prop_id
--      FROM trigger_word AS tgw
--      WHERE tgw.id = NEW.trigger_word_id;
--
--
--      SELECT aftp.id INTO application_propensity_id
--      FROM application_for_treatment_propensity AS aftp
--      WHERE aftp.application_for_treatment_id = application_id
--        AND aftp.propensity_id = prop_id;
--
--      IF application_propensity_id IS NULL THEN
--          INSERT INTO application_for_treatment_propensity (application_for_treatment_id, propensity_id)
--          VALUES (application_id, prop_id);
--      END IF;
--
--      RETURN NEW;
--  END;
--  $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_after_insert_comment_trigger_word
-- AFTER INSERT ON comment_trigger_word
-- FOR EACH ROW
-- EXECUTE FUNCTION create_application_for_threatment_comment();
--
--  CREATE OR REPLACE FUNCTION update_material_views_ban()
--  RETURNS TRIGGER AS $$
--  BEGIN
--      RAISE NOTICE 'Refreshing ban views';
--      REFRESH MATERIALIZED VIEW smesharik_ban;
--
--      REFRESH MATERIALIZED VIEW post_ban;
--
--      REFRESH MATERIALIZED VIEW comment_ban;
--
--      RETURN NEW;
--  END;
--  $$ LANGUAGE plpgsql;
--
--  CREATE TRIGGER trigger_update_bans
--  AFTER UPDATE OR insert or delete ON ban
--  FOR EACH statement
--  EXECUTE function  update_material_views_ban();
