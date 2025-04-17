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
    categoria VARCHAR(50) NOT NULL,
    prezzo DECIMAL(7,2) NOT NULL,
    IVA ENUM ('4', '10', '22'),
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
    IVA ENUM('4', '10', '22'),
    FOREIGN KEY (idOrdine) REFERENCES Ordine(id) ON DELETE CASCADE,
    FOREIGN KEY (idProdotto) REFERENCES Prodotto(id)
);


INSERT INTO Utente (email, username, telefono, password, isAdmin)
VALUES ('admin@wearful.com', 'adminUser', '1234567890', 'securePass123', TRUE);

INSERT INTO GruppoProdotti (nome)
VALUES ('T-shirt'), ('Felpe'), ('Accessori');

INSERT INTO Prodotto (
    nome, descrizione, taglia, colore, categoria, prezzo, IVA, disponibilita,
    personalizzabile, imgPath, publisher, gruppo
) VALUES
      (
          'T-shirt Basic Bianca',
          'T-shirt in cotone 100% biologico, morbida e resistente.',
          'M',
          'Bianco',
          'Maglietta',
          14.99,
          '22',
          50,
          TRUE,
          'images/prodotti/tshirt_bianca.jpg',
          'admin@wearful.com',
          1
      ),
      (
          'Felpa Hoodie Nera',
          'Felpa con cappuccio e tasca frontale, perfetta per l\'inverno.',
          'L',
          'Nero',
          'Felpa',
          39.90,
          '22',
          30,
          TRUE,
          'images/prodotti/felpa_nera.jpg',
          'admin@wearful.com',
          2
      ),
      (
          'Cappellino Snapback',
          'Cappellino regolabile con logo ricamato.',
          'M',
          'Blu',
          'Cappello',
          19.99,
          '22',
          20,
          FALSE,
          'images/prodotti/cappellino.jpg',
          'admin@wearful.com',
          3
      );
