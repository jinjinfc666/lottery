function SetPlayType(type)
{
    $("#PlayType").val(type);
    switch($("#PlayType").val())
    {
        case "0":
            ShowPlayMode("[0][1]");
            SetPlayMode(0);  //选中某个模式            
            break;
        case "1":
            ShowPlayMode("[0][1]");
            SetPlayMode(0);
            break;
        case "2":
            ShowPlayMode("[0][1]");
            SetPlayMode(0);
            break;
        case "3":
            ShowPlayMode("[2][3][4]");
            SetPlayMode(2);
            break;
        case "4":
            ShowPlayMode("[2][3][4]");
            SetPlayMode(2);
            break;
        case "5":
            ShowPlayMode("[2][3][4]");
            SetPlayMode(2);
            break;
        case "6":
            ShowPlayMode("[5][6][7]");
            SetPlayMode(5);
            break;
        case "7":
            ShowPlayMode("[5][6][7]");
            SetPlayMode(5);
            break;

        case "8":
            ShowPlayMode("[8][9][10]");
            SetPlayMode(8);
            break;
        case "9":
            ShowPlayMode("");
            break;
        default:
            break;
    }
    ShowSelectArea();
}
//参数 '[1][2]'
function ShowPlayMode(ls)
{   
    var skey = "";
    var sList = ls;
    for (var i = 0; i < 11; i++) {
        skey = "["+i + "]";
        if (sList.indexOf(skey) > -1) {
            $("#mode" + i).show();
        }
        else {
            $("#mode" + i).hide();
        }
    }
}

function SetPlayMode(Mode)
{
    $("input[name='radio_mode'][value=" + Mode + "]").attr("checked", true);
    $("#PlayMode").val(Mode);
    //alert("vv"+$("input[name='radio_mode']:checked").val());   
}
function ShowSelectArea()
{
    var PlayTypeID = $("#PlayType").val();
    var ModeID = $("input[name='radio_mode']:checked").val();
    SetProtypetext();
    initorigicost_new(); //初始化奖金
    switch (PlayTypeID) {
        case "0":
            if (ModeID=="0")
                SetArea("[0][1][2]");
            else
                SetArea("[9]");
            break;
        case "1":
            if (ModeID == "0")
                SetArea("[1][2][3]");
            else
                SetArea("[9]");
            break;
        case "2":
            if (ModeID == "0")
                SetArea("[2][3][4]");
            else
                SetArea("[9]");
            break;
        case "3":
            if (ModeID == "2")
                SetArea("[6]");
            else if (ModeID == "3")
                SetArea("[7]");
            else if (ModeID == "4")
                SetArea("[9]");
            break;
        case "4":
            if (ModeID == "2")
                SetArea("[6]");
            else if (ModeID == "3")
                SetArea("[7]");
            else if (ModeID == "4")
                SetArea("[9]");
            break;
        case "5":
            if (ModeID == "2")
                SetArea("[6]");
            else if (ModeID == "3")
                SetArea("[7]");
            else if (ModeID == "4")
                SetArea("[9]");
            break;
        case "6":
            if (ModeID == "5")
                SetArea("[0][1]");
            else if (ModeID == "6")
                SetArea("[9]");
            else if (ModeID == "7")
                SetArea("[5]");
            break;
        case "7":
            if (ModeID == "5")
                SetArea("[3][4]");
            else if (ModeID == "6")
                SetArea("[9]");
            else if (ModeID == "7")
                SetArea("[5]");
            break;
        case "8":
            SetArea("[8]");
            break;
        case "9":
            SetArea("[0][1][2][3][4]");
            break;
        default:
            break;
    }
}

//初始化当前奖金
function initorigicost_new() {
    var protypetext = $("#protypetext").val();
    var pattern = $("#pattern").val();
    $.ajax({
        url: "/spi/initOriginalCost?protypetext=" + protypetext + "&pattern=" + pattern + "&time=" + new Date(),
        type: "get",
        cache: false,
        success: function (date) {
            var dateArr = new Array();
            dateArr = date.split("$");
            $("#origicost").val(dateArr[0]);
            $("#origirate").val(dateArr[1]);
            $("#isguding").val(dateArr[3]);
            $("#pattern").val(dateArr[4]);
            $("#dianCost").val(dateArr[5]);
            setcurcost(dateArr[2]);
            //hideshowadjust();
            //countmoney();
        },
        error: function () {
            showmsg("初始化当前奖金错误，请联系管理");
        }
    });
}

//参数 '[1][2]'
function SetArea(ls) {
    var skey = "";
    var sList = ls;
    for (var i = 0; i < 10; i++) {
        skey = "[" + i + "]";
        if (sList.indexOf(skey) > -1) {
            $("#"+i+"_area").show();
        }
        else {
            $("#" + i + "_area").hide();
        }
    }
    ClearOn();
    ClearOrder();
}

function ClearOn()
{
    //选中所有0-9状态 all	
    $('.digital_wrap li').each(function () {
        var f = $(this);
        f.find('.all').removeClass('on');
        f.find('.num dd').removeClass('on');
        f.find('.txt dd').removeClass('on');
    })
    $("#keybordnumber").val("");
}

//判断投注号码是否合法
function ValidCode()
{
    var PlayTypeID = $("#PlayType").val();
    var ModeID = $("input[name='radio_mode']:checked").val();

    var number = "";
    var count = 0;
    var ticketid = "";
    //var tabid = "";
    //9_area
    if ($('#9_area').css('display') == "" || $('#9_area').css('display') == "block")
    {
        ticketid = "keyinput";
    }
    else
    {
        ticketid = "mousinput";
    }

    if (ticketid == "keyinput" && (PlayTypeID == "0" || PlayTypeID == "1" || PlayTypeID == "2" || PlayTypeID == "3" || PlayTypeID == "4" || PlayTypeID == "5")) {
        var keyValue = $("#keybordnumber").val().replace(/\s{2,}/g, " ");
        keyValue = $.trim(keyValue);
        keyValue = keyValue.replace('，',',');
        keyValue = keyValue.replace(/	/g, ",");
        keyValue = keyValue.replace(/\n/g, ",");
        keyValue = keyValue.replace(/ /g, ",");
        keyValue = keyValue.replace(/;/g, ",");

        var Rex2 = new RegExp(/^\d{3}(,\d{3})*$/);
        if (!Rex2.test(keyValue)) {
            showmsg("输入有误,每组三位,以逗号分割开!");
            return;
        }
        else {
            AppendOrder("keyinput",keyValue);
        }
    }
    else if (ticketid == "keyinput" && (PlayTypeID == "6" || PlayTypeID == "7")) {
        var keyValue = $("#keybordnumber").val().replace(/\s{2,}/g, " ");
        keyValue = $.trim(keyValue);
        keyValue = keyValue.replace('，', ',');
        keyValue = keyValue.replace(/	/g, ",");
        keyValue = keyValue.replace(/\n/g, ",");
        keyValue = keyValue.replace(/ /g, ",");
        keyValue = keyValue.replace(/;/g, ",");
        var Rex3 = new RegExp(/^\d{2}(,\d{2})*$/);
        if (!Rex3.test(keyValue)) {
            showmsg("输入有误,每组两位,以逗号分割开!");
            return;
        }
        else {
            AppendOrder("keyinput", keyValue);
        }
    }
    else  //选择输入
    {

        var a = '';
        $('.w .num .on').each(function () {
            a = a + $(this).html();
        })
        var b = '';
        $('.q .num .on').each(function () {
            b = b + $(this).html();
        })
        var c = '';
        $('.b .num .on').each(function () {
            c = c + $(this).html();
        })
        var d = '';
        $('.s .num .on').each(function () {
            d = d + $(this).html();
        })
        var e = '';
        $('.g .num .on').each(function () {
            e = e + $(this).html();
        })
        var z2 = '';
        $('.z2 .num .on').each(function () {
            z2 = z2 + $(this).html();
        })
        var z3 = '';
        $('.z3 .num .on').each(function () {
            z3 = z3 + $(this).html();
        })
        var z6 = '';
        $('.z6 .num .on').each(function () {
            z6 = z6 + $(this).html();
        })
        var bdw = '';
        $('.bdw .num .on').each(function () {
            bdw = bdw + $(this).html();
        })
        
        var codes_dwd = a + ',' + b + ',' + c + ',' + d + ',' + e; //定位胆
        var codes = "";  //其他
        if (a != '')
        {
            codes = codes + a + ',';
        }
        if (b != '') {
            codes = codes + b + ',';
        }
        if (c != '') {
            codes = codes + c + ',';
        }
        if (d != '') {
            codes = codes + d + ',';
        }
        if (e != '') {
            codes = codes + e + ',';
        }

        if (z2 != '') {
            codes = codes + z2 + ',';
        }
        if (z3 != '') {
            codes = codes + z3 + ',';
        }
        if (z6 != '') {
            codes = codes + z6 + ',';
        }
        if (bdw != '') {
            codes = codes + bdw + ',';
        }


        if (codes.length > 1)
        {
            codes = codes.substr(0, codes.length - 1);
        }
        //alert(codes);
        var dateArr = new Array();
        dateArr = codes.split(",");
        var iLen = dateArr.length;
        //alert(iLen);
        //长度判断
        if (PlayTypeID == "0" || PlayTypeID == "1" || PlayTypeID == "2" )
        {
            if (iLen < 3)
            {
                showmsg("数据不完整,最少选择3个数字!");
                return;
            }
        }
        else if ((PlayTypeID == "3" && ModeID == "3") || (PlayTypeID == "4" && ModeID == "3") || (PlayTypeID == "5" && ModeID == "3"))
        {
            if (codes.length < 3) {
                showmsg("数据不完整,最少选择3个数字!");
                return;
            }
        }
        else if ((PlayTypeID == "3" && ModeID == "2") || (PlayTypeID == "4" && ModeID == "2") || (PlayTypeID == "5" && ModeID == "2")) {
            if (codes.length < 2) {
                showmsg("数据不完整,最少选择2个数字!");
                return;
            }
        }
        else if ((PlayTypeID == "6" || PlayTypeID == "7") && (ModeID == "5")) {
            if (iLen < 2) {
                showmsg("数据不完整,每位最少选择一位数字!");
                return;
            }
        }
        else if ((PlayTypeID == "6" || PlayTypeID == "7") && (ModeID == "7")) {
            if ((codes.length < 2) || (codes.length > 7)) {
                showmsg("数据不完整,请选择2-7个数字!");
                return;
            }
        }
        else if (PlayTypeID == "8") {
            if (codes.length < 1) {
                showmsg("数据不完整,每位最少选择一位数字!");
                return;
            }
        }
        else if (PlayTypeID == "9")  //定位胆
        {
            if (codes.length < 1) {
                showmsg("数据不完整,每位最少选择一位数字!");
                return;
            }
            codes = codes_dwd;
        }
        AppendOrder("mousinput", codes);

    }

}

function AppendOrder(typeid,code)
{
    //$('.h_tz .tz_bd .tab02 tbody').append(code_row);
    var PlayTypeID = $("#PlayType").val();
    var ModeID = $("input[name='radio_mode']:checked").val();


    if (typeid == "keyinput")
    {
        var listcount = 0;
        var inputvalue = code.replace(/\s{2,}/g, " ");
        inputvalue = $.trim(inputvalue);
        inputvalue = inputvalue.replace(/	/g, ",");
        inputvalue = inputvalue.replace(/\n/g, ",");
        inputvalue = inputvalue.replace(/ /g, ",");
        inputvalue = inputvalue.replace(/;/g, ",");

        var cutnumber = inputvalue.split(",");



        if (ModeID == "4") { //混选玩法;已经出现的组合(不论顺序),不添加到投注列表

            var arrnumber = new Array();
            $('.h_tz .tz_bd .tab02 .code_rows').each(function () {
                arrnumber.push($(this).html()); //向数组中添加元素  
            })

            //$('#numberlist option').each(function () {
            //    arrnumber.push($(this).val()); //向数组中添加元素  
            //});
            for (i = 0; i < cutnumber.length; i++) {
                var arr = cutnumber[i].split('');
                var num1 = arr[0] + arr[2] + arr[1] + "";
                var num2 = arr[1] + arr[0] + arr[2] + "";
                var num3 = arr[1] + arr[2] + arr[0] + "";
                var num4 = arr[2] + arr[0] + arr[1] + "";
                var num5 = arr[2] + arr[1] + arr[0] + "";

                var inx = $.inArray(cutnumber[i], arrnumber);
                if (inx >= 0) continue;
                var inx1 = $.inArray(num1, arrnumber);
                if (inx1 >= 0) continue;
                var inx2 = $.inArray(num2, arrnumber);
                if (inx2 >= 0) continue;
                var inx3 = $.inArray(num3, arrnumber);
                if (inx3 >= 0) continue;
                var inx4 = $.inArray(num4, arrnumber);
                if (inx4 >= 0) continue;
                var inx5 = $.inArray(num5, arrnumber);
                if (inx5 >= 0) continue;

                //addnumberlist(cutnumber[i]);
                $('.h_tz .tz_bd .tab02 tbody').append(order_row(typeid, cutnumber[i]));
                arrnumber.push(cutnumber[i]);
            }
        }
        else {
            for (i = 0; i < cutnumber.length; i++) {

                //addnumberlist(cutnumber[i]);
                $('.h_tz .tz_bd .tab02 tbody').append(order_row(typeid, cutnumber[i]));
            }
        }
        $("#keybordnumber").val("");
    }
    else {
        $('.h_tz .tz_bd .tab02 tbody').append(order_row(typeid,code));
    }

    ClearOn();
    countmoney_all();
}
function ClearOrder()
{
    $('.h_tz .tz_bd .tab02 tbody').html("");
}
function order_row(typeid, code)
{
    return '<tr>' +
      '<td height="20" width="20%" align="center"> ' + $('.h_type li.on a').html() + '</td>' +
      '<td height="20" width="20%"  align="center"  class="code_rows">' + code + '</td>' +
      '<td height="20" width="20%" align="center"  class="zs_rows">' + count_zs_je(typeid,code,'0') + '注</td>' +
      '<td height="20" width="20%" align="center"  class="je_rows">' + count_zs_je(typeid, code, '1') + '</td>' +
      '<td height="20" width="20%" class="del" align="center"><div class="del">×</div></td>' +
      '</tr>';
}
function countmoney_all() {
    var dz_num = 0;
    var xm_num = 0;
    var je_num = 0;
    var nr = "";
    $('.h_tz .tz_bd .tab02 .zs_rows').each(function () {
        nr = $(this).html().replace('注','');
        dz_num += parseInt(nr);
        xm_num += 1;
    })
    $('.h_tz .tz_bd .tab02 .je_rows').each(function () {
        nr = $(this).html();
        je_num += parseFloat(nr);
    })
    $("#totalnum").html(dz_num);
    $("#totalmoney").html(je_num);
    $("#xm_num").html(xm_num);
}
//获取金额和单注数，返回格式  '单注数_金额'，如 '1_2.0';
function count_zs_je(typeid,code,result)
{
    var totalnum = 0;  //单注数量
    var je_num = 0;
    var PlayTypeID = $("#PlayType").val();
    var ModeID = $("input[name='radio_mode']:checked").val();


    if (typeid == "keyinput") {
        totalnum = 1;
    }
        //前三组选组六、中三组选组六、后三组选组六、任选三组六
    else if (ModeID == "3") {
        var tempval = 1;
        var test = 1;
        var temps = 1;
        for (m = 3; m < code.length; m++) {
            temps++;
            test += temps;
            tempval += test;
        }
        totalnum += tempval;
    }
        //五星前二组选、五星后二组选、任选二组选
    else if (ModeID == "7") {
        var tempval = 1;
        var temps = 1;
        for (m = 2; m < code.length; m++) {
            temps++;
            tempval += temps;
        }
        totalnum += tempval;
    }
        //前三不定位、后三不定位
    else if (ModeID == "8" || ModeID == "9" || ModeID == "10") {
        totalnum += code.length;
    }
        //前三组选组三、后三组选组三、任选三组三
    else if (ModeID == "2") {
        var tempval = 2;
        var temps = 2;
        for (m = 2; m < code.length; m++) {
            temps += 2;
            tempval += temps;
        }
        totalnum += tempval;
    }
    //定位胆
    else if (PlayTypeID == "9") {
        var tempval = code.split(",");
        for (m = 0; m < tempval.length; m++)
            totalnum += tempval[m].length;
    }
    else {
        var temp = 1;
        var tempval = code.split(",");
        for (m = 0; m < tempval.length; m++)
            temp = temp * tempval[m].length;
        totalnum += temp;
    }
    je_num = (totalnum * 2 * $("#tomultiple").val() * $("#yjpattern").val()).toFixed(2);
    if (result == "0")
        return totalnum;
    else
        return je_num;

}

function confirmorder() {
    var xm_num = 0;
    $('.h_tz .tz_bd .tab02 .zs_rows').each(function () {
        xm_num += 1;
    })

    if (xm_num<1) {
        showmsg("请输入号码！");
        return;
    }
    $.messager.defaults = { ok: "是", cancel: "否" };
    var msg = ' 您确定加入 <span id="confirmexpect" class="red">' + $("#nextexpect").html() + '</span> 期:<div class="clean"></div> 加入号码: <div id="confirmnum" style="height:60px; width:260px; border:1px solid #ddd; overflow-x:hidden; overflow-y:scroll"> ' + getnumberstr_new() + '</div>         <div class="clean"></div>     总金额:<span id="confirmmoney" class="red">' + $("#totalmoney").html() + '</span>元';
    $.messager.confirm('确认投注', msg, function (r) {
        if (r) {
            createorders_new();
        }
    });
}

function getnumberstr_new() {
    var strnumber = "";
    var nr = "";
    $('.h_tz .tz_bd .tab02 .code_rows').each(function () {
        nr = $(this).html();
        strnumber += nr + "<br>";
    })
    return strnumber;
}

function delall_new()
{
    $('.h_tz .tz_bd .tab02 tbody').html("");
    $("#totalnum").html("0");
    $("#totalmoney").html("0");
    $("#xm_num").html("0");
}

function GetProtypetext() {
    var PlayTypeID = $("#PlayType").val();
    var ModeID = $("input[name='radio_mode']:checked").val();
    var protype = "";
    if (PlayTypeID == "0" && ModeID == "0") {
        protype = "022";
    }
    if (PlayTypeID == "0" && ModeID == "1") {
        protype = "021";
    }

    if (PlayTypeID == "1" && ModeID == "0") {
        protype = "040";
    }
    else if (PlayTypeID == "1" && ModeID == "1") {
        protype = "041";
    }
    else if (PlayTypeID == "2" && ModeID == "0") {
        protype = "002";
    }
    else if (PlayTypeID == "2" && ModeID == "1") {
        protype = "001";
    }

        /////////////
    else if (PlayTypeID == "3" && ModeID == "2") {
        protype = "024";
    }
    else if (PlayTypeID == "3" && ModeID == "3") {
        protype = "025";
    }
    else if (PlayTypeID == "3" && ModeID == "4") {
        protype = "023";
    }

    else if (PlayTypeID == "4" && ModeID == "2") {
        protype = "042";
    }
    else if (PlayTypeID == "4" && ModeID == "3") {
        protype = "043";
    }
    else if (PlayTypeID == "4" && ModeID == "4") {
        protype = "044";
    }

    else if (PlayTypeID == "5" && ModeID == "2") {
        protype = "004";
    }
    else if (PlayTypeID == "5" && ModeID == "3") {
        protype = "005";
    }
    else if (PlayTypeID == "5" && ModeID == "4") {
        protype = "003";
    }
        //////////////
    else if (PlayTypeID == "6" && ModeID == "5") {
        protype = "011";
    }
    else if (PlayTypeID == "6" && ModeID == "6") {
        protype = "010";
    }
    else if (PlayTypeID == "6" && ModeID == "7") {
        protype = "019";
    }

    else if (PlayTypeID == "7" && ModeID == "5") {
        protype = "014";
    }
    else if (PlayTypeID == "7" && ModeID == "6") {
        protype = "013";
    }
    else if (PlayTypeID == "7" && ModeID == "7") {
        protype = "017";
    }
        ///////////////
    else if (PlayTypeID == "8" && ModeID == "8") {
        protype = "028";
    }
    else if (PlayTypeID == "8" && ModeID == "9") {
        protype = "009";
    }
    else if (PlayTypeID == "8" && ModeID == "10") {
        protype = "008";
    }
    else if (PlayTypeID == "9") {
        protype = "016";
    }
    return protype;
}
function SetProtypetext()
{
    var PlayTypeID = $("#PlayType").val();
    var ModeID = $("input[name='radio_mode']:checked").val();
    var protype = GetProtypetext();
    $("#protypetext").val("SSC"+protype);
}

function createorders_new() {

    //$("#selected_submit").unbind("click");
    //var linkBtn = $(".selected_submit");
    //linkBtn.removeAttr("onclick");
    var PlayTypeID = $("#PlayType").val();
    var ModeID = $("input[name='radio_mode']:checked").val();

    var protypetext = "";//$("#protypetext").val();  //SSC035
    var nextexpect = $("#nextexpect").html();
    var tomultiple = $("#tomultiple").val();
    var pattern = $("#pattern").val();
    var protype = ""; //$("#protypetext").val().substr(3, 3); //035

    var numberval = "";
    var numwei = "";

    var protype = GetProtypetext();
    protypetext = "SSC" + protype;

    //var zhushu = $("#numberlist").children("option");
    //for (i = 0; i < zhushu.length; i++) {
    //    numberval += $(zhushu[i]).text();
    //    if (i < zhushu.length - 1)
    //        numberval += "$";
    //};

    $('.h_tz .tz_bd .tab02 .code_rows').each(function () {
        numberval += $(this).html() + "$";
    })
    //删除结尾的"$"
    if (numberval.length > 1)
    {
        numberval = numberval.substring(0, numberval.length-1);
    }

    ////任选三组六
    //if (protype == "035") {
    //    var games = document.getElementsByName('ckzl[]');
    //    for (i = 0; i < games.length; i++) {
    //        if (games[i].checked) {
    //            numwei += games[i].value + ',';
    //        }
    //    }
    //}
    //任选三组三
    //if (protype == "034") {
    //    var games = document.getElementsByName('ckzs[]');
    //    for (i = 0; i < games.length; i++) {
    //        if (games[i].checked) {
    //            numwei += games[i].value + ',';
    //        }
    //    }
    //}
    //任选三组二
    //if (protype == "037") {
    //    var games = document.getElementsByName('ckze[]');
    //    for (i = 0; i < games.length; i++) {
    //        if (games[i].checked) {
    //            numwei += games[i].value + ',';
    //        }
    //    }
    //}
    // alert(pattern);
    //alert("nextexpect" + nextexpect + " pattern=" + pattern);
    if (nextexpect == "00000000-000" || pattern == "") {
        showmsg("系统忙，请稍后再试！");
        return;
    }
    if (isNaN(tomultiple) || tomultiple <= 0) {
        showmsg("倍数输入错误，请重新输入！");
        return;
    }
    //delall_new();
    var activitytype = "1";
    if ($("#hostwallet").attr("checked") == "checked") {
        activitytype = "0";
    }
    //cleannumber();
    $.ajax({
        type: "post",
        url: "/spi/createOrder?t=" + Math.random(),
        data: {
            nextexpect: nextexpect,
            tomultiple: tomultiple,
            protypetext: protypetext,
            pattern: pattern,
            numberval: numberval,
            numwei: numwei,
            activitytype: activitytype
        },
        cache: false,

        beforeSend: function () {
            //$("#selected_submit").removeAttr("onclick");
            //$("#selected_submit").attr("href","#")
            //alert("f")
            //cleannumber();
        },
        success: function (date) {
            //alert("11")
            //if($('#selected_submit').attr("href")=="#")
            //{
            //$('#selected_submit').bind('click', createorders);
            // }
            showmsg(date);
            delall_new();
            //initPageInfo("initPage");
            if ($("#ltType").val() == "mmc") {
                //initPageInfo("initPage");
            }
        },
        error: function () {
            showmsg("暂时无法投注,请联系管理员！");
        }
    })
}