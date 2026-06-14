-- ════════════════════════════════════════════════════════
-- Clean existing data (order matters due to foreign keys)
-- ════════════════════════════════════════════════════════
DELETE
FROM plushies;
DELETE
FROM factories;
DELETE
FROM brands;
DELETE
FROM distributors;

-- ════════════════════════════════════════════════════════
-- Insert brands (ID auto‑generated)
-- ════════════════════════════════════════════════════════
INSERT INTO brands (name, country, founded_year)
VALUES ('J.K. Rowling', 'UK', 1997),
       ('Laure Moulin', 'France', 1969),
       ('Antoine de Saint-Exupéry', 'France', 1943),
       ('George Orwell', 'UK', 1949),
       ('Yuval Noah Harari', 'Israel', 2011),
       ('Albert Camus', 'France', 1942),
       ('Frank Herbert', 'USA', 1965),
       ('Arthur Conan Doyle', 'UK', 1887),
       ('James Clear', 'USA', 2018),
       ('J.R.R. Tolkien', 'UK', 1954),
       ('Marjane Satrapi', 'Iran', 2000),
       ('Stephen Hawking', 'UK', 1988);

-- ════════════════════════════════════════════════════════
-- Insert distributors (ID auto‑generated)
-- ════════════════════════════════════════════════════════
INSERT INTO distributors (name, country)
VALUES ('Gallimard', 'France'),
       ('Livre de Poche', 'France'),
       ('Penguin', 'USA'),
       ('Flammarion', 'France'),
       ('HarperCollins', 'USA'),
       ('Robert Laffont', 'France'),
       ('Hachette', 'France'),
       ('Random House', 'USA'),
       ('Del Rey', 'USA'),
       ('L''Iconoclaste', 'France');

-- ════════════════════════════════════════════════════════
-- Insert factories (ID auto‑generated)
-- ════════════════════════════════════════════════════════
INSERT INTO factories (name, country, number_of_employees, distributor_id)
VALUES ('Sorbonne Plush Works', 'France', 120,
        (SELECT id FROM distributors WHERE name = 'Gallimard')),
       ('Tokyo Kawaii Factory', 'Japan', 340,
        (SELECT id FROM distributors WHERE name = 'Penguin')),
       ('Berlin Soft Toys', 'Germany', 85,
        (SELECT id FROM distributors WHERE name = 'Flammarion')),
       ('Shenzhen Cuddle Co', 'China', 1500,
        (SELECT id FROM distributors WHERE name = 'Random House')),
       ('Lyon Peluches Artisanales', 'France', 42,
        (SELECT id FROM distributors WHERE name = 'Hachette'));

-- ════════════════════════════════════════════════════════
-- Insert plushies – id auto‑generated, foreign keys resolved by name lookup
-- ════════════════════════════════════════════════════════
INSERT INTO plushies (name, brand_id, distributor_id, factory_id, category)
VALUES ('Harry Potter à l''école des sorciers',
        (SELECT id FROM brands WHERE name = 'J.K. Rowling'),
        (SELECT id FROM distributors WHERE name = 'Flammarion'),
        (SELECT id FROM factories WHERE name = 'Sorbonne Plush Works'),
        'Fiction'),

       ('Jean Moulin : Biographie',
        (SELECT id FROM brands WHERE name = 'Laure Moulin'),
        (SELECT id FROM distributors WHERE name = 'Livre de Poche'),
        (SELECT id FROM factories WHERE name = 'Tokyo Kawaii Factory'),
        'Biography'),

       ('Le Petit Prince',
        (SELECT id FROM brands WHERE name = 'Antoine de Saint-Exupéry'),
        (SELECT id FROM distributors WHERE name = 'Gallimard'),
        (SELECT id FROM factories WHERE name = 'Berlin Soft Toys'),
        'Fiction'),

       ('1984',
        (SELECT id FROM brands WHERE name = 'George Orwell'),
        (SELECT id FROM distributors WHERE name = 'Penguin'),
        (SELECT id FROM factories WHERE name = 'Shenzhen Cuddle Co'),
        'Fiction'),

       ('Sapiens : Une brève histoire de l''humanité',
        (SELECT id FROM brands WHERE name = 'Yuval Noah Harari'),
        (SELECT id FROM distributors WHERE name = 'Hachette'),
        (SELECT id FROM factories WHERE name = 'Lyon Peluches Artisanales'),
        'History'),

       ('L''Étranger',
        (SELECT id FROM brands WHERE name = 'Albert Camus'),
        (SELECT id FROM distributors WHERE name = 'Flammarion'),
        (SELECT id FROM factories WHERE name = 'Sorbonne Plush Works'),
        'NonFiction'),

       ('Dune',
        (SELECT id FROM brands WHERE name = 'Frank Herbert'),
        (SELECT id FROM distributors WHERE name = 'Robert Laffont'),
        (SELECT id FROM factories WHERE name = 'Tokyo Kawaii Factory'),
        'SciFi'),

       ('Une étude en rouge',
        (SELECT id FROM brands WHERE name = 'Arthur Conan Doyle'),
        (SELECT id FROM distributors WHERE name = 'Livre de Poche'),
        (SELECT id FROM factories WHERE name = 'Berlin Soft Toys'),
        'Fiction'),

       ('Atomic Habits',
        (SELECT id FROM brands WHERE name = 'James Clear'),
        (SELECT id FROM distributors WHERE name = 'Random House'),
        (SELECT id FROM factories WHERE name = 'Shenzhen Cuddle Co'),
        'NonFiction'),

       ('Le Seigneur des Anneaux',
        (SELECT id FROM brands WHERE name = 'J.R.R. Tolkien'),
        (SELECT id FROM distributors WHERE name = 'Del Rey'),
        (SELECT id FROM factories WHERE name = 'Lyon Peluches Artisanales'),
        'Fiction'),

       ('Persépolis',
        (SELECT id FROM brands WHERE name = 'Marjane Satrapi'),
        (SELECT id FROM distributors WHERE name = 'L''Iconoclaste'),
        (SELECT id FROM factories WHERE name = 'Sorbonne Plush Works'),
        'Fiction'),

       ('Une brève histoire du temps',
        (SELECT id FROM brands WHERE name = 'Stephen Hawking'),
        (SELECT id FROM distributors WHERE name = 'Flammarion'),
        (SELECT id FROM factories WHERE name = 'Tokyo Kawaii Factory'),
        'Science');

-- Additional plushies to create multiple distributors per brand
INSERT INTO plushies (name, brand_id, distributor_id, factory_id, category) VALUES
       -- J.K. Rowling gets a second distributor (Gallimard)
       ('Harry Potter et la Chambre des secrets',
        (SELECT id FROM brands WHERE name = 'J.K. Rowling'),
        (SELECT id FROM distributors WHERE name = 'Gallimard'),
        (SELECT id FROM factories WHERE name = 'Berlin Soft Toys'),
        'Fiction'),

       -- Stephen Hawking gets Penguin as second distributor
       ('The Universe in a Nutshell',
        (SELECT id FROM brands WHERE name = 'Stephen Hawking'),
        (SELECT id FROM distributors WHERE name = 'Penguin'),
        (SELECT id FROM factories WHERE name = 'Shenzhen Cuddle Co'),
        'Science'),

       -- George Orwell gets HarperCollins (new distributor)
       ('Homage to Catalonia',
        (SELECT id FROM brands WHERE name = 'George Orwell'),
        (SELECT id FROM distributors WHERE name = 'HarperCollins'),
        (SELECT id FROM factories WHERE name = 'Lyon Peluches Artisanales'),
        'NonFiction'),

       -- J.R.R. Tolkien gets HarperCollins (second distributor)
       ('The Hobbit',
        (SELECT id FROM brands WHERE name = 'J.R.R. Tolkien'),
        (SELECT id FROM distributors WHERE name = 'HarperCollins'),
        (SELECT id FROM factories WHERE name = 'Sorbonne Plush Works'),
        'Fiction');
