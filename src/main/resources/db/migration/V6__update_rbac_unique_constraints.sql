-- Drop old unique constraints on name
ALTER TABLE roles DROP INDEX name;
ALTER TABLE permissions DROP INDEX name;

-- Add new compound unique constraints on (name, sys_code)
ALTER TABLE roles ADD UNIQUE INDEX uq_roles_name_sys_code (name, sys_code);
ALTER TABLE permissions ADD UNIQUE INDEX uq_permissions_name_sys_code (name, sys_code);
