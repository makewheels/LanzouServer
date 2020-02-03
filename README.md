蓝奏云，我在想，做视频点播，获取分享直连

因为它不限速，也不限容量，也不限流量

这就是云端的对象存储了

FFmpeg生成m3u8文件命令：

ffmpeg -i 1.mp4 -codec copy -vbsf h264_mp4toannexb -map 0 -f segment -segment_list out.m3u8 -segment_time 10 videoid-%d.ts

不安全的，不校验跨域的浏览器的参数是这样的：

--disable-web-security --user-data-dir=C:\MyChromeDevUserData

Linux Docker跑mongo命令：

docker run -itd --name mongo -p 27017:27017 mongo
