create table device_token (
     id varchar(64) primary key,
     driver_id varchar(64) not null,
     user_id varchar(64),
     device_id varchar(128),
     fcm_token varchar(512) not null,
     platform varchar(20) not null,
     app_version varchar(50),
     active boolean not null default true,
     last_seen_at timestamptz,
     created_at timestamp(6) with time zone,
     created_by varchar(255),
     updated_at timestamp(6) with time zone,
     updated_by varchar(255)
);

create unique index uk_driver_device_token_token
    on device_token(fcm_token);