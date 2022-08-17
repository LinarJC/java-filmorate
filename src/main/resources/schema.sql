DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa_ratings;

create table if not exists GENRES
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(50) not null,
    constraint GENRES_PK
        primary key (GENRE_ID)
);

create table if not exists MPA_RATINGS
(
    MPA_ID     INTEGER auto_increment,
    MPA_NAME        CHARACTER VARYING(10) not null,
    DESCRIPTION CHARACTER VARYING(100),
    constraint MPA_RATINGS_PK
        primary key (MPA_ID)
);

create table if not exists FILMS
(
    FILM_ID      INTEGER auto_increment,
    FILM_NAME    CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(255) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    MPA_ID      INTEGER                not null,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FOREIGN_KEY_MPA
        foreign key (MPA_ID) references MPA_RATINGS
);

create table if not exists FILMS_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    UNIQUE(FILM_ID, GENRE_ID),
    constraint FOREIGN_KEY_FILM_ID
        foreign key (FILM_ID) references FILMS,
    constraint FOREIGN_KEY_GENRE_ID
        foreign key (GENRE_ID) references GENRES
);

create table if not exists USERS
(
    USER_ID   INTEGER auto_increment,
    USER_NAME CHARACTER VARYING(100) not null,
    LOGIN     CHARACTER VARYING(50)  not null,
    EMAIL     CHARACTER VARYING(200) not null,
    BIRTHDAY  DATE                   not null,
    constraint USER_ID
        primary key (USER_ID)
);

create unique index if not exists EMAIL_UNQ
    on USERS (EMAIL);

create unique index if not exists USERS_LOGIN_UNQ
    on USERS (LOGIN);

create table if not exists FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint FOREIGN_KEY_FRIEND
        foreign key (FRIEND_ID) references USERS,
    constraint FOREIGN_KEY_USER
        foreign key (USER_ID) references USERS
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FOREIGN_KEY_FILM
        foreign key (FILM_ID) references FILMS,
    constraint FOREIGN_KEY_USERLIKE
        foreign key (USER_ID) references USERS
);

