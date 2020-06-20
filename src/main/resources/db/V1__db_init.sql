DROP TABLE IF EXISTS item_feature;
DROP TABLE IF EXISTS feature;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS restaurant;

CREATE TABLE restaurant (
    id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE menu (
    id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    restaurant_id INT NOT NULL,
    CONSTRAINT restaurant_id_menu_id UNIQUE (restaurant_id, id),
    CONSTRAINT FK_menu_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant(id),
    PRIMARY KEY (id)
);

CREATE TABLE section (
    id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    menu_id INT NOT NULL,
    CONSTRAINT menu_id_section_id UNIQUE (menu_id, id),
    CONSTRAINT FK_section_menu FOREIGN KEY (menu_id) REFERENCES menu(id),
    PRIMARY KEY (id)
);

CREATE TABLE item (
    id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DOUBLE(2) NOT NULL,
    section_id INT NOT NULL,
    CONSTRAINT section_id_item_id UNIQUE (section_id, id),
    CONSTRAINT FK_item_section FOREIGN KEY (section_id) REFERENCES section(id),
    PRIMARY KEY (id)
);

CREATE TABLE feature (
    id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE item_feature (
    item_id INT NOT NULL,
    feature_id INT NOT NULL,
    CONSTRAINT item_id_feature_id UNIQUE (item_id, feature_id),
    CONSTRAINT FK_item_feature_item FOREIGN KEY (item_id) REFERENCES item (id),
    CONSTRAINT FK_item_feature_feature FOREIGN KEY (feature_id) REFERENCES feature (id)
);












