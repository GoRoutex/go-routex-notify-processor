alter table recent_activity
    add column if not exists merchant_id varchar(64);

create index if not exists idx_recent_activity_merchant_occurred_at
    on recent_activity (merchant_id, occurred_at desc, id desc);

