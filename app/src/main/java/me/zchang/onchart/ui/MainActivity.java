/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.zchang.onchart.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import me.zchang.onchart.BuildConfig;
import me.zchang.onchart.R;
import me.zchang.onchart.config.ConfigManager;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.session.BitJwcSession;
import me.zchang.onchart.session.Session;
import me.zchang.onchart.session.events.HomepageFetchOverEvent;
import me.zchang.onchart.session.events.ScheduleFetchOverEvent;
import me.zchang.onchart.session.events.SessionErrorEvent;
import me.zchang.onchart.session.events.SessionStartOverEvent;
import me.zchang.onchart.session.events.SwitchWeekNumEvent;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.student.LabelCourse;
import me.zchang.onchart.ui.adapter.CoursePagerAdapter;
import me.zchang.onchart.ui.adapter.DiffTransformer;
import me.zchang.onchart.ui.adapter.WeekNumListAdapter;
import zchang.me.uilibrary.SideBarLayout;

public class MainActivity extends AppCompatActivity
		implements LoginFragment.LoginListener,
		ConfigManager.OnConfigChangeListener {

	public final static int REQ_POSITION = 0;
	public final static int REQ_SETTING = 1;

	public final static long MILLISECONDS_IN_A_DAY = 24 * 3600 * 1000;
    public final static long MILLISECONDS_IN_A_WEEK = MILLISECONDS_IN_A_DAY * 7;

	public final static String TAG = "MainActivity";
	private Toolbar mainToolbar;
	private ViewPager mainListPager;
	private TabLayout weekdayTabs;
	private CoursePagerAdapter mainListAdapter;
	private List<LessonListFragment> fragments;
	private ImageView stuffImage;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ViewGroup drawerHeader;
	private TextView nameText;
	private ProgressBar refreshProgress;
	private TextView weekNumText;
	private TextView versionText;
	private NavigationView drawerView;
	private AppBarLayout toolbarContainer;
	private FloatingActionButton addButton;
	private SideBarLayout weekSelectLayout;
	private TextView popupWeekText;

	private Session session;
	private ConfigManager configManager;

	private int curWeek;
	private int numOfWeekdays;
	private Calendar today;
	private boolean firstLaunch = true;

	// debug
	private boolean showAllFlag = false; // if show all the courses, for debug.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			firstLaunch = false;
		} else {
            setTheme(R.style.MainTheme);
        }
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		configManager = ((MainApp) getApplication()).getConfigManager();
		today = Calendar.getInstance();
		today.setTimeZone(TimeZone.getDefault());

		session = new BitJwcSession();
		mainToolbar = (Toolbar) findViewById(R.id.tb_main);
		setSupportActionBar(mainToolbar);

		curWeek = configManager.getWeek();

		mainListPager = (ViewPager) findViewById(R.id.vp_lessons);
		weekdayTabs = (TabLayout) findViewById(R.id.tl_weekday);
		stuffImage = (ImageView) findViewById(R.id.iv_stuff);
		drawerLayout = (DrawerLayout) findViewById(R.id.dl_drawer);
		drawerView = (NavigationView) findViewById(R.id.nv_drawer);
		toolbarContainer = (AppBarLayout) findViewById(R.id.appb_container);
		addButton = (FloatingActionButton) findViewById(R.id.fab_add_course);
		weekSelectLayout = (SideBarLayout) findViewById(R.id.sbl_week_num);
		popupWeekText = (TextView) findViewById(R.id.tv_popup_week);
		drawerHeader = (ViewGroup) drawerView.getHeaderView(0);
		nameText = (TextView) drawerHeader.findViewById(R.id.tv_stu_name);
		weekNumText = (TextView) drawerHeader.findViewById(R.id.tv_week);
		versionText = (TextView) drawerHeader.findViewById(R.id.tv_version);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && firstLaunch) {
            // FIXME translation doesn't work
//			toolbarContainer.setTranslationY(- toolbarContainer.getLayoutParams().height);
//			toolbarContainer.setTranslationY(-100);
			addButton.setScaleX(0.f);
			addButton.setScaleY(0.f);
			addButton.setAlpha(0.f);
		}

		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getSupportFragmentManager().beginTransaction()
						.replace(android.R.id.content, new AddCourseFragment())
						.addToBackStack(null)
						.commit();
			}
		});

		if (versionText != null)
			versionText.setText(BuildConfig.VERSION_NAME);

		nameText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LoginFragment dialog = new LoginFragment();
				dialog.setListener(MainActivity.this);
				dialog.show(getSupportFragmentManager(), TAG);
			}
		});

		if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
				== WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
			ViewGroup.LayoutParams params = stuffImage.getLayoutParams();
			params.height = getStatusBarHeight(this);
			stuffImage.setLayoutParams(params);
		}

		numOfWeekdays = configManager.getNumOfWeekdays();

		setupDrawer();
		setupSideBar();

		// if haven't refreshed week for a week.
		if (Math.abs(configManager.getLastFetchWeekTime() - today.getTimeInMillis()) > MILLISECONDS_IN_A_WEEK) {
			refreshWeek();
		}

		setupFragments();
		setupList();// ATTENTION, order of refresh and setup
        updateWeekNumDisplay();
		fragments.get(mainListPager.getCurrentItem()).setSlideAnimFlag(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop()");
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	private void setupFragments() {
		fragments = new ArrayList<>();
		for (int i = 0; i < Math.abs(numOfWeekdays); i++) {
			fragments.add(new LessonListFragment());
		}
		mainListAdapter = new CoursePagerAdapter(
				this, getSupportFragmentManager(), fragments, numOfWeekdays);
		mainListPager.setPageTransformer(false, new DiffTransformer());
		mainListPager.setAdapter(mainListAdapter);
		mainListPager.setClipChildren(false);
		mainListPager.setClipToPadding(false);

		weekdayTabs.setupWithViewPager(mainListPager);
		weekdayTabs.setTabsFromPagerAdapter(mainListAdapter);
	}

	private void setupList() {
		List<Course> courses = null;
		courses = configManager.getSchedule();
		for (LessonListFragment f : fragments)
			f.clearCourse();
		if (courses != null) {
			for (Course course : courses) {
				int index = course.getWeekDay();
				if (showAllFlag) {
					//  show all the courses, only for test
					if (index >= 0 && index < fragments.size())
						fragments.get(index).addCourse(course);
				} else {
					if (index >= 0
							&& index < fragments.size()
							&& curWeek >= course.getStartWeek()
							&& curWeek <= course.getEndWeek()) {
						if (course.getWeekParity() < 0)
							fragments.get(index).addCourse(course);
						else if (curWeek % 2 == course.getWeekParity()) // odd or even week num
							fragments.get(index).addCourse(course);
					}
				}
			}

			for (LessonListFragment f : fragments) {
				f.updateList();
			}
		}

		// change to page of this day
		int curWeekDay = today.get(Calendar.DAY_OF_WEEK);
		curWeekDay = (curWeekDay + 5) % 7;
		if (curWeekDay < mainListAdapter.getCount()) {
			mainListPager.setCurrentItem(curWeekDay);
		} else {
			mainListPager.setCurrentItem(mainListAdapter.getCount() - 1);
		}
	}

    private void updateWeekNumDisplay() {
        if (weekNumText != null) {
            weekNumText.setText(String.format(getString(R.string.weekday_week), curWeek));
            weekNumText.setLongClickable(true);
            weekNumText.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					refreshWeek();
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("Warning")
							.setMessage("This is a testing function, and the stability is not guaranteed.")
							.show();
					return false;
				}
			});
        }
    }

	private void setupDrawer() {
		drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				drawerLayout.closeDrawers();
				int id = menuItem.getItemId();
				if (id == R.id.item_settings) {
					Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
					startActivityForResult(intent, REQ_SETTING);
				} else if (id == R.id.item_exams) {
					Intent intent = new Intent(MainActivity.this, ExamsActivity.class);
					startActivity(intent);
				} else if (id == R.id.item_share) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_share)
							+ "\n\r"
							+ getString(R.string.url_download));
					startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
				} else if (id == R.id.item_donate) {
					DonateFragment donateFragment = new DonateFragment();
					donateFragment.show(getSupportFragmentManager(), TAG);
				}
				return false;
			}
		});

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu();
			}
		};
		drawerToggle.setDrawerIndicatorEnabled(true);
		drawerLayout.setDrawerListener(drawerToggle);
		drawerToggle.syncState();
		updateDrawer();
	}

	private void setupSideBar() {
		View header = getLayoutInflater().inflate(R.layout.header_week_num, weekSelectLayout, false);
		weekSelectLayout.setHeader(header);
		RecyclerView weekNumList = (RecyclerView) header.findViewById(R.id.rv_week_num_options);
        RecyclerView.Adapter adapter = new WeekNumListAdapter(MainActivity.this);
        weekNumList.setAdapter(adapter);
        weekNumList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
	}

	public void refreshWeek() {
		if (refreshProgress != null)
			refreshProgress.setVisibility(View.VISIBLE);
		session.fetchHomePage();
	}

	@Override
	protected void onResume() {
		super.onResume();
		configManager.registerListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		configManager.unRegisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		refreshProgress = (ProgressBar) menu.findItem(R.id.item_refresh).getActionView().findViewById(R.id.pb_refresh);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLoginInputFinish(String usrNum, String psw) {
		if (refreshProgress != null)
			refreshProgress.setVisibility(View.VISIBLE);

		if (session.isStarted())
			session = new BitJwcSession();

		session.setStuNum(usrNum);
		session.setPsw(psw);
		session.start();
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private void updateDrawer() {
		String stuName = configManager.getName();
		if (stuName != null && !stuName.equals(getString(R.string.null_stu_name))) {
			nameText.setText(stuName);
			nameText.setClickable(false);
			drawerLayout.closeDrawers();
		} else {
			nameText.setText(getString(R.string.title_login));
			nameText.setClickable(true);
		}
	}

	@TargetApi(21)
	@Override
	public void onEnterAnimationComplete() {
		super.onEnterAnimationComplete();
//		toolbarContainer.animate()
//				.translationY(0)
//				.setStartDelay(50)
//				.setDuration(200)
//				.setInterpolator(new AccelerateDecelerateInterpolator());
		RecyclerView recyclerView = fragments.get(mainListPager.getCurrentItem()).getCourseRecyclerView();
        if (recyclerView != null && firstLaunch) {
            recyclerView.scheduleLayoutAnimation();
        }
		addButton.animate()
				.alpha(1.f)
				.scaleX(1.f)
				.scaleY(1.f)
				.setDuration(200)
				.setStartDelay(200);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
            // returned result from Settings Activity
			case REQ_SETTING:
				if (resultCode == RESULT_OK) {
					int returnWeekday = data.getIntExtra(getString(R.string.key_num_of_weekday), -1);
					int isLogout = data.getIntExtra(getString(R.string.key_logout), SettingsActivity.FLAG_NO_LOGOUT);
					/**
					 * 0 assign num of weekdays and setupFragments
					 * 1 updateDrawer
					 * 2 setupList
					 */
					boolean flags[] = new boolean[4];
					if (returnWeekday != -1) {
						flags[0] = true;
						flags[2] = true;
					}
					if (isLogout != SettingsActivity.FLAG_NO_LOGOUT) {
						flags[1] = true;
						flags[2] = true;
					}
					for (int i = 0; i < flags.length; i++) {
						if (flags[i]) {
							switch (i) {
								case 0:
									numOfWeekdays = returnWeekday;
									setupFragments();
									break;
								case 1:
									updateDrawer();
									break;
								case 2:
									setupList();
                                    updateWeekNumDisplay();
									break;
								default:
									break;
							}
						}
					}
				}
				break;
			case REQ_POSITION:
				LessonListFragment curFragment = fragments.get(mainListPager.getCurrentItem());
				int pos = data.getIntExtra(getString(R.string.intent_position), 0);

				if (resultCode == RESULT_OK) {
					LabelCourse course = (LabelCourse) curFragment.findCourseById(data.getLongExtra(getString(R.string.intent_course_id), -1));
					if (course != null) {
						course.setLabelImgIndex(data.getIntExtra(getString(R.string.intent_label_image_index), 0));
                        course.resetColors();
						curFragment.adapter
								.notifyItemChanged(pos);
					}

					if (curFragment == null)
						Log.e(TAG, "curFragment is null");
					if (curFragment.adapter == null)
						Log.e(TAG, "adapater is null");
				}
				break;
		}
	}

	ConfigManager getConfigManager() {
		return configManager;
	}

	LessonListFragment getListFragment() {
		return fragments.get(mainListPager.getCurrentItem());
	}

    public void onClickThisWeek(View view) {
	    popupThisWeek(curWeek);
	    setupList();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(getString(R.string.pref_week_num))) {
			// TODO update changed items
			setupList();
            updateWeekNumDisplay();
		}
	}

	@Override
	public void onInsertCourse(Course course) {
		if (course.getWeekDay() < fragments.size()) {
			fragments.get(course.getWeekDay()).addCourse(course);
			fragments.get(course.getWeekDay()).updateList();
		}
	}

	@Override
	public void onDeleteCourse(long id) {

	}

	private void popupThisWeek(int weekNum) {
		popupWeekText.setText(String.format(getString(R.string.weekday_week), weekNum));
		popupWeekText.setVisibility(View.VISIBLE);
		popupWeekText.setScaleX(.8f);
		popupWeekText.setScaleY(.8f);
		popupWeekText.setAlpha(.3f);
//		Animator scaleAnimator = ObjectAnimator.ofFloat(popupWeekText, "scaleX", 1f);
//		Animator scaleAnimator2 = ObjectAnimator.ofFloat(popupWeekText, "scaleY", 1f);
		Animator alphaAnimator2 = ObjectAnimator.ofFloat(popupWeekText, "alpha", .7f);
//		scaleAnimator.setDuration(350);
//		scaleAnimator2.setDuration(350);
		alphaAnimator2.setDuration(50);
		Animator alphaAnimator = ObjectAnimator.ofFloat(popupWeekText, "alpha", 0f);
		alphaAnimator.setStartDelay(50);
		alphaAnimator.setDuration(1300);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(alphaAnimator2)
//				.with(scaleAnimator)
//				.with(scaleAnimator2)
				.with(alphaAnimator);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				popupWeekText.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animatorSet.start();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSessionStartOverEvent(SessionStartOverEvent event) {
         if (event.getTarget().equals(TAG)) {
             session.fetchSchedule();
         }
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSessionStartErrorEvent(SessionErrorEvent event) {
		switch (event.getEc()) {
			case SESSION_EC_FAIL_TO_CONNECT:
				Toast.makeText(MainActivity.this, getString(R.string.alert_network_error), Toast.LENGTH_SHORT).show();
				break;
			case SESSION_EC_INVALID_ACCOUNT:
				Toast.makeText(MainActivity.this, getString(R.string.alert_invalid_account), Toast.LENGTH_SHORT).show();
				break;
		}

		if (refreshProgress != null)
            refreshProgress.setVisibility(View.GONE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onScheduleFetchOverEvent(ScheduleFetchOverEvent event) {
		List<Course> courses = event.getCourses();
		if (courses != null) {
			configManager.saveSchedule(courses);
			configManager.saveStuNo(session.getStuNum());
			configManager.savePassword(session.getPsw());
			setupList();
		}

		String stuName = null;
		stuName = session.fetchName();
		Log.i(TAG, "try getting name");
		if (stuName != null) {
			configManager.saveName(stuName);
			Log.i(TAG, "get name " + stuName);
			updateDrawer();
		} else {
			Toast.makeText(MainActivity.this, getString(R.string.alert_invalid_account), Toast.LENGTH_SHORT).show();
		}
		if (refreshProgress != null)
			refreshProgress.setVisibility(View.INVISIBLE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onHomepageFetchOverEvent(HomepageFetchOverEvent event) {
		int integer = event.getWeek();
		if (integer == 0) {
			Toast.makeText(MainActivity.this, "Unable to fetch week", Toast.LENGTH_SHORT).show();
			return;
		}
		// save the nearest past Monday
        long onePastMonday = 946828800000L; // Jan.3rd, 2000.
        long lastFetchTime = today.getTimeInMillis() -
                (today.getTimeInMillis() - onePastMonday) % MILLISECONDS_IN_A_WEEK;
		configManager.saveLastFetchWeekTime(lastFetchTime);

		if (curWeek != integer && integer > 0) {
			curWeek = integer;
			configManager.saveWeek(curWeek);
		}
		if (refreshProgress != null)
			refreshProgress.setVisibility(View.INVISIBLE);
	}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchWeekNum(SwitchWeekNumEvent event) {
        int curNumBak = curWeek;
	    if (event.getWeekNum() > 0) {
		    if (event.getWeekNum() != curWeek)
			    curWeek = event.getWeekNum();
		    popupThisWeek(curWeek);
        }
        Log.i(TAG, "switch week num");
        setupList();
        curWeek = curNumBak;
    }
}
