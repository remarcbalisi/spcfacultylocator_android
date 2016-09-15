package com.ibsph.spcfacloc2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentHomeActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<String> faculty_username = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Bundle faculty_data = getIntent().getExtras();

        String faculty_array_string = faculty_data.getString("online_faculties");
        try {
            JSONArray facultyJsonArray = new JSONArray(faculty_array_string);
            ListView lv = (ListView) findViewById(R.id.listview);
            generateListContent(facultyJsonArray);
            lv.setAdapter(new MyListAdapter(this, R.layout.list_view, data, faculty_username));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void generateListContent(JSONArray facultyJsonArray) throws JSONException {
        for (int i=0; i<facultyJsonArray.length(); i++){
            JSONObject facultyObject = facultyJsonArray.getJSONObject(i);
            String name = facultyObject.getString("name");
            String username = facultyObject.getString("username");
            data.add( name );
            faculty_username.add( username );
        }
    }


    private class MyListAdapter extends ArrayAdapter<String>{
        private int layout;
        private  List faculty_name, faculty_username;
        public MyListAdapter(Context context, int resource, ArrayList<String> data, List<String> username) {
            super(context, resource, data);
            layout = resource;
            faculty_name = data;
            faculty_username = username;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if( convertView == null ){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.title.setText((CharSequence) faculty_name.get(position));
                viewHolder.button = (Button) convertView.findViewById(R.id.locate_btn);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Fetching data from " + faculty_username.get(position) + "...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StudentHomeActivity.this, MapsActivity.class);
                        intent.putExtra("username", faculty_username.get(position).toString() );
                        startActivity(intent);
                    }
                });
                convertView.setTag(viewHolder);
            }
            else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.title.setText(getItem(position));
            }
            return convertView;
        }
    }

    public class ViewHolder{
        ImageView thumbnail;
        TextView title;
        Button button;
    }
}
