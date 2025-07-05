DROP DATABASE IF EXISTS Wearful;
CREATE DATABASE Wearful;
USE Wearful;

CREATE TABLE Utente (
    email    VARCHAR(255) PRIMARY KEY,
    username VARCHAR(50)  NOT NULL,
    telefono VARCHAR(13)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    isAdmin  BOOLEAN      NOT NULL,
    CONSTRAINT check_password CHECK (CHAR_LENGTH(password) >= 8)
);

CREATE TABLE InfoConsegna (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    citta VARCHAR(255) NOT NULL,
    cap INT NOT NULL,
    via VARCHAR(255) NOT NULL,
    altro VARCHAR(255),
    destinatario VARCHAR(255) NOT NULL,
    idUtente VARCHAR(255) NOT NULL,
    FOREIGN KEY (idUtente) REFERENCES Utente(email)
);

CREATE TABLE GruppoProdotti (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE Prodotto (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descrizione VARCHAR(4096) NOT NULL,
    taglia ENUM('XXS','XS','S', 'M', 'L', 'XL', 'XXL') NOT NULL,
    colore VARCHAR(50) NOT NULL,
    codiceColore CHAR(7) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    prezzo DECIMAL(7,2) NOT NULL,
    IVA TINYINT UNSIGNED NOT NULL,
    disponibilita INT NOT NULL DEFAULT 0,
    personalizzabile BOOLEAN NOT NULL,
    imgPath VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    gruppo BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (gruppo) REFERENCES GruppoProdotti(id),
    FOREIGN KEY (publisher) REFERENCES Utente(email)
);

CREATE TABLE Carrello (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    idUtente VARCHAR(255),
    FOREIGN KEY (idUtente) REFERENCES Utente(email) ON DELETE CASCADE
);

CREATE TABLE CartItem (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    idProdotto BIGINT UNSIGNED NOT NULL,
    idCarrello BIGINT UNSIGNED NOT NULL,
    quantita INT UNSIGNED NOT NULL,
    personalizzato BOOLEAN NOT NULL,
    imgPath VARCHAR(256),
    FOREIGN KEY (idCarrello) REFERENCES Carrello(id) ON DELETE CASCADE,
    FOREIGN KEY (idProdotto) REFERENCES Prodotto(id) ON DELETE CASCADE
);

CREATE TABLE Ordine (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    idUtente VARCHAR(255) NOT NULL,
    infoConsegna BIGINT UNSIGNED NOT NULL,
    dataOrdine DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idUtente) REFERENCES Utente(email),
    FOREIGN KEY (infoConsegna) REFERENCES InfoConsegna(id)
);

CREATE TABLE OrderItem (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    idProdotto BIGINT UNSIGNED NOT NULL,
    idOrdine BIGINT UNSIGNED NOT NULL,
    prezzo DECIMAL(7,2) NOT NULL,
    quantita INT NOT NULL,
    IVA TINYINT UNSIGNED NOT NULL ,
    FOREIGN KEY (idOrdine) REFERENCES Ordine(id) ON DELETE CASCADE,
    FOREIGN KEY (idProdotto) REFERENCES Prodotto(id)
);


INSERT INTO Utente (email, username, telefono, password, isAdmin)
VALUES ('admin@wearful.com', 'adminUser', '+390123456789', 'CcV0rNKcBuduhNc5BLywbuFP+TGvMLEPmqCp2jrpxok=', TRUE);

INSERT INTO GruppoProdotti(id, nome)
VALUES (1,'I am a Ducktor'),
        (2, 'Obi-Juan'),
        (3,'Maglia Tramonto'),
        (4,'Felpa Star Wars'),
        (5,'Felpa Nah'),
        (6,'Felpa Not Today'),
        (7,'Live Fast Eat Trash'),
        (8,'Garpez'),
        (9,'Jaque Mate'),
        (10,'I am too old'),
        (11,'Riot'),
        (12,'Cappello North Face'),
        (13,'Cappello Sportswear'),
        (14,'Cappello What');

INSERT INTO Prodotto( nome, descrizione, taglia, colore, codiceColore, categoria, prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo)
VALUES ('I am a Ducktor', 'Maglia unisex con papera divertente', 'M', 'Verde', '#cedace','Felpa',20.00, 22,3,false,'./img/uploads/ducktor.jpg','admin@wearful.com',1 ),
       ('I am a Ducktor', 'Maglia unisex con papera divertente', 'L', 'Verde', '#cedace','Felpa',20.00, 22,3,false,'./img/uploads/ducktor.jpg','admin@wearful.com',1 ),
       ('I am a Ducktor', 'Maglia unisex con papera divertente', 'XL', 'Verde', '#cedace','Felpa',20.00, 22,2,false,'./img/uploads/ducktor.jpg','admin@wearful.com',1 ),
       ('I am a Ducktor', 'Maglia unisex con papera divertente', 'S', 'Verde', '#cedace','Felpa',20.00, 22,1,false,'./img/uploads/ducktor.jpg','admin@wearful.com',1 ),
       ('I am a Ducktor', 'Maglia unisex con papera divertente', 'XS', 'Verde', '#cedace','Felpa',20.00, 22,1,false,'./img/uploads/ducktor.jpg','admin@wearful.com',1 ),
       ('Obi-Juan', 'Maglia unisex tema star wars divertente', 'XS', 'Nera', '#1e1e1e','Felpa',25.00, 22,1,false,'./img/uploads/obiJuan.jpg','admin@wearful.com',2),
       ('Obi-Juan', 'Maglia unisex tema star wars divertente', 'M', 'Nera', '#1e1e1e','Felpa',25.00, 22,1,false,'./img/uploads/obiJuan.jpg','admin@wearful.com',2),
       ('Obi-Juan', 'Maglia unisex tema star wars divertente', 'S', 'Nera', '#1e1e1e','Felpa',25.00, 22,2,false,'./img/uploads/obiJuan.jpg','admin@wearful.com',2),
       ('Obi-Juan', 'Maglia unisex tema star wars divertente', 'XL', 'Nera', '#1e1e1e','Felpa',25.00, 22,3,false,'./img/uploads/obiJuan.jpg','admin@wearful.com',2),
       ('Obi-Juan', 'Maglia unisex tema star wars divertente', 'XXL', 'Nera', '#1e1e1e','Felpa',25.00, 22,2,false,'./img/uploads/obiJuan.jpg','admin@wearful.com',2),
       ('Felpa Star Wars', 'Maglia tema Star Wars nera unisex', 'XS','Nero','#000000','Felpa',30.00,22,2,false,'./img/uploads/pewpew.jpg','admin@wearful.com',4),
       ('Felpa Star Wars', 'Maglia tema Star Wars nera unisex', 'S','Nero','#000000','Felpa',30.00,22,2,false,'./img/uploads/pewpew.jpg','admin@wearful.com',4),
       ('Felpa Star Wars', 'Maglia tema Star Wars nera unisex', 'M','Nero','#000000','Felpa',30.00,22,4,false,'./img/uploads/pewpew.jpg','admin@wearful.com',4),
       ('Felpa Star Wars', 'Maglia tema Star Wars nera unisex', 'L','Nero','#000000','Felpa',30.00,22,1,false,'./img/uploads/pewpew.jpg','admin@wearful.com',4),
       ('Felpa Star Wars', 'Maglia tema Star Wars nera unisex', 'XL','Nero','#000000','Felpa',30.00,22,2,false,'./img/uploads/pewpew.jpg','admin@wearful.com',4),
       ('Felpa Nah', 'Felpa carina con cane','M','Giallo','#f0d69a','Felpa',20.00,22,5,false,'./img/uploads/nah.jpg','admin@wearful.com',5),
       ('Felpa Nah', 'Felpa carina con cane','L','Giallo','#f0d69a','Felpa',20.00,22,2,false,'./img/uploads/nah.jpg','admin@wearful.com',5),
       ('Felpa Nah', 'Felpa carina con cane','XL','Giallo','#f0d69a','Felpa',20.00,22,3,false,'./img/uploads/nah.jpg','admin@wearful.com',5),
       ('Felpa Nah', 'Felpa carina con cane','S','Giallo','#f0d69a','Felpa',20.00,22,1,false,'./img/uploads/nah.jpg','admin@wearful.com',5),
       ('Felpa Nah', 'Felpa carina con cane','XS','Giallo','#f0d69a','Felpa',20.00,22,3,false,'./img/uploads/nah.jpg','admin@wearful.com',5),
       ('Felpa Not Today', 'Felpa grigia carina con gatto','M','Grigio','#a5aab0','Felpa',20.00,22,4,false,'./img/uploads/not_today.jpg','admin@wearful.com',6),
       ('Felpa Not Today', 'Felpa grigia carina con gatto','S','Grigio','#a5aab0','Felpa',20.00,22,2,false,'./img/uploads/not_today.jpg','admin@wearful.com',6),
       ('Felpa Not Today', 'Felpa grigia carina con gatto','L','Grigio','#a5aab0','Felpa',20.00,22,3,false,'./img/uploads/not_today.jpg','admin@wearful.com',6),
       ('Felpa Not Today', 'Felpa grigia carina con gatto','XL','Grigio','#a5aab0','Felpa',20.00,22,4,false,'./img/uploads/not_today.jpg','admin@wearful.com',6),
       ('Felpa Not Today', 'Felpa grigia carina con gatto','XS','Grigio','#a5aab0','Felpa',20.00,22,1,false,'./img/uploads/not_today.jpg','admin@wearful.com',6),
       ('Live Fast Eat Trash', 'Maglia unisex nera con procioni divertente', 'M', 'Nero', '#050507', 'Maglia',20.00, 22,5,false,'./img/uploads/eat_trash.jpg', 'admin@wearful.com',7 ),
       ('Live Fast Eat Trash', 'Maglia unisex nera con procioni divertente', 'L','Nero', '#050507', 'Maglia',20.00, 22,3,false,'./img/uploads/eat_trash.jpg', 'admin@wearful.com',7),
       ('Live Fast Eat Trash', 'Maglia unisex nera con procioni divertente', 'XL','Nero', '#050507', 'Maglia',20.00, 22,2,false,'./img/uploads/eat_trash.jpg', 'admin@wearful.com',7 ),
       ('Live Fast Eat Trash', 'Maglia unisex nera con procioni divertente', 'S','Nero', '#050507', 'Maglia',20.00, 22,1,false,'./img/uploads/eat_trash.jpg', 'admin@wearful.com',7 ),
       ('Live Fast Eat Trash', 'Maglia unisex nera con procioni divertente', 'XS','Nero', '#050507', 'Maglia',20.00, 22,2,false,'./img/uploads/eat_trash.jpg', 'admin@wearful.com',7 ),
       ('Garpez','Maglia unisex tre uomini e una gamba','M', 'Bianco','#ffffff','Maglia',20.00,22,3,false,'./img/uploads/garpez.jpg','admin@wearful.com',8),
       ('Garpez','Maglia unisex tre uomini e una gamba','S', 'Bianco','#ffffff','Maglia',20.00,22,5,false,'./img/uploads/garpez.jpg','admin@wearful.com',8),
       ('Garpez','Maglia unisex tre uomini e una gamba','L', 'Bianco','#ffffff','Maglia',20.00,22,4,false,'./img/uploads/garpez.jpg','admin@wearful.com',8),
       ('Garpez','Maglia unisex tre uomini e una gamba','XL', 'Bianco','#ffffff','Maglia',20.00,22,2,false,'./img/uploads/garpez.jpg','admin@wearful.com',8),
       ('Garpez','Maglia unisex tre uomini e una gamba','XS', 'Bianco','#ffffff','Maglia',20.00,22,1,false,'./img/uploads/garpez.jpg','admin@wearful.com',8),
       ('Jaque Mate', 'Maglia unisex scacchi e basket','M', 'Rosso', '#742125','Maglia',20.00,22,3,false,'./img/uploads/jaque_mate.jpg','admin@wearful.com',9),
       ('Jaque Mate', 'Maglia unisex scacchi e basket','L', 'Rosso', '#742125','Maglia',20.00,22,3,false,'./img/uploads/jaque_mate.jpg','admin@wearful.com',9),
       ('Jaque Mate', 'Maglia unisex scacchi e basket','S', 'Rosso', '#742125','Maglia',20.00,22,4,false,'./img/uploads/jaque_mate.jpg','admin@wearful.com',9),
       ('Jaque Mate', 'Maglia unisex scacchi e basket','XS', 'Rosso', '#742125','Maglia',20.00,22,2,false,'./img/uploads/jaque_mate.jpg','admin@wearful.com',9),
       ('Jaque Mate', 'Maglia unisex scacchi e basket','XL', 'Rosso', '#742125','Maglia',20.00,22,2,false,'./img/uploads/jaque_mate.jpg','admin@wearful.com',9),
       ('Im too old', 'Maglia unisex mago seduto','M','Nero','#19182c','Maglia', 15.00,22,4,false,'./img/uploads/too_old.jpg','admin@wearful.com',10 ),
       ('Im too old', 'Maglia unisex mago seduto','L','Nero','#19182c','Maglia', 15.00,22,8,false,'./img/uploads/too_old.jpg','admin@wearful.com',10 ),
       ('Im too old', 'Maglia unisex mago seduto','XL','Nero','#19182c','Maglia', 15.00,22,9,false,'./img/uploads/too_old.jpg','admin@wearful.com',10 ),
       ('Im too old', 'Maglia unisex mago seduto','S','Nero','#19182c','Maglia', 15.00,22,4,false,'./img/uploads/too_old.jpg','admin@wearful.com',10 ),
       ('Im too old', 'Maglia unisex mago seduto','XS','Nero','#19182c','Maglia', 15.00,22,3,false,'./img/uploads/too_old.jpg','admin@wearful.com',10 ),
       ('Riot', 'Maglia unisex con papera assassina', 'M', 'Marrone sabbia','#817350','Maglia',20.00,22,4,false,'./img/uploads/riot.jpg','admin@wearful.com',11),
       ('Riot', 'Maglia unisex con papera assassina', 'L', 'Marrone sabbia','#817350','Maglia',20.00,22,2,false,'./img/uploads/riot.jpg','admin@wearful.com',11),
       ('Riot', 'Maglia unisex con papera assassina', 'XL', 'Marrone sabbia','#817350','Maglia',20.00,22,6,false,'./img/uploads/riot.jpg','admin@wearful.com',11),
       ('Riot', 'Maglia unisex con papera assassina', 'S', 'Marrone sabbia','#817350','Maglia',20.00,22,2,false,'./img/uploads/riot.jpg','admin@wearful.com',11),
       ('Riot', 'Maglia unisex con papera assassina', 'XS', 'Marrone sabbia','#817350','Maglia',20.00,22,1,false,'./img/uploads/riot.jpg','admin@wearful.com',11),
       ('Tramonto Nero', 'Maglia con logo del tramonto unisex', 'M', 'Nera','#000000', 'Maglia',15.00,22,2,false,'./img/uploads/tramonto_nero.jpg','admin@wearful.com',3),
       ('Tramonto Blu', 'Maglia con logo del tramonto unisex', 'M', 'Blu','#16192c', 'Maglia',15.00,22,1,false,'./img/uploads/tramonto_blu.jpg','admin@wearful.com',3),
       ('Tramonto Nero', 'Maglia con logo del tramonto unisex', 'L', 'Nera','#000000', 'Maglia',15.00,22,6,false,'./img/uploads/tramonto_nero.jpg','admin@wearful.com',3),
       ('Tramonto Blu', 'Maglia con logo del tramonto unisex', 'L', 'Blu','#16192c', 'Maglia',15.00,22,3,false,'./img/uploads/tramonto_blu.jpg','admin@wearful.com',3),
       ('Tramonto Rosso', 'Maglia con logo del tramonto unisex', 'L', 'Rosso','#751e23', 'Maglia',15.00,22,4,false,'./img/uploads/tramonto_rosso.jpg','admin@wearful.com',3),
       ('Tramonto Ciano', 'Maglia con logo del tramonto unisex', 'L', 'Ciano','#456b74', 'Maglia',15.00,22,3,false,'./img/uploads/tramonto_ciano.jpg','admin@wearful.com',3),
       ('Tramonto Rosso', 'Maglia con logo del tramonto unisex', 'M', 'Rosso','#751e23', 'Maglia',15.00,22,2,false,'./img/uploads/tramonto_rosso.jpg','admin@wearful.com',3),
       ('Tramonto Ciano', 'Maglia con logo del tramonto unisex', 'M', 'Ciano','#456b74', 'Maglia',15.00,22,5,false,'./img/uploads/tramonto_ciano.jpg','admin@wearful.com',3),
       ('Cappello North Face','Cappello della North Face nero','M','Nero','#0a0a0a','Cappello',20.00,22,3,false,'./img/uploads/cappello1.jpg','admin@wearful.com',12),
       ('Cappello North Face','Cappello della North Face nero','L','Nero','#0a0a0a','Cappello',20.00,22,4,false,'./img/uploads/cappello1.jpg','admin@wearful.com',12),
       ('Cappello North Face','Cappello della North Face nero','XL','Nero','#0a0a0a','Cappello',20.00,22,5,false,'./img/uploads/cappello1.jpg','admin@wearful.com',12),
       ('Cappello Sportswear', 'Cappello Sportswear sportivo','M', 'Blu', '#1f283a','Cappello', 20.00,22,5,false,'./img/uploads/cappello2.jpg','admin@wearful.com',13),
       ('Cappello Sportswear', 'Cappello Sportswear sportivo','L', 'Blu', '#1f283a','Cappello', 20.00,22,3,false,'./img/uploads/cappello2.jpg','admin@wearful.com',13),
       ('Cappello Sportswear', 'Cappello Sportswear sportivo','M', 'Blu', '#1f283a','Cappello', 20.00,22,2,false,'./img/uploads/cappello2.jpg','admin@wearful.com',13),
       ('Cappello What', 'Cappello What fantasia vernice ','M', 'Bianco', '#cacccb','Cappello', 20.00,22,5,false,'./img/uploads/cappello3.jpg','admin@wearful.com',14),
       ('Cappello What', 'Cappello What fantasia vernice ','L', 'Bianco', '#cacccb','Cappello', 20.00,22,2,false,'./img/uploads/cappello3.jpg','admin@wearful.com',14),
       ('Cappello What', 'Cappello What fantasia vernice ','XL', 'Bianco', '#cacccb','Cappello', 20.00,22,4,false,'./img/uploads/cappello3.jpg','admin@wearful.com',14);