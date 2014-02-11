package com.mikhov.Weather;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.*;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.mikhov.Weather.Async.*;
import android.support.v4.content.LocalBroadcastManager;

public class Act extends ListActivity implements OnTaskCompleteListener, View.OnClickListener {

    public static enum TransitionType {
        SlideLeft
    }
    public static TransitionType transitionType;

    AlertDialog.Builder deletes, interrupted, second_chance;

    private static final int DELETE_ID = Menu.FIRST + 1;

    Database database;
    ImageAdapter simpleCursorAdapter;
    private AsyncTaskManager mAsyncTaskManager;
    AdapterView.AdapterContextMenuInfo adapterContextMenuInfo;
    Cursor cursor;
    String cur_task = "";
    Toast toastInternet;
    ImageButton btnUpdate, btnAdd, btnInfo, btnUser;
    Intent intentUpdater;
    long del_town_id;
    TextView tv;
    boolean allowed;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cur_task = "";
        mAsyncTaskManager = new AsyncTaskManager(this, this);
        mAsyncTaskManager.handleRetainedTask(getLastNonConfigurationInstance());
        this.getListView().setDividerHeight(1);
        toastInternet = Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT);

        btnUpdate = (ImageButton) findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);

        btnAdd = (ImageButton) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        btnInfo = (ImageButton) findViewById(R.id.btn_info);
        btnInfo.setOnClickListener(this);

        btnUser = (ImageButton) findViewById(R.id.btn_user);
        btnUser.setOnClickListener(this);

        tv = (TextView) findViewById(R.id.empty);
        database = new Database(this);
        database.open();

        allowed = database.cIsNotEmpty();

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fillData();
            }
        }, new IntentFilter(Updater.UPDATE_TAG));
        intentUpdater = new Intent(Act.this, Updater.class);

        initDialogs();

        Bundle extras = getIntent().getExtras();
        if (internetAccess()) {
            btnUpdate.setImageResource(R.drawable.update_on);
            btnAdd.setImageResource(R.drawable.add_on);
            if (!database.yaIsNotEmpty() && extras == null) {
                cur_task = "yandex";
                mAsyncTaskManager.setupTask(new Task("yandex", getResources(), this));
            } else {
                btnAdd.setImageResource(R.drawable.add_on);
            }
            if (extras != null) {
                if (internetAccess()) {
                    update();
                }
            }
        } else {
            btnUpdate.setImageResource(R.drawable.update_off);
            btnAdd.setImageResource(R.drawable.add_off);
            if (extras != null) {
                toastInternet.show();
            }
        }

        fillData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void fillData() {
        if (allowed) {
            btnInfo.setImageResource(R.drawable.info_off);
            btnAdd.setImageResource(R.drawable.add_on);
        } else {
            btnInfo.setImageResource(R.drawable.info_on);
            btnAdd.setImageResource(R.drawable.add_off);
        }
        if (database.isNotEmpty()) {
            btnUpdate.setImageResource(R.drawable.update_on);
            tv.setText("");
        } else {
            btnUpdate.setImageResource(R.drawable.update_off);
            tv.setText(getString(R.string.base_empty));
        }
        cursor = database.getAllData();
        startManagingCursor(cursor);
        String[] from = new String[] { Database.COL_TOWN, Database.COL_TEMPERATURE1, Database.COL_IMAGE1 };
        int[] to = new int[] { R.id.town, R.id.temperature, R.id.image };
        simpleCursorAdapter = new ImageAdapter(this, R.layout.item, cursor, from, to);
        setListAdapter(simpleCursorAdapter);
        registerForContextMenu(getListView());
    }

    public void update() {
        cur_task = "forecast";
        mAsyncTaskManager.setupTask(new Task("forecast", getResources(), this));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                if (database.isNotEmpty()) {
                    update();
                }
                break;
            case R.id.btn_user:
                this.finish();
                Intent i = new Intent(this, Author.class);
                startActivity(i);
                transitionType = TransitionType.SlideLeft;
                overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                break;
            case R.id.btn_info:
                if (allowed) {
                    Toast.makeText(this, " " + getString(R.string.full_update), Toast.LENGTH_SHORT).show();
                } else {
                    second_chance.show();
                }
                break;
            case R.id.btn_add:
                if (allowed) {
                    startService(intentUpdater);
                    if (internetAccess()) {
                            btnAdd.setImageResource(R.drawable.add_on);
                            this.finish();
                            Intent intent = new Intent(this, AddTownAct.class);
                            startActivity(intent);
                            transitionType = TransitionType.SlideLeft;
                            overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                    } else {
                        btnAdd.setImageResource(R.drawable.add_off);
                        toastInternet.show();
                        if (database.isNotEmpty()) {
                            tv.setText("");
                        } else {
                            tv.setText(getString(R.string.no_internet_empty));
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                del_town_id = adapterContextMenuInfo.id;
                deletes.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (database.getImageByTownId(id) != null) {
            Intent intent = new Intent(Act.this, Act2.class);
            intent.putExtra("id", id);
            startActivity(intent);
            transitionType = TransitionType.SlideLeft;
            overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
        }
    }

    public boolean internetAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo1 : networkInfo) {
            if (networkInfo1.getTypeName().equalsIgnoreCase("mobile")) {
                if (networkInfo1.isConnected()) {
                    return true;
                }
            } else if (networkInfo1.getTypeName().equalsIgnoreCase("wifi")) {
                if (networkInfo1.isConnected())  {
                    return true;
                }
            }
        }
        return false;
    }

    public void initDialogs() {
        deletes = new AlertDialog.Builder(this);
        deletes.setTitle(getResources().getString(R.string.deleting));
        deletes.setMessage(getResources().getString(R.string.confirm_deleting));
        deletes.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                database.deleteTown(del_town_id);
                fillData();
            }
        });
        deletes.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        deletes.setCancelable(true);
        deletes.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });

        interrupted = new AlertDialog.Builder(this);
        interrupted.setTitle(getString(R.string.interrupted));
        interrupted.setMessage(getString(R.string.interrupted_msg));
        interrupted.setPositiveButton(getString(R.string.repeat), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                mAsyncTaskManager.setupTask(new Task("yandex", getResources(), Act.this));
            }
        });
        interrupted.setCancelable(false);

        second_chance = new AlertDialog.Builder(this);
        second_chance.setTitle(getString(R.string.one_more));
        second_chance.setMessage(getString(R.string.one_more_msg));
        second_chance.setPositiveButton(getString(R.string.second_chance), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                cur_task = "yandex";
                mAsyncTaskManager.setupTask(new Task("yandex", getResources(), Act.this));
            }
        });
        second_chance.setCancelable(true);
        second_chance.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    @Override
    public void onTaskComplete(Task task) {
        if (task.isCancelled()) {
            if (cur_task.equals("yandex")) {
                interrupted.show();
            } else {
                Toast.makeText(this, getString(R.string.task_cancelled), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.task_completed), Toast.LENGTH_SHORT).show();
            database.fillC();
            btnAdd.setImageResource(R.drawable.add_on);
            btnInfo.setImageResource(R.drawable.info_off);
            allowed = true;
        }
        fillData();
    }
}
