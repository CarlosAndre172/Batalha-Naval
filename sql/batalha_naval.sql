create database batalha_navalbd;

create table if not exists jogadores (
	id integer auto_increment primary key,
    nome varchar(20) not null
);

create table if not exists partidas (
	id_partida integer auto_increment primary key,
    id_jogador integer,
    pontuacao integer not null,
    tempo integer not null,
    tabuleiro enum('POCA', 'LAGO', 'OCEANO') not null,
    
    constraint fk_partida_jogador foreign key(id_jogador) references jogadores(id)
);

