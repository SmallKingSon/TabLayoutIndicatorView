package com.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by yjwfn on 15-12-16.
 */
public class TabLayoutIndicatorView extends View
    implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

  //用于给用户自定义绘画的接口
  private Painter mPainter;

  private Paint mShapePaint;

  private TabLayout mTabLayout;

  private ViewPager mViewPager;

  private RectF mRange = new RectF();

  public TabLayoutIndicatorView(Context context) {
    this(context, null);
  }

  public TabLayoutIndicatorView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TabLayoutIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViews(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public TabLayoutIndicatorView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initViews(context, attrs, defStyleAttr, defStyleRes);
  }

  private void initViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    mShapePaint = new Paint();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    //初始画笔
    if (mPainter != null) {
      mPainter.initPaint(mShapePaint);
    }
  }

  /*
  绑定TabLayout 的 scroll
  清除Tab的indicator
   */
  private void setupWithTabLayout(final TabLayout tableLayout) {
    mTabLayout = tableLayout;

    tableLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
    tableLayout.setOnTabSelectedListener(this);

    tableLayout.getViewTreeObserver()
        .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
          @Override public void onScrollChanged() {
            if (mTabLayout.getScrollX() != getScrollX()) {
              scrollTo(mTabLayout.getScrollX(), mTabLayout.getScrollY());
            }
          }
        });

    ViewCompat.setElevation(this, ViewCompat.getElevation(mTabLayout));
    tableLayout.post(new Runnable() {
      @Override public void run() {
        if (mTabLayout.getTabCount() > 0) onTabSelected(mTabLayout.getTabAt(0));
      }
    });

    //清除Tab background
    for (int tab = 0; tab < tableLayout.getTabCount(); tab++) {
      View tabView = getTabViewByPosition(tab);
      tabView.setBackgroundResource(0);
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (mPainter != null) {
      mPainter.onDraw(canvas, mRange, mShapePaint);
    }
  }

  /*
  当ViewPager滑动的时候 计算绘画Indicator的范围
   */
  private void generateDrawRange(int position, float positionOffset) {

    mRange.setEmpty();
    View tabView = getTabViewByPosition(position);

    if (tabView == null) return;

    int left, top, right, bottom;
    left = top = right = bottom = 0;

    if (positionOffset > 0.f && position < mTabLayout.getTabCount() - 1) {
      View nextTabView = getTabViewByPosition(position + 1);
      left += (int) (nextTabView.getLeft() * positionOffset + tabView.getLeft() * (1.f
          - positionOffset));
      right += (int) (nextTabView.getRight() * positionOffset + tabView.getRight() * (1.f
          - positionOffset));

      top = tabView.getTop() + getPaddingTop();
      bottom = tabView.getBottom() - getPaddingBottom();
      mRange.set(left, top, right, bottom);
    } else {

      left = tabView.getLeft();
      right = tabView.getRight();
      top = tabView.getTop() + getPaddingTop();
      bottom = tabView.getBottom() - getPaddingBottom();
      mRange.set(left, top, right, bottom);
    }
  }

  private View getTabViewByPosition(int position) {
    if (mTabLayout != null && mTabLayout.getTabCount() > 0) {
      ViewGroup tabStrip = (ViewGroup) mTabLayout.getChildAt(0);
      return tabStrip != null ? tabStrip.getChildAt(position) : null;
    }
    return null;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    generateDrawRange(position, positionOffset);
    invalidate();
  }

  @Override public void onPageSelected(int position) {
    if (mTabLayout.getSelectedTabPosition() != position) mTabLayout.getTabAt(position).select();
  }

  @Override public void onPageScrollStateChanged(int state) {

  }

  /**
   * 当已经有一个ViewPager后，当TabLayout的tab改变的时候在onTabSelected方法直接调用ViewPager的
   * setCurrentItem方法调用这个方法后会触发ViewPager的scroll事件也就是在onPageScrolled方法中调用
   * generatePath方法来更新Path，如果没有ViewPager的话直接在onTabSelected的方法中调用generatePath
   * 方法。
   **/
  @Override public void onTabSelected(TabLayout.Tab tab) {
    if (mViewPager != null) {
      if (tab.getPosition() != mViewPager.getCurrentItem()) {
        mViewPager.setCurrentItem(tab.getPosition());
      }
    } else {
      generateDrawRange(tab.getPosition(), 0);
      invalidate();
    }
  }

  @Override public void onTabUnselected(TabLayout.Tab tab) {

  }

  @Override public void onTabReselected(TabLayout.Tab tab) {

  }

  /**
   * 附着TabLayout 将Indicator 添加到当前TabLayout所在的位置
   */
  public static TabLayoutIndicatorView attach(TabLayout tabLayout) {
    Context context = tabLayout.getContext();
    TabLayoutIndicatorView tabLayoutIndicatorView = new TabLayoutIndicatorView(context);
    tabLayoutIndicatorView.setupWithTabLayout(tabLayout);
    FrameLayout frameLayout = new FrameLayout(context);
    ViewGroup parent = (ViewGroup) tabLayout.getParent();
    parent.removeView(tabLayout);
    parent.addView(frameLayout, tabLayout.getLayoutParams());
    FrameLayout.LayoutParams layoutParams =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    frameLayout.addView(tabLayoutIndicatorView, layoutParams);
    frameLayout.addView(tabLayout, layoutParams);

    return tabLayoutIndicatorView;
  }

  public TabLayoutIndicatorView viewPager(ViewPager viewPager) {
    mViewPager = viewPager;
    viewPager.addOnPageChangeListener(this);
    return this;
  }

  public TabLayoutIndicatorView painter(Painter painter) {
    this.mPainter = painter;
    return this;
  }

  public interface Painter {
    void initPaint(Paint paint);

    void onDraw(Canvas canvas, RectF range, Paint paint);
  }
}