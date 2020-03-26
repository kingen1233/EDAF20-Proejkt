DELETE FROM IngredientAmount;
DELETE FROM StoredIngredients;
DELETE FROM Pallet;
DELETE FROM Customers;
DELETE FROM Cookies;
DELETE FROM sqlite_sequence WHERE name = "Pallet";
DELETE FROM sqlite_sequence WHERE name = "Customers";
INSERT INTO Cookies (cookieName)
VALUES ("Almond delight"),
("Amneris"),
("Berliner"),
("Nut cookie"),
("Nut ring"),
("Tango");

INSERT INTO Customers (customerName, address)
VALUES ("Bjudkakor AB", "Ystad"),
("Finkakor AB", "Helsingborg"),
("Gästkakor AB", "Hässleholm"),
("Kaffebröd AB", "Landskrona"),
("Kalaskakor AB", "Trelleborg"),
("Partykakor AB", "Kristianstad"),
("Skånekakor AB", "Perstorp"),
("Småbröd AB", "Malmö");

INSERT INTO StoredIngredients(ingredient, amountInStorage, lastDeliveryDate, lastDeliverySize, unit)
VALUES("Bread crumbs", 500000, null, null, "g"),
("Butter", 500000, null, null, "g"),
("Chocolate", 500000, null, null, "g"),
("Chopped almonds", 500000, null, null, "g"),
("Cinnamon", 500000, null, null, "g"),
("Egg whites", 500000, null, null, "ml"),
("Eggs", 500000, null, null, "g"),
("Fine-ground nuts", 500000, null, null, "g"),
("Flour", 500000, null, null, "g"),
("Ground, roasted nuts", 500000, null, null, "g"),
("Icing sugar", 500000, null, null, "g"),
("Marzipan", 500000, null, null, "g"),
("Potato starch", 500000, null, null, "g"),
("Roasted, chopped nuts", 500000, null, null, "g"),
("Sodium bicarbonate", 500000, null, null, "g"),
("Sugar", 500000, null, null, "g"),
("Vanilla sugar", 500000, null, null, "g"),
("Vanilla", 500000, null, null, "g"),
("Wheat flour", 500000, null, null, "g");


INSERT INTO IngredientAmount(cookieName, ingredient, amount)
VALUES("Almond delight", "Butter", 400),
("Almond delight", "Chopped almonds", 279),
("Almond delight", "Cinnamon", 10),
("Almond delight", "Flour", 400),
("Almond delight", "Sugar", 270),
("Amneris", "Butter", 250),
("Amneris", "Eggs", 250),
("Amneris", "Marzipan", 750),
("Amneris", "Potato starch", 25),
("Amneris","Wheat flour", 25),
("Berliner", "Butter", 250),
("Berliner", "Chocolate", 50),
("Berliner", "Eggs", 50),
("Berliner", "Flour", 350),
("Berliner", "Icing sugar", 100),
("Berliner", "Vanilla sugar", 5),
("Nut cookie", "Bread crumbs", 125),
("Nut cookie", "Chocolate", 50),
("Nut cookie", "Egg whites", 350),
("Nut cookie", "Fine-ground nuts", 750),
("Nut cookie", "Ground, roasted nuts", 625),
("Nut cookie", "Sugar", 375),
("Nut ring", "Butter", 450),
("Nut ring", "Flour", 450),
("Nut ring", "Icing sugar", 190),
("Nut ring", "Roasted, chopped nuts", 225),
("Tango", "Butter", 200),
("Tango", "Flour", 300),
("Tango", "Sodium bicarbonate", 4),
("Tango", "Sugar", 250),
("Tango", "Vanilla", 2)