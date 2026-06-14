DELETE FROM plushies;
DELETE FROM factories;
DELETE FROM brands;
DELETE FROM distributors;

INSERT INTO brands (name, country, founded_year) VALUES
    ('Câlin & Compagnie', 'France', 1998),
    ('Les Doux Rêveurs', 'France', 2005),
    ('Peluche Bio', 'France', 2012),
    ('Tendre Coton', 'France', 2015),
    ('Nounours Nature', 'France', 2009),
    ('La Fabrique à Câlins', 'France', 2003),
    ('Mistoufle', 'France', 2018),
    ('Coton & Tendresse', 'Belgique', 2010),
    ('Petite Laine', 'France', 2014),
    ('Doudou Vert', 'France', 2016),
    ('Atelier du Câlin', 'Suisse', 2007),
    ('Bichon & Bouille', 'France', 2019);

INSERT INTO distributors (name, country) VALUES
    ('Au Royaume du Doudou', 'France'),
    ('La Caverne aux Peluches', 'France'),
    ('Doudouland', 'France'),
    ('Câlinou Boutique', 'France'),
    ('Le Comptoir des Peluches', 'France'),
    ('Tendresse & Cie', 'France'),
    ('Peluches de France', 'France'),
    ('La Maison du Câlin', 'France'),
    ('Doudous & Merveilles', 'France'),
    ('Le Nid Douillet', 'France');

INSERT INTO factories (name, country, number_of_employees, distributor_id) VALUES
    ('Atelier Coton Doux', 'France', 80,
        (SELECT id FROM distributors WHERE name = 'Au Royaume du Doudou')),
    ('Manufacture du Câlin', 'France', 150,
        (SELECT id FROM distributors WHERE name = 'La Caverne aux Peluches')),
    ('Les Ateliers Verts', 'France', 60,
        (SELECT id FROM distributors WHERE name = 'Doudouland')),
    ('Fabrique Éthique du Doudou', 'France', 45,
        (SELECT id FROM distributors WHERE name = 'Câlinou Boutique')),
    ('Atelier Coton Recyclé', 'France', 70,
        (SELECT id FROM distributors WHERE name = 'Le Comptoir des Peluches')),
    ('La Filature Solidaire', 'France', 55,
        (SELECT id FROM distributors WHERE name = 'Tendresse & Cie')),
    ('Atelier Laine & Nature', 'Belgique', 40,
        (SELECT id FROM distributors WHERE name = 'Peluches de France')),
    ('Manufacture Douce de Wallonie', 'Belgique', 90,
        (SELECT id FROM distributors WHERE name = 'La Maison du Câlin'));

INSERT INTO plushies (name, brand_id, distributor_id, factory_id, category) VALUES
    ('Câlinours',
        (SELECT id FROM brands WHERE name = 'Câlin & Compagnie'),
        (SELECT id FROM distributors WHERE name = 'Au Royaume du Doudou'),
        (SELECT id FROM factories WHERE name = 'Atelier Coton Doux'),
        'Bear'),

    ('Lapinou Bisou',
        (SELECT id FROM brands WHERE name = 'Câlin & Compagnie'),
        (SELECT id FROM distributors WHERE name = 'Câlinou Boutique'),
        (SELECT id FROM factories WHERE name = 'Fabrique Éthique du Doudou'),
        'Rabbit'),

    ('Chatouille',
        (SELECT id FROM brands WHERE name = 'Les Doux Rêveurs'),
        (SELECT id FROM distributors WHERE name = 'La Caverne aux Peluches'),
        (SELECT id FROM factories WHERE name = 'Manufacture du Câlin'),
        'Cat'),

    ('Renardoux',
        (SELECT id FROM brands WHERE name = 'Les Doux Rêveurs'),
        (SELECT id FROM distributors WHERE name = 'Doudouland'),
        (SELECT id FROM factories WHERE name = 'Les Ateliers Verts'),
        'Fox'),

    ('Éléphantastique',
        (SELECT id FROM brands WHERE name = 'Peluche Bio'),
        (SELECT id FROM distributors WHERE name = 'Doudouland'),
        (SELECT id FROM factories WHERE name = 'Les Ateliers Verts'),
        'Elephant'),

    ('Mollo le Paresseux',
        (SELECT id FROM brands WHERE name = 'Peluche Bio'),
        (SELECT id FROM distributors WHERE name = 'Le Comptoir des Peluches'),
        (SELECT id FROM factories WHERE name = 'Atelier Coton Recyclé'),
        'Sloth'),

    ('Pandadou',
        (SELECT id FROM brands WHERE name = 'Tendre Coton'),
        (SELECT id FROM distributors WHERE name = 'Le Comptoir des Peluches'),
        (SELECT id FROM factories WHERE name = 'Atelier Coton Recyclé'),
        'Panda'),

    ('Chouette Alors',
        (SELECT id FROM brands WHERE name = 'Tendre Coton'),
        (SELECT id FROM distributors WHERE name = 'Au Royaume du Doudou'),
        (SELECT id FROM factories WHERE name = 'Atelier Coton Doux'),
        'Owl'),

    ('Lainou le Mouton',
        (SELECT id FROM brands WHERE name = 'Nounours Nature'),
        (SELECT id FROM distributors WHERE name = 'Peluches de France'),
        (SELECT id FROM factories WHERE name = 'Atelier Laine & Nature'),
        'Sheep'),

    ('Hippo Dodu',
        (SELECT id FROM brands WHERE name = 'Nounours Nature'),
        (SELECT id FROM distributors WHERE name = 'La Caverne aux Peluches'),
        (SELECT id FROM factories WHERE name = 'Manufacture du Câlin'),
        'Hippo'),

    ('Pingouin Glaçon',
        (SELECT id FROM brands WHERE name = 'La Fabrique à Câlins'),
        (SELECT id FROM distributors WHERE name = 'La Maison du Câlin'),
        (SELECT id FROM factories WHERE name = 'Manufacture Douce de Wallonie'),
        'Penguin'),

    ('Coucou la Girafe',
        (SELECT id FROM brands WHERE name = 'La Fabrique à Câlins'),
        (SELECT id FROM distributors WHERE name = 'Tendresse & Cie'),
        (SELECT id FROM factories WHERE name = 'La Filature Solidaire'),
        'Giraffe'),

    ('Requin Câlin',
        (SELECT id FROM brands WHERE name = 'Mistoufle'),
        (SELECT id FROM distributors WHERE name = 'La Maison du Câlin'),
        (SELECT id FROM factories WHERE name = 'Manufacture Douce de Wallonie'),
        'Shark'),

    ('Pieuvre à Huit Bisous',
        (SELECT id FROM brands WHERE name = 'Mistoufle'),
        (SELECT id FROM distributors WHERE name = 'La Caverne aux Peluches'),
        (SELECT id FROM factories WHERE name = 'Manufacture du Câlin'),
        'Octopus'),

    ('Koalou',
        (SELECT id FROM brands WHERE name = 'Coton & Tendresse'),
        (SELECT id FROM distributors WHERE name = 'Peluches de France'),
        (SELECT id FROM factories WHERE name = 'Atelier Laine & Nature'),
        'Koala'),

    ('Noisette Câline',
        (SELECT id FROM brands WHERE name = 'Coton & Tendresse'),
        (SELECT id FROM distributors WHERE name = 'Au Royaume du Doudou'),
        (SELECT id FROM factories WHERE name = 'Atelier Coton Doux'),
        'Squirrel'),

    ('Dino Doudou',
        (SELECT id FROM brands WHERE name = 'Petite Laine'),
        (SELECT id FROM distributors WHERE name = 'Doudouland'),
        (SELECT id FROM factories WHERE name = 'Les Ateliers Verts'),
        'Dinosaur'),

    ('Étincelle la Licorne',
        (SELECT id FROM brands WHERE name = 'Petite Laine'),
        (SELECT id FROM distributors WHERE name = 'Tendresse & Cie'),
        (SELECT id FROM factories WHERE name = 'La Filature Solidaire'),
        'Unicorn'),

    ('Pikou le Hérisson',
        (SELECT id FROM brands WHERE name = 'Doudou Vert'),
        (SELECT id FROM distributors WHERE name = 'Le Comptoir des Peluches'),
        (SELECT id FROM factories WHERE name = 'Atelier Coton Recyclé'),
        'Hedgehog'),

    ('Loup-Câlin',
        (SELECT id FROM brands WHERE name = 'Doudou Vert'),
        (SELECT id FROM distributors WHERE name = 'Câlinou Boutique'),
        (SELECT id FROM factories WHERE name = 'Fabrique Éthique du Doudou'),
        'Wolf'),

    ('Plouf le Dauphin',
        (SELECT id FROM brands WHERE name = 'Atelier du Câlin'),
        (SELECT id FROM distributors WHERE name = 'La Maison du Câlin'),
        (SELECT id FROM factories WHERE name = 'Manufacture Douce de Wallonie'),
        'Dolphin'),

    ('Axolotl Rigolo',
        (SELECT id FROM brands WHERE name = 'Atelier du Câlin'),
        (SELECT id FROM distributors WHERE name = 'Peluches de France'),
        (SELECT id FROM factories WHERE name = 'Atelier Laine & Nature'),
        'Axolotl'),

    ('Tortue Tout Doux',
        (SELECT id FROM brands WHERE name = 'Bichon & Bouille'),
        (SELECT id FROM distributors WHERE name = 'Tendresse & Cie'),
        (SELECT id FROM factories WHERE name = 'La Filature Solidaire'),
        'Turtle'),

    ('Roux-Roux le Panda',
        (SELECT id FROM brands WHERE name = 'Bichon & Bouille'),
        (SELECT id FROM distributors WHERE name = 'Câlinou Boutique'),
        (SELECT id FROM factories WHERE name = 'Fabrique Éthique du Doudou'),
        'RedPanda');
