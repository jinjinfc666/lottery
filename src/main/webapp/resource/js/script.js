$(function(){
	
	//单选复选
		//$('.ht_head li input[type=radio]').click(function(){
		
		//	if($(this).attr('id')=='radio1'){
		//		var n = 0;
		//	}
		//	else{
		//		var n = 1;
		//	}
			
		//	$('.ht_body').hide().eq(n).show();
			
		//})
    //选择圆角分
    /*
	$('.yjf span').click(function(){
		$('.yjf span').removeClass('on')
		$(this).addClass('on')
	})
    */
	//当前选中前三直选
	$('.h_type li').click(function(){
		$('.h_type li').removeClass('on')
		$(this).addClass('on')
		return false;
	})
	//0-9选中当前
	$('.digital_wrap li dl.num').each(function(){
		var f=$(this)
		$(this).find('dd').click(function(){
//			

//			$(this).addClass('on');
			if ($(this).hasClass('on')) {
				$(this).removeClass('on')
			} else{
				$(this).addClass('on')
			}
		})
	})
	
	//选中所有0-9状态 all	
	$('.digital_wrap li').each(function(){
		var f=$(this)
		$(this).find('.all').click(function(){
			f.find('.num dd').addClass('on');
			f.find('.txt dd').removeClass('on');
			$(this).addClass('on');
		})
		
		//清除所有0-9状态none 	
		$(this).find('.none').click(function(){
			f.find('.num dd').removeClass('on');
			f.find('.txt dd').removeClass('on');
			$(this).addClass('on');
		})
		
		//选中所有奇数状态odd 	
		$(this).find('.odd').click(function(){
			f.find('.num .od').addClass('on');
			f.find('.num .ev,.num .zo').removeClass('on');
			f.find('.txt dd').removeClass('on');
			$(this).addClass('on');
		})
		
		//选中所有偶数状态even	
		$(this).find('.even').click(function(){
			f.find('.num .ev').addClass('on');
			f.find('.num .od,.num .zo').removeClass('on');
			f.find('.txt dd').removeClass('on');
			$(this).addClass('on');
		})
	
		//选中所有数值大的数字big
		
		$(this).find('.big').click(function(){
			f.find('.num .bi').addClass('on');
			f.find('.num .sm').removeClass('on');
			f.find('.txt dd').removeClass('on');
			$(this).addClass('on');
		})
	
		//选中所有数值大的数字small			
		$(this).find('.small').click(function(){
			f.find('.num .sm').addClass('on');
			f.find('.num .bi').removeClass('on');
			f.find('.txt dd').removeClass('on');
			$(this).addClass('on');
		})
	
	})
		
	//滚动条美化
	$('#content').niceScroll({
        cursorcolor: "#d2ae7e",//#CC0071 光标颜色
        cursoropacitymax: 1, //改变不透明度非常光标处于活动状态（scrollabar“可见”状态），范围从1到0
        touchbehavior: false, //使光标拖动滚动像在台式电脑触摸设备
        cursorwidth: "8px", //像素光标的宽度
        cursorborder: "0", // 	游标边框css定义
        cursorborderradius: "8px",//以像素为光标边界半径
        autohidemode: false //是否隐藏滚动条
    });
	
	
	//删除
	$('.tz_bd .del').live('click',function(){
		$(this).parent().fadeOut(function(){
		    $(this).remove();
		    countmoney_all();
		});	
	})

	//关闭浮动
	$('.float').each(function(){
		var obj = $(this);
		$(this).find('.close').click(function(){
			obj.fadeOut();
		})
	})
	
	
    //	选号添加一行
    /*
	$('#pick').click(function () {

	    ValidCode("");
	    return;

    	 $('.h_tz .tz_bd .tab02 tbody').append(function(n){
    	 
    	var a='';
    	 $('.w .num .on').each(function(){
    	 	a = a+ $(this).html();
    	 })
    	 var b= '';
    	 $('.q .num .on').each(function(){
    	 	b = b+ $(this).html();
    	 })
    	 var c = '';
    	 $('.b .num .on').each(function(){
    	 	c = c+ $(this).html();
    	 })
    	 var d = '';
    	 $('.s .num .on').each(function(){
    	 	d = d+ $(this).html();
    	 })
    	 var e = '';
    	 $('.g .num .on').each(function(){
    	 	e = e+ $(this).html();
    	 })
	      return '<tr>'+
			    '<td height="20" width="20%" align="center"> '+$('.h_type li.on a').html()+ '</td>'+
			    '<td height="20" width="20%"  align="center" class="cod_rows">'+a+','+b+','+c+','+d+','+e+'</td>'+
			    '<td height="20" width="20%" align="center"> 1注 </td>'+
			    '<td height="20" width="20%" align="center">￥10</td>'+
			    '<td height="20" width="20%" class="del" align="center"><div class="del">×</div></td>'+
		    '</tr>';
	    });
		return false;
	})
	*/	
	
	
    //倒计时
    /*
	var intDiff = parseInt(6000);//倒计时总秒数量
	
	function timer(intDiff){
		window.setInterval(function(){
		var day=0,
			hour=0,
			minute=0,
			second=0;//时间默认值		
		if(intDiff > 0){
			day = Math.floor(intDiff / (60 * 60 * 24));
			hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
			minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
			second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
		}
		if (minute <= 9) minute = '0' + minute;
		if (second <= 9) second = '0' + second;
		$('#day_show').html(day);
		$('#hour_show').html('<s id="h"></s>'+hour);
		$('#minute_show').html('<s></s>'+minute);
		$('#second_show').html('<s></s>'+second);
		intDiff--;
		}, 1000);
	} 
	
	$(function(){
		timer(intDiff);
	});	
    */
})