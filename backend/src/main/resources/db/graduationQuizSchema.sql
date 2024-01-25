DROP SCHEMA IF EXISTS graduationQuiz;
CREATE SCHEMA graduationQuiz;
USE graduationQuiz;

Create table user (
id bigint AUTO_INCREMENT NOT NULL,
email VARCHAR(255) NOT NULL,
username VARCHAR(255) NOT NULL,
password VARCHAR(255) NOT NULL,
PRIMARY KEY(id)
);

Create table asocijacije (
id bigint AUTO_INCREMENT NOT NULL,
a1 VARCHAR(255) NOT NULL,
a2 VARCHAR(255) NOT NULL,
a3 VARCHAR(255) NOT NULL,
a4 VARCHAR(255) NOT NULL,
b1 VARCHAR(255) NOT NULL,
b2 VARCHAR(255) NOT NULL,
b3 VARCHAR(255) NOT NULL,
b4 VARCHAR(255) NOT NULL,
c1 VARCHAR(255) NOT NULL,
c2 VARCHAR(255) NOT NULL,
c3 VARCHAR(255) NOT NULL,
c4 VARCHAR(255) NOT NULL,
d1 VARCHAR(255) NOT NULL,
d2 VARCHAR(255) NOT NULL,
d3 VARCHAR(255) NOT NULL,
d4 VARCHAR(255) NOT NULL,
konacnoA VARCHAR(255) NOT NULL,
konacnoB VARCHAR(255) NOT NULL,
konacnoC VARCHAR(255) NOT NULL,
konacnoD VARCHAR(255) NOT NULL,
konacno VARCHAR(255) NOT NULL,
PRIMARY KEY(id)
);

create table koZnaZna (
id bigint AUTO_INCREMENT NOT NULL,
question VARCHAR(255) NOT NULL,
option1 VARCHAR(255) NOT NULL,
option2 VARCHAR(255) NOT NULL,
option3 VARCHAR(255) NOT NULL,
option4 VARCHAR(255) NOT NULL,
answer VARCHAR(255) NOT NULL,
PRIMARY KEY(id)
);


create table korakpokorak (
id bigint AUTO_INCREMENT NOT NULL,
hint1 VARCHAR(255) NOT NULL,
hint2 VARCHAR(255) NOT NULL,
hint3 VARCHAR(255) NOT NULL,
hint4 VARCHAR(255) NOT NULL,
hint5 VARCHAR(255) NOT NULL,
hint6 VARCHAR(255) NOT NULL,
hint7 VARCHAR(255) NOT NULL,
konacno VARCHAR(255) NOT NULL,
PRIMARY KEY(id)
);

create table spojnice (
id bigint AUTO_INCREMENT NOT NULL,
subject VARCHAR(255) NOT NULL,
left1 VARCHAR(255) NOT NULL,
left2 VARCHAR(255) NOT NULL,
left3 VARCHAR(255) NOT NULL,
left4 VARCHAR(255) NOT NULL,
left5 VARCHAR(255) NOT NULL,
right1 VARCHAR(255) NOT NULL,
right2 VARCHAR(255) NOT NULL,
right3 VARCHAR(255) NOT NULL,
right4 VARCHAR(255) NOT NULL,
right5 VARCHAR(255) NOT NULL,
PRIMARY KEY(id)
);


INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno)
VALUES('Skladiste','Virtuelna','ROM','Racunar','Vid','Okvir','Staklo','Dioptrija', 'Mona Lisa', 'Crtez', 'Pejzaz', 'Freska', 'Refleksija', 'Zakrivljeno', 'Ravno', 'Zidno', 'Memorija', 'Naocare', 'Slika', 'Ogledalo', 'Ram');
INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno)
VALUES('Sud','Pravnik','Zastita','Tuzba','Ustav','Amandman','Skupstina','Parlament', 'Ubistvo', 'Kazna', 'Kriminal', 'Zrtva', 'Student', 'Dekan', 'Ispit', 'Kolokvijum', 'Advokat', 'Zakon', 'Zlocin', 'Fakultet', 'Pravo');

INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer)
VALUES('Prednja strana kovanog novca se naziva: ','Revers','Provers','Apoen','Avers','Avers');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer)
VALUES('Jacanje drzavne intervencije naziva se: ','Etatizam','Manirizam','Socijalizam','Elitizam','Etatizam');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer)
VALUES('Anglofobija je strah od:','Zivotinja','Svemira','Uglova','Engleza','Engleza');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer)
VALUES('Kako se naziva deficit vitamina C?','Bulimija','Skorbut','Anoreksija','Butskor','Skorbut');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer)
VALUES('Persijska marama je:','Esarpa','Sal','Bradaca','Samija','Sal');


INSERT INTO korak_po_korak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno)
VALUES('Moze biti brzo','Moze biti slepo','Moze se odnositi na srce','Odnosi se i na vrata','Ima veze sa tastaturom','Moze se odnositi na sat', 'Moze biti u drvo', 'Kucanje');
INSERT INTO korak_po_korak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno)
VALUES('Ima svoju sijalicu','Prema znacenju je skriven','U imenu je fiktivne materije','U sebi ima redni broj 36','Jedan je od plemenitih gasova','Oznacava se sa Kr', 'Otkriven je 1898.godine', 'Kripton');

INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5)
VALUES('Hemijske formule organskih jedinjenja','C6H6','C6H807','CH4','C3H60','C2H2', 'Aceton', 'Benzen', 'Limunska kiselina', 'Alkan', 'Acetilen');

INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5)
VALUES('Povezite reke sa drzavama u kojim se nalaze','SAD','Australija','Francuska','Meksiko','Brazil', 'Amazon', 'Sena', 'Kolorado', 'Mari', 'Ohajo');
