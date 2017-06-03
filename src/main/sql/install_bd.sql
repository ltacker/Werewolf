/**
 * Author:  lartigab
 * Created: 2 avr. 2017
 */

DROP TABLE Contaminations CASCADE CONSTRAINTS;
DROP TABLE Revelations CASCADE CONSTRAINTS;
DROP VIEW  Votes;
DROP TABLE Bulletins CASCADE CONSTRAINTS;
DROP TABLE Messages CASCADE CONSTRAINTS;
DROP TABLE Chatrooms CASCADE CONSTRAINTS;
DROP TABLE ProbasPouvoirs CASCADE CONSTRAINTS;
DROP VIEW  PartiesView;
DROP TABLE Villageois CASCADE CONSTRAINTS;
DROP TABLE Parties CASCADE CONSTRAINTS;
DROP TABLE Utilisateurs CASCADE CONSTRAINTS;

CREATE TABLE Utilisateurs (
    Login VARCHAR2(64) NOT NULL,
    Salt BLOB NOT NULL,
    HashedPassword BLOB NOT NULL,
    CONSTRAINT pk_Utilisateurs PRIMARY KEY (Login)
);

CREATE TABLE Parties (
    ID NUMBER GENERATED ALWAYS AS IDENTITY,
    NomPartie VARCHAR2(128) NOT NULL,
    DateDebut TIMESTAMP NOT NULL,
    DureeJour NUMBER NOT NULL,
    DureeNuit NUMBER NOT NULL,
    MinParticipants NUMBER NOT NULL,
    MaxParticipants NUMBER NOT NULL,
    ProportionLG NUMBER NOT NULL,
    Vainqueurs VARCHAR(16) DEFAULT NULL,
    CONSTRAINT pk_Parties PRIMARY KEY (ID),
    CONSTRAINT value_NombreParticipants CHECK (MinParticipants >= 4 AND MaxParticipants >= MinParticipants AND MaxParticipants <= 30),
    CONSTRAINT value_ProportionLG CHECK (ProportionLG BETWEEN 0 AND 1),
    CONSTRAINT value_Vainqueurs CHECK (Vainqueurs IN ('Humain', 'Loup-Garou'))
);

CREATE TABLE Villageois (
    Joueur_ID VARCHAR2(64) NOT NULL,
    Partie_ID NUMBER NOT NULL,
    RoleVillageois VARCHAR2(16) DEFAULT NULL,
    PouvoirVillageois VARCHAR2(16) DEFAULT NULL,
    Vivant NUMBER DEFAULT 1,
    CONSTRAINT pk_Villageois PRIMARY KEY (Joueur_ID, Partie_ID),
    CONSTRAINT fk_Villageois_Joueur FOREIGN KEY (Joueur_ID) REFERENCES Utilisateurs(Login) ON DELETE CASCADE,
    CONSTRAINT fk_Villageois_Partie FOREIGN KEY (Partie_ID) REFERENCES Parties(ID) ON DELETE CASCADE,
    CONSTRAINT value_RoleVillageois CHECK (RoleVillageois IN ('Humain', 'Loup-Garou')),
    CONSTRAINT value_PouvoirVillageois CHECK (PouvoirVillageois IN ('Contamination', 'Insomnie', 'Voyance', 'Spiritisme'))
);

CREATE VIEW PartiesView AS (
    SELECT P.ID ID, P.NomPartie NomPartie, P.DateDebut DateDebut,
        P.DureeJour DureeJour, P.DureeNuit DureeNuit, P.MinParticipants MinParticipants, P.MaxParticipants MaxParticipants,
        P.ProportionLG ProportionLG, P.Vainqueurs Vainqueurs, COUNT(V.Joueur_ID) NombreParticipants
    FROM Parties P LEFT OUTER JOIN Villageois V ON P.ID = V.Partie_ID
    GROUP BY P.ID, P.NomPartie, P.DateDebut, P.DureeJour, P.DureeNuit,
        P.MinParticipants, P.MaxParticipants, P.ProportionLG, P.Vainqueurs
);

CREATE TABLE ProbasPouvoirs (
    Partie_ID NUMBER NOT NULL,
    Pouvoir VARCHAR2(16),
    Proba NUMBER,
    CONSTRAINT pk_ProbasPouvoirs PRIMARY KEY (Partie_ID, Pouvoir),
    CONSTRAINT fk_Proba_Partie FOREIGN KEY (Partie_ID) REFERENCES Parties(ID) ON DELETE CASCADE,
    CONSTRAINT value_Pouvoir CHECK (Pouvoir IN ('Contamination', 'Insomnie', 'Voyance', 'Spiritisme')),
    CONSTRAINT value_Proba CHECK (Proba BETWEEN 0 AND 1)
);

CREATE TABLE Chatrooms (
    ID NUMBER GENERATED ALWAYS AS IDENTITY,
    Partie_ID NUMBER,
    TypeChatroom VARCHAR2(16),
    NumeroJour NUMBER,
    DateDebut TIMESTAMP,
    DateFin TIMESTAMP,
    Elimine_ID VARCHAR2(64) DEFAULT NULL,
    EspritAppele_ID VARCHAR2(64) DEFAULT NULL,
    CONSTRAINT pk_Chatrooms PRIMARY KEY (ID),
    CONSTRAINT unique_Chatroom UNIQUE (Partie_ID, TypeChatroom, NumeroJour),
    CONSTRAINT fk_Chatroom_Partie FOREIGN KEY (Partie_ID) REFERENCES Parties(ID) ON DELETE CASCADE,
    CONSTRAINT fk_Chatroom_Elimine FOREIGN KEY (Partie_ID, Elimine_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE,
    CONSTRAINT fk_Chatroom_EspritAppele FOREIGN KEY (Partie_ID, EspritAppele_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE,
    CONSTRAINT value_TypeChatroom CHECK (TypeChatroom IN ('Village', 'Loups-Garous', 'Medium'))
);

CREATE TABLE Messages (
    ID NUMBER GENERATED ALWAYS AS IDENTITY,
    Chatroom_ID NUMBER NOT NULL,
    Envoyeur_ID VARCHAR2(64) NOT NULL,
    DateEnvoi TIMESTAMP NOT NULL,
    Contenu VARCHAR2(1024),
    CONSTRAINT pk_Messages PRIMARY KEY (ID),
    CONSTRAINT fk_Message_Chatroom FOREIGN KEY (Chatroom_ID) REFERENCES Chatrooms(ID) ON DELETE CASCADE
);

CREATE TABLE Bulletins (
    Partie_ID NUMBER NOT NULL,
    Chatroom_ID NUMBER NOT NULL,
    Votant_ID VARCHAR2(64) NOT NULL,
    Cible_ID VARCHAR2(64) NOT NULL,
    CONSTRAINT pk_Votes PRIMARY KEY (Chatroom_ID, Votant_ID, Cible_ID),
    CONSTRAINT fk_Votes_Chatroom FOREIGN KEY (Chatroom_ID) REFERENCES Chatrooms(ID) ON DELETE CASCADE,
    CONSTRAINT fk_Votes_Votant FOREIGN KEY (Partie_ID, Votant_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE,
    CONSTRAINT fk_Votes_Cible FOREIGN KEY (Partie_ID, Cible_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE,
    CONSTRAINT No_Self_Vote CHECK(Cible_ID <> Votant_ID)
);

CREATE VIEW Votes AS (
    SELECT Partie_ID, Chatroom_ID, Cible_ID, COUNT(Votant_ID) NombreVotes
    FROM Bulletins
    GROUP BY Partie_ID, Chatroom_ID, Cible_ID
);

CREATE TABLE Revelations (
    Partie_ID NUMBER NOT NULL,
    Chatroom_ID NUMBER NOT NULL,
    Voyant_ID VARCHAR2(64) NOT NULL,
    Revele_ID VARCHAR2(64) NOT NULL,
    RoleDecouvert VARCHAR2(16),
    PouvoirDecouvert VARCHAR2(16),
    CONSTRAINT pk_Revelations PRIMARY KEY (Chatroom_ID, Voyant_ID),
    CONSTRAINT fk_Revelations_Chatroom FOREIGN KEY (Chatroom_ID) REFERENCES Chatrooms(ID) ON DELETE CASCADE,
    CONSTRAINT fk_Revelations_Voyant FOREIGN KEY (Partie_ID, Voyant_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE,
    CONSTRAINT fk_Revelations_Revele FOREIGN KEY (Partie_ID, Revele_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE,
    CONSTRAINT unique_Revele UNIQUE (Partie_ID, Voyant_ID, Revele_ID),
    CONSTRAINT value_RoleDecouvert CHECK (RoleDecouvert IN ('Humain', 'Loup-Garou')),
    CONSTRAINT value_PouvoirDecouvert CHECK (PouvoirDecouvert IN ('Contamination', 'Insomnie', 'Voyance', 'Spiritisme'))
);

CREATE TABLE Contaminations (
    Partie_ID NUMBER NOT NULL,
    Chatroom_ID NUMBER NOT NULL,
    Contaminateur_ID VARCHAR2(64) NOT NULL,
    Contamine_ID VARCHAR2(64) NOT NULL,
    CONSTRAINT pk_Contaminations PRIMARY KEY (Chatroom_ID, Contaminateur_ID),
    CONSTRAINT fk_Contaminations_Chatroom FOREIGN KEY (Chatroom_ID) REFERENCES Chatrooms(ID) ON DELETE CASCADE,
    CONSTRAINT fk_Contaminateur FOREIGN KEY (Partie_ID, Contaminateur_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE,
    CONSTRAINT fk_Contaminations_Contamine FOREIGN KEY (Partie_ID, Contamine_ID) REFERENCES Villageois(Partie_ID, Joueur_ID) ON DELETE CASCADE
);
