package com.example.kevinmeehan.todos;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Todos extends AppCompatActivity {

    final Context context = this;
    private ArrayList<Todo> todoItems;
    private ArrayAdapter<Todo> aToDoAdapter;
    private ListView lvItems;
    private Toolbar toolbar;
    private EditText etEditText;
    private TodosDatabaseHelper todoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);

        todoDB = TodosDatabaseHelper.getInstance(context);
        populateArrayItems();

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialogForIndex(id);
            }

        });
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Todo toBeDeleted = todoItems.get(position);
                todoItems.remove(position);
                aToDoAdapter.notifyDataSetChanged();
                writeItem(toBeDeleted);
                return true;
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Todo List");
        setSupportActionBar(toolbar);

        /*btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void populateArrayItems() {
        readItems();
        aToDoAdapter = new ArrayAdapter<Todo>(this, android.R.layout.simple_list_item_1, todoItems);
    }

    private void readItems() {
        /*File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            todoItems = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            todoItems = new ArrayList<String>();
        }*/
        todoItems = todoDB.getAllVisibleTodos();
    }

    private void writeItem(Todo todo) {
        /*File filesDir = getFilesDir();
        File file = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(file, todoItems);
        } catch (IOException e) {

        }*/
        todoDB.updateTodo(todo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todos, menu);
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

    public void showEditDialogForIndex(long index) {
        final int int_index = (int) (index + 0);
        final Todo editableTodo = todoItems.get(int_index);
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(editableTodo.getText());
        userInput.setSelection(userInput.getText().length());

        // set dialog interface listeners
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        DialogInterface.OnClickListener updateListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editableTodo.setText(userInput.getText().toString());
                aToDoAdapter.notifyDataSetChanged();
                writeItem(editableTodo);
            }
        };
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", updateListener)
                .setNegativeButton("Cancel", cancelListener);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    public void onAddItem(View view) {
        Todo newTodo = new Todo(etEditText.getText().toString(), false, false);
        etEditText.setText("");
        aToDoAdapter.add(newTodo);
        writeItem(newTodo);
    }
}
