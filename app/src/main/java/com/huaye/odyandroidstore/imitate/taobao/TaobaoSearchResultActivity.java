package com.huaye.odyandroidstore.imitate.taobao;

import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huaye.odyandroidstore.R;
import com.huaye.odyandroidstore.base.BaseActivity;
import com.huaye.odyandroidstore.retrofit.taobao.TaoBaoProductBean;
import com.huaye.odyandroidstore.utils.ConvertUtils;
import com.huaye.odyandroidstore.utils.ScreenUtils;
import com.huaye.odyandroidstore.widget.DecorationSpace;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 高仿淘宝搜索结果页
 * Samuel
 */
public class TaobaoSearchResultActivity extends BaseActivity implements SearchResultView {
    private static final int LIST = 0;
    private static final int GRID = 1;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.tabs)
    LinearLayout tabs;

    @BindView(R.id.mps)
    RecyclerView mps;

    @BindView(R.id.sort)
    LinearLayout sort;

    @BindView(R.id.sortLin)
    LinearLayout sortLin;

    @BindView(R.id.sort_sale)
    TextView sortSale;

    @BindView(R.id.up_img)
    ImageView upImg;

    @BindView(R.id.showTypeImg)
    ImageView showTypeImg;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private int showType = LIST;
    private int page = 1;
    private String sortType;
    private ProductAdapter adapter;
    private SearchResultPresenter presenter;
    private PopupWindow popupWindow;

    @Override
    protected void init() {
        super.init();
        presenter = new SearchResultPresenter(this);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_taobao_search_result;
    }


    @Override
    protected void initView() {
        ButterKnife.bind(this);
        toolBar.setNavigationIcon(R.mipmap.taobao_back);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);

        adapter = new ProductAdapter(R.layout.item_taobao_product);
        mps.setLayoutManager(new LinearLayoutManager(this));
        mps.addItemDecoration(new DecorationSpace(ConvertUtils.dp2px(3)));
        mps.setAdapter(adapter);
        //初始化排序弹层
        initPopWindow();
    }

    @Override
    protected void initData() {
        super.initData();
        presenter.getProductList("SD卡", page);
    }

    @Override
    protected void initListener() {
        super.initListener();
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setUpdateListener(adapter);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopWindow(view);
            }
        });

        sortSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //销量排序
                sortSale.setSelected(true);
                page = 1;
                sortType = "_sale";
                presenter.getProductList("SD卡", page, sortType);
            }
        });

        mps.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //滑动到顶部按钮展示
                ValueAnimator animator;
                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) upImg.getLayoutParams();

                if (recyclerView.canScrollVertically(-1)) {//表示是否能向下滚动，false表示已经滚动到顶部
                    animator = ValueAnimator.ofFloat(0f, 1f);
                    if (animator.isRunning() || params.width > 0)
                        return;
                } else {

                    animator = ValueAnimator.ofFloat(1f, 0f);
                    if (animator.isRunning() || params.width == 0) {
                        return;
                    }
                }
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float value = (float) valueAnimator.getAnimatedValue();

                        params.width = (int) (ConvertUtils.dp2px(36) * value);
                        params.height = (int) (ConvertUtils.dp2px(36) * value);
                        upImg.setLayoutParams(params);
                    }
                });
                animator.setDuration(1000);
                animator.start();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        upImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mps.scrollToPosition(0);
            }
        });

        //网格和列表展示切换
        showTypeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showType == LIST) {
                    showType = GRID;
                    showTypeImg.setImageResource(R.mipmap.grid);
                    adapter = new ProductAdapter(R.layout.item_taobao_product_grid, adapter.getData());
                    mps.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                } else {
                    showType = LIST;
                    showTypeImg.setImageResource(R.mipmap.list);
                    adapter = new ProductAdapter(R.layout.item_taobao_product, adapter.getData());
                    mps.setLayoutManager(new LinearLayoutManager(mContext));
                }
                mps.setAdapter(adapter);
                setUpdateListener(adapter);
            }
        });
    }

    private void setUpdateListener(ProductAdapter adapter) {
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getProductList("SD卡", ++page, sortType);
            }
        }, mps);
    }

    private void initPopWindow() {
        popupWindow = new PopupWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.popup_sort, null);
        popupWindow.setContentView(view);
        popupWindow.setWidth(ScreenUtils.getScreenWidth());
        //PopupWindow对象设置高度
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //PopupWindow对象设置获取焦点
        popupWindow.setFocusable(true);
        //PopupWindow对象设置可以触发点击事件
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setAnimationStyle(android.R.style.Animation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    private void showPopWindow(View anchor) {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            int[] a = new int[2];
            anchor.getLocationInWindow(a);
            //PopupWindow对象设置高度
            popupWindow.setHeight(ScreenUtils.getScreenHeight() - a[1] - anchor.getHeight());
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, a[1] + anchor.getHeight());
        } else {
            popupWindow.showAsDropDown(anchor);
        }
        popupWindow.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_taobao, menu);
        return true;
    }

    @Override
    public void showProduct(List<TaoBaoProductBean.Product> list) {
        adapter.setNewData(list);
    }

    @Override
    public void addProduct(List<TaoBaoProductBean.Product> list) {
        adapter.addData(list);
        adapter.loadMoreComplete();
    }
}
