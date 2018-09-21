Android 音视频开发

1. 概念
（1） 采样率（samplerate）
8 kHz - 电话所用采样率, 对于人的说话已经足够
22.05 KHz - FM广播的声音品质
44.1 KHz - 是理论上的CD音质界限
48 KHz - 更加精确一些



2. 播放音频

MediaPlayer 更加适合在后台长时间播放本地音乐文件或者在线的流式资源;
SoundPool 则适合播放比较短的音频片段，比如游戏声音、按键声、铃声片段等等，它可以同时播放多个音频;
而 AudioTrack 则更接近底层，提供了非常强大的控制能力，支持低延迟播放，适合流媒体和VoIP语音电话等场景。



MediaExtractor 的作用是把音频和视频的数据进行分离。
MediaMuxer的作用是生成音频或视频文件；还可以把音频与视频混合成一个音视频文件。



看到了
https://www.cnblogs.com/renhui/p/7474096.html







https://github.com/979451341/Audio-and-video-learning-materials/blob/master/%E6%94%B6%E9%9B%86Camera%E6%95%B0%E6%8D%AE%EF%BC%8C%E5%B9%B6%E8%BD%AC%E7%A0%81%E4%B8%BAH264%E5%AD%98%E5%82%A8%E5%88%B0%E6%96%87%E4%BB%B6/java/com/example/jni/H264Encoder.java

https://www.cnblogs.com/Sharley/p/5771030.html
https://blog.csdn.net/weixin_38021928/article/details/80303935



参考资料
Android音频开发（1）：基础知识
http://blog.51cto.com/ticktick/1748506




Android 音视频开发入门指南
http://blog.51cto.com/ticktick/1956269


《深入Java虚拟机(原书第二版)》



原生音频API
AudioFormat、AudioRecord、AudioTrack

视频API
MediaCodec、MediaExtractor、MediaFormat、MediaMuxer、MediaRecorder

FFmpeg



















