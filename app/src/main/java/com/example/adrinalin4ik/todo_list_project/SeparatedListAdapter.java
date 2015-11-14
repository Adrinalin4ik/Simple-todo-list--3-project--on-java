package com.example.adrinalin4ik.todo_list_project;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

public class SeparatedListAdapter extends BaseAdapter
	{
		public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
		public final ArrayAdapter<String> headers;
		public final static int TYPE_SECTION_HEADER = 0;
		private LayoutInflater mInflater;
		public final ViewHolder holder =  new ViewHolder();
        final Context context1;
        public CheckBox checkBox;
		public SeparatedListAdapter(final Context context)
			{
                context1=context;
				headers = new ArrayAdapter<String>(context, R.layout.list_header);
				mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


			}

		public void addSection(String section, Adapter adapter)
			{
				this.headers.add(section);
				this.sections.put(section, adapter);
			}

		public Object getItem(int position)
			{
				for (Object section : this.sections.keySet())
					{
						Adapter adapter = sections.get(section);
						int size = adapter.getCount() + 1;

						// check if position inside this section
						if (position == 0) return section;
						if (position < size) return adapter.getItem(position - 1);

						// otherwise jump into next section
						position -= size;
					}
				return null;
			}

		public int getCount()
			{
				// total together all sections, plus one for each section header
				int total = 0;
				for (Adapter adapter : this.sections.values())
					total += adapter.getCount() + 1;
				return total;
			}

		@Override
		public int getViewTypeCount()
			{
				// assume that headers count as one, then total all sections
				int total = 1;
				for (Adapter adapter : this.sections.values())
					total += adapter.getViewTypeCount();
				return total;
			}

		@Override
		public int getItemViewType(int position)
			{
				int type = 1;
				for (Object section : this.sections.keySet())
					{
						Adapter adapter = sections.get(section);
						int size = adapter.getCount() + 1;

						// check if position inside this section
						if (position == 0) return TYPE_SECTION_HEADER;
						if (position < size) return type + adapter.getItemViewType(position - 1);

						// otherwise jump into next section
						position -= size;
						type += adapter.getViewTypeCount();
					}
				return -1;
			}

		public boolean areAllItemsSelectable()
			{
				return false;
			}

		@Override
		public boolean isEnabled(int position)
			{
				return (getItemViewType(position) != TYPE_SECTION_HEADER);
			}


		@Override
		public View getView(int position, View convertView, ViewGroup parent)
			{


                final int position_for_update = position;
				int sectionnum = 0;
				for (Object section : this.sections.keySet())
					{
						Adapter adapter = sections.get(section);
						int size = adapter.getCount() + 1;

						// check if position inside this section
						if (position == 0) return headers.getView(sectionnum, convertView, parent);
						if (position < size) {

							convertView = mInflater.inflate(R.layout.list_item, null);
							checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
							convertView.setTag(holder);
							final String[] attrib = adapter.getItem(position - 1).toString().split(",");// text = [0] isCompleted = [1] id = [2]
							checkBox.setText(attrib[0]);
							checkBox.setChecked(Boolean.valueOf(attrib[1]));
							checkBox.setFocusable(false);
                            final String itemselect = adapter.getItem(position - 1).toString();
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                    if(!Boolean.valueOf(attrib[1])) {
                                        Ion.with(context1)
                                                .load("https://task5todo.herokuapp.com/todo/" + String.valueOf(attrib[2]))
                                                .setHeader("id", String.valueOf(position_for_update))
                                                .setBodyParameter("id", String.valueOf(attrib[2]))
                                                .asString();

                                    }else{
                                        Ion.with(context1)
                                                .load("https://task5todo.herokuapp.com/todo/" + String.valueOf(attrib[2]))
                                                .setHeader("id", String.valueOf(position_for_update))
                                                .setBodyParameter("id", String.valueOf(attrib[2]))
                                                .asString();
                                    }


                                }
                            });

							return convertView;
						}

						// otherwise jump into next section
						position -= size;
						sectionnum++;


					}

				return null;
			}

		@Override
		public long getItemId(int position)
			{
				return position;
			}


		public static class ViewHolder {
			public CheckBox checkBox;
		}

	}



