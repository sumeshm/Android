package planetjup.com.quickexpense.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import planetjup.com.quickexpense.R;
import planetjup.com.quickexpense.fragments.ExpenseFragment;
import planetjup.com.quickexpense.fragments.UserFragment;

public class TripTabsActivity extends AppCompatActivity {

    private static final String TAG = TripTabsActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.ic_user,
            R.drawable.ic_expense
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        setContentView(R.layout.activity_trip_tabs);

        // add trip to DB
        String tripName = getIntent().getStringExtra("tripName");
        setTitle(tripName);

        setupTabs();
    }

    private void setupTabs() {
        Log.v(TAG, "setupTabs()");

        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserFragment());
        adapter.addFragment(new ExpenseFragment());

        this.viewPager = findViewById(R.id.viewpager);
        this.viewPager.setAdapter(adapter);

        this.tabLayout = findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(viewPager);
        this.tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        this.tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    class TabPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        public TabPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // icon-only
            return null;
        }
    }
}
