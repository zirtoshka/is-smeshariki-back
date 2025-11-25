
CREATE OR REPLACE FUNCTION update_last_active()
 RETURNS TRIGGER AS $$
 BEGIN
     NEW.last_active := NOW();
     RETURN NEW;
 END;
 $$ LANGUAGE plpgsql;


 CREATE TRIGGER trigger_update_last_active
 BEFORE UPDATE on smesharik
 FOR EACH ROW
 WHEN ( NEW.is_online = true )
 EXECUTE FUNCTION update_last_active();

 CREATE OR REPLACE FUNCTION update_publication_time()
 RETURNS TRIGGER AS $$
 BEGIN
     NEW.publication_date := NOW();
     RETURN NEW;
 END;
 $$ LANGUAGE plpgsql;


 CREATE TRIGGER trigger_update_publication_time
 BEFORE UPDATE on post
 FOR EACH ROW
 WHEN (NEW.is_draft = false and OLD.is_draft IS DISTINCT FROM NEW.is_draft )
 EXECUTE FUNCTION update_publication_time();


 CREATE OR REPLACE FUNCTION cancel_application_on_delete()
 RETURNS TRIGGER AS $$
 BEGIN
     UPDATE application_for_treatment
     SET status = 'canceled'
     WHERE post_id = OLD.id OR comment_id = OLD.id;
     RETURN OLD;
 END;
 $$ LANGUAGE plpgsql;

 CREATE TRIGGER trigger_cancel_application_on_delete_post
 AFTER DELETE ON post
 FOR EACH ROW
 EXECUTE FUNCTION cancel_application_on_delete();

 CREATE TRIGGER trigger_cancel_application_on_delete_comment
 AFTER DELETE ON comment
 FOR EACH ROW
 EXECUTE FUNCTION cancel_application_on_delete();


 CREATE OR REPLACE FUNCTION create_application_for_threatment_post()
 RETURNS TRIGGER AS $$
 DECLARE
 	application_id int8;
 	prop_id int8;
 	application_propensity_id int8;
 BEGIN
     SELECT apt.id INTO application_id
     FROM application_for_treatment AS apt
     WHERE apt.post_id = NEW.post_id;

     IF application_id IS NULL THEN
         INSERT INTO application_for_treatment (post_id)
         VALUES (NEW.post_id)
         RETURNING id INTO application_id;
     END IF;

     SELECT tgw.propensity_id INTO prop_id
     FROM trigger_word AS tgw
     WHERE tgw.id = NEW.trigger_word_id;


     SELECT aftp.id INTO application_propensity_id
     FROM application_for_treatment_propensity AS aftp
     WHERE aftp.application_for_treatment_id = application_id
       AND aftp.propensity_id = prop_id;

     IF application_propensity_id IS NULL THEN
         INSERT INTO application_for_treatment_propensity (application_for_treatment_id, propensity_id)
         VALUES (application_id, prop_id);
     END IF;

     RETURN NEW;
 END;
 $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_after_insert_post_trigger_word
AFTER INSERT ON post_trigger_word
FOR EACH ROW
EXECUTE FUNCTION create_application_for_threatment_post();

CREATE OR REPLACE FUNCTION create_application_for_threatment_comment()
 RETURNS TRIGGER AS $$
 DECLARE
 	application_id int8;
 	prop_id int8;
 	application_propensity_id int8;
 BEGIN
     SELECT apt.id INTO application_id
     FROM application_for_treatment AS apt
     WHERE apt.comment_id = NEW.comment_id;

     IF application_id IS NULL THEN
         INSERT INTO application_for_treatment (comment_id)
         VALUES (NEW.comment_id)
         RETURNING id INTO application_id;
     END IF;

     SELECT tgw.propensity_id INTO prop_id
     FROM trigger_word AS tgw
     WHERE tgw.id = NEW.trigger_word_id;


     SELECT aftp.id INTO application_propensity_id
     FROM application_for_treatment_propensity AS aftp
     WHERE aftp.application_for_treatment_id = application_id
       AND aftp.propensity_id = prop_id;

     IF application_propensity_id IS NULL THEN
         INSERT INTO application_for_treatment_propensity (application_for_treatment_id, propensity_id)
         VALUES (application_id, prop_id);
     END IF;

     RETURN NEW;
 END;
 $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_after_insert_comment_trigger_word
AFTER INSERT ON comment_trigger_word
FOR EACH ROW
EXECUTE FUNCTION create_application_for_threatment_comment();

 CREATE OR REPLACE FUNCTION update_material_views_ban()
 RETURNS TRIGGER AS $$
 BEGIN
     RAISE NOTICE 'Refreshing ban views';
     REFRESH MATERIALIZED VIEW smesharik_ban;

     REFRESH MATERIALIZED VIEW post_ban;

     REFRESH MATERIALIZED VIEW comment_ban;

     RETURN NEW;
 END;
 $$ LANGUAGE plpgsql;

 CREATE TRIGGER trigger_update_bans
 AFTER UPDATE OR insert or delete ON ban
 FOR EACH statement
 EXECUTE function  update_material_views_ban();
