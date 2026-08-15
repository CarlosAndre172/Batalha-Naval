create database batalha_navalbd;

-- O nome é único: o mesmo jogador sempre reaproveita a mesma linha, e é assim que
-- o ranking sabe que aquela pontuação nova é um recorde a bater (e não outra pessoa).
create table if not exists jogadores (
	id integer auto_increment primary key,
    nome varchar(20) not null unique
);

-- Uma linha por jogador em cada mar, guardando o MELHOR resultado dele ali.
-- Partida nova só substitui a antiga quando a pontuação é maior.
create table if not exists partidas (
	id_partida integer auto_increment primary key,
    id_jogador integer,
    pontuacao integer not null,
    tempo integer not null,
    tabuleiro enum('POCA', 'LAGO', 'OCEANO') not null,

    constraint fk_partida_jogador foreign key(id_jogador) references jogadores(id),
    constraint uk_recorde_por_mapa unique (id_jogador, tabuleiro)
);
