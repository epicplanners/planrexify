delete from lehrer
where nachname = 'Franz';

delete from lehrer
where nachname = 'Maier';

insert into lehrer(nachname,vorname)
values
('Franz','Josef');

insert into lehrer(nachname,vorname)
values
('Maier','Josef');
