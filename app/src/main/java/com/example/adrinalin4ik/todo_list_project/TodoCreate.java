package com.example.adrinalin4ik.todo_list_project;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class TodoCreate extends AppCompatActivity {
    //список проектов
    ArrayList<Project> projects = new ArrayList<Project>();
    //отдельный список для titles. Нужен только для того чтобы поместить в адаптер.
    ArrayList<String> titles = new ArrayList<String>();
    int selectedItem;//позиция выбранного элемента в spenner'е
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_todo_create);

        Bundle extras = getIntent().getBundleExtra("projects");

            // Get the Bundle Object
            Bundle bundleObject = getIntent().getExtras();

            // Get ArrayList Bundle
            ArrayList<Project> projects = (ArrayList<Project>) bundleObject.getSerializable("todo_projects");

            //Retrieve Objects from Bundle
            for (int i=0;i<projects.size();i++)
            {
                titles.add(projects.get(i).title);
            }


            // Получаем экземпляр элемента Spinner
            final Spinner spinner = (Spinner)findViewById(R.id.new_todo_spinner);

            // Настраиваем адаптер
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);


            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Вызываем адаптер
            spinner.setAdapter(adapter);


           //обработчики pinner'а
           spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                selectedItem=selectedItemPosition;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
// обрабатываем нажатие на кнопку создания нового туду (ОК)
    public void onOkButtonClick(View v) {
//инициализация текстового поля
        final EditText textBox = (EditText) findViewById(R.id.editText);
//инициализация массивов для параметров
        //вложенные параметры {"title":"___","text":"___"}
        JsonObject params_include = new JsonObject();

        params_include.addProperty("title",titles.get(selectedItem) );
        params_include.addProperty("text",textBox.getText().toString());

        //параметры {project:{"title":"___","text":"___"}}
        JsonObject params = new JsonObject();
        params.add("project",params_include);
        try {
            Ion.with(this)
                    .load("https://task5todo.herokuapp.com/project")
                    .setJsonObjectBody(params)
            .asJsonObject();
            Toast.makeText(this, "Задание успешно создано", Toast.LENGTH_SHORT).show();
                     //finish();
                // из за необходимости обновления листа , метод finish() не подходит , зато intent более чем подходит

            Intent passIntent = new Intent();
            passIntent.setClass(TodoCreate.this, TodoList.class);
            startActivity(passIntent);


        }catch (Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();}
    }
   //Обработчик кнопки  назад
    public void onBackButtonClick(View v) {
        finish();
    }

}
