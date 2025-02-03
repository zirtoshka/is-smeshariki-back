INSERT INTO smesharik (name, login, password, email, role, is_online, last_active)
VALUES
    ('Совунья', 'sovunya', '$2a$12$572hV1YC5TWxACAgTSVTJeAF6CQ/3b31C4P2HNExgbv/qd26E8gcq', 'sovunya@example.com', 'DOCTOR', true, CURRENT_TIMESTAMP  - interval '5 days'),
    ('Крош', 'krosh', '$2a$12$RZLDarMx6np2nXGUmDdxzuRWr7ofIUXvQa8WJiXlsIWc1zrzucXMa', 'andreivydra@yandex.ru', 'USER', false, CURRENT_TIMESTAMP  - interval '1 days'),
    ('Копатыч', 'kopatych', '$2a$12$i0pQgE3hOCd5qmQBtkDewOk5D/lWjjIW8TGcy87LGc6hosbh.pb3m', 'zirtoshka@yandex.ru', 'ADMIN', false, CURRENT_TIMESTAMP  - interval '1 days'),
    ('Лосяш', 'losyash', '$2a$12$IWePMWXbo4QsMzLKiSYDuuUGLAH12r5UKBY87lJJhqHXgQ5Jz7v4G', 'losyash@example.com', 'USER', false, CURRENT_TIMESTAMP  - interval '1 days'),
    ('Ежик', 'ezhik', '$2a$12$QM30TYscVKxacXIvSpBhXuQ77zqWx3xt9hspTQs0P.2IqD4N7YvM6', 'ezhik@example.com', 'USER', true, CURRENT_TIMESTAMP  - interval '1 days'),
    ('Пин', 'pin', '$2a$12$vZVmv6osHJ/34EhuT9bvN.WHOuCKblgc9m9CTE1xcqtwthySC.cvG', 'pin@example.com', 'DOCTOR', true, CURRENT_TIMESTAMP  - interval '1 days'),
    ('Нюша', 'nusha', '$2a$12$bTN4O6ZjbQP4bwnyFKU4Wexf8blrsO09CmGiL0UK2L2/gPIjpChQS', 'nusha@example.com', 'USER', false, CURRENT_TIMESTAMP  - interval '1 days'),
    ('Бараш', 'barash', '$2a$12$BZNd5XuGR5IugXHpfCK4veRqzvWzDk1BcGRN6M0UJRnYGXl1Kwdk2', 'barash@example.com', 'USER', true, CURRENT_TIMESTAMP  - interval '5 days');

INSERT INTO friend (followee_id, follower_id, status)
VALUES
    (1, 2, 'FRIENDS'), -- Совунья и Крош друзья
    (3, 1, 'FRIENDS'), -- Копатыч и Совунья друзья
    (4, 5, 'NEW'), -- Лосяш и Ежик заявка
    (7, 6, 'FRIENDS'), -- Нюша и Пин друзья
    (3, 8, 'FRIENDS'); -- Копатыч и Бараш заявка

INSERT INTO post (author_id, is_draft, text, private, path_to_image)
VALUES
    (1, false, 'Совунья делится секретами успеха для здоровья! Убить всех врагов. Ударить их до смерти.', false, null), -- Пост 1 содержит триггерные слова "убить" и "ударить"
    (2, true, 'Крош в поисках нового приключения! Смерть наступит скоро, и все это будет непредсказуемо.', true, null), -- Пост 2 содержит триггерное слово "смерть"
    (3, false, 'Копатыч открыл свой сад для всех желающих!', false, null),
    (4, true, 'Лосяш обсуждает философию жизни в лесу.', true, null),
    (5, false, 'Ежик поехал в лес за грибами!', true, null),
    (6, false, 'Пин расскажет, как быть хорошим врачом в лесу.', true, null),
    (7, true, 'Нюша изучает новые тенденции моды для смешариков!', true, null),
    (8, false, 'Бараш читает свою новую книгу о мире в лесу.', false, null);


INSERT INTO comment (smesharik_id, post_id, comment_id, text)
VALUES
    (2, 1, null, 'Очень интересный пост, Совунья! Спасибо за советы! Это что-то вроде петли вокруг шеи.'),  -- Комментарий 1 содержит слово "петля"
    (3, 1, null, 'Совунья, твой опыт невероятен! Я чувствую себя суицидальным.'),  -- Комментарий 2 содержит слово "суицидальный"
    (1, null, 2, 'Копатыч, респект! Ножки, такие нежные, такие идеальные.'),  -- Комментарий 3 содержит слово "ножки"
    (1, 2, null, 'Крош, твои приключения всегда захватывают!'),
    (7, 6, null, 'Пин, твои советы всегда на высоте, спасибо!'),
    (6, null, 5, 'Нюша, спасибо!');


INSERT INTO ban (reason, smesharik_id, post_id, comment_id, creation_date, end_date)
VALUES
    ('Мошенничество', null, 8, null,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '1 hour'),
    ('Спам', 5, null, null,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '1 day'),
    ('Угроза', null, null, 6,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '1 day');


INSERT INTO notification (smesharik_id, notification_count)
VALUES
    (1, 3),
    (2, 5),
    (3, 1),
    (4, 2),
    (5, 4),
    (6, 1),
    (7, 0),
    (8, 6);


INSERT INTO carrot (smesharik_id, post_id, comment_id, creation_date)
VALUES
    (1, 1, null, CURRENT_TIMESTAMP),
    (1, null, 1, CURRENT_TIMESTAMP),
    (1, null, 2, CURRENT_TIMESTAMP),

    (2, 1, null, CURRENT_TIMESTAMP),
    (2, null, 1, CURRENT_TIMESTAMP),

    (3, 1, null, CURRENT_TIMESTAMP);

INSERT INTO complaint (violation_type, description, admin_id, post_id, comment_id, status, creation_date)
VALUES
    ('SPAM', 'Спам в посте', null, 1, null, 'NEW', CURRENT_TIMESTAMP),  -- Жалоба на пост 1
    ('EROTIC_CONTENT', 'Неприемлемый контент в комментарии', 3, null, 1, 'IN_PROGRESS', CURRENT_TIMESTAMP),  -- Жалоба на комментарий 1
    ('VIOLENCE', 'Призыв к насилию в посте', 3, 2, null, 'CANCELED', CURRENT_TIMESTAMP),  -- Жалоба на пост 2
    ('HONEY', 'Мошенничество', null, null, 5, 'NEW', CURRENT_TIMESTAMP);  -- Жалоба на комментарий 5

INSERT INTO propensity (name, description)
VALUES
    ('Насилие', 'Насилие'),
    ('Суицидальные наклонности', 'Суицид'),
    ('Перверсии', 'Перверсии'),
    ('Асоциальность', 'Асоциальность');

INSERT INTO trigger_word (word, propensity_id)
VALUES
    ('убить', 1),
    ('ударить', 1),
    ('смерть', 2),
    ('петля', 2),
    ('суицидальный', 2),
    ('ножки', 3),
    ('аморальное', 4);

INSERT INTO post_trigger_word (post_id, trigger_word_id)
VALUES
    (1, 1),  -- Пост 1 содержит слово "убить"
    (1, 2),  -- Пост 1 содержит слово "ударить"
    (2, 3);  -- Пост 2 содержит слово "смерть"

INSERT INTO comment_trigger_word (comment_id, trigger_word_id)
VALUES
    (1, 4),  -- Комментарий 1 содержит слово "петля"
    (2, 5),  -- Комментарий 2 содержит слово "суицидальный"
    (3, 6);  -- Комментарий 3 содержит слово "ножки"


-- Заявка на лечение для поста 1 (врач - Пин)
INSERT INTO application_for_treatment (post_id, doctor_id, status)
VALUES (1, 6, 'NEW'),
       (2, 1, 'NEW');

-- Заявка на лечение для комментария 1 (врач - Пин)
INSERT INTO application_for_treatment (comment_id, doctor_id, status)
VALUES (1, 6, 'NEW'),
    (2, 1, 'NEW'),
    (3, 6, 'NEW');

INSERT INTO application_for_treatment_propensity (application_for_treatment_id, propensity_id)
VALUES (1, 1),
       (1, 2),
       (1, 4),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 3);