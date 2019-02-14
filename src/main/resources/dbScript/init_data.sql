insert into dictionary_code(id, code, name, editable)
values ('f4de0532379344d28fb82a16e70123ce', 'EquipmentType', '设备类别', false),
       ('381f290ca6f34cc99ff855c64d1efad5', 'Brand', '品牌', false);

insert into group_info(id, code, name, editable)
values ('158e534a75cc4b0fa63b955e9cbcca8a', 'admin', '管理员', false);

insert into user_info(id, username, password, full_name, enable, locked, created)
values ('e4d332f46be642b5a0aa8c07191e4fdf', 'admin', '$2a$10$Tae72Uw/ahmqYWlbxQy/Y.wg2ypr4YGJwbezZ1hMcv1MHvPKPMEju', 'admin', true, false,
        current_timestamp());


