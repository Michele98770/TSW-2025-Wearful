# Wearful 👕 - Progetto TSW 2024/2025
![logo Wearful](./src/main/webapp/img/wide_logo.png)

**Wearful** è un progetto universitario per il corso di **Tecnologie Software Web** dell'anno accademico 2024/2025. L'applicazione simula una piattaforma di e-commerce moderna e funzionale per la vendita di abbigliamento.

[![Java](https://img.shields.io/badge/Java-11-blue.svg)](https://jdk.java.net/java-se-ri/11-MR3)
[![Maven](https://img.shields.io/badge/Maven-4.0.0-red.svg)](https://maven.apache.org/)
[![Tomcat](https://img.shields.io/badge/Tomcat-9-yellow.svg)](https://tomcat.apache.org/download-90.cgi)
[![Status](https://img.shields.io/badge/status-completato-gree.svg)](https://github.com/Michele98770/TSW-2025-Wearful)

## 📜 Indice

*   [Descrizione](#-descrizione)
*   [Funzionalità](#-funzionalità)
*   [Tecnologie Utilizzate](#-tecnologie-utilizzate)
*   [Prerequisiti](#-prerequisiti)
*   [Installazione e Avvio](#-installazione-e-avvio)
*   [Struttura del Progetto](#-struttura-del-progetto)
*   [Autori](#-autori)

## 📝 Descrizione

L'obiettivo del progetto è creare un'applicazione web completa che gestisca le funzionalità tipiche di un e-commerce, tra cui:
*   Catalogo prodotti dinamico.
*   Gestione degli utenti (registrazione e login).
*   Carrello della spesa.
*   Processo di acquisto e storico ordini.
*   Un pannello di amministrazione per la gestione di prodotti, utenti e ordini.

## ✨ Funzionalità

Elenco delle principali funzionalità implementate:

*   **👤 Gestione Utenti:**
    *   Registrazione di nuovi utenti.
    *   Login e Logout sicuro con gestione della sessione.
    *   Area personale con storico ordini e dati anagrafici.
*   **👕 Catalogo Prodotti:**
    *   Visualizzazione dei prodotti con filtri per categoria.
    *   Pagina di dettaglio per ogni prodotto con descrizione, immagini e prezzo.
    *   Barra di ricerca per trovare prodotti specifici.
*   **🛒 Carrello:**
    *   Aggiunta, modifica quantità e rimozione di prodotti dal carrello.
    *   Riepilogo del carrello aggiornato dinamicamente.
*   **💳 Checkout:**
    *   Procedura di acquisto guidata.
    *   Gestione degli indirizzi di spedizione e metodi di pagamento.
*   **⚙️ Pannello Admin:**
    *   CRUD (Create, Read, Update, Delete) per i prodotti.
    *   Visualizzazione e gestione degli ordini.
    *   Gestione degli utenti registrati.

## 🛠️ Tecnologie Utilizzate

*   **Backend:**
    *   Java 11
    *   Java Servlet
    *   JavaServer Pages (JSP)
*   **Frontend:**
    *   HTML5
    *   CSS3
    *   JavaScript (per la logica client-side e le interazioni dinamiche)
*   **Database:**
    *   MySQL 
*   **Build & Dependency Management:**
    *   Apache Maven
*   **Web Server:**
    *   Apache Tomcat 9

## 📋 Prerequisiti

Prima di iniziare, assicurati di avere installato sul tuo sistema:
*   [JDK 11](https://jdk.java.net/java-se-ri/11-MR3) o superiore.
*   [Apache Maven](https://maven.apache.org/download.cgi).
*   [Apache Tomcat 9](https://tomcat.apache.org/download-90.cgi) o superiore.
*   Un server [MySQL](https://dev.mysql.com/downloads/mysql/) o un altro DB a tua scelta.
*   Un IDE come [IntelliJ IDEA](https://www.jetbrains.com/idea/) o [Eclipse for Enterprise Java and Web Developers](https://www.eclipse.org/downloads/packages/).

## 🚀 Installazione e Avvio

Segui questi passaggi per avviare il progetto in locale:

1.  **Clona il repository:**
    ```bash
    git clone https://github.com/Michele98770/TSW-2025-Wearful.git
    cd TSW-2025-Wearful
    ```

2.  **Configura il database:**
    *   Crea un nuovo database (`wearful`).
    *   Importa lo schema del database (nel file`databse.sql` in Documenti).
    *   cambia le credenziali in `src/main/java/model/ConnectionPool.java`
3.  **Compila il progetto con Maven:**
    ```bash
    mvn clean install
    ```
    Questo comando scaricherà le dipendenze e creerà il file `.war` nella cartella `target/`.

4.  **Esegui il deploy su Tomcat:**
    *   Copia il file `target/Wearful.war` (o il nome generato da Maven) nella cartella `webapps` della tua installazione di Tomcat.
    *   Avvia il server Tomcat.

5.  **Apri l'applicazione:**
    Apri il tuo browser e vai all'indirizzo `http://localhost:8080/Wearful` (l'URL potrebbe variare in base alla configurazione di Tomcat e al nome del file `.war`).

## 📁 Struttura del Progetto
L'architettura del progetto segue rigorosamente il design pattern **Model-View-Controller (MVC)**, un approccio fondamentale per organizzare il codice in applicazioni web complesse, garantendo separazione delle responsabilità, manutenibilità e scalabilità.

Ecco come i componenti del pattern sono mappati sulla struttura del progetto:

### 🔵 Model
Il **Model** rappresenta i dati e la logica di business dell'applicazione. È responsabile di accedere e manipolare i dati, indipendentemente da come verranno presentati.
*   **Dove si trova:** `src/main/java/model`
*   **Componenti:**
    *   **JavaBeans :** Classi semplici che modellano le entità del dominio (es. `Utente`, `Prodotto`, `Ordine`), con attributi privati, getter e setter.
    *   **DAO (Data Access Object):** Classi che incapsulano la logica di accesso al database. Forniscono metodi per le operazioni CRUD (Create, Read, Update, Delete) senza esporre i dettagli SQL al resto dell'applicazione.

### 🟢 View
La **View** è responsabile della presentazione dei dati all'utente. Si occupa esclusivamente dell'interfaccia utente (UI) e non contiene logica di business.
*   **Dove si trova:** `src/main/webapp/`
*   **Componenti:**
    *   **JSP (JavaServer Pages):** Pagine dinamiche che ricevono i dati dal Controller e generano l'HTML da inviare al browser.
    *   **HTML, CSS, JavaScript:** Risorse statiche che definiscono la struttura, lo stile e l'interattività client-side delle pagine.

### 🔴 Controller
Il **Controller** agisce da intermediario tra il Model e la View. Gestisce le richieste HTTP in arrivo, interagisce con il Model per recuperare o aggiornare i dati e seleziona la View appropriata per generare la risposta.
*   **Dove si trova:** `src/main/java/control`
*   **Componenti:**
    *   **Servlet:** Classi Java che estendono `HttpServlet`. Ogni servlet è mappata su uno o più URL e funge da punto di ingresso per le richieste degli utenti. Coordina le azioni, richiama i metodi del Model e inoltra (forward) la richiesta alla View (JSP) corretta, arricchendola con i dati necessari (Model).

## 👨‍💻 Autori

*   **Michele** - [Michele98770](https://github.com/Michele98770)
*  **Felicia** - [Felicia](https://github.com/ljcia4)
*  **Giovanni** - [Giovanni](https://github.com/Giodr03)
