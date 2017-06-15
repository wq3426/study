/**
 * Created by liuso on 2017/4/11.
 */

// 当前页
var currentPage = 1;
// 数量
var currentSize = 10;

/**
 * 页面初始化
 */
var updateFlag,deleteFlag,allocateWareHouseFlag,allocateRightFlag;
$(function () {
	
	// 更新权限
    updateFlag = ('true' == $("#updateFlag").val());
    // 删除权限
    deleteFlag = ('true' == $("#deleteFlag").val());
    // 分配仓库权限
    allocateWareHouseFlag = ('true' == $("#allocateWareHouseFlag").val());
    // 分配权限
    allocateRightFlag = ('true' == $("#allocateRightFlag").val());
    
    // 加载数据
    loadData(currentPage, currentSize);

    // 绑定搜索事件
    $('#queryBut').click(function(){
    	loadData(1, currentSize);
    });
    
    // 添加模态框
    $('#addRoleInfoModal').on('click', '.btn-primary', function () {
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
        resetForm($(this));
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
        	name: {
                validators: {
                    notEmpty: {
                        message: '角色名称不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "role/findNameIsExist",
                    	data : {id : 0},
                        type: 'GET',
                        delay: 1000,
                        message:'角色名称已经存在'
                    }
                }
            }
        }
    }).on('success.form.fv', function (e) {
        // Prevent form submission
        e.preventDefault();

        var $form = $(e.target),
            fv = $form.data('formValidation'),
            $modal = $form.parent().parent().parent().parent();
        
        $.LoadingOverlay("show");
        $.ajax({
            url: $form.attr('action'),
            type: 'PUT',
            data: $form.serialize(),
            success: function (result) {
            	$.LoadingOverlay("hide");
                if (result) {
                    console.log('添加成功');
                    // 隐藏模态框
                    $modal.modal('hide');
                    // 重新加载数据
                    loadData(1, currentSize);
                } else {
                    console.log('添加失败');
                    $modal.find('.alert').show();
                }
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('添加失败');
                $modal.find('.alert').show();
            }
        });
    });

    // 编辑模态框
    $('#editRoleInfoModal').on('click', '.btn-primary', function () {
        // 提交表单
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
        // 隐藏模态框时，清理校验状态
        resetForm($(this));
    }).on('show.bs.modal', function (event) {
    	
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes

        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        modal.find('[name=id]').val(id);

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        $.getJSON($('#baseUrl').attr('href') + 'role/' + id, function (result) {
            if (result) {
                modal.find('[name=name]').val(result.name);
            }
        });
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
            name: {
                validators: {
                    notEmpty: {
                        message: '角色名称不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "role/findNameIsExist",
                    	data : function(validator, $field, value){
                    		return {id : $('#editRoleInfoModal').find("[name=id]").val()};
                    	},
                        type: 'GET',
                        delay: 1000,
                        message:'角色名称已经存在'
                    }
                }
            }
        }
    }).on('success.form.fv', function (e) {
        // Prevent form submission
        e.preventDefault();

        var $form = $(e.target),
            fv = $form.data('formValidation'),
            $modal = $form.parent().parent().parent().parent();
        
        $.LoadingOverlay("show");
        $.ajax({
            url: $form.attr('action'),
            type: 'PATCH',
            data: $form.serialize(),
            success: function (result) {
            	$.LoadingOverlay("hide");
                if (result) {
                    console.log('编辑成功');
                    // 隐藏模态框
                    $modal.modal('hide');
                    // 重新加载数据
                    loadData(currentPage, currentSize);
                } else {
                    console.log('编辑失败');
                    $modal.find('.alert').show();
                }
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('编辑失败');
                $modal.find('.alert').show();
            }
        });
    });

    // 删除模态框
    $('#deleteRoleInfoModal').on('click', '.btn-danger', function () {
        var $form = $(this).parent().prev().find('form');
        var id = $form.find('[name=id]').val();
        var $modal = $(this).parent().parent().parent().parent();
        $.ajax({
            url: $form.attr('action') + '/' + id,
            type: 'DELETE',
            success: function (result) {
                if (result) {
                    console.log('删除成功');
                    // 隐藏模态框
                    $modal.modal('hide');
                    // 重新加载数据
                    loadData(currentPage, currentSize);
                }else{
                	console.log('删除失败');
                	alert("删除失败");
                }
            },error : function(result){
            	alert("删除失败");
            }
        });
    }).on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes
        var name = button.data('name'); // Extract info from data-* attributes

        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        modal.find('[name=id]').val(id);
        modal.find('.label-info').text(name);
    });
    
    // 分配仓库模态框
    $('#allocateWareHouseModal').on('click', '.btn-primary', function () {
    	
    	var selected = '';
    	$('#warehouseList').find("input[type=checkbox]").each(function(){
    		if($(this).prop("checked")){
    			selected += $(this).val() + ",";
    		}
    	});
    	if(selected == ''){
    		alert('请选择要分配的仓库');
    	}else{
    		var $allocateWareHouseModal = $('#allocateWareHouseModal');
    		// 角色id
    		var roleId = $allocateWareHouseModal.find("[name=id]").val();
    		$.LoadingOverlay("show");
    		$.ajax({
                url: $('#baseUrl').attr('href') + "role/updateRoleWareHouse",
                type: 'Patch',
                data: {roleId:roleId,warehouseId:selected},
                success: function (result) {
                	$.LoadingOverlay("hide");
                    if (result) {
                        console.log('分配成功');
                        // 隐藏模态框
                        $allocateWareHouseModal.modal('hide');
                        // 重新加载数据
                        loadData(currentPage, currentSize);
                    } else {
                        console.log('分配失败');
                        $allocateWareHouseModal.find('.alert').show();
                    }
                },error: function(result){
                	$.LoadingOverlay("hide");
                	console.log('分配失败');
                    $allocateWareHouseModal.find('.alert').show();
                }
            });
    	}

    }).on('show.bs.modal', function (event) {
    	
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes

        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        modal.find('[name=id]').val(id);
        modal.find('[type=checkbox]').prop("checked",false);

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        $.getJSON($('#baseUrl').attr('href') + 'role/selectWareHouseByRoleId/' + id, function (result) {
            if (result) {
            	loadWareHouseData(1,currentSize,result);
            }
        });
    });
    
    // 分配仓库模态框
    $('#allocateRightModal').on('click', '.btn-primary', function () {
    	
    	var selected = '';
    	
    	var zTree = $.fn.zTree.getZTreeObj("rightTree");  
		var nodes = zTree.getCheckedNodes(true); 
      	for (var i = 0; i < nodes.length; i++) {  
      		selected += nodes[i].id + ",";  
        }
      	
    	if(selected == ''){
    		alert('请选择要分配的权限');
    	}else{
    		var $allocateRightModal = $('#allocateRightModal');
    		// 角色id
    		var roleId = $allocateRightModal.find("[name=id]").val();
    		$.LoadingOverlay("show");
    		$.ajax({
                url: $('#baseUrl').attr('href') + "role/updateRoleRight",
                type: 'Patch',
                data: {roleId:roleId,rightId:selected},
                success: function (result) {
                	$.LoadingOverlay("hide");
                    if (result) {
                        console.log('分配成功');
                        // 隐藏模态框
                        $allocateRightModal.modal('hide');
                    } else {
                        console.log('分配失败');
                        $allocateRightModal.find('.alert').show();
                    }
                },error: function(result){
                	$.LoadingOverlay("hide");
                	console.log('分配失败');
                    $allocateRightModal.find('.alert').show();
                }
            });
    	}
    }).on('show.bs.modal', function (event) {
    	
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes

        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        modal.find('[name=id]').val(id);

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        $.getJSON($('#baseUrl').attr('href') + 'role/loadTree/' + id, function (result) {
            if(result){
            	 /* zTree树 */
				var leftRightTree = result;
				
				$(function(){
					function zTreeOnClick(event, treeId, treeNode) {
					    if(treeNode.level==0){
							return;
					    }
					};
					var setting = {
						callback : {
							onClick : zTreeOnClick
						},
						check: {  
		                	enable: true //显示复选框  
		            	},
		            	data: {
					       simpleData: {
					         enable: true
					       }
					     }
					};
					roleTree = $.fn.zTree.init($("#rightTree"), setting, leftRightTree);
				});
            }
        });
    });
});

//重置modal
function resetForm($modal){
	var $form = $modal.find('form');
	$form.find('[type=text]').val('');
	$modal.find('.alert').hide();
	$form.formValidation('resetForm', true);
}

/**
 * 加载仓库数据
 * 
 * @param page 页码
 * @param size 数量
 */
function loadWareHouseData(page, size, selectedWareHouseList) {
    
	var data = {
        page: page || 1,
        size: size || 10
    };

    // 获取数据
    $.get($('#baseUrl').attr('href') + 'ware-house', data, function (result) {
    	
        if (result) {
        	
            // 清空表格
            $('#warehouseList').empty();

            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#warehouseList').append('<tr>' +
                	"<td><input type='checkbox' value='" + item.id + "'" + (getCheckedFlag(selectedWareHouseList,item.id) ? 'checked' : '') + "></td>" +
                    '<td>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.code || '') + '</td>' +
                '</tr>');
            });
            
            // 初始化分页控件
            $("#warehousePagination").bs_pagination({
                currentPage: page,
                totalRows: result.total,
                rowsPerPage: size,
                totalPages: result.pages,
                onChangePage: function (event, data) {
                    console.log(event, data);
                    $("#allocateWareHouseModal").find(":checkbox").prop("checked",false);
                    loadWareHouseData(data.currentPage, data.rowsPerPage,selectedWareHouseList);
                }
            });
        } else {
            console.log('获取仓库列表失败，请重试');
        }
    });
}

function getCheckedFlag(selectedWareHouseList,id){
	var flag = false;
	if(selectedWareHouseList){
		$.each(selectedWareHouseList,function(index,item){
			if(id == item.id){
				flag = true;
			}
		});
	}
	return flag;
}

 /**
 * 加载角色数据
 * 
 * @param page 页码
 * @param size 数量
 */
function loadData(page, size) {

    currentSize = size;
    currentPage = page;

    var data = {
    	name: $('.navbar-form').find('[name=name]').val() || '',
        page: page || 1,
        size: size || 10
    };

    // 获取数据
    $.get($('#baseUrl').attr('href') + 'role', data, function (result) {
        if (result) {
            // 清空表格
            $('#roleList').empty();

            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#roleList').append('<tr>' +
                    '<td>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.name || '') + '</td>' +
                    '<td>' + (getWareHouseCode(item.wareHouseList) || '') + '</td>' +
                    '<td>' +
                    '<div class="btn-group">' +
                    (updateFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#editRoleInfoModal" data-id="' + item.id + '" title="编辑"><i class="fa fa-edit"></i></button>') : '') +
                    (deleteFlag ? ('<button class="btn btn-danger" data-toggle="modal" data-target="#deleteRoleInfoModal" data-id="' + item.id + '" data-name="' + item.name + '" title="删除"><i class="fa fa-trash"></i></button>') :'') +
                    (allocateWareHouseFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#allocateWareHouseModal" data-id="' + item.id +'" title="分配仓库"><i class="fa fa-home"></i></button>'):'') +
                    (allocateRightFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#allocateRightModal" data-id="' + item.id +'" title="分配权限"><i class="fa fa-cog"></i></button>') : '') +
                    '</div>' +
                    '</td>' +
                    '</tr>');
            });

            // 初始化分页控件
            $("#pagination").bs_pagination({
                currentPage: currentPage,
                totalRows: result.total,
                rowsPerPage: currentSize,
                totalPages: result.pages,
                onChangePage: function (event, data) {
                    console.log(event, data);
                    loadData(data.currentPage, data.rowsPerPage);
                }
            });
        } else {
            console.log('获取角色列表失败，请重试');
        }
    });
}

function getWareHouseCode(list){
	if(list && list.length > 0){
		var code = '';
		$.each(list,function(index, item){
			code += item.code + ',';
		})
		return code.substring(0,code.length - 1);
	}
	return '';
}