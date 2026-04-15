create table recent_activity
(
    id            varchar(255) primary key,
    event_type    varchar(150) not null,
    aggregate_id  varchar(64),
    event_key     varchar(100),
    occurred_at   timestamp(6) with time zone not null,
    title         varchar(255),
    message       text,
    actor_user_id varchar(64),
    actor_name    varchar(255),
    entity_type   varchar(100),
    entity_id     varchar(64),
    header        jsonb,
    payload       jsonb,
    created_at    timestamp(6) with time zone,
    created_by    varchar(255),
    updated_at    timestamp(6) with time zone,
    updated_by    varchar(255)
);

create index idx_recent_activity_occurred_at
    on recent_activity (occurred_at desc, id desc);

create index idx_recent_activity_event_type
    on recent_activity (event_type);

