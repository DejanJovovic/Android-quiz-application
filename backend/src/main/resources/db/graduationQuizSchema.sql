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

ALTER TABLE asocijacije ADD COLUMN language VARCHAR(50) DEFAULT 'sr';
ALTER TABLE korakpokorak ADD COLUMN language VARCHAR(50) DEFAULT 'sr';
ALTER TABLE koznazna ADD COLUMN language VARCHAR(50) DEFAULT 'sr';
ALTER TABLE spojnice ADD COLUMN language VARCHAR(50) DEFAULT 'sr';


INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno)
VALUES('Skladiste','Virtuelna','ROM','Racunar','Vid','Okvir','Staklo','Dioptrija', 'Mona Lisa', 'Crtez', 'Pejzaz', 'Freska', 'Refleksija', 'Zakrivljeno', 'Ravno', 'Zidno', 'Memorija', 'Naocare', 'Slika', 'Ogledalo', 'Ram');
INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno)
VALUES('Sud','Pravnik','Zastita','Tuzba','Ustav','Amandman','Skupstina','Parlament', 'Ubistvo', 'Kazna', 'Kriminal', 'Zrtva', 'Student', 'Dekan', 'Ispit', 'Kolokvijum', 'Advokat', 'Zakon', 'Zlocin', 'Fakultet', 'Pravo');


INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno, language)
VALUES('Stock','Virtual','ROM','Computer','Sight','Framework','Glass','Diopter', 'Mona Lisa', 'Drawing', 'Landscape', 'Fresco', 'Reflection', 'Curved', 'Flat', 'Wall', 'Memory', 'Glasses', 'Photo', 'Mirror', 'Frame', 'en');
INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno, language)
VALUES('Court','Lawyer','Protection','Lawsuit','Constitution','Amendment','Assembly','Parliament', 'Murder', 'Punishment', 'Criminal', 'Victim', 'Student', 'Dean', 'Exam', 'Colloquium', 'Attorney', 'Law', 'Crime', 'College', 'Justice', 'en');


INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno, language)
VALUES('Existencias','Virtual','ROM','Computadora','Vista','Estructura','Vaso','Dioptría', 'Mona Lisa', 'Dibujo', 'Paisaje', 'Fresco', 'Reflexión', 'Curvo', 'Plano', 'Muro', 'Memoria', 'Anteojos', 'Foto', 'Espejo', 'Marco', 'es');
INSERT INTO asocijacije (a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4, konacnoA, konacnoB, konacnoC, konacnoD, konacno, language)
VALUES('Corte','Abogado','Proteccion','Pleito','Constitución','Enmienda','Asamblea','Parlamento', 'Asesinato', 'Castigo', 'Delincuente', 'Víctima', 'Alumno', 'Decano', 'Examen', 'Coloquio', 'Procurador', 'Ley', 'Delito', 'Colegio', 'Justicia', 'es');



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



INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('The front side of the coin is called: ','Revers','Provers','Denomination','Obverse','Obverse', 'en');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('Strengthening of state intervention is called: ','Statism','Mannerism','Socialism','Elitism','Statism', 'en');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('Anglophobia is the fear of:','Animals','Space','Angles','English people','English people', 'en');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('What is vitamin C deficiency called?','Bulimia','Scurvy','Anorexia','Butskor','Scurvy', 'en');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('Persian headscarf is:','Esarpa','Scarf','Beards','Samia','Scarf', 'en');


INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('El anverso de la moneda se llama: ','Solapa','Probadores','Denominación','Anverso','Anverso', 'es');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('El fortalecimiento de la intervención estatal se llama: ','Estatismo','Manierismo','Socialismo','Elitismo','Estatismo', 'es');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('La anglofobia es el miedo a:','Animales','Espacio','Anglos','Gente inglesa','Gente inglesa', 'es');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('¿Cómo se llama la deficiencia de vitamina C?','Bulimia','Escorbuto','Anorexia','Butskor','Escorbuto', 'es');
INSERT INTO koZnaZna (question, option1, option2, option3, option4, answer, language)
VALUES('El pañuelo persa es:','Esarpa','Bufanda','Barbas','Samia','Bufanda', 'es');




INSERT INTO korakpokorak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno)
VALUES('Moze biti brzo','Moze biti slepo','Moze se odnositi na srce','Odnosi se i na vrata','Ima veze sa tastaturom','Moze se odnositi na sat', 'Moze biti u drvo', 'Kucanje');
INSERT INTO korakpokorak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno)
VALUES('Ima svoju sijalicu','Prema znacenju je skriven','U imenu je fiktivne materije','U sebi ima redni broj 36','Jedan je od plemenitih gasova','Oznacava se sa Kr', 'Otkriven je 1898.godine', 'Kripton');



INSERT INTO korakpokorak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno, language)
VALUES('It can be fast','It can be blind','It can refer to the heart','It also applies to doors','It has something to do with the keyboard','It can refer to a watch', 'It can be in wood', 'Knocking', 'en');
INSERT INTO korakpokorak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno, language)
VALUES('It has its own light bulb','According to the meaning, it is hidden','It is in the name of fictitious matter','It has serial number 36 in it','It is one of the noble gases','It is marked with Kr', 'It was discovered in 1898', 'Krypton', 'en');


INSERT INTO korakpokorak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno, language)
VALUES('Puede ser rapido','Puede ser ciego','Puede referirse al corazón','También se aplica a las puertas','Tiene algo que ver con el teclado','Puede referirse a un reloj', 'Puede ser en madera', 'Golpes', 'es');
INSERT INTO korakpokorak (hint1, hint2, hint3, hint4, hint5, hint6, hint7, konacno, language)
VALUES('Tiene su propia bombilla','Según el significado, está oculto','Es en nombre de materia ficticia','Tiene el número de serie 36','Es uno de los gases nobles','Está marcado con Kr', 'Fue descubierto en 1898', 'Criptón', 'es');




INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5)
VALUES('Hemijske formule organskih jedinjenja','C6H6','C6H807','CH4','C3H60','C2H2', 'Aceton', 'Benzen', 'Limunska kiselina', 'Alkan', 'Acetilen');

INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5)
VALUES('Povezite reke sa drzavama u kojim se nalaze','SAD','Australija','Francuska','Meksiko','Brazil', 'Amazon', 'Sena', 'Kolorado', 'Mari', 'Ohajo');



INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5, language)
VALUES('Chemical formulas of organic compounds','C6H6','C6H807','CH4','C3H60','C2H2', 'Acetone', 'Benzene', 'Citric acid', 'Alkan', 'Acetylene', 'en');

INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5, language)
VALUES('Match the rivers with the states in which they are located','USA','Australia','France','Mexico','Brazil', 'Amazon', 'Seine', 'Colorado', 'Mary', 'Ohio', 'en');


INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5, language)
VALUES('Fórmulas químicas de compuestos orgánicos','C6H6','C6H807','CH4','C3H60','C2H2', 'Acetona', 'Benceno', 'Ácido cítrico', 'Alcano', 'Acetileno', 'es');

INSERT INTO spojnice (subject, left1, left2, left3, left4, left5, right1, right2, right3, right4, right5, language)
VALUES('Relaciona los ríos con los estados en los que se encuentran','USA','Australia','Francia','México','Brasil', 'Amazonas', 'Sena', 'Colorado', 'Mary', 'Ohio', 'es');

