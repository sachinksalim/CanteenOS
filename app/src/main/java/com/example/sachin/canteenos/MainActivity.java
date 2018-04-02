package com.example.sachin.canteenos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TransactionsDB mDbHelper = new TransactionsDB(this);

    private Button button_submit;
    EditText input_rollno,input_amount;
    String str_rollno, str_amount;
    ImageButton button_remove;
    private LinearLayout transTable; // Table showing recent transactions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        transTable = (LinearLayout) findViewById(R.id.transactions1);
        button_submit=(Button)findViewById(R.id.submit1);
        button_remove = (ImageButton)findViewById(R.id.remove1);

        showAllData(db);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyboard(MainActivity.this);

                input_rollno=(EditText)findViewById(R.id.input1);
                input_amount=(EditText)findViewById(R.id.input2);
                str_rollno = input_rollno.getText().toString();
                str_amount = input_amount.getText().toString();

                if (!validEntries(str_rollno, str_amount)) {
                    return;
                }

                if(mDbHelper.insertData(db, str_rollno, str_amount)) {
                    Toast.makeText(MainActivity.this,"Data Inserted: (" + str_rollno + ", " + str_amount + ")",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this,"Error inserting data!",Toast.LENGTH_SHORT).show();
                }

                showAllData(db);

                // Disable button for 2 seconds
                button_submit.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        button_submit.setEnabled(true);
                    }
                },2000);

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Adds a new transaction entry by creating a new view when 'Submit' button is clicked.
     * @return The newly created view.
     */
    public ViewGroup onAddField() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup rowView = (ViewGroup) inflater.inflate(R.layout.home_transactions, null);
        // Add the new row at the top
        transTable.addView(rowView, transTable.getChildCount());
        return rowView;
    }

    /**
     * Prepares the view to get the input by turning on edit.
     * @param v The view of the field to be updated.
     */
    public void onUpdate(View v) {
        ViewGroup trans = (ViewGroup)v.getParent();
        EditText disp1 = (EditText) trans.getChildAt(1);
        disp1.setFocusableInTouchMode(true);
        disp1.setBackgroundResource(R.drawable.shape_rect_fill);
        EditText disp2 = (EditText) trans.getChildAt(2);
        String amount = disp2.getText().toString();
        disp2.setText(amount.substring(1));
        disp2.setFocusableInTouchMode(true);
        disp2.setBackgroundResource(R.drawable.shape_rect_fill);
        ImageButton submit = (ImageButton) trans.getChildAt(3);
        submit.setVisibility(View.VISIBLE);
        ImageButton update = (ImageButton) trans.getChildAt(4);
        update.setVisibility(View.GONE);
        ImageButton remove = (ImageButton) trans.getChildAt(5);
        remove.setVisibility(View.GONE);
    }

    /**
     * Receives the updated input and updates in the database.
     * @param v The view of the field to be updated.
     */
    public void onUpdateSubmit(View v) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ViewGroup trans = (ViewGroup)v.getParent();
        EditText disp1 = (EditText) trans.getChildAt(1);
        String rollno = disp1.getText().toString();
        EditText disp2 = (EditText) trans.getChildAt(2);
        String amount = disp2.getText().toString();
        if (!validEntries(rollno, amount)) {
            return;
        }
        disp1.setBackgroundResource(R.drawable.shape_rect_nofill);
        disp2.setBackgroundResource(R.drawable.shape_rect_nofill);
        disp1.setFocusable(false);
        disp2.setFocusable(false);
        ImageButton submit = (ImageButton) trans.getChildAt(3);
        submit.setVisibility(View.GONE);
        ImageButton update = (ImageButton) trans.getChildAt(4);
        update.setVisibility(View.VISIBLE);
        ImageButton remove = (ImageButton) trans.getChildAt(5);
        remove.setVisibility(View.VISIBLE);

        TextView id_field = (TextView) trans.getChildAt(0);
        String id = id_field.getText().toString();  // ID of the view to be updated
        Integer updatedRows = mDbHelper.updateData(db, id, rollno, amount);
        showAllData(db);
        if (updatedRows != 0) {
            Toast.makeText(MainActivity.this,"Data updated to: (" + rollno + ", " + amount + ")",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this,"Data updating failed!",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the entry in v from the database.
     * @param v The view of the field to be deleted.
     */
    public void onDelete(View v) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ViewGroup trans = (ViewGroup)v.getParent();
        TextView id_field = (TextView) trans.getChildAt(0);
        final String id = id_field.getText().toString();   // ID of the view to be deleted
        EditText disp1 = (EditText) trans.getChildAt(1);
        String rollno = disp1.getText().toString();
        EditText disp2 = (EditText) trans.getChildAt(2);
        String amount = disp2.getText().toString();

        /* Asks for confirmation */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation");
        builder.setMessage("Entry" + System.lineSeparator() + "RollNo: " + rollno + System.lineSeparator() + "Amount: " + amount + System.lineSeparator() + "will be deleted");
        builder.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer deletedRows = mDbHelper.deleteData(db, id);
                        showAllData(db);
                        if (deletedRows != 0) {
                            Toast.makeText(MainActivity.this,"ID: " + id + " deleted!",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Data deletion failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Shows the last 'Maxshow' entries in the database.
     * @param db The corresponding database.
     */
    public void showAllData(SQLiteDatabase db) {
        transTable.removeAllViews();
        Cursor res = mDbHelper.getAllData(db);
        res.moveToLast();
        ViewGroup trans;
        int Maxshow = 4; // Maximum number of transactions to be shown
        while (!res.isBeforeFirst()) {
            Maxshow -= 1;
            if(Maxshow < 0) {
                break;
            }
            trans = onAddField();
            TextView nextChild;
            for (int idx = 0; idx <= 1; idx++) {
                nextChild = (TextView) ((ViewGroup) trans).getChildAt(idx);
                nextChild.setText(res.getString(idx));
            }
            nextChild = (TextView) ((ViewGroup) trans).getChildAt(2);
            nextChild.setText(getString(R.string.show_amount, res.getString(2)));
            res.moveToPrevious();
        }
    }

    /**
     * Check if the entries are valid.
     * @param str_rollno Roll No.
     * @param str_amount Amount.
     */
    public boolean validEntries(String str_rollno, String str_amount) {
        if (isEmptyString(str_rollno) || isEmptyString(str_amount)) {
            Toast.makeText(MainActivity.this,"Empty field!",Toast.LENGTH_SHORT).show();
            return false;
        }
        Integer int_rollno = Integer.parseInt(str_rollno);
        Integer int_amount = Integer.parseInt(str_amount);
        if(int_rollno < 13000 || (int_rollno > 15000 && int_rollno < 150000) || (int_rollno > 180000)) {
            Toast.makeText(MainActivity.this,"Invalid Roll Number!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(int_amount < 0 || int_amount > 10000) {
            Toast.makeText(MainActivity.this,"Amount too big!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Check if the string is empty.
     * @param string String.
     */
    public boolean isEmptyString(String string) {
        if (string != null && string.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Dismiss the soft keyboard
     */
    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_food_menu) {
            Intent intent = new Intent(this, FoodMenu.class);
            startActivity(intent);
        } else if (id == R.id.nav_customer_data) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
