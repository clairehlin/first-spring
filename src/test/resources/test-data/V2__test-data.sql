INSERT INTO restaurant (id, name) VALUES (1, 'Ruth Steakhouse');
INSERT INTO restaurant (id, name) VALUES (2, 'Sam Steakhouse');
INSERT INTO menu (id, name, restaurant_id) VALUES (1, 'Simple Menu', 1);
INSERT INTO menu (id, name, restaurant_id) VALUES (2, 'Holiday Menu', 1);
INSERT INTO menu (id, name, restaurant_id) VALUES (3, 'Standard Menu', 2);
INSERT INTO section (id, name, menu_id) VALUES (1, 'Salad Section', 1);
INSERT INTO section (id, name, menu_id) VALUES (2, 'Pasta Section', 1);
INSERT INTO section (id, name, menu_id) VALUES (3, 'Poultry Section', 2);
INSERT INTO item (id, name, description, price, section_id) VALUES (1, 'thai papaya salad', 'papaya salad in thai sauce', 12.29, 1);
INSERT INTO item (id, name, description, price, section_id) VALUES (2, 'mango prawn salad', 'prawn salad with mango', 23.99, 1);
INSERT INTO item (id, name, description, price, section_id) VALUES (3, 'Italian pasta with pesto and mushrooms', 'pasta with pesto sauce', 15.99, 2);
INSERT INTO item (id, name, description, price, section_id) VALUES (4, 'low calories salad', 'tomatoes and cucumbers', 15.99, 2);
INSERT INTO feature (id, name) VALUES (1, 'Keto');
INSERT INTO feature (id, name) VALUES (2, 'Vegetarian');
INSERT INTO feature (id, name) VALUES (3, 'Low Fat');
INSERT INTO item_feature (item_id, feature_id) VALUES (3, 2);
INSERT INTO item_feature (item_id, feature_id) VALUES (1, 1);
INSERT INTO item_feature (item_id, feature_id) VALUES (4, 1);
INSERT INTO item_feature (item_id, feature_id) VALUES (4, 3);


