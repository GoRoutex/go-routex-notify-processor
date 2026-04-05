create table notification_template (
    code varchar(50) primary key,
    title_template text not null,
    body_template text not null,
    active boolean not null,
    created_at timestamp(6) with time zone,
    created_by varchar(255),
    updated_at timestamp(6) with time zone,
    updated_by varchar(255)
)