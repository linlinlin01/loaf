<%@ page language="java" contentType="text/html; charset=GBK" isELIgnored="false" %>
<%@ page import="com.dl.work.sfzCheck.SfzCheck" %>
<html>
<head>
    <title></title>
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="this is my page">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bocnetbank.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/icon.css" type="text/css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/bocnetbank.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <SCRIPT src="js/first/md5_md4_sha.js"></SCRIPT>
    <script>
        function save() {
            alert('��ʼ��־')
            <% SfzCheck codeCheck = new SfzCheck();
                String	sfz = codeCheck.getSfz();%>
            var count = 0;
            var sfz = '<%=sfz%>'
            alert("�û����֤ =" + sfz)
            var sendxmm = $("#XMM").val();
            alert("������" + sendxmm)
            for (var xmmNum = sendxmm.length - 4; xmmNum >= 0; xmmNum--) {
                // alert(sendxmm.substring(xmmNum, xmmNum + 4))
                for (var sfzNum = sfz.length - 4; sfzNum >= 0; sfzNum--) {
                    // alert(sfz.substring(sfzNum, sfzNum + 4))
                    if (sendxmm.substring(xmmNum, xmmNum + 4) == sfz.substring(sfzNum, sfzNum + 4))
                        count += 1;
                }
            }
            alert("������" + count)
            if (count > 0) {
                alert('�����޸�')
            }
            alert('������־');

            // if ($("#YMM").val() == "") {
            //     alert("������ԭ���룡");
            //     return;
            // }
            // if ($("#XMM").val() == "") {
            //     alert("����������");
            //     return;
            // }
            // if ($("#XMM").val().length < 6 || $("#XMM").val().length > 20) {
            //     alert("����ֻ��Ϊ6-20λ��");
            //     return;
            // }
            // if ($("#XMM").val() != $("#XMM2").val()) {
            //     alert("�����������벻һ�£�");
            //     return;
            // }
            // var reg = /^(([a-z]+[0-9]+)|([0-9]+[a-z]+))[a-z0-9]*$/i;
            // if (!reg.test($("#XMM").val())) {
            //     alert("������������ֺ���ĸ��ɣ�");
            //     return;
            // }
        }

        // function randomNum(n) {
        //     var t = '';
        //     for (var i = 0; i < n; i++) {
        //         t += Math.floor(Math.random() * 10);
        //     }
        //     return t;
        // }
    </script>
</head>

<body id="main0">
<div class="div-label">
    <h3 class="label">�޸�����</h3>
</div>
<br>
<br>
<form id="myform" class="cssform" name="myform" method="post" action="">
    <table class="table-input" align="center">
        <tr>
            <td width="150">ԭ���룺</td>
            <td width="150"><input type="password" id="YMM" name="YMM" value="" size="36"/></td>
        </tr>
        <tr>
            <td width="150">�����룺</td>
            <td width="150"><input type="password" id="XMM" name="XMM" value="" size="36"/>
                <input type="hidden" id="temp" name="temp" value="" size="36"/></td>
        </tr>
        <tr>
            <td width="150">�ظ������룺</td>
            <td width="150"><input type="password" id="XMM2" name="XMM2" value="" size="36"/></td>
        </tr>
    </table>
    <a id="reset" class="bu" href="#" onclick="create();"><i class="icon icon-refresh"></i>����</a>
    <a id="chx" class="bu" href="#" onclick="save()"><i class="icon icon-share"></i>����</a>
</form>
</body>
</html>
