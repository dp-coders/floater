package dcom.taneja.dhruv.floater.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import dcom.taneja.dhruv.floater.R

/**
 * Created by dhruvtaneja on 30/11/17.
 */

class FloaterService : Service() {

    private lateinit var floatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var isCollapsed = true
    private var hasMoved = false
    private var dragCollapse = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget,
                null)
        params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )

        with (params) {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 100
            width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        collapsedView = floatingView.findViewById(R.id.collapsedImageView)
        expandedView = floatingView.findViewById(R.id.expandedView)

        collapsedView.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y

                    initialTouchX = event.rawX.toInt()
                    initialTouchY = event.rawY.toInt()
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP -> {
                    val xDiff = event.rawX - initialTouchX
                    val yDiff = event.rawY - initialTouchY

                    if (dragCollapse) {
                        expandFloater()
                        hasMoved = false
                        dragCollapse = false
                        Log.d("blah up", "expanded")
                    } else {
                        if (xDiff < 10f && yDiff < 10f) {
                            toggleFloater()
                            Log.d("blah up", "toggled")
                        }
                    }
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_MOVE -> {
                    Log.d("blah move", "moving")
                    if (!isCollapsed) {
//                        collapseFloater()
                        dragCollapse = true
                        hasMoved = true
                        Log.d("blah move", "collapsed")
                    }
                    collapseFloater()
                    params.x = initialX + (event.rawX.toInt() - initialTouchX)
                    params.y = initialY + (event.rawY.toInt() - initialTouchY)
                    windowManager.updateViewLayout(floatingView, params)
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener false
                }
            }
        }

    }

    private fun toggleFloater() {
        expandedView.visibility =
                if (expandedView.visibility == View.GONE) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
    }

    private fun expandFloater() {
        expandedView.visibility = View.VISIBLE
        isCollapsed = false
    }

    private fun collapseFloater() {
        expandedView.visibility = View.GONE
        isCollapsed = true
        params.x = initialX
        params.y = initialY
        windowManager.updateViewLayout(floatingView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }
}