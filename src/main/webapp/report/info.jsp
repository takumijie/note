<%--
  Created by IntelliJ IDEA.
  User: asus
  Date: 2024/9/25
  Time: 15:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<div class="col-md-9">

  <div class="data_list">
    <div class="data_list_title"><span class="glyphicon glyphicon-signal"></span>&nbsp;数据报表 </div>
    <div class="container-fluid">
      <div class="row" style="padding-top: 20px;">
        <div class="col-md-12">
<%--          柱状图所需容器--%>
             <div id="monthChart" style="height: 500px"></div>
<%--百度地图的加载--%>
             <h3 align="center">用户地图分布</h3>
    <%--            百度地图容器--%>
             <div id="baiduMap" style="width: 100%;height: 600px"> </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript" src="static/echarts/echarts.min.js"></script>
<script type="text/javascript" src="//api.map.baidu.com/api?v=1.0&type=webgl&ak=Yr2uGUrQPbSjWyxLuCZK55jnVvmLKStY"></script>
<script type="text/javascript">
<%--  通过月份查询对应的云记数量--%>
  $.ajax({
    type:"get",
    url:"report",
    data:{
      actionName:"month"
    },
    success:function (result){
      console.log(result);
      if (result.code == 1){
          //得到月份（x轴）
          var monthArray = result.result.monthArray;
          //得到对应的月份（y轴）
          var dataArray = result.result.dataArray;
          //加载柱状图
          loadMonthChart(monthArray,dataArray);
      }
    }
  });

  /**
   * 加载柱状图
   */
  function loadMonthChart(monthArray,dataArray){
    var myChart = echarts.init(document.getElementById('monthChart'));
    // prettier-ignore
    let dataAxis = monthArray;
// prettier-ignore
    let data = dataArray;
    let yMax = 30;
    let dataShadow = [];
    for (let i = 0; i < data.length; i++) {
      dataShadow.push(yMax);
    }
    var option = {
      title: {
        text: '月份统计',
        subtext: '通过月份查询云记数量',
        left:'center'
      },
      tooltip:{},
      legend:{
        data:[]
      },
      xAxis: {
        data: dataAxis,
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        }
      },
      yAxis: {
        axisLabel: {
          color: '#999'
        }
      },
      dataZoom: [
        {
          type: 'inside'
        }
      ],
      series: [
        {
          type: 'bar',
          showBackground: true,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#2378f7' },
                { offset: 0.7, color: '#2378f7' },
                { offset: 1, color: '#83bff6' }
              ])
            }
          },
          data: data
        }
      ]
    };
    // 使用 `setOption` 方法设置图表
    myChart.setOption(option)
  }

/**
 * 通过用户发布的坐标查询云记
 */

$.ajax({
     type:"get",
     url:"report",
     data:{
         actionName: "location"
     },
     success:function (result){
        console.log(result)
        if (result.code == 1){
        //     加载百度地图
            loadBaiduMap(result.result)
        }
     }
 })

/**
 * 加载百度地图
 */
function loadBaiduMap(markers){
    // 加载地图实例
    var map = new BMapGL.Map("baiduMap");
    // 开启鼠标滚轮缩放
    map.enableScrollWheelZoom(true);
    // 添加比例尺控件
    var zoomCtrl = new BMapGL.ZoomControl();
    map.addControl(zoomCtrl);
    // 判断是否有坐标
    if (markers != null && markers.length > 0){//集合中的第一个坐标是用户当前所在位置，其他是云记对应的经纬度
    //     将用户所在位置设置为中心点
        // 地图初始化，BMapGL.Map.centerAndZoom()方法要求设置中心点坐标和地图级别
        map.centerAndZoom(new BMapGL.Point(markers[0].lon, markers[0].lat), 10);
    //     循环在地图上添加点标记
        for (var i = 1;i<markers.length;i++){
            // 创建标注
            var marker = new BMapGL.Marker(new BMapGL.Point(markers[i].lon, markers[i].lat));
            // 将标注添加到地图中
            map.addOverlay(marker);
        }
    }


  }
</script>