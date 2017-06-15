/**
 * 2015-10-14 解决删除收货地址不能发货问题，
 * 添加一列。用户删除收货地址为假删
 */
alter table es_member_address add isdel int default 0