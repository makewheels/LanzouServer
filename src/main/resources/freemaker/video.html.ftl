<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <link href="https://cdn.bootcss.com/video.js/7.6.5/video-js.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/video.js/7.6.5/video.min.js"></script>
    <script src="https://cdn.bootcss.com/video.js/7.6.5/lang/en.js"></script>
    <script src="https://cdn.bootcss.com/video.js/7.6.5/lang/zh-CN.js"></script>
    <script src="https://cdn.bootcss.com/videojs-contrib-hls/5.15.0/videojs-contrib-hls.min.js"></script>
</head>

<body>
<video id="videoId" class="video-js vjs-default-skin" controls preload="auto">
    <source src="${m3u8Url}" type="application/x-mpegURL">
</video>
<h3>${title}</h3>
<script>
    var player = videojs('videoId', {
        width: 1088,
        height: 612
    });
</script>
</body>
</html>