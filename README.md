# speed-test网络测速组件

## 1、项目说明

本项目是基于vue.js、elementUI组件库、springboot的前后端分离的即开即用的网络测速小组件。包含下载和上行（上行数据仅供参考）。

下载速率：单位时间客户端下载服务端资源的资源大小

上行速率：单位时间客户端上传资源到服务端的资源大小

## 2、使用方法

你起码需要了解vue.js和elementUI，同时还需要了解ajax。利用cdn引入vue.js和elementUI以及jQuery

## 3、项目整体设计思路

项目分为客户端和服务端。客户端指用户的浏览器（PC、手机），服务端则是指网站所在的机房（服务器）。

通过在客户端向服务端发起下载或者上传请求，此时获得对应的开始请求时间戳；然后下载或者上传完毕后获得对应的完成时间戳（注意，开始和结束时间戳只能统一在客户端或者服务端获得，因为客户端和服务端的时间戳一定不相同）。然后利用下载或上传的资源大小除以开始到结束的时间戳的差得出单位时间下载的资源量（也就是网速），需要注意单位换算：

```
1byte = 8bit 	（1字节等于8字）
1Kb = 1024byte 	（1千字等于1024字节）
1Mb = 1024Kb	（1兆字等于1024千字）
```

## 4、代码实现

- 下载测速

  利用js内置的Image对象的构造函数构造一个图片对象。此时获取到开始下载该图片资源瞬间的时间戳

  ```js
  let image = new Image(); //定义Image对象
  let imageSrc='./download/test.JPG';	//图片路径（绝对或相对或互联网URL资源）
  let imageSize=7984555;			   //图片大小（单位字节byte）
  image.src = imageSrc + '?n=' +Math.random(); //随机访问该图片资源
  let startTime = new Date().getTime(); //开始下载时的时间戳
  ```

  利用image内置的onload图片加载完毕会执行的回调钩子函数。此时获取到图片资源完成下载的瞬时时间戳。完成下载时的时间戳减去开始下载时的时间戳就是时间差，同时利用公式计算出每秒下载的资源量的字节数即单位时间下载的资源大小

  ```js
  image.onload = function () { //图片加载完时会执行的回调函数
      let endTime = new Date().getTime(); //完成下载的时的时间
      let diffSeconds = (endTime - startTime)/1000; //差时间转为秒
      let speedBps = (imageSize/diffSeconds)*8; //每秒下载多少B的资源
      let speedKBps = speedBps / 1024;  //每秒下载多少KB（千B）的资源
      let speedMbps = speedKBps / 1024; //每秒下载多少MB（兆B）的资源
      console.log('['+that.count/10+']'+'下载速率',speedMbps,'Mbps');
      //将该次测速得到的速率追加到速率速组里
      that.speedArray.push(speedMbps);
      delete image; //下载完成后删除该图片资源
      if (that.count<that.maxCount){//如果没有到达最大次数，则依然执行
          that.startDownload();
      } else {
          that.flag = false;
      }
  };
  ```

  需要注意的是：网速计算规则需要将单位时间下载的资源大小乘以8（具体原因请参考https://baike.baidu.com/item/%E7%BD%91%E9%80%9F）

- 上行测速

  生成一个适量大小的资源（不要太大，一般手机上传速度很慢，运营商可能会拦截太大的上传请求；不要太小，不好计算）
  
  ```js
  let text = 'A'; //一个字母为1byte
  let totalText ;
  for (let i = 0; i < 1024 * 1024 * 2; i++) {
      totalText+=text; //生成2M大小的资源
  }
  ```
  
  利用ajax上传该资源（需要将timeout超时时间修改长一点）。需要注意的是需要获得从后端返回的此次http请求的contentLength的大小
  
  ```js
  $.ajax({
      url: '//localhost:8080/uploadSpeedTest',
      type: 'POST',
      timeout: 60000,
      processData:false,
      contentType:false,
      data:formData,
      success(res){
          let endTime = new Date().getTime();
          let diffSeconds = (endTime-startTime)/1000;
          let contentLength = res.data.contentLength;
          let bps = contentLength/diffSeconds;
          let speedBps = bps * 8;
          let speedKbps = speedBps / 1024 ;
          let speedMbps = speedKbps / 1024 ;
          console.log('['+that.count/20+']'+'上行速率',speedMbps,'Mbps（仅供参考）');
          //将该次测速得到的速率追加到速率速组里
          that.speedArray.push(speedMbps);
          if (that.count<that.maxCount){//如果没有到达最大次数，则依然执行
              that.startUpload();
          } else {
              that.flag = false;
          }
      },
      error(err){
          that.isError = true;
          that.flag = false;
          console.log(err);
      }
  })
  ```
  
  后端处理http请求（目的是获得contentLength大小）
  
  ```java
  @RestController
  @Slf4j
  @CrossOrigin("*")
  public class SpeedController {
      @PostMapping("/uploadSpeedTest")
      public Map<String,Object> uploadSpeedTest(HttpServletRequest request){
          
          //得到该http请求的contentLength
          long contentLength = request.getContentLengthLong();
          log.info("contentLength->[{} Byte]",contentLength);
  
          //数据集合
          HashMap<String, Object> dataMap = new HashMap<>();
          dataMap.put("contentLength",contentLength);
  
          //res返回体集合
          HashMap<String, Object> resMap = new HashMap<>();
          resMap.put("msg","客户端到服务端请求");
          resMap.put("data",dataMap);
  
          return resMap;
      }
  }
  
  ```
  
  