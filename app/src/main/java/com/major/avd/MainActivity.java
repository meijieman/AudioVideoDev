package com.major.avd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.major.avd.util.PcmToWavUtil;
import com.major.avd.util.Util;
import com.major.base.log.LogUtil;
import com.major.base.util.CloseUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private SurfaceView mSurfaceView;
    private AudioRecord mRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.init(getPackageName(), "tag_ma", true, false);

        mSurfaceView = findViewById(R.id.sv_1);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (holder == null) {
                    return;
                }
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                Canvas canvas = holder.lockCanvas();
                canvas.drawBitmap(bitmap, 0, 0, paint);
                paint.setColor(Color.RED);
                canvas.drawCircle(50, 50, 20, paint);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsRecoding = false;
    }

    private boolean mIsRunning;
    private boolean mIsRecoding;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
            {
                // 录音
                LogUtil.i("start " + System.currentTimeMillis());

                int audioSource = MediaRecorder.AudioSource.MIC;
                int sampleRateInHz = 44100; // 采样率
                int channelConfig = AudioFormat.CHANNEL_IN_STEREO; // 声道
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

                int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
                mRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize);

                mIsRunning = true;
                LogUtil.i("start1 " + System.currentTimeMillis());

                new Thread(() -> {
                    FileOutputStream fos = null;
                    try {
                        byte[] data = new byte[bufferSize];
                        mRecord.startRecording();

                        File file = new File(Util.getFilePath(), "record.pcm");
                        LogUtil.i("file " + file.getAbsolutePath());
                        fos = new FileOutputStream(file);

                        while (mIsRunning) {
                            int read = mRecord.read(data, 0, bufferSize);
                            if (AudioRecord.ERROR_INVALID_OPERATION != read && AudioRecord.ERROR_BAD_VALUE != read) {
                                // 保存原始的音频数据
                                fos.write(data, 0, read);
                            } else {
                                LogUtil.e("ERROR_INVALID_OPERATION, ERROR_BAD_VALUE");
                            }
                        }
                        fos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("e " + e);
                    } finally {
                        CloseUtil.close(fos);
                    }
                }).start();
            }
                break;
            case R.id.btn_2:
                // 停止录音
                LogUtil.i("停止");
                mIsRunning = false;
                mRecord.stop();
                mRecord.release();
                mRecord = null;
                break;
            case R.id.btn_3:
            {
                // 播放pcm
                int streamType = AudioManager.STREAM_MUSIC;
                int sampleRateInHz = 44100; //44100; // 采样率
                int channelConfig = AudioFormat.CHANNEL_OUT_STEREO; // 声道
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
                LogUtil.i("bufferSize " + bufferSize);
                // MODE_STATIC 需要先调用 write，再调用 play
                AudioTrack audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
                int state = audioTrack.getState();
                LogUtil.i("state " + state);
                audioTrack.play();

                new Thread(()->{
                    try {

                        File file = new File(Util.getFilePath(), "record.pcm");
                        FileInputStream fis = new FileInputStream(file);
                        int read;
                        byte[] buff = new byte[1024];
                        while ((read = fis.read(buff)) != -1) {
                            audioTrack.write(buff, 0, read);
                        }

                        audioTrack.stop();
                        audioTrack.release();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
                break;
            case R.id.btn_4:
                int sampleRate = 44100; // 采样率
                int channel = AudioFormat.CHANNEL_IN_STEREO; // 声道
                int encoding = AudioFormat.ENCODING_PCM_16BIT;

                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(sampleRate, channel, encoding);
                File file = new File(Util.getFilePath(), "record.pcm");
                boolean b = pcmToWavUtil.pcmToWav(file.getAbsolutePath(), file.getAbsolutePath() + ".wav");
                LogUtil.i("b " + b);
                break;
            case R.id.btn_5:
            {
                // 边录边播
                int audioSource = MediaRecorder.AudioSource.MIC;
                int sampleRateInHz = 44100; // 采样率
                int channelConfig = AudioFormat.CHANNEL_IN_STEREO; // 声道
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

                AudioRecord record = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize);
                record.startRecording();

                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
                track.play();
                mIsRecoding = true;

                new Thread(()->{
                    while (mIsRecoding) {
                        byte[] buff = new byte[1024];

                        int read = record.read(buff, 0, buff.length);
                        if (AudioRecord.ERROR_BAD_VALUE != read && AudioRecord.ERROR_INVALID_OPERATION != read) {
                            track.write(buff, 0, read);
                        }
                    }
                }).start();



            }
                break;

            case R.id.btn_6:
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
                break;
            case R.id.btn_7:
                startActivity(new Intent(MainActivity.this, Video2Activity.class));
                break;
            case R.id.btn_8:
                // MediaExtractor 使用示例
                new Thread(()->{
                    try {
                        process();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
        }
    }

    private MediaExtractor mMediaExtractor;
    private MediaMuxer mMediaMuxer;



    // 从MP4文件中提取视频并生成新的视频文件
    private boolean process() throws IOException {
        mMediaExtractor = new MediaExtractor();
        mMediaExtractor.setDataSource(Util.getFilePath() + "/ss.mp4");

        int mVideoTrackIndex = -1;
        int framerate = 15; // 默认值 15
        for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (!mime.startsWith("video/")) {
                continue;
            }
            if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                framerate = format.getInteger(MediaFormat.KEY_FRAME_RATE);
                LogUtil.i("format " + format);
            } else {
                LogUtil.w("dont containsKey ");

            }
            mMediaExtractor.selectTrack(i);
            mMediaMuxer = new MediaMuxer(Util.getFilePath() + "/ouput.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mVideoTrackIndex = mMediaMuxer.addTrack(format);
            mMediaMuxer.start();
        }

        if (mMediaMuxer == null) {
            return false;
        }

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        info.presentationTimeUs = 0;
        ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
        int sampleSize = 0;
        while ((sampleSize = mMediaExtractor.readSampleData(buffer, 0)) > 0) {
            info.offset = 0;
            info.size = sampleSize;
            info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
            info.presentationTimeUs += 1000 * 1000 / framerate;
            mMediaMuxer.writeSampleData(mVideoTrackIndex, buffer, info);
            mMediaExtractor.advance();
        }

        mMediaExtractor.release();

        mMediaMuxer.stop();
        mMediaMuxer.release();

        return true;
    }

}
