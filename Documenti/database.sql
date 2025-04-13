drop database if exists Wearful;
create database Wearful;
use Wearful;

create table Utente(
	email varchar(255) primary key,
    username varchar(50) not null,
    telfono varchar(13) not null,
    password varchar(255) not null,
    isAdmin boolean not null,
    constraint check_password check(char_length(password)>=8)
);

CREATE TABLE InfoConsegna
(
    id bigint unsigned auto_increment primary key not null,
    citta varchar(255) not null,
    cap int not null,
    via varchar(255) not null,
    altro varchar(255),
    destinatario varchar(255) not null,
    idUtente varchar(255) not null,
    FOREIGN KEY (idUtente) REFERENCES Utente (email)
);

create table Prodotto(
	id bigint unsigned not null auto_increment primary key,
    nome varchar(255) not null,
	descrizione varchar(4096) not null,
    taglia enum('XXXS','XXS','XS','S', 'M', 'L', 'XL', 'XXL', 'XXXL') not null,
    categoria varchar(50) not null,
    prezzo decimal(7,2) not null,
    IVA enum ('4', '10', '22'),
    personalizzabile boolean not null,
    imgPath varchar(255) not null,
    publisher varchar(255) not null,
    foreign key(publisher)  references Utente(email)
);

create table Carrello(
	id bigint unsigned not null auto_increment,
    idUtente varchar(255),
    primary key (id, idUtente),
    foreign key (idUtente) references Utente (email) on delete cascade
);

CREATE TABLE Ordine (
    id bigint unsigned auto_increment not null,
    idUtente varchar(255) not null,
    infoConsegna bigint unsigned not null,
    dataOrdine   datetime default current_timestamp,
    PRIMARY KEY (id, idUtente),
    FOREIGN KEY (idUtente) REFERENCES Utente (email),
    FOREIGN KEY (infoConsegna) REFERENCES InfoConsegna (id)
);

create table OrderItem(
	id bigint unsigned auto_increment not null,
    nome varchar(255) not null,
    idProdotto bigint unsigned not null,
    idOrdine bigint unsigned not null,
    prezzo decimal(7,2) not null,
    quantita int not null,
    IVA enum('4', '10', '22'),
    primary key (id,idProdotto,idOrdine),
    foreign key (idOrdine) references Ordine(id) on delete cascade,
    foreign key (idProdotto) references Prodotto(id)
    );
    
create table CartItem(
	id bigint unsigned auto_increment not null,
    idProdotto bigint unsigned not null,
    idCarrello bigint unsigned not null,
    quantita int unsigned not null,
    personalizzato boolean not null,
    imgPath varchar(256),
    primary key (id,idProdotto,idCarrello),
    foreign key (idCarrello) references Carrello(id) on delete cascade,
    foreign key (idProdotto) references Prodotto(id) on delete cascade
);    



