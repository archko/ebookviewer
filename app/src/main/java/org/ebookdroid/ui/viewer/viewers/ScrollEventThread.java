package org.ebookdroid.ui.viewer.viewers;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;

import org.ebookdroid.core.models.DocumentModel;
import org.ebookdroid.ui.viewer.IActivityController;
import org.ebookdroid.ui.viewer.IView;
import org.ebookdroid.ui.viewer.IViewController;
import org.emdev.utils.MathUtils;

import java.lang.reflect.Field;

final class ScrollEventThread {

    private static final int PAGE_POOL_SIZE = 6;
    private static final int MSG_DECODE_START = 0;
    private static final int MSG_DECODE_CANCEL = 2;
    private static final int MSG_DECODE_FINISH = 4;

    private static boolean mergeEvents = false;

    private static Field SCROLL_X;
    private static Field SCROLL_Y;

    static {
        final Field[] fields = View.class.getDeclaredFields();
        for (final Field f : fields) {
            if ("mScrollX".equals(f.getName())) {
                SCROLL_X = f;
                SCROLL_X.setAccessible(true);
            } else if ("mScrollY".equals(f.getName())) {
                SCROLL_Y = f;
                SCROLL_Y.setAccessible(true);
            }
        }
    }

    private final IActivityController base;

    private IView view = null;
    private Handler mHandler;
    private final Handler.Callback mCallback = msg -> {
        int what = msg.what;
        if (what == MSG_DECODE_START) {
            run(msg.obj);
        } else if (what == MSG_DECODE_CANCEL) {
            if (null != view) {
                view.stopScroller();
            }
            return true;
        } else if (what == MSG_DECODE_FINISH) {
            return true;
        }
        return true;
    };

    private void initScrollThread() {
        HandlerThread handlerThread = new HandlerThread("taskThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), mCallback);
    }

    ScrollEventThread(final IActivityController base, IView view) {
        initScrollThread();
        this.base = base;
        this.view = view;
    }

    public void run(Object obj) {
        final OnScrollEvent event = (OnScrollEvent) obj;

        process(event);
    }

    void finish() {
        if (null != mHandler) {
            mHandler.sendEmptyMessage(MSG_DECODE_FINISH);
            mHandler.getLooper().quit();
        }
    }

    void scrollTo(final int x, final int y) {
        final IViewController dc = base.getDocumentController();
        final DocumentModel dm = base.getDocumentModel();
        final View w = view.getView();

        if (dc != null && dm != null) {
            final Rect l = dc.getScrollLimits();
            final int xx = MathUtils.adjust(x, l.left, l.right);
            final int yy = MathUtils.adjust(y, l.top, l.bottom);

            if (SCROLL_X == null || SCROLL_Y == null) {
                try {
                    view._scrollTo(xx, yy);
                } catch (Throwable th) {
                    System.err.println("(1) " + th.getMessage());
                }
            } else {
                try {
                    int mScrollX = SCROLL_X.getInt(w);
                    int mScrollY = SCROLL_Y.getInt(w);
                    if (mScrollX != xx || mScrollY != yy) {
                        int oldX = mScrollX;
                        int oldY = mScrollY;
                        SCROLL_X.setInt(w, xx);
                        SCROLL_Y.setInt(w, yy);
                        view.onScrollChanged(mScrollX, mScrollY, oldX, oldY);
                    }
                } catch (Throwable th) {
                    System.err.println("(2) " + th.getMessage());
                    try {
                        view._scrollTo(xx, yy);
                    } catch (Throwable thh) {
                        System.err.println("(3) " + thh.getMessage());
                    }
                }
            }
        }
    }

    void onScrollChanged(final int curX, final int curY, final int oldX, final int oldY) {
        OnScrollEvent event = new OnScrollEvent(curX, curY, oldX, oldY);
        Message message = Message.obtain();
        message.obj = event;
        message.what = MSG_DECODE_START;
        mHandler.sendMessage(message);
    }

    private void process(final OnScrollEvent event) {
        //final long t1 = System.currentTimeMillis();
        try {
            final int dX = event.m_curX - event.m_oldX;
            final int dY = event.m_curY - event.m_oldY;

            base.getDocumentController().onScrollChanged(dX, dY);
        } catch (final Throwable th) {
            th.printStackTrace();
        } finally {
            //final long t2 = System.currentTimeMillis();
            //System.out.println("ScrollEventThread.onScrollChanged(): " + (t2 - t1) + " ms, "+event);
        }
    }

    final static class OnScrollEvent {

        int m_oldX;
        int m_curY;
        int m_curX;
        int m_oldY;

        OnScrollEvent(final int curX, final int curY, final int oldX, final int oldY) {
            reuse(curX, curY, oldX, oldY);
        }

        void reuse(final int curX, final int curY, final int oldX, final int oldY) {
            m_oldX = oldX;
            m_curY = curY;
            m_curX = curX;
            m_oldY = oldY;
        }

        @Override
        public String toString() {
            return "OnScrollEvent{" +
                    "m_oldX=" + m_oldX +
                    ", m_curY=" + m_curY +
                    ", m_curX=" + m_curX +
                    ", m_oldY=" + m_oldY +
                    '}';
        }
    }
}
