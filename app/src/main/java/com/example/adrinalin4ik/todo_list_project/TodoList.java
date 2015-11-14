package com.example.adrinalin4ik.todo_list_project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.software.shell.fab.ActionButton;

public class TodoList extends Activity  {

    public final static String ITEM_TITLE = "title";
    public final static String ITEM_CAPTION = "caption";


    //Заголовок журнала заданий
    private ListView addJournalEntryItem;

    // Adapter for ListView Contents
    private SeparatedListAdapter adapter;

    // ListView для журнала заданий
    private ListView journalListView;
    final ArrayList<Project> projects = new ArrayList<Project>();
    final ArrayList<Todo> todos = new ArrayList<Todo>();

    private CheckBox mCheckBox;

    public Map<String, ?> createItem(String title, String caption)
    {
        Map<String, String> item = new HashMap<String, String>();
        item.put(ITEM_TITLE, title);
        item.put(ITEM_CAPTION, caption);
        return item;
    }

    @Override
    public void onCreate(Bundle icicle)
    {

     //
        List<String> project_todos_text;
        JsonArray json_projects = null;
        try {
            json_projects = Ion.with(this)
                    .load("https://task5todo.herokuapp.com/get_projects").asJsonArray().get();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "failed " +  e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (json_projects!=null) {
           // Toast.makeText(getApplicationContext(), "Projects completed", Toast.LENGTH_SHORT).show();
            for (final JsonElement projectJsonElement : json_projects) {

                projects.add(new Gson().fromJson(projectJsonElement, Project.class));

            }
            //Toast.makeText(getApplicationContext(), projects.get(0).title, Toast.LENGTH_LONG).show();
        }else  Toast.makeText(getApplicationContext(), "Failed. You have no todos", Toast.LENGTH_LONG).show();
/////////////////////////////////////////////////////////

        JsonArray json_todos = null;
        try {
            json_todos = Ion.with(this)
                    .load("https://task5todo.herokuapp.com/get_todos").asJsonArray().get();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "failed " +  e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (json_todos!=null) {
          //  Toast.makeText(getApplicationContext(), "Todos completed", Toast.LENGTH_SHORT).show();
            for (final JsonElement todoJsonElement : json_todos) {

                todos.add(new Gson().fromJson(todoJsonElement, Todo.class));

            }
            //Toast.makeText(getApplicationContext(), todos.get(1).id, Toast.LENGTH_LONG).show();
          /*  Toast.makeText(getApplicationContext(), todos.get(2).projectId, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), todos.get(3).projectId, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), todos.get(4).projectId, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), todos.get(5).projectId, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), todos.get(6).projectId, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), todos.get(7).projectId, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), todos.get(8).id, Toast.LENGTH_LONG).show();*/
        }else  Toast.makeText(getApplicationContext(), "Failed. You have no todos", Toast.LENGTH_LONG).show();


        super.onCreate(icicle);








        // Sets the View Layer
        setContentView(R.layout.main);

        // Interactive Tools
        final ArrayAdapter<String> journalEntryAdapter = new ArrayAdapter<String>(this, R.layout.add_journalentry_menuitem, new String[]{"Задачи"});

        // AddJournalEntryItem
        addJournalEntryItem = (ListView) this.findViewById(R.id.add_journalentry_menuitem);
        addJournalEntryItem.setAdapter(journalEntryAdapter);
        // обработчик нажатия на заголовок
        addJournalEntryItem.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long duration)
            {
                String item = journalEntryAdapter.getItem(position);

            }
        });

        // Create the ListView Adapter
        adapter = new SeparatedListAdapter(this);
        ArrayAdapter<String> listadapter;

        // Add Sections
        //sorting array by down-up by id
        Collections.sort(todos, new Comparator<Todo>() {
            public int compare(Todo o1, Todo o2) {
                return Integer.valueOf(o1.id) - Integer.valueOf(o2.id);

            }
        });

        try {
            for (int i = 0; i < projects.size(); i++) {
                project_todos_text = new ArrayList<String>();
                for(int j =0;j<todos.size();j++){
                    //проверка на принадлежность к списку по projectId и todoId. В случае успеха пополняем список
                    if (Integer.valueOf(todos.get(j).project_id) == Integer.valueOf(projects.get(i).id)){
                    project_todos_text.add(todos.get(j).text+","+todos.get(j).isCompleted+","+todos.get(j).id);
                    }

                }
                //Заполняем журнал заданий.
                listadapter = new ArrayAdapter<String>(this, R.layout.list_item, project_todos_text);
                adapter.addSection(projects.get(i).title, listadapter);



            }
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        // Get a reference to the ListView holder
        journalListView = (ListView) this.findViewById(R.id.list_journal);

        // Set the adapter on the ListView holder
        journalListView.setAdapter(adapter);

        // обработчик нажатия на журнал заданий
        journalListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long duration) {
                String item = (String) adapter.getItem(position);

            }

        });



    }

    //обработчик нажатия на кнопку создания нового туду
    public void onNewTodoButtonClick(View v) {

        Intent passIntent = new Intent();
         passIntent.setClass(TodoList.this, TodoCreate.class);

        Bundle bundleObject = new Bundle();
        bundleObject.putSerializable("todo_projects", projects);


        passIntent.putExtras(bundleObject);
        startActivity(passIntent);

    }

}

