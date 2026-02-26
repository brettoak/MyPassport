-- Add sys_code to roles table
ALTER TABLE roles ADD COLUMN sys_code VARCHAR(255) NOT NULL DEFAULT 'default_sys';

-- Add sys_code to permissions table
ALTER TABLE permissions ADD COLUMN sys_code VARCHAR(255) NOT NULL DEFAULT 'default_sys';

-- (Optional) If we want to drop the default constraints later, uncomment the following:
-- ALTER TABLE roles ALTER COLUMN sys_code DROP DEFAULT;
-- ALTER TABLE permissions ALTER COLUMN sys_code DROP DEFAULT;
