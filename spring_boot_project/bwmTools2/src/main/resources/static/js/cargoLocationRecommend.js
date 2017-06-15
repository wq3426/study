/**
 * Created by liuso on 2017/4/11.
 */

// 当前页
var currentPage = 1;
// 数量
var currentSize = 10;

// 上次货位预留值
var lastreservePercentage = '';

// 图表
var myChart;

// 查询结果
var queryResult = '';

/**
 * 页面初始化
 */
$(function () {
	
    // 加载数据
    //loadData(currentPage, currentSize);
    
    // 加载货位类型数据
    loadCargoType($("#cargoLocationTypeId"));
    
    // 绑定导出事件
    $('#exportBut').click(function () {
    	window.location.href = $('#baseUrl').attr('href') + 'cargo-location-recommend/export';
    });

    // 绑定搜索事件(先进行货位推荐，推荐成功之后再查询)
    $('#queryBut').click(function(){
    	
    	// 货位预留值
    	var reservePercentage = $('.navbar-form').find('[name=reservePercentage]').val() || '0';
    	
    	if(!$.isNumeric(reservePercentage)){
    		alert('货位预留必须为正整数');
    		return;
    	}
    	
    	// 避免每次查询都重新推荐货位，如果两次输入的货位预留值相同则不重新推荐
    	if(lastreservePercentage != reservePercentage){
    		// 设置上次预留值
    		lastreservePercentage = reservePercentage;
    		
    		$.LoadingOverlay("show",{custom  : getCustomElement('正在为物料推荐货位，请耐心等待...')});
        	// 推荐货位
        	$.get($('#baseUrl').attr('href') + "cargo-location-recommend/recommend",{'reservePercentage':reservePercentage},function(data){
        		 $.LoadingOverlay("hide");
        		if(data){
        			if(data.success){
            			// 重新加载数据
                        loadData(1, currentSize);
            		}else{
            			alert(data.msg);
            		}
        		}else{
        			alert('重新推荐失败');
        		}
        	});
    	}else{
    		loadData(1, currentSize);
    	}
    });
    
    // 图表
    $('#chartBut').click(function(){
    	if(!queryResult){
    		alert('请先进行查询,没有数据不能生成图表');
    		return;
    	}
    	$("#chartModal").modal('show');
    });
    
    // 导入物料拣货频率数据
    $('#importMaterialFreqModal').on('click', '.btn-primary', function () {
        // 提交表单
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
    	$(this).find('form').formValidation('resetForm', true);
    }).find("form").formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
            file: {
                validators: {
                    notEmpty: {
                        message: '导入文件不能为空'
                    },
                    file: {
                    	extension : 'xlsx',
                    	message: '文件格式有误,应该是xlsx'
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
        
        	var formdata = new FormData($form[0]);
        	var reservePercentage = $('.navbar-form').find('[name=reservePercentage]').val() || '0';
        	
            $.LoadingOverlay("show",{custom  : getCustomElement('正在导入物料的拣货频率，请耐心等待...')});
            $.ajax({
                type : 'post',
                url : $form.attr('action') + '/' + reservePercentage,
                data : formdata,
                cache : false,
                processData : false, // 不处理发送的数据，因为data值是Formdata对象，不需要对数据做处理
                contentType : false, // 不设置Content-type请求头
                success : function(result){
             	  $.LoadingOverlay("hide");
             	   if (result.success) {
                        console.log('导入成功');
                        // 隐藏模态框
                        $modal.modal('hide');
                        // 重新加载数据
                        loadData(1, currentSize);
                    } else {
                        console.log('导入失败');
                        if(result.msg){
                     	   $("#importError").html(result.msg);
                        }else{
                     	   $("#importError").html('导入物料拣货频率失败，请重试!');
                        }
                        $modal.find('.alert').show();
                    }
        	   	},
                error : function(){
                   $.LoadingOverlay("hide");
             	   console.log('导入失败');
             	   $("#importError").html('导入物料拣货频率失败，请重试!');
                   $modal.find('.alert').show();
        	   }
            });
    });
    
    // 基于准备好的dom，初始化echarts实例
    myChart = echarts.init(document.getElementById('main'));
    
    // 指定图表的配置项和数据
    var option = {
    	    tooltip: {
    	        trigger: 'axis',
    	        axisPointer: {
    	            type: 'cross',
    	            crossStyle: {
    	                color: '#999'
    	            }
    	        }
    	    },
    	    toolbox: {
    	        feature: {
    	        	magicType: {show: true, type: ['line', 'bar']},
    	            saveAsImage: {show: true}
    	        }
    	    },
    	    legend: {
    	        data:['原货位分值','拣货频率','推荐货位分值']
    	    },
    	    xAxis: [
    	        {
    	            type: 'category',
    	            data: [],
    	            axisPointer: {
    	                type: 'shadow'
    	            },
    	            axisTick:{
    	            	interval:0
    	            },
    	            axisLabel:{
                        interval:0,
                        rotate: 45
                    }
    	        }
    	    ],
    	    yAxis: [
    	        {
    	            type: 'value',
    	            name: '分值',
    	            min: 0,
    	            max: 1000,
    	            interval: 100,
    	            axisLabel: {
    	                formatter: '{value}'
    	            }
    	        },
    	        {
    	            type: 'value',
    	            name: '拣货频率',
    	            min: 0,
    	            max: 200,
    	            interval: 20,
    	            axisLabel: {
    	                formatter: '{value}'
    	            }
    	        }
    	    ],
    	    series: [
    	        {
    	            name:'原货位分值',
    	            type:'line',
    	            data:[]
    	        },
    	        {
    	            name:'拣货频率',
    	            type:'line',
    	            yAxisIndex: 1,
    	            data:[]
    	        },
    	        {
    	            name:'推荐货位分值',
    	            type:'line',
    	            data:[]
    	        }
    	    ]
    	};
    
    // 图表
    $('#chartModal').on('show.bs.modal', function () {
    	
    	var xName = [];// X轴名称
    	var pickUpRate = [];// 拣货频率
    	var cargoLocationScore = [];// 原货位分值
    	var recommendedLocationScore = [];// 推荐货位分值
    	
    	if(queryResult){
    		
    		$.each(queryResult,function(index,item){
    			var name = (item.cargoLocationCode || '') + item.code + (item.recommendedLocationCode || '');
    			//xName.push(item.cargoLocationCode || '');
    			xName.push(item.code || '');
    			//xName.push(item.recommendedLocationCode || '');
    			pickUpRate.push(parseInt(item.pickUpRate || '0'));
    			cargoLocationScore.push(parseFloat(item.cargoLocationScore || '0'));
    			recommendedLocationScore.push(parseFloat(item.recommendedLocationScore || '0'));
    			
    			option.xAxis[0].data = xName;
    			option.series[0].data = cargoLocationScore;
    			option.series[1].data = pickUpRate;
    			option.series[2].data = recommendedLocationScore;
    		});
    		
    		// 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
    	}
    });
});


/**
 * 加载货位类型
 */
function loadCargoType($select){
	$.get($('#baseUrl').attr('href') + 'cargo-location-type/list',function (result) {
		if(result){
			$(result).each(function(index,item){
				$select.append("<option value='" + item.id + "'>" + item.code + "</option>");
			});
		}
	});
}


/**
 * 加载数据
 * @param page 页码
 * @param size 数量
 */
function loadData(page, size) {

    currentSize = size;
    currentPage = page;

    var data = {
    	cargoLocationTypeId: $('.navbar-form').find('[name=cargoLocationTypeId]').val() || '',
    	code: $('.navbar-form').find('[name=code]').val() || '',
        page: page || 1,
        size: size || 10
    };
    
    // 获取数据
    $.get($('#baseUrl').attr('href') + 'cargo-location-recommend', data, function (result) {
        
    	if (result) {
    		
    		queryResult = result.list;
        	
            // 清空表格
            $('#materialList').empty();
            
            var fast = $('.navbar-form').find('[name=fast]').val();
    		var slower = $('.navbar-form').find('[name=slower]').val();
            
            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#materialList').append('<tr>' +
                    '<td>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.code || '') + '</td>' +
                    '<td>' + (item.cargoLocationCode || '') + '</td>' +
                    '<td>' + (item.pickUpRate || '') + '</td>' +
                    '<td>' + (item.cargoLocationScore || '') + '</td>' +
                    '<td>' + (item.recommendedLocationCode || '') + '</td>' +
                    '<td>' + (item.recommendedLocationScore || '') + '</td>' +
                    '<td>' + (getOptimizationRecommendation(item,fast,slower) || '') + '</td>' +
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
        	queryResult = '';
            console.log('获取物料推荐信息失败，请重试');
        }
    });
}

function getOptimizationRecommendation(item,fast,slower){
	if(!item.cargoLocationCode){
		return '新物料';
	}else{
		var cargoLocationScore = item.cargoLocationScore || '0';
		var recommendedLocationScore = item.recommendedLocationScore || '0';
		if($.isNumeric(fast)){
			if(recommendedLocationScore - cargoLocationScore > fast){
				return 'fast mover';
			}
		}
		if($.isNumeric(slower)){
			if(cargoLocationScore - recommendedLocationScore > slower){
				return 'slower mover';
			}
		}
	}
	return '';
}