package com.example.studywithai.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.studywithai.Adapters.ViewPagerAdapter;
import com.example.studywithai.MainActivity;
import com.example.studywithai.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNav;
    ViewPager2 viewPager;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    Intent intent;
    Bundle bundle;
    private String username = "";
    private int userId = 0;
    Menu menu;
    SharedPreferences spf;

    @Override
    protected void onStart() {
        super.onStart();
        if (userId <= 0 || TextUtils.isEmpty(username)){
            Intent checkLogin = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(checkLogin);
            finish();
        }
        activeMenu(intent, bundle);
    }
    private void activeMenu(Intent intent, Bundle bundle){
        intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null){
            String menuName = bundle.getString("MENU_TAB", "");
            if (menuName.equals("CATEGORY_TAB")){
                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomNav = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewPager);
        toolbar   = findViewById(R.id.toolBar);
        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_navigation);
        intent = getIntent();
        bundle = intent.getExtras(); // lay du lieu tu bundle, intent tu login truyen sang
        spf = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        username = spf.getString("USERNAME_USER", "");
        userId   = spf.getInt("ID_USER", 0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openNav, R.string.closeNav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setupViewPager();

        // xu ly logout va hien thi ten dang nhap
        menu = navigationView.getMenu();
        MenuItem itemUser = menu.findItem(R.id.account_menu);
        if (username != null){
            itemUser.setTitle(username);
        }
        MenuItem itemLogout = menu.findItem(R.id.logout_menu);
        itemLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(GravityCompat.START);
                if (bundle != null){
                    intent.removeExtra("ID_ACCOUNT");
                    intent.removeExtra("USERNAME_ACCOUNT");
                    intent.removeExtra("EMAIL_ACCOUNT");
                }
                SharedPreferences.Editor editor = spf.edit();
                editor.clear();
                editor.apply();
                Intent login = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(login);
                finish();
                return true;
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.home_menu){
                    viewPager.setCurrentItem(0);
                } else if (menuItem.getItemId() == R.id.category_menu) {
                    viewPager.setCurrentItem(1);
                } else if (menuItem.getItemId() == R.id.quiz_menu) {
                    viewPager.setCurrentItem(2);
                } else if (menuItem.getItemId() == R.id.settings_menu) {
                    viewPager.setCurrentItem(3);
                }
                return true;
            }
        });
    }
    private void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0){
                    bottomNav.getMenu().findItem(R.id.home_menu).setChecked(true);
                } else if (position == 1) {
                    bottomNav.getMenu().findItem(R.id.category_menu).setChecked(true);
                } else if (position == 2) {
                    bottomNav.getMenu().findItem(R.id.quiz_menu).setChecked(true);
                } else if (position == 3) {
                    bottomNav.getMenu().findItem(R.id.settings_menu).setChecked(true);
                } else {
                    bottomNav.getMenu().findItem(R.id.home_menu).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.home_menu){
            viewPager.setCurrentItem(0);
        } else if (menuItem.getItemId() == R.id.category_menu) {
            viewPager.setCurrentItem(1);
        } else if (menuItem.getItemId() == R.id.quiz_menu) {
            viewPager.setCurrentItem(2);
        } else if (menuItem.getItemId() == R.id.settings_menu) {
            viewPager.setCurrentItem(3);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
