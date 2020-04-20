CREATE TABLE einheit (
    einheitID   NUMERIC NOT NULL,
    name         VARCHAR(30) NOT NULL,
    tag          VARCHAR(30) NOT NULL,
    stunde       NUMERIC(2) NOT NULL,
    raum         VARCHAR(3),
    notizen      VARCHAR(3000)
);

ALTER TABLE einheit ADD CONSTRAINT einheit_pk PRIMARY KEY ( einheitID );

CREATE TABLE lehrer (
    nachname    VARCHAR(30) NOT NULL,
    vorname     VARCHAR(30),
    lehrerID   NUMERIC NOT NULL
);

ALTER TABLE lehrer ADD CONSTRAINT lehrer_pk PRIMARY KEY ( lehrerID );

CREATE TABLE sondereinheit (
    sondereinheitID   NUMERIC NOT NULL,
    beschreibung         VARCHAR(30) NOT NULL,
    datum                DATE NOT NULL,
    s_einheitID       NUMERIC NOT NULL
);

ALTER TABLE sondereinheit ADD CONSTRAINT sondereinheit_pk PRIMARY KEY ( sondereinheitID );

CREATE TABLE termin (
    titel              VARCHAR(30) NOT NULL,
    "Start"            DATE NOT NULL,
    ende               DATE,
    ort                VARCHAR(30),
    benachrichtigung   NUMERIC(4),
    beschreibung       VARCHAR(2000),
    terminID          NUMERIC NOT NULL
);

ALTER TABLE termin ADD CONSTRAINT termin_pk PRIMARY KEY ( terminID );

CREATE TABLE unterrichtet (
    einheitID   NUMERIC NOT NULL,
    lehrerID     NUMERIC NOT NULL
);

ALTER TABLE unterrichtet ADD CONSTRAINT unterrichtet_pk PRIMARY KEY ( einheitID,
                                                                      lehrerID );

CREATE TABLE wiederholen (
    täglich            CHAR(1),
    wöchentlich        CHAR(1),
    monatlich          CHAR(1),
    jährlich           CHAR(1),
    terminID           NUMERIC NOT NULL
);

ALTER TABLE sondereinheit
    ADD CONSTRAINT sondereinheit_einheit_fk FOREIGN KEY ( s_einheitID )
        REFERENCES einheit ( einheitID );

ALTER TABLE unterrichtet
    ADD CONSTRAINT unterrichtet_einheit_fk FOREIGN KEY ( einheitID )
        REFERENCES einheit ( einheitID );

ALTER TABLE unterrichtet
    ADD CONSTRAINT unterrichtet_lehrer_fk FOREIGN KEY ( lehrerID )
        REFERENCES lehrer ( lehrerID );

ALTER TABLE wiederholen
    ADD CONSTRAINT wiederholen_termin_fk FOREIGN KEY ( terminID )
        REFERENCES termin ( terminID );
