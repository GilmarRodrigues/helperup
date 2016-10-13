package br.com.alimentar.alergia.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.ViewPagerAdapter;
import br.com.alimentar.alergia.utils.FadePageTransformer;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class ViewPageActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private Button btnCadastrar;
    private Button btnLogin;
    private ViewPager viewpager;
    private LinearLayout pager_indicador;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;

    private int[] mImagesResources = {
            R.drawable.art_canteen_intro1,
            R.drawable.art_canteen_intro2,
            R.drawable.art_canteen_intro3,
            R.drawable.art_canteen_intro2
    };
    private int[] mTitles = {R.string.title_canteen_intro1, R.string.title_canteen_intro2, R.string.title_canteen_intro3, R.string.title_canteen_intro4};
    private int[] mDescriprion = {R.string.description_canteen_intro1, R.string.description_canteen_intro2, R.string.description_canteen_intro3, R.string.description_canteen_intro4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);

        setReference();

    }

    private void setReference() {
        viewpager = (ViewPager) findViewById(R.id.pager_introduction);
        //viewpager.setPageTransformer(false, new FadePageTransformer());
        viewpager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });
        btnCadastrar = (Button) findViewById(R.id.btn_cadastrar);
        btnLogin = (Button) findViewById(R.id.btn_login);

        pager_indicador = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        btnCadastrar.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        mAdapter = new ViewPagerAdapter(ViewPageActivity.this, mImagesResources, mTitles, mDescriprion);
        viewpager.setAdapter(mAdapter);
        viewpager.setCurrentItem(0);
        viewpager.setOnPageChangeListener(this);
        setUiPageViewController();
    }

    private void setUiPageViewController() {
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(10,0,10,0);

            pager_indicador.addView(dots[i], params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i=0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cadastrar:
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;
            case R.id.btn_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
