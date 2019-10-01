package com.once2go.androidto_accessorymode.projection;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.once2go.androidto_accessorymode.ConnectedActivity;
import com.once2go.androidto_accessorymode.R;

import java.io.IOException;
import java.nio.ByteBuffer;


public class ProjectionDemoActivity extends ConnectedActivity {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 720;
    private static final int DPI = 120;
    private static final int BITRATE = 2000 * 1000; //kbit / sec;
    private static final int FRAMERATE = 25;
    private static final int FRAME_INTERVAL = 4;
    private static final int OUT_BUF_SIZE = 64;

    private MediaCodec codec;
    private ProjectionScreen projectionScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projection);

        DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, WIDTH, HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BITRATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMERATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, FRAME_INTERVAL);

        try {
            codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        } catch (IOException e) {
            throw new RuntimeException("failed to create raw encoder", e);
        }
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        Surface surface = codec.createInputSurface();
        codec.start();

        VirtualDisplay virtualDisplay = mDisplayManager.createVirtualDisplay(
                "projection", WIDTH, HEIGHT, DPI, surface, 0);

        projectionScreen = new ProjectionScreen(this, virtualDisplay.getDisplay());
        projectionScreen.show();
    }

    @Override
    protected void onConnected() {
        if (codec == null) {
            Toast.makeText(this, "Codec was not initialized", Toast.LENGTH_SHORT).show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                while (true) {
                    int index = codec.dequeueOutputBuffer(info, 1000000);
                    if (index >= 0) {
                        ByteBuffer buffer = codec.getOutputBuffer(index);
                        byte[] arr = new byte[info.size];
                        buffer.get(arr);
                        byte[] out = new byte[64];
                        int cnts = info.size / OUT_BUF_SIZE;
                        int tail = info.size % OUT_BUF_SIZE;
                        for (int i = 0; i < cnts; i++) {
                            System.arraycopy(arr, i * OUT_BUF_SIZE, out, 0, OUT_BUF_SIZE);
                            if (outputStream != null) {
                                try {
                                    outputStream.write(out);
                                    outputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (tail > 0) {
                            System.arraycopy(arr, info.size - tail, out, 0, info.size - cnts * OUT_BUF_SIZE);
                            if (outputStream != null) {
                                try {
                                    outputStream.write(out);
                                    outputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        codec.releaseOutputBuffer(index, false);
                    }
                }
            }
        }).start();
    }


    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (projectionScreen != null) {
           return projectionScreen.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }


}
