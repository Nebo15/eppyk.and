package nebo15.eppyk;

/**
 * Created by anton on 06/05/16.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import nebo15.eppyk.data_api.L10N;

public class L10nAdapter extends BaseAdapter {
    Context context;
    L10N[] data;
    String selectedCode = "";
    private static LayoutInflater inflater = null;

    public L10nAdapter(Context context, L10N[] data, String selectedCode) {
        // TODO Auto-generated constructor stub
        this.data = data;
        this.context = context;
        this.selectedCode = selectedCode;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return this.data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView name;
        TextView description;
        ImageView checked;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.activity_listview, null);
        holder.name = (TextView) rowView.findViewById(R.id.CellName);
        holder.name.setTypeface(Typeface.createFromAsset(this.context.getAssets(), "Geometria-Bold.otf"));
        holder.description = (TextView) rowView.findViewById(R.id.CellDescription);
        holder.description.setTypeface(Typeface.createFromAsset(this.context.getAssets(), "Geometria-Bold.otf"));
        holder.checked = (ImageView) rowView.findViewById(R.id.CellChecked);
        if (data[position].code.equals(this.selectedCode) )
            holder.checked.setVisibility(View.VISIBLE);
        else
            holder.checked.setVisibility(View.GONE);
        holder.name.setText("English");
        holder.description.setText(String.format("Answers from %s", data[position].title));
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                L10N item = data[position];
                selectedCode = item.code;
                notifyDataSetChanged();
            }
        });
        return rowView;
    }

}
