-- Generiert von Oracle SQL Developer Data Modeler 19.2.0.182.1216
--   am/um:        2020-03-30 15:09:28 MESZ
--   Site:      Oracle Database 11g
--   Typ:      Oracle Database 11g



CREATE TABLE einheit (
    einheit_id   NUMBER NOT NULL,
    name         VARCHAR2(30) NOT NULL,
    tag          VARCHAR2(30) NOT NULL,
    stunde       NUMBER(2) NOT NULL,
    raum         VARCHAR2(3),
    notizen      VARCHAR2(3000)
);

ALTER TABLE einheit ADD CONSTRAINT einheit_pk PRIMARY KEY ( einheit_id );

CREATE TABLE lehrer (
    nachname    VARCHAR2(30) NOT NULL,
    vorname     VARCHAR2(30),
    lehrer_id   NUMBER NOT NULL
);

ALTER TABLE lehrer ADD CONSTRAINT lehrer_pk PRIMARY KEY ( lehrer_id );

CREATE TABLE sondereinheit (
    einheit_einheit_id   NUMBER NOT NULL,
    beschreibung         VARCHAR2(30) NOT NULL,
    datum                DATE NOT NULL
);

ALTER TABLE sondereinheit ADD CONSTRAINT sondereinheit_pk PRIMARY KEY ( einheit_einheit_id );

CREATE TABLE termin (
    titel              VARCHAR2(30) NOT NULL,
    "Start"            DATE NOT NULL,
    ende               DATE,
    ort                VARCHAR2(30),
    benachrichtigung   NUMBER(4),
    beschreibung       VARCHAR2(2000),
    termin_id          NUMBER NOT NULL
);

ALTER TABLE termin ADD CONSTRAINT termin_pk PRIMARY KEY ( termin_id );

CREATE TABLE unterrichtet (
    einheit_einheit_id   NUMBER NOT NULL,
    lehrer_lehrer_id     NUMBER NOT NULL
);

ALTER TABLE unterrichtet ADD CONSTRAINT unterrichtet_pk PRIMARY KEY ( einheit_einheit_id,
                                                                      lehrer_lehrer_id );

CREATE TABLE wiederholen (
    täglich            CHAR(1),
    wöchentlich        CHAR(1),
    monatlich          CHAR(1),
    jährlich           CHAR(1),
    termin_termin_id   NUMBER NOT NULL
);

ALTER TABLE sondereinheit
    ADD CONSTRAINT sondereinheit_einheit_fk FOREIGN KEY ( einheit_einheit_id )
        REFERENCES einheit ( einheit_id );

ALTER TABLE unterrichtet
    ADD CONSTRAINT unterrichtet_einheit_fk FOREIGN KEY ( einheit_einheit_id )
        REFERENCES einheit ( einheit_id );

ALTER TABLE unterrichtet
    ADD CONSTRAINT unterrichtet_lehrer_fk FOREIGN KEY ( lehrer_lehrer_id )
        REFERENCES lehrer ( lehrer_id );

ALTER TABLE wiederholen
    ADD CONSTRAINT wiederholen_termin_fk FOREIGN KEY ( termin_termin_id )
        REFERENCES termin ( termin_id );

CREATE SEQUENCE einheit_einheit_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER einheit_einheit_id_trg BEFORE
    INSERT ON einheit
    FOR EACH ROW
    WHEN ( new.einheit_id IS NULL )
BEGIN
    :new.einheit_id := einheit_einheit_id_seq.nextval;
END;
/

CREATE SEQUENCE lehrer_lehrer_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER lehrer_lehrer_id_trg BEFORE
    INSERT ON lehrer
    FOR EACH ROW
    WHEN ( new.lehrer_id IS NULL )
BEGIN
    :new.lehrer_id := lehrer_lehrer_id_seq.nextval;
END;
/

CREATE SEQUENCE termin_termin_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER termin_termin_id_trg BEFORE
    INSERT ON termin
    FOR EACH ROW
    WHEN ( new.termin_id IS NULL )
BEGIN
    :new.termin_id := termin_termin_id_seq.nextval;
END;
/



-- Zusammenfassungsbericht für Oracle SQL Developer Data Modeler: 
-- 
-- CREATE TABLE                             6
-- CREATE INDEX                             0
-- ALTER TABLE                              9
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           3
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          3
-- CREATE MATERIALIZED VIEW                 0
-- CREATE MATERIALIZED VIEW LOG             0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
