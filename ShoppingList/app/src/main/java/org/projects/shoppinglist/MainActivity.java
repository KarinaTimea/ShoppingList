package org.projects.shoppinglist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //ArrayAdapter<Product> adapter;
    ListView listView;
    ArrayList<Product> bag = new ArrayList<Product>();
    private String product = "";
    private String amount = "";
    FirebaseListAdapter<Product> productsAdapter;
    Firebase ref = new Firebase("https://karinasshopping.firebaseio.com/products");

    public FirebaseListAdapter getMyAdapter()
    {
        return productsAdapter;
    }
    public Product getItem(int index)
    {
        return (Product) getMyAdapter().getItem(index);

    }
    Product lastDeletedProduct;
    int lastDeletedPosition;
    //save a copy of the selected product
    public void saveCopy () {
        lastDeletedPosition = listView.getCheckedItemPosition();
        lastDeletedProduct = getItem(lastDeletedPosition);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int position = -1;

        if (savedInstanceState != null)
        {
         //   bag = savedInstanceState.getParcelableArrayList("bag");
            position = savedInstanceState.getInt("position");
        }

        productsAdapter = new FirebaseListAdapter<Product> (
                this,
                Product.class,
                android.R.layout.simple_list_item_checked,
                ref
        ) {
            @Override
        protected void populateView(View v, Product product, int i) {
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setText(product.toString());
            }

        };
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(productsAdapter);

        //setting the adapter on the listview
        listView.setAdapter(productsAdapter);

        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"

        //here we create a new adapter linking the bag and the
        //listview
        //adapter = new ArrayAdapter<Product>(this,
         //       android.R.layout.simple_list_item_checked, bag);
        // again Product instead of String

        //setting the adapter on the listview
        //listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        //listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (position != -1) {
           listView.setSelection(position);
        }

        Button addButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final EditText productInput = (EditText) findViewById(R.id.productInput);
        final EditText productQuantity = (EditText) findViewById(R.id.productQuantity);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productInput.getText().toString();
                int quantity = Integer.valueOf(productQuantity.getText().toString());
                Product p = new Product(name, quantity);
            ref.push().setValue(p);
            getMyAdapter().notifyDataSetChanged();
            }
        });

        //The next line is needed in order to say to the ListView
        //that the data has changed - we have added stuff now!
        //getMyAdapter().notifyDataSetChanged();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCopy();
                int index = listView.getCheckedItemPosition();
                getMyAdapter().getRef(index).setValue(null);

                Snackbar snackbar = Snackbar
                        .make(listView, "Item Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ref.push().setValue(lastDeletedProduct);
                                getMyAdapter().notifyDataSetChanged();
                                Snackbar snackbar = Snackbar.make(listView, "Item restored!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        });

                snackbar.show();
            }

        });

        //add some stuff to the list
        //bag.add("Bananas");
        //bag.add("Apples");

        getPreferences();
    }

    /*public void clearList(View view) {
        MyDialogFragment dialog = new MyDialogFragment() {
            @Override
            protected void positiveClick() {
                //Here we override the methods and can now
                bag.clear();
                getMyAdapter().notifyDataSetChanged();
                //do something
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Your list is cleared", Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            protected void negativeClick() {
                //Here we override the method and can now do something
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Your list remains the same", Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        dialog.show(getFragmentManager(), "MyFragment");
    }*/


    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("bag", bag);
        savedInstanceState.putInt("position", listView.getCheckedItemPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public String convertListToSring() {
        String result="";
        for (int i = 0; i<productsAdapter.getCount(); i++)
        {
            Product p = (Product) productsAdapter.getItem(i);
            result = result + p.getQuantity()+ " " + p.getName() +"\n";
        }


        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.item_clear:

                MyDialogFragment dialog = new MyDialogFragment() {
                    @Override
                    protected void positiveClick() {
                        bag.clear();
                        getMyAdapter().notifyDataSetChanged();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "You cleared your list", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    @Override
                    protected void negativeClick() {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Your list didn't change", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                };
                dialog.show(getFragmentManager(), "MyFragment");
                return true;
            case R.id.action_settings:
                setPreferences();
                return true;
            case R.id.share_list:
                String productList = convertListToSring();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, productList);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) // exited preference key
        {
            Toast toast =
            Toast.makeText(MainActivity.this, "back from preferences", Toast.LENGTH_SHORT);
            toast.setText("back from our preferences");
            toast.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setPreferences() {
        // new activity starts
        Intent intent = new Intent(this, SettingsActivity.class);
        // startActivity(intent); // we can use this if we don't care about the result

        // we can use this if we need to know when the user exists our preference screens
        startActivityForResult(intent, 1);
    }

    public void getPreferences() {
        // we read the shared preferences from the
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String gender = prefs.getString("gender", "");
        boolean soundEnabled = prefs.getBoolean("sound", false);

        Toast.makeText(
                this,
                "Email: " + email + "\nGender: " + gender + "\nSound Enabled: " + soundEnabled, Toast.LENGTH_LONG).show();

    }
}
