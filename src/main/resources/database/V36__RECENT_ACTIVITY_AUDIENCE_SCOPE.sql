alter table recent_activity
    add column if not exists audience_type varchar(30),
    add column if not exists scope_type varchar(30),
    add column if not exists scope_id varchar(64),
    add column if not exists visibility varchar(30),
    add column if not exists severity varchar(20),
    add column if not exists status varchar(30),
    add column if not exists source_service varchar(80),
    add column if not exists correlation_id varchar(100),
    add column if not exists entity_display_name varchar(255);

update recent_activity
set audience_type = case when merchant_id is null or merchant_id = '' then 'ADMIN' else 'MERCHANT' end,
    scope_type = case when merchant_id is null or merchant_id = '' then 'SYSTEM' else 'MERCHANT' end,
    scope_id = case when merchant_id is null or merchant_id = '' then null else merchant_id end,
    visibility = case when merchant_id is null or merchant_id = '' then 'ADMIN_ONLY' else 'MERCHANT_ONLY' end,
    severity = coalesce(severity, 'INFO'),
    status = coalesce(status, 'SUCCESS')
where audience_type is null
   or scope_type is null
   or visibility is null
   or severity is null
   or status is null;

create index if not exists idx_recent_activity_audience_occurred_at
    on recent_activity (audience_type, occurred_at desc, id desc);

create index if not exists idx_recent_activity_scope_occurred_at
    on recent_activity (scope_type, scope_id, occurred_at desc, id desc);

create index if not exists idx_recent_activity_severity_occurred_at
    on recent_activity (severity, occurred_at desc);

create index if not exists idx_recent_activity_entity
    on recent_activity (entity_type, entity_id);

create index if not exists idx_recent_activity_correlation
    on recent_activity (correlation_id);
