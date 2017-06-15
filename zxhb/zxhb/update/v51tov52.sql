/**
 * 2015-11-10  
 * 增加商品类型关联商品规格的功能
 */
CREATE TABLE `es_type_spec` (`type_id` int(11) DEFAULT NULL, `spec_id` int(11) DEFAULT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `es_goods_type` ADD `join_spec` smallint(1) DEFAULT 0 NOT NULL;
UPDATE `es_goods_type` SET `join_spec`='0';


/**
 * 2015-10-14 解决删除收货地址不能发货问题，
 * 添加一列。用户删除收货地址为假删
 */
alter table es_member_address add isdel int default 0