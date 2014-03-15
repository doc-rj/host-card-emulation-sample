package de.grundid.hcedemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IsoDepAdapter extends BaseAdapter {
    
private class Message {
    private String text;
    private int type;
    
    Message(String _text, int _type) {
        text = _text;
        type = _type;
    }
};

	private LayoutInflater layoutInflater;
	private List<Message> messages = new ArrayList<Message>(100);
	private int messageCounter;
	private Context context;

	public IsoDepAdapter(LayoutInflater layoutInflater) {
		this.layoutInflater = layoutInflater;
		this.context = layoutInflater.getContext();
	}

	public void addMessage(String message, int type) {
		messageCounter++;
		messages.add(new Message(context.getString(R.string.rcv_msg_header) +
		        messageCounter + "]: " + message, type));
		notifyDataSetChanged();
	}
	
	private CharSequence getItemText(int position) {
	    return (CharSequence)messages.get(position).text;
	}
	
	private int getItemType(int position) {
	    return messages.get(position).type;
	}

	@Override
	public int getCount() {
		return messages == null ? 0 : messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_1, parent, false);
		}
		TextView view = (TextView)convertView.findViewById(R.id.list_item_text);
		view.setText(getItemText(position));
		int color = (getItemType(position) == 0) ?
		        android.R.color.black : android.R.color.holo_red_light;
		view.setTextColor(context.getResources().getColor(color));
		return convertView;
	}
}
