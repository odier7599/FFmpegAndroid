package com.frank.ffmpeg.kotlin.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.frank.ffmpeg.R
import com.frank.ffmpeg.kotlin.handler.FFmpegKotlinHandler
import com.frank.ffmpeg.kotlin.handler.FFmpegKotlinHandler.Companion.MSG_BEGIN
import com.frank.ffmpeg.kotlin.handler.FFmpegKotlinHandler.Companion.MSG_FINISH
import com.frank.ffmpeg.kotlin.model.MediaKotlinBean
import com.frank.ffmpeg.kotlin.tool.JsonParseKotlinTool
import com.frank.ffmpeg.util.FFmpegUtil
import com.frank.ffmpeg.util.FileUtil

/**
 * Using ffprobe to parse media format data
 * Created by frank on 2020/1/7.
 */

class ProbeFormatKotlinActivity : BaseKotlinActivity() {

    private var txtProbeFormat: TextView? = null
    private var progressProbe: ProgressBar? = null
    private var layoutProbe: RelativeLayout? = null
    private var ffmpegHandler: FFmpegKotlinHandler? = null

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_BEGIN -> {
                    progressProbe!!.visibility = View.VISIBLE
                    layoutProbe!!.visibility = View.GONE
                }
                MSG_FINISH -> {
                    progressProbe!!.visibility = View.GONE
                    layoutProbe!!.visibility = View.VISIBLE
                    val result = msg.obj as MediaKotlinBean
                    val resultFormat = JsonParseKotlinTool.stringFormat(result)
                    if (!TextUtils.isEmpty(resultFormat) && txtProbeFormat != null) {
                        txtProbeFormat!!.text = resultFormat
                    }
                }
                else -> {
                }
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_probe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        ffmpegHandler = FFmpegKotlinHandler(mHandler)
    }

    private fun initView() {
        progressProbe = getView(R.id.progress_probe)
        layoutProbe = getView(R.id.layout_probe)
        initViewsWithClick(R.id.btn_probe_format)
        txtProbeFormat = getView(R.id.txt_probe_format)
    }

    public override fun onViewClick(view: View) {
        selectFile()
    }

    override fun onSelectedFile(filePath: String) {
        doHandleAudio(filePath)
    }

    /**
     * use ffprobe to parse video/audio format metadata
     *
     * @param srcFile srcFile
     */
    private fun doHandleAudio(srcFile: String) {
        if (!FileUtil.checkFileExist(srcFile)) {
            return
        }
        val commandLine = FFmpegUtil.probeFormat(srcFile)
        if (ffmpegHandler != null) {
            ffmpegHandler!!.executeFFprobeCmd(commandLine)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

}
