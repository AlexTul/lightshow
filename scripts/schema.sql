--CREATE TABLE--

create table Colors
(
    id              bigserial primary key,
    name            text not null unique
);

create table Lights
(
    id              bigserial primary key,
    label           text not null unique,
    color_id        bigint not null references Colors(id),
    enabled         boolean
);

create table Color_history
(
    id              bigserial primary key,
    light_id        bigint not null references Lights(id),
    old_color_id    bigint not null references Colors(id),
    new_color_id    bigint not null references Colors(id),
    changed_at      timestamp not null
);

create index on Color_history(light_id);
create index on Color_history(old_color_id, new_color_id);
create index on Color_history(changed_at);